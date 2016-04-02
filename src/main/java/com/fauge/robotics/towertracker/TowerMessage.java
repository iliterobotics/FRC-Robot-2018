package com.fauge.robotics.towertracker;

import java.awt.image.BufferedImage;

public class TowerMessage
{

    public final double distance;
    public final double AoE;
    public final String alignmentX;
    public final String alignmentY;
    public final BufferedImage bImage;
    public final int xOffSet;

     
    public TowerMessage(double distance, double AoE, String alignmentX, String alignmentY, BufferedImage bImage, int xOffSet){
        this.distance = distance;
        this.AoE = AoE;
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.bImage = bImage;
        this.xOffSet = xOffSet;
      
    }


}
