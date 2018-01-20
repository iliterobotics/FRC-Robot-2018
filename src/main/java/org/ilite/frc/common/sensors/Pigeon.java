package org.ilite.frc.common.sensors;

import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.sensors.PigeonIMU;

public class Pigeon {

	private PigeonIMU mPigeon;
	
	public Pigeon(Hardware pHardware)
	{
		mPigeon = pHardware.getPigeon();
	}
	
	public double getAngle()
	{
		return mPigeon.getFusedHeading();
	}
	
	public void zeroAngle()
	{
		
	}
}
