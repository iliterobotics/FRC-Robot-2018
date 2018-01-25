package org.ilite.frc.common.types;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.PigeonIMU;

public class Pigeon
{

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }
    
    PigeonIMU pigeon;
    private double initialAngle;
    double[] ypr = new double[3];
    
    
    
    public Pigeon(){
        this(0);
    }
    
    public Pigeon(int deviceNumber){
        pigeon = new PigeonIMU(deviceNumber); 
        ErrorCode ypr = pigeon.getYawPitchRoll(new double[3]);
        
    }
    
    public double getInitialAngle(){
        return this.initialAngle;
    }
    
    public double getYaw(){
        pigeon.getYawPitchRoll(ypr);
        if(ypr[0] > 360)
            ypr[0] = ypr[0] - ((int)(Math.floor(ypr[0] / 360)) * 360);
        
        if(ypr[0] < 0)
        {
            if(ypr[0] < -360)
            {
                ypr[0] = ypr[0] + ((int)(Math.floor(ypr[0] / 360)) * 360);
            }
            
            ypr[0] = 360 - Math.abs(ypr[0]);
        }
        return ypr[0];
    }
    
    public double getPitch(){
        pigeon.getYawPitchRoll(ypr);
        if(ypr[1] > 360)
            ypr[1] = ypr[1] - ((int)(Math.floor(ypr[1] / 360)) * 360);
        
        if(ypr[1] < 0)
        {
            if(ypr[1] < -360)
            {
                ypr[1] = ypr[1] + ((int)(Math.floor(ypr[1] / 360)) * 360);
            }
            
            ypr[1] = 360 - Math.abs(ypr[1]);
        }
        return ypr[1];
    }
    
    public double getRoll(){
        pigeon.getYawPitchRoll(ypr);
        if(ypr[2] > 360)
            ypr[2] = ypr[2] - ((int)(Math.floor(ypr[2] / 360)) * 360);
        
        if(ypr[2] < 0)
        {
            if(ypr[2] < -360)
            {
                ypr[2] = ypr[2] + ((int)(Math.floor(ypr[2] / 360)) * 360);
            }
            
            ypr[2] = 360 - Math.abs(ypr[2]);
        }
        return ypr[2];
    }
    
    public double getCompassHading(){
        return pigeon.getAbsoluteCompassHeading();
    }
    
    public double getAngleOffStart(){
        return this.getAngleSum(this.getCompassHading(), -initialAngle);
    }
    
    public void setInitialAngle(double yaw){
        initialAngle = yaw;
    }
    
    public double getAngleSum(double ang1, double ang2){
        double sum = ang1 + ang2;
        if(sum > 180){
            sum = -360 + sum;
        } else if(sum < -180){
            sum = 360 + sum;
        }
        return sum;
    }
    public double getAngleDistance(double angle1, double angle2){
        return getAngleSum(angle1, -angle2);
    }

}
