package org.ilite.frc.robot.commands;

import java.io.File;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.robot.modules.drivetrain.ProfilingMessage;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class FollowPath implements ICommand {
	
	private DriveControl mDriveControl;
	private Data data;
	
	private Config mConfig;
	private Trajectory mLeftTrajectory, mRightTrajectory;
	private EncoderFollower mLeftFollower, mRightFollower;
	
	private boolean mIsBackwards;
	
	public FollowPath(DriveControl pDriveControl, Data data, Trajectory pTrajectory, boolean pIsBackwards) {
		this.mDriveControl = pDriveControl;
		this.data = data;
		this.mIsBackwards = pIsBackwards;
		
		TankModifier mTankModifier = new TankModifier(pTrajectory);
		mTankModifier.modify(SystemSettings.DRIVETRAIN_EFFECTIVE_WHEELBASE);
		
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveControl pDriveControl, Data data, Trajectory pLeftTrajectory, Trajectory pRightTrajectory, boolean pIsBackwards) {
		this.mDriveControl = pDriveControl;
		this.data = data;
		this.mIsBackwards = pIsBackwards;
		this.mLeftTrajectory = pLeftTrajectory;
		this.mRightTrajectory = pRightTrajectory;
		this.mLeftFollower = new EncoderFollower(mLeftTrajectory);
		this.mRightFollower = new EncoderFollower(mRightTrajectory);
	}
	
	public FollowPath(DriveControl pDriveControl, Data data, File pTrajectoryFile, boolean pIsBackwards) {
		this(pDriveControl, data, Pathfinder.readFromCSV(pTrajectoryFile), pIsBackwards);
	}
	
	public FollowPath(DriveControl pDriveControl, Data data, boolean pIsBackwards, Segment ... pSegments) {
		this(pDriveControl, data, new Trajectory(pSegments), pIsBackwards);
	}
	
	public void initialize() {
		mDriveControl.setDriveMessage(new DriveMessage(0, 0, DriveMode.Pathfinder, NeutralMode.Brake));
		mLeftFollower.configureEncoder(data.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS).intValue(), (int)SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER);
		mLeftFollower.configurePIDVA(SystemSettings.DRIVETRAIN_VELOCITY_kP, SystemSettings.DRIVETRAIN_VELOCITY_kI, SystemSettings.DRIVETRAIN_VELOCITY_kD, SystemSettings.DRIVETRAIN_kV, SystemSettings.DRIVETRAIN_kA);
		
		mRightFollower.configureEncoder(data.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS).intValue(), (int)SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN, SystemSettings.DRIVETRAIN_WHEEL_DIAMETER);
		mRightFollower.configurePIDVA(SystemSettings.DRIVETRAIN_VELOCITY_kP, SystemSettings.DRIVETRAIN_VELOCITY_kI, SystemSettings.DRIVETRAIN_VELOCITY_kD, SystemSettings.DRIVETRAIN_kV, SystemSettings.DRIVETRAIN_kA);
	}
	
	public boolean update() {
		if(mLeftFollower.isFinished() && mRightFollower.isFinished()) return true;
		
		mDriveControl.setProfilingMessage(new ProfilingMessage(mLeftFollower, mRightFollower, data.pigeon.get(EPigeon.YAW), mIsBackwards));
		
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	

}
