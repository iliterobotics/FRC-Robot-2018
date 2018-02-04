package org.ilite.frc.common.sensors;

import org.ilite.frc.common.util.FilteredAverage;

public abstract  class IMU {
  public enum Axis {
    YAW,
    PITCH,
    ROLL
  }

  //Collision Threshold => Temporary Value
  protected double mCollisionThreshold_DeltaG;
  protected final FilteredAverage mAccelerationX;
  protected final FilteredAverage mAccelerationY;
  protected double mJerkX = 0d;
  protected double mJerkY = 0d;
  protected double mLastUpdate = 0d;
  
  public IMU(double[] pFilterGains) {
    mAccelerationX = new FilteredAverage(pFilterGains);
    mAccelerationY = new FilteredAverage(pFilterGains);
  }

  /**
   * Sets the g-force threshold.  This is a tuneable parameter between different robots & years.
   * @param pCollisionThreshold_DeltaG - new g-force parameter
   */
  public final void setCollisionThreshold_DeltaG(double pCollisionThreshold_DeltaG) {
    mCollisionThreshold_DeltaG = pCollisionThreshold_DeltaG;
  }
  
  public double get(Axis pAxis) {
    switch(pAxis) {
    case PITCH:
      return getPitch();
    case ROLL:
      return getRoll();
    case YAW:
    default:
      return getYaw();
    }
  }
  
  /**
   * Pre-populates the filters & calculated values so it's done only once per cycle
   * @param pTimestampNow
   */
  public void update(double pTimestampNow) {
    updateSensorCache(pTimestampNow);
    double currentAccelX = getRawAccelX();
    double currentAccelY = getRawAccelY();
    
    mJerkX = (currentAccelX - mAccelerationX.getAverage()) / (pTimestampNow - mLastUpdate);
    mJerkY = (currentAccelY - mAccelerationY.getAverage()) / (pTimestampNow - mLastUpdate);
    
    mAccelerationX.addNumber(currentAccelX);
    mAccelerationY.addNumber(currentAccelY);
    mLastUpdate = pTimestampNow;
  }
  
  public abstract double getYaw();
  public abstract double getPitch();
  public abstract double getRoll();
  public abstract void zeroAll();
  protected abstract double getRawAccelX();
  protected abstract double getRawAccelY();
  protected abstract void updateSensorCache(double pTimestampNow);

  /**
   * @return whether or not the current values of JerkX and JerkY constitute a collision
   */
  public boolean detectCollision(){
    // Combines both axes to get vector magnitude
    return Math.sqrt(Math.pow(mJerkX, 2) + Math.pow(mJerkY, 2)) >= mCollisionThreshold_DeltaG;
//    return Math.abs(mJerkX) >= mCollisionThreshold_DeltaG || Math.abs(mJerkY) >= mCollisionThreshold_DeltaG;
  }
  
  public double getFilteredAccelX() {
    return mAccelerationX.getAverage();
  }
  
  public double getFilteredAccelY() {
    return mAccelerationY.getAverage();
  }
  
  /**
   * @return the change in acceleration over time
   */
  public double getJerkX() {
    return mJerkX;
  }
  
  /**
   * @return the change in acceleration over time
   */
  public double getJerkY() {
    return mJerkY;
  }
  
  // =====================================================================
  // Utility Methods
  // =====================================================================
  public static double clampDegrees(double pRawValue) {
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
  
  public static double getAngleSum(double angle1, double angle2) {
    double sum = angle1 + angle2;
    if(sum > 180){
      sum = -360 + sum;
    } else if(sum < -180){
      sum = 360 + sum;
    }
    return sum;
  }


  public static double convertTo360(double angle){
    if(angle < 0) return angle + 360;
    return angle;
  }
  
  public static double getAngleDistance(double angle1, double angle2){
    return getAngleSum(angle1, -angle2);
  }
}
