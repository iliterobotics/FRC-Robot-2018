package org.ilite.frc.robot.commands;

import java.io.File;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainProfilingMessage;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class FollowPath implements ICommand {
	
	private DriveTrain mDriveControl;
	private Data data;
	
	private Config mConfig;
	private Integer initialLeftPosition, initialRightPosition;
	private Trajectory mLeftTrajectory, mRightTrajectory;
	private EncoderFollower mLeftFollower, mRightFollower;
	
	private boolean mIsBackwards;
	
	public FollowPath(DriveTrain pDriveControl, Data data, Trajectory pTrajectory, boolean pIsBackwards) {
		this.mDriveControl = pDriveControl;
		this.data = data;
		this.mIsBackwards = pIsBackwards;
		
		TankModifier mTankModifier = new TankModifier(pTrajectory);
		mTankModifier.modify(SystemSettings.DRIVETRAIN_EFFECTIVE_WHEELBASE);
		
		this.mLeftTrajectory = mTankModifier.getLeftTrajectory();
		this.mRightTrajectory = mTankModifier.getRightTrajectory();
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveTrain pDriveControl, Data pData, File pLeftTrajectoryFile, File pRightTrajectoryFile, boolean pIsBackwards) {
		this(pDriveControl, pData, Pathfinder.readFromCSV(pLeftTrajectoryFile), Pathfinder.readFromCSV(pRightTrajectoryFile), pIsBackwards);
	}
	
	public FollowPath(DriveTrain pDriveControl, Data data, Trajectory pLeftTrajectory, Trajectory pRightTrajectory, boolean pIsBackwards) {
		this.mDriveControl = pDriveControl;
		this.data = data;
		this.mIsBackwards = pIsBackwards;
		this.mLeftTrajectory = pLeftTrajectory;
		this.mRightTrajectory = pRightTrajectory;
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveTrain pDriveControl, Data data, File pTrajectoryFile, boolean pIsBackwards) {
		this(pDriveControl, data, Pathfinder.readFromCSV(pTrajectoryFile), pIsBackwards);
	}
	
	public FollowPath(DriveTrain pDriveControl, Data data, boolean pIsBackwards, Segment ... pSegments) {
		this(pDriveControl, data, new Trajectory(pSegments), pIsBackwards);
	}
	
	public void initialize(double pNow) {
	  System.out.println("Init FollowPath");
		mDriveControl.setDrivetrainMessage(new DrivetrainMessage(0, 0, DrivetrainMode.Pathfinder, NeutralMode.Brake));
		
		initialLeftPosition = (initialLeftPosition == null) ? 0 : data.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS).intValue();
		initialRightPosition = (initialRightPosition == null) ? 0 : data.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS).intValue();
		
		mLeftFollower.configureEncoder(initialLeftPosition, (int)SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER_FEET);
		mLeftFollower.configurePIDVA(SystemSettings.DRIVETRAIN_LEFT_VELOCITY_kP, SystemSettings.DRIVETRAIN_LEFT_VELOCITY_kI, SystemSettings.DRIVETRAIN_LEFT_VELOCITY_kD, SystemSettings.DRIVETRAIN_LEFT_kV, SystemSettings.DRIVETRAIN_LEFT_kA);
		
		mRightFollower.configureEncoder(initialRightPosition, (int)SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER_FEET);
		mRightFollower.configurePIDVA(SystemSettings.DRIVETRAIN_RIGHT_VELOCITY_kP, SystemSettings.DRIVETRAIN_RIGHT_VELOCITY_kI, SystemSettings.DRIVETRAIN_RIGHT_VELOCITY_kD, SystemSettings.DRIVETRAIN_RIGHT_kV, SystemSettings.DRIVETRAIN_RIGHT_kA);

    System.out.println("Updating FollowPath");
    mDriveControl.setProfilingMessage(new DrivetrainProfilingMessage(mLeftFollower, mRightFollower, mIsBackwards));
	}
	
	public boolean update(double pNow) {
		if(mLeftFollower.isFinished() && mRightFollower.isFinished()) {
		  System.out.println("Finished FollowPath");
		  return true;
		}
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	

}
