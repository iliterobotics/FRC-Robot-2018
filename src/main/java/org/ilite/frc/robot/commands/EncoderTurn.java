package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderTurn implements ICommand {
	
  private final double kDegreeTolerance;
  
	private double mStartTime;
	private double mInitialYaw, mSetpointDegrees;
	private double mLeftTargetPosition, mRightTargetPosition;
	private double mLeftPosition, mRightPosition;
	
	private DriveTrain mDrivetrain;
	private Data mData;
	
	public EncoderTurn(double pDegrees, double pDegreeTolerance, DriveTrain pDrivetrain, Data pData) {
		this.mSetpointDegrees = pDegrees;
		this.kDegreeTolerance = pDegreeTolerance;
		this.mDrivetrain = pDrivetrain;
		this.mData = pData;
	}
	
	public void initialize(double pNow) {
		mStartTime = pNow;
		
		mInitialYaw = IMU.clampDegrees(mData.pigeon.get(EPigeon.YAW));
		mSetpointDegrees = IMU.getAngleDistance(mInitialYaw, IMU.getAngleSum(mInitialYaw, mSetpointDegrees));
		
		mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
		mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
		
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		
		mLeftTargetPosition += mLeftPosition;
		mRightTargetPosition += mRightPosition;
		
		mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPosition + mLeftTargetPosition, mRightPosition + mRightTargetPosition, DrivetrainMode.MotionMagic, NeutralMode.Brake));
	}
	
	public boolean update(double pNow) {
		mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
		mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
				
		if(pNow - mStartTime > SystemSettings.AUTO_TURN_TIMEOUT) {
			System.out.println("EncoderTurn timed out.");
			mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPosition, mRightPosition, DrivetrainMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		
		if(isFinished()) {
			System.out.println("EncoderTurn completed.");
			mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPosition, mRightPosition, DrivetrainMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		System.out.printf("Left: %s Left Target: %s Right: %s Right Target: %s Yaw: %s Target Yaw: %s\n", mLeftPosition, mLeftTargetPosition, mRightPosition, mRightTargetPosition, IMU.clampDegrees(mData.pigeon.get(EPigeon.YAW)), IMU.getAngleSum(mSetpointDegrees, mInitialYaw));
		return false;
	}
	
	public boolean isFinished() {
		boolean leftFinished = Math.abs(mLeftTargetPosition - mLeftPosition) < SystemSettings.AUTO_TURN_POS_TOLERANCE;
		boolean rightFinished = Math.abs(mRightTargetPosition - mRightPosition) < SystemSettings.AUTO_TURN_POS_TOLERANCE;
		boolean angleInTolerance = Math.abs(IMU.getAngleSum(mSetpointDegrees, mInitialYaw) - mData.pigeon.get(EPigeon.YAW)) < kDegreeTolerance;
		return leftFinished && rightFinished && angleInTolerance;
	}
	
	public void shutdown(double pNow) {
		
	}
	
}
