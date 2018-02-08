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
	private double mLeftTargetPosition, mRightTargetPosition, mAbsoluteTargetPosition;
	private double mLeftPosition, mRightPosition, mAbsolutePosition;
	
	private DriveControl mDriveControl;
	private Data mData;
	
	public EncoderTurn(double pDegrees, DriveControl pDriveControl, Data pData) {
		this.mSetpointDegrees = pDegrees;
		this.mDriveControl = pDriveControl;
		this.mData = pData;
	}
	
	public void initialize(double pNow) {
		mStartTime = pNow;
		
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mAbsoluteTargetPosition = Utils.absoluteAverage(mLeftTargetPosition, mRightTargetPosition);
		
		mDriveControl.setDriveMessage(new DriveMessage(mLeftTargetPosition, mRightTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	}
	
	public boolean update(double pNow) {
		mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
		mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
		mAbsolutePosition = Utils.absoluteAverage(mLeftPosition, mRightPosition);
		
		if(pNow - mStartTime > SystemSettings.AUTO_TURN_TIMEOUT) {
			System.out.println("EncoderTurn timed out.");
			mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition, mRightPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		
		if(mAbsoluteTargetPosition - mAbsolutePosition < SystemSettings.AUTO_POS_TOLERANCE) {
			System.out.println("EncoderTurn completed.");
			mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition, mRightPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	    	return true;
		}
		
		return false;
	}
	
	public void shutdown(double pNow) {
		
	}
	
}
