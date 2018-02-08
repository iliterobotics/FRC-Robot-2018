package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderTurn implements ICommand {
	
	private double mCurrentAngle;
	private double mSetpointDegrees;
	private double mLeftTargetPosition, mRightTargetPosition;
	private double mLeftPosition, mRightPosition;
	
	private DriveControl mDriveControl;
	
	public EncoderTurn(double pDegrees, DriveControl pDriveControl) {
		this.mSetpointDegrees = pDegrees;
		this.mDriveControl = pDriveControl;
	}
	
	public void initialize() {
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
		mDriveControl.setDriveMessage(new DriveMessage(mLeftTargetPosition, mRightTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
	}
	
	public boolean update() {
		return false;
	}
	
	public void shutdown() {
		
	}
	
}
