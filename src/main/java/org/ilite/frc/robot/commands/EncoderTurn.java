package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.DriveTrain;

public class EncoderTurn {
	
	private double mCurrentAngle;
	private double mSetpointDegrees;
	private double mLeftTargetPosition, mRightTargetPosition;
	private double mLeftPosition, mRightPosition;
	
	private DriveTrain mDrivetrain;
	
	public EncoderTurn(double pDegrees, DriveTrain pDrivetrain) {
		this.mSetpointDegrees = pDegrees;
		this.mDrivetrain = pDrivetrain;
	}
	
	public void initialize() {
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE;
	}
	
	public boolean update() {
		mLeftTargetPosition = mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE;
		mRightTargetPosition = -mSetpointDegrees * SystemSettings.DRIVETRAIN_WHEEL_TURNS_PER_DEGREE;
		return false;
	}
	
	public void shutdown() {
		
	}
	
}
