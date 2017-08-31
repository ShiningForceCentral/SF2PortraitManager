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
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
            Tile[] tiles = null;
            Color[] palette = null;
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
             System.err.println("com.sfc.sf2.background.io.DisassemblyManager.parseGraphics() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }    
        System.out.println("com.sfc.sf2.background.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return portrait;
    }
    
    public static void exportDisassembly(Portrait portrait, String basepath){
        System.out.println("com.sfc.sf2.background.io.DisassemblyManager.exportDisassembly() - Exporting disassembly ...");
 /*       try {
            for(Portrait background : portrait){
                String index = String.format("%02d", background.getIndex());
                String filePath = basepath + System.getProperty("file.separator") + BASE_FILENAME.replace("XX.bin", index+".bin");
                Tile[] tileset1 = new Tile[192];
                Tile[] tileset2 = new Tile[192];
                System.arraycopy(background.getTiles(),0,tileset1,0,192);
                System.arraycopy(background.getTiles(),192,tileset2,0,192);
                StackGraphicsEncoder.produceGraphics(tileset1);
                byte[] newTileset1 = StackGraphicsEncoder.getNewGraphicsFileBytes();
                StackGraphicsEncoder.produceGraphics(tileset2);
                byte[] newTileset2 = StackGraphicsEncoder.getNewGraphicsFileBytes(); 
                byte[] newBackgroundFileBytes = new byte[2+2+2+32+newTileset1.length+newTileset2.length];
                short tileset2Offset = (short) (newTileset1.length + 6 + 32 - 2);
                newBackgroundFileBytes[0] = 0;
                newBackgroundFileBytes[1] = 0x26;
                newBackgroundFileBytes[2] = (byte)((tileset2Offset>>8)&0xFF);
                newBackgroundFileBytes[3] = (byte)(tileset2Offset&0xFF);
                newBackgroundFileBytes[4] = 0;
                newBackgroundFileBytes[5] = 2;
                PaletteEncoder.producePalette(tileset1[0].getPalette());
                byte[] palette = PaletteEncoder.getNewPaletteFileBytes();
                System.arraycopy(palette, 0, newBackgroundFileBytes, 6, palette.length);
                System.arraycopy(newTileset1, 0, newBackgroundFileBytes, 0x26, newTileset1.length);
                System.arraycopy(newTileset2, 0, newBackgroundFileBytes, 0x26+newTileset1.length, newTileset2.length);
                Path graphicsFilePath = Paths.get(filePath);
                Files.write(graphicsFilePath,newBackgroundFileBytes);
                System.out.println(newBackgroundFileBytes.length + " bytes into " + graphicsFilePath);                
            }
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        } */           
        System.out.println("com.sfc.sf2.background.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
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
