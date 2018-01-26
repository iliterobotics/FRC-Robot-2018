package org.ilite.frc.common.sensors;

public interface IMU {
  public enum Axis {
    YAW,
    PITCH,
    ROLL
  }
  
  public default double get(Axis pAxis) {
    switch(pAxis) {
    case PITCH:
      return getYaw();
    case ROLL:
      return getRoll();
    case YAW:
    default:
      return getYaw();
    }
  }
  
  public double getYaw();
  public double getPitch();
  public double getRoll();
  public void zeroAll();
  
  public default double clampDegrees(double pRawValue) {
    double result = pRawValue;
    if(result > 360)
      result = result - ((int)(Math.floor(result / 360)) * 360);
    
    if(result < 0)
    {
      if(result < -360)
      {
        result = result + ((int)(Math.floor(result / 360)) * 360);
      }
      
      result = 360 - Math.abs(result);
    }
      
    return result;
  }
}
