/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.portrait;

import com.sfc.sf2.graphics.Tile;
import java.awt.image.BufferedImage;

/**
 *
 * @author wiz
 */
public class Portrait {
    
    private Tile[] tiles;
    
    private int[][] eyeTiles;
    
    private int[][] mouthTiles;
    
    private BufferedImage image;

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    public int[][] getEyeTiles() {
        return eyeTiles;
    }

    public void setEyeTiles(int[][] eyeTiles) {
        this.eyeTiles = eyeTiles;
    }

    public int[][] getMouthTiles() {
        return mouthTiles;
    }

    public void setMouthTiles(int[][] mouthTiles) {
        this.mouthTiles = mouthTiles;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    
}
