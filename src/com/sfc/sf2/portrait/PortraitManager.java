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
import com.sfc.sf2.palette.PaletteManager;
import java.awt.Color;

/**
 *
 * @author wiz
 */
public class PortraitManager {
       
    private PaletteManager paletteManager = new PaletteManager();
    private GraphicsManager graphicsManager = new GraphicsManager();
    private Tile[] tiles;
    private Portrait portrait;

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
       
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
    
    
    public void importPng(String filepath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importPng() - Importing PNG ...");
        portrait = PngManager.importPng(filepath);
        this.tiles = portrait.getTiles();
        graphicsManager.setTiles(portrait.getTiles());
        System.out.println("com.sfc.sf2.portrait.PortraitManager.importPng() - PNG imported.");
    }
    
    public void exportPng(String filepath){
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportPng() - Exporting PNG ...");
        PngManager.exportPng(portrait, filepath);
        System.out.println("com.sfc.sf2.portrait.PortraitManager.exportPng() - PNG exported.");       
    }
}