package org.ilite.frc.robot.commands;

import java.io.File;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.robot.modules.DriveTrain;

import com.ctre.phoenix.motorcontrol.ControlMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class FollowPath implements ICommand {
	
	private DriveTrain mDrivetrain;
	private Pigeon mPigeon;
	
	private Config mConfig;
	private Trajectory mLeftTrajectory, mRightTrajectory;
	private EncoderFollower mLeftFollower, mRightFollower;
	
	private boolean mIsBackwards;
	
	private double mDesiredLeftOutput, mDesiredRightOutput;
	private double mActualLeftOutput, mActualRightOutput;
	
	double mActualHeading, mDesiredHeading, mHeadingError;
	double mLeftProfileOutput, mRightProfileOutput, mTurnOutput;
	
	public FollowPath(DriveTrain pDrivetrain, Pigeon pPigeon, Trajectory pTrajectory, boolean pIsBackwards) {
		this.mDrivetrain = pDrivetrain;
		this.mPigeon = pPigeon;
		this.mIsBackwards = pIsBackwards;
		
		TankModifier mTankModifier = new TankModifier(pTrajectory);
		mTankModifier.modify(SystemSettings.DRIVETRAIN_EFFECTIVE_WHEELBASE);
		
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveTrain pDrivetrain, Pigeon pPigeon, Trajectory pLeftTrajectory, Trajectory pRightTrajectory, boolean pIsBackwards) {
		this.mDrivetrain = pDrivetrain;
		this.mPigeon = pPigeon;
		this.mIsBackwards = pIsBackwards;
		this.mLeftTrajectory = pLeftTrajectory;
		this.mRightTrajectory = pRightTrajectory;
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveTrain pDrivetrain, Pigeon pNavx, File pTrajectoryFile, boolean pIsBackwards) {
		this(pDrivetrain, pNavx, Pathfinder.readFromCSV(pTrajectoryFile), pIsBackwards);
	}
	
	public FollowPath(DriveTrain pDrivetrain, Pigeon pNavx, boolean pIsBackwards, Segment ... pSegments) {
		this(pDrivetrain, pNavx, new Trajectory(pSegments), pIsBackwards);
	}
	
	public void initialize() {
		mDrivetrain.initMode(ControlMode.PercentOutput);
		
		mLeftFollower.configureEncoder(mDrivetrain.getLeftPosition(), SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER);
		mLeftFollower.configurePIDVA(SystemSettings.DRIVETRAIN_VELOCITY_kP, SystemSettings.DRIVETRAIN_VELOCITY_kI, SystemSettings.DRIVETRAIN_VELOCITY_kD, SystemSettings.DRIVETRAIN_kV, SystemSettings.DRIVETRAIN_kA);
		
		mRightFollower.configureEncoder(mDrivetrain.getRightPosition(), SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER);
		mRightFollower.configurePIDVA(SystemSettings.DRIVETRAIN_VELOCITY_kP, SystemSettings.DRIVETRAIN_VELOCITY_kI, SystemSettings.DRIVETRAIN_VELOCITY_kD, SystemSettings.DRIVETRAIN_kV, SystemSettings.DRIVETRAIN_kA);
	}
	
	public boolean update() {
		if(mLeftFollower.isFinished() && mRightFollower.isFinished()) return true;
		
		mLeftProfileOutput = mLeftFollower.calculate(mDrivetrain.getLeftPosition());
		mRightProfileOutput = mRightFollower.calculate(mDrivetrain.getRightPosition());
		
		mActualHeading = (mIsBackwards) ? Pathfinder.boundHalfDegrees(mPigeon.getYaw() + 180): mPigeon.getYaw();
		mDesiredHeading = Pathfinder.r2d(mLeftFollower.getHeading()); //Only need to use 1 side because both sides are parallel
		mHeadingError = Pathfinder.boundHalfDegrees(mDesiredHeading - mActualHeading);
		mTurnOutput = SystemSettings.DRIVETRAIN_ANGLE_kP * mHeadingError;
		
		mDesiredLeftOutput = (mLeftProfileOutput + mTurnOutput);
		mDesiredRightOutput = (mRightProfileOutput - mTurnOutput);
		
		mActualLeftOutput = (mIsBackwards) ? mDesiredRightOutput : -mDesiredLeftOutput;
		mActualRightOutput = (mIsBackwards) ? mDesiredLeftOutput : -mDesiredRightOutput;
		
		mDrivetrain.set(mActualLeftOutput, mActualRightOutput);
		
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	

}
