/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.portrait;

import com.sfc.sf2.graphics.GraphicsManager;
import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.portrait.io.DisassemblyManager;
import com.sfc.sf2.portrait.io.PngManager;
import com.sfc.sf2.portrait.io.GifManager;
import com.sfc.sf2.palette.PaletteManager;
import com.sfc.sf2.portrait.io.MetaManager;

/**
 *
 * @author wiz
 */
public class PortraitManager {
       
    private PaletteManager paletteManager = new PaletteManager();
    private GraphicsManager graphicsManager = new GraphicsManager();
    private Tile[] tiles;
    private Portrait portrait = new Portrait();
       
    public void importDisassembly(String filePath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Importing disassembly ...");
        portrait = DisassemblyManager.importDisassembly(filePath);
        this.tiles = portrait.getTiles();
        graphicsManager.setTiles(portrait.getTiles());
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String filepath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Exporting disassembly ...");
        DisassemblyManager.exportDisassembly(portrait, filepath);
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Disassembly exported.");        
    }   
       
    public Portrait[] importAllDisassembly(String basePath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Importing ALL disassembly files ...");
        Portrait[] portraits = DisassemblyManager.importAllDisassembly(basePath);
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - ALL Disassembly files imported.");
        return portraits;
    } 
       
    public Portrait[] importDisassemblyFromEntryFile(String basePath, String entryPath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - Importing ALL disassembly files ...");
        Portrait[] portraits = DisassemblyManager.importDisassemblyFromEntryFile(basePath, entryPath);
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importDisassembly() - ALL Disassembly files imported.");
        return portraits;
    }
    
    
    public void importPng(String filepath, String metadataPath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importPng() - Importing PNG ...");
        portrait.setTiles(PngManager.importPng(filepath).getTiles());
        MetaManager.importMetadata(portrait, getMetadataFullPath(filepath, metadataPath));
        this.tiles = portrait.getTiles();
        graphicsManager.setTiles(portrait.getTiles());
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importPng() - PNG imported.");
    }
    
    public void exportPng(String filepath, String metadataPath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportPng() - Exporting PNG ...");
        PngManager.exportPng(portrait, filepath);
        MetaManager.exportMetadata(portrait, getMetadataFullPath(filepath, metadataPath));
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportPng() - PNG exported.");       
    }
    
    public void importGif(String filepath, String metadataPath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importGif() - Importing GIF ...");
        portrait.setTiles(GifManager.importGif(filepath).getTiles());
        MetaManager.importMetadata(portrait, getMetadataFullPath(filepath, metadataPath));
        this.tiles = portrait.getTiles();
        graphicsManager.setTiles(portrait.getTiles());
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importGif() - GIF imported.");
    }
    
    public void exportGif(String filepath, String metadataPath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportGif() - Exporting GIF ...");
        GifManager.exportGif(portrait, filepath);
        MetaManager.exportMetadata(portrait, getMetadataFullPath(filepath, metadataPath));
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportGif() - GIF exported.");       
    }
    
    private String getMetadataFullPath(String filepath, String metadataPath) {
        if (metadataPath.equals(".meta")) {
            return filepath.substring(0, filepath.lastIndexOf('.'))+metadataPath;
        } else {
            return metadataPath;
        }
    }

    public Portrait getPortrait() {
        return portrait;
    }

    public void setPortrait(Portrait portrait) {
        this.portrait = portrait;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
}
