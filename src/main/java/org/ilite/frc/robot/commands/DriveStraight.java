package org.ilite.frc.robot.commands;

import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriveStraight implements ICommand{
	
	public final double INITIAL_POWER;
	public static final double PROPORTION = 0.02;
	
//	private final DriveTrain driveTrain;
	private DriveControl mDriveControl;
	private Data mData;
	private Double mError, mLastError, mTotalError;
//	private final NavX navx;
	private final Double mDistance, mAlignedCount;
	private Double mSetDistanceInches, mTargetYaw, mCurrentYaw, mAllowableError, mInitialYaw;
	
	private double initialYaw;
	
	public DriveStraight(DriveControl pDriveControl, Data pData, double initialPower, double pAllowableError, double pDistance){
		this.mDriveControl = pDriveControl;
		this.mData = pData;
		this.mDistance = pDistance;
		this.mSetDistanceInches = pDistance;
		this.mAllowableError = pAllowableError;
		this.mAlignedCount = 0.0;
		this.INITIAL_POWER = initialPower;
	}
	
	
	
	public boolean update(double pNow){
		System.out.println("Command printing");

		double yawError = getAngleSum(initialYaw, mData.pigeon.get(EPigeon.YAW));
		//driveTrain.setPower(-(INITIAL_POWER + yawError * PROPORTION), -(INITIAL_POWER - yawError * PROPORTION));
		mDriveControl.setDriveMessage(new DriveMessage((INITIAL_POWER + yawError * PROPORTION), (INITIAL_POWER - yawError * PROPORTION), DriveMode.PercentOutput, NeutralMode.Brake));
		
		return false;
	}
	
	public void adjustBearing(double angleDiff){
		initialYaw = getAngleSum(initialYaw, angleDiff);
	}

	@Override
	public void initialize(double pNow) {
		//this.mError = getError();
		mInitialYaw = mData.pigeon.get(EPigeon.YAW);
	    this.mLastError = mError; // Calculate the initial error value
	    this.mTotalError = this.mError;
	}

	
//	public boolean update(double pNow) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	public static double getAngleSum(double pRawValue1, double pRawValue2) {//temporary method
	    double sum = pRawValue1 + pRawValue2;
	    if(sum > 180){
	      sum = -360 + sum;
	    } else if(sum < -180){
	      sum = 360 + sum;
	    }
	    return sum;
	  }

}
