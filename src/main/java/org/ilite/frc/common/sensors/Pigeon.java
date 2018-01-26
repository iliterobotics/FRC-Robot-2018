package org.ilite.frc.common.sensors;

import org.ilite.frc.common.util.FilteredAverage;
import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.sensors.*;

public class Pigeon implements IMU{

	private double[] ypr;
	private PigeonIMU mPigeon;

  //Collision Threshold => Temporary Value
  final static double kCollisionThreshold_DeltaG = 0.5f;
  private final FilteredAverage mAccelerationX;
  private final FilteredAverage mAccelerationY;
  private double mLastUpdate = 0d;
  
  private static final double[] kCollisionGains = {0.75, 0.25};
	
	public Pigeon(Hardware pHardware)
	{
		ypr = new double[3];
		mPigeon = pHardware.getPigeon();
		System.out.println(mPigeon.getFusedHeading());
//		mPigeon.getYawPitchRoll(ypr);
		
		mAccelerationX = new FilteredAverage(kCollisionGains);
		mAccelerationY = new FilteredAverage(kCollisionGains);

	}

	public double getYaw() {
    mPigeon.getYawPitchRoll(ypr);
    return clampDegrees(ypr[0]);
	  
	  //TODO - is this one correct for the pigeon?
//	  return mPigeon.getFusedHeading();
	}
	
	public double getPitch() {
    mPigeon.getYawPitchRoll(ypr);
    return clampDegrees(ypr[1]);
	}
	
	public double getRoll() {
    mPigeon.getYawPitchRoll(ypr);
    return clampDegrees(ypr[2]);
	}	
	
	public void zeroAll()
	{
		for(int i = 0; i < 3; i++)
		{
			ypr[i] = 0;
		}
	}

	public double getAccelX() {
	  //TODO
		return 0d;
	}

	public double getAccelY() {
		// TODO Auto-generated method stub
		return 0d;
	}
  
  
  public boolean detectCollision(double pNow){
    double currentAccelX = getAccelX();
    double currentAccelY = getAccelY();
    
    double currentJerkX = (currentAccelX - mAccelerationX.getAverage()) / (pNow - mLastUpdate);
    double currentJerkY = (currentAccelY - mAccelerationY.getAverage()) / (pNow - mLastUpdate);
    
    mAccelerationX.addNumber(currentAccelX);
    mAccelerationY.addNumber(currentAccelY);
    mLastUpdate = pNow;
    
    return Math.abs(currentJerkX) >= kCollisionThreshold_DeltaG || Math.abs(currentJerkY) >= kCollisionThreshold_DeltaG;
  }
}