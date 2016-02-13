package com.fauge.robotics.towertracker;


public class TowerMessage
{

    public final double distance;
    public final double AoE;
    public TowerMessage(double distance, double AoE){
        this.distance = distance;
        this.AoE = AoE;
    }

}
