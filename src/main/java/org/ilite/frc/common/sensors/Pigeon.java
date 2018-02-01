package org.ilite.frc.common.sensors;

import org.ilite.frc.common.util.FilteredAverage;

import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pigeon extends IMU{

	private double[] ypr;
	private PigeonIMU mPigeon;

  //Collision Threshold => Temporary Value
  final static double kCollisionThreshold_DeltaG = 0.5f;
  private final FilteredAverage mAccelerationX;
  private final FilteredAverage mAccelerationY;
  private double mJerkX = 0d;
  private double mJerkY = 0d;
  private double mLastUpdate = 0d;
  
  //TODO - single value for now - could be VERY noisy
  // others to try: {0.75, 0.25}, {0.6, 0.4}, {0.5, 0.3, 0.2}
  private static final double[] kCollisionGains = {1.0};
	
	public Pigeon(PigeonIMU pHardware){
		super(kCollisionGains);
		this.mAccelerationX = super.mAccelerationX;
		this.mAccelerationY = super.mAccelerationY;
		ypr = new double[3];
		mPigeon = pHardware;
		//mAccelerationX = new FilteredAverage(kCollisionGains);
		//mAccelerationY = new FilteredAverage(kCollisionGains);
	}
	
	/**
	 * Pre-populates the filters & calculated values so it's done only once per cycle
	 * @param pTimestampNow
	 */
	public void update(double pTimestampNow) {
    mPigeon.getYawPitchRoll(ypr);
    for(int i = 0 ; i < ypr.length; i++) {
      ypr[i] = clampDegrees(ypr[i]);
    }

    double currentAccelX = getRawAccelX();
    double currentAccelY = getRawAccelY();
    
    mJerkX = (currentAccelX - mAccelerationX.getAverage()) / (pTimestampNow - mLastUpdate);
    mJerkY = (currentAccelY - mAccelerationY.getAverage()) / (pTimestampNow - mLastUpdate);
    
    mAccelerationX.addNumber(currentAccelX);
    mAccelerationY.addNumber(currentAccelY);
    mLastUpdate = pTimestampNow;
    
    SmartDashboard.putNumber("Yaw", getYaw());
    SmartDashboard.putNumber("Pitch", getPitch());
    SmartDashboard.putNumber("Roll", getRoll());
    SmartDashboard.putNumber("AccelX", getAccelX());
    SmartDashboard.putNumber("AccelX", getAccelY());
    SmartDashboard.putNumber("JerkX", getJerkX());
    SmartDashboard.putNumber("JerkY", getJerkX());
	}
	
	public double getHeading() {
	  return mPigeon.getFusedHeading();
	}
	
	public double getYaw() {
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
	
	public double getPitch() {
    return ypr[1];
	}
	
	public double getRoll() {
    return ypr[2];
	}	
	
	public double getAccelX() {
	  return mAccelerationX.getAverage();
	}
	
	public double getAccelY() {
	  return mAccelerationY.getAverage();
	}
	
	public double getJerkX() {
	  return mJerkX;
	}
	
	public double getJerkY() {
	  return mJerkY;
	}
	
	public void zeroAll() {
		for(int i = 0; i < ypr.length; i++) {
			ypr[i] = 0;
		}
		mPigeon.setFusedHeading(0d, 20); //TODO - figure out CAN timeout defaults
	}

	public double getRawAccelX() {
	  //TODO - either biased accelerometer, or somewhere in the quaternion?
		return 0d;
	}

	public double getRawAccelY() {
    //TODO - either biased accelerometer, or somewhere in the quaternion?
		return 0d;
	}
  
  
  public boolean detectCollision(){
    return Math.abs(mJerkX) >= kCollisionThreshold_DeltaG || Math.abs(mJerkY) >= kCollisionThreshold_DeltaG;
  }

@Override
protected void updateSensorCache(double pTimestampNow) {
	// TODO Auto-generated method stub
	
}
}