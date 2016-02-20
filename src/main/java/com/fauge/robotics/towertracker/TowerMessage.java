package com.fauge.robotics.towertracker;

import java.awt.image.BufferedImage;

public class TowerMessage
{

    public final double distance;
    public final double AoE;
    public final String alignment;
    public final BufferedImage bImage;
    
    public TowerMessage(double distance, double AoE, String alignment, BufferedImage bImage){
        this.distance = distance;
        this.AoE = AoE;
        this.alignment = alignment;
        this.bImage = bImage;
        
        
        
        
    }


}
