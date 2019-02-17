/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.portrait.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.compressed.BasicGraphicsDecoder;
import com.sfc.sf2.graphics.compressed.BasicGraphicsEncoder;
import com.sfc.sf2.portrait.Portrait;
import com.sfc.sf2.graphics.compressed.StackGraphicsDecoder;
import com.sfc.sf2.graphics.compressed.StackGraphicsEncoder;
import com.sfc.sf2.palette.graphics.PaletteDecoder;
import com.sfc.sf2.palette.graphics.PaletteEncoder;
import com.sfc.sf2.portrait.layout.PortraitLayout;
import java.awt.Color;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    
    public static Portrait importDisassembly(String filepath){
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importDisassembly() - Importing disassembly file ...");
        
        Portrait portrait = new Portrait();
        try{
            Tile[] tiles;
            Color[] palette;
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                byte[] data = Files.readAllBytes(path);
                if(data.length>36){
                    short eyesTileNumber = getNextWord(data,0);
                    int[][] eyesTiles = new int[eyesTileNumber][4];
                    for(int i=0;i<eyesTileNumber;i++){
                        eyesTiles[i][0] = (int)(getNextByte(data,2+i*4+0)&0xFF);
                        eyesTiles[i][1] = (int)(getNextByte(data,2+i*4+1)&0xFF);
                        eyesTiles[i][2] = (int)(getNextByte(data,2+i*4+2)&0xFF);
                        eyesTiles[i][3] = (int)(getNextByte(data,2+i*4+3)&0xFF);
                    }
                    portrait.setEyeTiles(eyesTiles);
                    short mouthTileNumber = getNextWord(data,2+eyesTileNumber*4);
                    int[][] mouthTiles = new int[mouthTileNumber][4];
                    for(int i=0;i<mouthTileNumber;i++){
                        mouthTiles[i][0] = (int)(getNextByte(data,2+eyesTileNumber*4+2+i*4+0)&0xFF);
                        mouthTiles[i][1] = (int)(getNextByte(data,2+eyesTileNumber*4+2+i*4+1)&0xFF);
                        mouthTiles[i][2] = (int)(getNextByte(data,2+eyesTileNumber*4+2+i*4+2)&0xFF);
                        mouthTiles[i][3] = (int)(getNextByte(data,2+eyesTileNumber*4+2+i*4+3)&0xFF);
                    } 
                    portrait.setMouthTiles(mouthTiles);
                    int paletteOffset = 2+eyesTileNumber*4+2+mouthTileNumber*4;
                    byte[] paletteData = new byte[32];
                    System.arraycopy(data, paletteOffset, paletteData, 0, paletteData.length);
                    palette = PaletteDecoder.parsePalette(paletteData);
                    palette[0] = new Color(255, 255, 255, 0);
                    int graphicsOffset = paletteOffset + 32;
                    byte[] tileData = new byte[data.length-graphicsOffset];
                    System.arraycopy(data, graphicsOffset, tileData, 0, tileData.length);
                    tiles = new StackGraphicsDecoder().decodeStackGraphics(tileData, palette);
                    portrait.setTiles(tiles);
                }else{
                    System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.parseGraphics() - File ignored because of too small length (must be a dummy file) " + data.length + " : " + filepath);
                }
            }            
        }catch(Exception e){
             System.err.println("com.sfc.sf2.portrait.io.DisassemblyManager.parseGraphics() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }    
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return portrait;
    }
    
    public static Portrait[] importAllDisassembly(String filepath){
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importAllDisassembly() - Importing ALL disassembly files ...");
        Portrait[] portraits = null;
        List<Portrait> portraitList = new ArrayList();
        
        String dir = filepath.substring(0, filepath.lastIndexOf(System.getProperty("file.separator")));
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for(File f : files){        
            if(f.getName().endsWith(".bin")){
                System.out.println("Importing "+f.getAbsolutePath()+" ...");
                Portrait portrait = importDisassembly(f.getAbsolutePath());
                portrait.setImage(PortraitLayout.buildImage(portrait.getTiles(), 8, true));
                portraitList.add(portrait);
            }
                
            
        }
        portraits = new Portrait[portraitList.size()];
        portraits = portraitList.toArray(portraits);
        
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importAllDisassembly() - ALL Disassembly files imported.");
        return portraits;
    }
    
    public static Portrait[] importDisassemblyFromEntryFile(String basepath, String entriesPath){
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importAllDisassembly() - Importing ALL disassembly files ...");
        Portrait[] portraits = null;
        List<Portrait> portraitList = new ArrayList();
        try{
            File entryFile = new File(entriesPath);
            Scanner scan = new Scanner(entryFile);
            List<String> filepaths = new ArrayList();
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.contains("dc.l")){
                    String pointer = line.substring(line.indexOf("dc.l")+5).trim();
                    String filepath = null;
                    Scanner filescan = new Scanner(entryFile);
                    while(filescan.hasNext()){
                        String pathline = filescan.nextLine();
                        if(pathline.startsWith(pointer)){
                            filepath = pathline.substring(pathline.indexOf("\"")+1, pathline.lastIndexOf("\""));
                        }
                    }
                    filepaths.add(filepath);
                }
            }            
            for(int i=0;i<filepaths.size();i++){
                String filePath = basepath + filepaths.get(i);
                System.out.println("Importing "+filePath+" ...");
                Portrait portrait = importDisassembly(filePath);
                portrait.setImage(PortraitLayout.buildImage(portrait.getTiles(), 8, true));
                portraitList.add(portrait);
            }
        }catch(Exception e){
             System.err.println("com.sfc.sf2.mapsprite.io.PngManager.importPng() - Error while parsing graphics data : "+e);
        }      
        portraits = new Portrait[portraitList.size()];
        portraits = portraitList.toArray(portraits);   
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.importAllDisassembly() - ALL Disassembly files imported.");
        return portraits;
    }
    
    public static void exportDisassembly(Portrait portrait, String filepath){
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.exportDisassembly() - Exporting disassembly ...");
        try{
                byte[] eyeTiles = new byte[2+portrait.getEyeTiles().length*4];
                eyeTiles[0] = 0;
                eyeTiles[1] = (byte)(portrait.getEyeTiles().length & 0xFF);
                for(int i=0;i<portrait.getEyeTiles().length;i++){
                    eyeTiles[2+i*4+0] = (byte)(portrait.getEyeTiles()[i][0] & 0xFF);
                    eyeTiles[2+i*4+1] = (byte)(portrait.getEyeTiles()[i][1] & 0xFF);
                    eyeTiles[2+i*4+2] = (byte)(portrait.getEyeTiles()[i][2] & 0xFF);
                    eyeTiles[2+i*4+3] = (byte)(portrait.getEyeTiles()[i][3] & 0xFF);
                }
                byte[] mouthTiles = new byte[2+portrait.getMouthTiles().length*4];
                mouthTiles[0] = 0;
                mouthTiles[1] = (byte)(portrait.getMouthTiles().length & 0xFF);
                for(int i=0;i<portrait.getMouthTiles().length;i++){
                    mouthTiles[2+i*4+0] = (byte)(portrait.getMouthTiles()[i][0] & 0xFF);
                    mouthTiles[2+i*4+1] = (byte)(portrait.getMouthTiles()[i][1] & 0xFF);
                    mouthTiles[2+i*4+2] = (byte)(portrait.getMouthTiles()[i][2] & 0xFF);
                    mouthTiles[2+i*4+3] = (byte)(portrait.getMouthTiles()[i][3] & 0xFF);
                }
                PaletteEncoder.producePalette(portrait.getTiles()[0].getPalette());
                byte[] palette = PaletteEncoder.getNewPaletteFileBytes();
                StackGraphicsEncoder.produceGraphics(portrait.getTiles());
                byte[] tileset = StackGraphicsEncoder.getNewGraphicsFileBytes();
                byte[] newPortraitFileBytes = new byte[eyeTiles.length+mouthTiles.length+palette.length+tileset.length];
                System.arraycopy(eyeTiles, 0, newPortraitFileBytes, 0, eyeTiles.length);
                System.arraycopy(mouthTiles, 0, newPortraitFileBytes, eyeTiles.length, mouthTiles.length);
                System.arraycopy(palette, 0, newPortraitFileBytes, eyeTiles.length+mouthTiles.length, palette.length);
                System.arraycopy(tileset, 0, newPortraitFileBytes, eyeTiles.length+mouthTiles.length+palette.length, tileset.length);
                Path graphicsFilePath = Paths.get(filepath);
                Files.write(graphicsFilePath,newPortraitFileBytes);
                System.out.println(newPortraitFileBytes.length + " bytes into " + graphicsFilePath);                
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }  
        System.out.println("com.sfc.sf2.portrait.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
    }     
    
    private static short getNextWord(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor+1]);
        bb.put(data[cursor]);
        short s = bb.getShort(0);
        return s;
    }
    
    private static byte getNextByte(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor]);
        byte b = bb.get(0);
        return b;
    }    

    
}
