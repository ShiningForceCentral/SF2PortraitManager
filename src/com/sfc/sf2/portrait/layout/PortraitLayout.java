/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.portrait.layout;

import com.sfc.sf2.graphics.Tile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import javax.swing.JPanel;

/**
 *
 * @author wiz
 */
public class PortraitLayout extends JPanel {
    
    private static final int DEFAULT_TILES_PER_ROW = 8;
    
    private int tilesPerRow = DEFAULT_TILES_PER_ROW;
    private Tile[] tiles;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        BufferedImage image = buildImage(this.tiles,this.tilesPerRow, false);
        setSize(image.getWidth(), image.getHeight());
        return image;
    }
    
    public static BufferedImage buildImage(Tile[] tiles, int tilesPerRow, boolean pngExport){
        int imageHeight = (tiles.length/tilesPerRow)*8;
        if(tiles.length%tilesPerRow!=0){
            imageHeight+=8;
        }
        BufferedImage image;
        IndexColorModel icm = buildIndexColorModel(tiles[0].getPalette());
        image = new BufferedImage(tilesPerRow*8, imageHeight , BufferedImage.TYPE_BYTE_BINARY, icm);
        Graphics graphics = image.getGraphics();     
            for(int i=0;i<8;i++){
                for(int j=0;j<8;j++){
                    graphics.drawImage(tiles[(i*8)+j].getImage(), j*8, i*8, null);
                }
            }
        return image;
    }
    
    private static IndexColorModel buildIndexColorModel(Color[] colors){
        byte[] reds = new byte[16];
        byte[] greens = new byte[16];
        byte[] blues = new byte[16];
        byte[] alphas = new byte[16];
        reds[0] = (byte)0xFF;
        greens[0] = (byte)0xFF;
        blues[0] = (byte)0xFF;
        alphas[0] = 0;
        for(int i=1;i<16;i++){
            reds[i] = (byte)colors[i].getRed();
            greens[i] = (byte)colors[i].getGreen();
            blues[i] = (byte)colors[i].getBlue();
            alphas[i] = (byte)0xFF;
        }
        IndexColorModel icm = new IndexColorModel(4,16,reds,greens,blues,alphas);
        return icm;
    }     
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
        public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public void setTilesPerRow(int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;
    }
    
}
