package org.ilite.frc.common.sensors;

import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.sensors.*;

public class Pigeon implements IMU{

	private double[] ypr;
	private PigeonIMU mPigeon;

  //Collision Threshold => Temporary Value
  final static double kCollisionThreshold_DeltaG = 0.5f;
  double lastAccelX;
  double lastAccelY;
  double lastAccelZ;
	
	public Pigeon(Hardware pHardware)
	{
		ypr = new double[3];
		mPigeon = pHardware.getPigeon();
		System.out.println(mPigeon.getFusedHeading());
//		mPigeon.getYawPitchRoll(ypr);

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
		
		return 0;
	}

	public double getAccelY() {
		// TODO Auto-generated method stub
		return 0;
	}
  
  
  public void detectCollision(){
    boolean collisionDetected = false;
    double currentAccelX = pidgey.getAccelX();
    double currentJerkX = currentAccelX - lastAccelX;
    lastAccelX = currentAccelX;
    double currentAccelY = pidgey.getAccelY();
    double currentJerkY = currentAccelY - lastAccelY;
    lastAccelY = currentAccelY;
    //          double currentAccelZ = pidgey.getAccelZ();
    //          double currentJerkZ = currentAccelZ - lastAccelZ;
    //          lastAccelZ = currentAccelZ;
    if ( ( Math.abs(currentJerkX) > kCollisionThreshold_DeltaG ) ||
        ( Math.abs(currentJerkY) > kCollisionThreshold_DeltaG) ) {
      collisionDetected = true;
    }
    
    
  }
}