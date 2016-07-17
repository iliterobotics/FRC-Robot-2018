package com.fauge.robotics.towertracker;

import java.awt.image.BufferedImage;

public class TowerMessage
{

    public final double distance;
    public final double aoe_X;
    public final String alignmentX;
    public final String alignmentY;
    public final BufferedImage bImage;
    public final int xOffSet;
	public final double aoe_Y;

     
    public TowerMessage(double distance, double pAoe_X, double pAoe_Y, String alignmentX, String alignmentY, BufferedImage bImage, int xOffSet){
        this.distance = distance;
        this.aoe_X = pAoe_X;
        this.aoe_Y = pAoe_Y;
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.bImage = bImage;
        this.xOffSet = xOffSet;
      
    }


}
