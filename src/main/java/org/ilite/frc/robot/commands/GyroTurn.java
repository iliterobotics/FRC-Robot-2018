package org.ilite.frc.robot.commands;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class GyroTurn implements ICommand {

private static final int TIMEOUT = 3000;
	
	private DriveControl mDriveControl;
	private Data mData;
	
	private static final int kMIN_ALIGNED_COUNT = 5;
	private static final double kP = 0.0101;
	private static final double kD = 0.0105;
	private static final double kI = 0.0;
	private static final double kMIN_POWER = 0.05;
	
	private double mSetpointDegrees, mTargetYaw;
	private double mError, mLastError, mTotalError;
	private double mAlignedCount;
	private final double mAllowableError;
	
	private long mStartTime;
	
	double mLeftPower, mRightPower, mOutput = 0;
	
	public GyroTurn(DriveControl pDriveControl, Data pData, double pDegrees, double pAllowableError)
	{
		this.mDriveControl = pDriveControl;
		this.mData = pData;
		this.mSetpointDegrees = pDegrees;
		this.mAlignedCount = 0;
		this.mAllowableError = pAllowableError;
	}

	@Override
	public void initialize(double pNow) {
		this.mTargetYaw = mSetpointDegrees;  //Calculate the target heading off of # of degrees to turn
		this.mLastError = this.mError = getError(); //Calculate the initial error value
		this.mTotalError += this.mError;
		mStartTime = System.currentTimeMillis();
	}
	
	public boolean update(double pNow)
	{
		mError = getError(); //Update error value
		System.out.println(mError);
		this.mTotalError += this.mError; //Update running error total
		
		if((Math.abs(mError) < mAllowableError)) mAlignedCount++;
		if(mAlignedCount >= kMIN_ALIGNED_COUNT) return true;
		if(System.currentTimeMillis() - mStartTime > TIMEOUT) return true;
		
		mOutput = ((kP * mError) + (kI * mTotalError) + (kD * (mError - mLastError)));
		if(Math.abs(mOutput) < kMIN_POWER){
			double scalar = mOutput>0?1:-1;
			mOutput = kMIN_POWER * scalar;
		}
		mLeftPower = mOutput; 
		mRightPower = -mOutput;
		
		mDriveControl.setDriveMessage(new DriveMessage(mLeftPower, mRightPower, DriveMode.PercentOutput, NeutralMode.Brake));
		
		mLastError = mError;
		return false;
	}
	
	public double getError(){
		return IMU.getAngleDistance(IMU.convertTo360(IMU.clampDegrees(mData.pigeon.get(EPigeon.YAW))), mTargetYaw);
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
}
