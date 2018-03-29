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
  /**
   * Take any value in degrees, mod it
   * @param pRawValue input, in degrees
   * @return a value between 0 and 360
   */
  public static double clampDegrees(double pRawValue) {
    double result = pRawValue % 360;
    if(result < 0) {
      result += 360d;
    }
    return result;
  }
  
  public static double getAngleSum(double pRawValue1, double pRawValue2) {
    double sum = pRawValue1 + pRawValue2;
    if(sum > 180d){
      sum = -360d + sum;
    } else if(sum < -180d){
      sum = 360d + sum;
    }
    return sum;
  }

  public static double getAngleDistance(double pFrom, double pTo){
    return getAngleSum(pFrom, -pTo);
  }
  
  public static void main(String[] pArgs) {
    final double incr = 60d;
    System.out.println("INPUT\tCLAMP\tANGLEDIST\tANGLESUM");
    for(double i = -720d ; i <= 720d ; i += incr) {
      System.out.println(
          i + "\t" + 
          clampDegrees(i) + "\t" + 
          getAngleDistance(i-incr/2, i+incr/2) + "\t\t" + 
          getAngleSum(i-incr/2, i+incr/2));
    }
  }
}
