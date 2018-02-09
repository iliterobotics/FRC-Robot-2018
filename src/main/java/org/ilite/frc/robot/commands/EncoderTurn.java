package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Timer;

public class EncoderTurn implements ICommand {
	
	private double mStartTime;
	private double mSetpointDegrees;
	private double mLeftTargetPosition, mRightTargetPosition;
	private double mLeftPosition, mRightPosition;
	
	private DriveControl mDriveControl;
	private Data mData;
	
	public EncoderTurn(double pDegrees, DriveControl pDriveControl, Data pData) {
		this.mSetpointDegrees = pDegrees / 2;
		this.mDriveControl = pDriveControl;
		this.mData = pData;
	}
	
	public void initialize(double pNow) {
		mStartTime = pNow;

		mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
		mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
		
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		
		mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition + mLeftTargetPosition, mRightPosition + mRightTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	}
	
	public boolean update(double pNow) {
		mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
		mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
		
		if(pNow - mStartTime > SystemSettings.AUTO_TURN_TIMEOUT) {
			System.out.println("EncoderTurn timed out.");
			mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition, mRightPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		
		if(isFinished()) {
			System.out.println("EncoderTurn completed.");
			mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition, mRightPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		
		return false;
	}
	
	public boolean isFinished() {
		boolean leftFinished = Math.abs(mLeftTargetPosition - mLeftPosition) < SystemSettings.AUTO_TURN_POS_TOLERANCE;
		boolean rightFinished = Math.abs(mRightTargetPosition - mRightPosition) < SystemSettings.AUTO_TURN_POS_TOLERANCE;
		return leftFinished && rightFinished;
	}
	
	public void shutdown(double pNow) {
		
	}
	
}
