package org.ilite.frc.common.sensors;

import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.sensors.*;

public class Pigeon {

	private double[] ypr;
	private PigeonIMU mPigeon;
	
	public Pigeon(Hardware pHardware)
	{
		ypr = new double[3];
		mPigeon = pHardware.getPigeon();
		System.out.println(mPigeon.getFusedHeading());
//		mPigeon.getYawPitchRoll(ypr);

	}

	public double getYaw()
	{
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
	
	public double getPitch()
	{
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
	
	public double getRoll()
	{
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
	public void zeroAll()
	{
		for(int i = 0; i < 3; i++)
		{
			ypr[i] = 0;
		}
	}
	
	public PigeonIMU getPigeon()
	{
		return mPigeon;
	}

	public double getAccelX() {
		
		return 0;
	}

	public double getAccelY() {
		// TODO Auto-generated method stub
		return 0;
	}
}