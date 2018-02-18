package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainControl;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainProfilingMessage;
import org.ilite.frc.robot.modules.drivetrain.PathFollower;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.codex.Codex;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);

	private DrivetrainControl driveControl;

	private Data data;
	//private PDM g;
	
	final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	
	private DrivetrainMode driveMode;
	private ControlMode controlMode; 
	
	private int leftPositionTicks, rightPositionTicks, leftVelocityTicks, rightVelocityTicks, leftMaxVelocityTicks, rightMaxVelocityTicks = 0;
	
	public DriveTrain(DrivetrainControl driveControl, Data data)
	{
		this.driveControl = driveControl;
		this.data = data;
		leftMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_MASTER);
		leftFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_FOLLOW1);
    	leftFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_FOLLOW2);
    
		rightMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_MASTER);
		rightFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_FOLLOW1);
		rightFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_FOLLOW2);
		
		rightFollower.follow(rightMaster);
		rightFollower2.follow(rightMaster);
		leftFollower.follow(leftMaster);
		leftFollower2.follow(leftMaster);
		
		controlMode = ControlMode.PercentOutput;
		driveMode = DrivetrainMode.PercentOutput;
		
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		
		rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 100, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		rightMaster.setInverted(true);
		rightFollower.setInverted(true);
		rightFollower2.setInverted(true);
		
		leftMaster.setInverted(false);
		leftFollower.setInverted(false);
		leftFollower2.setInverted(false);
		
		rightMaster.setSensorPhase(true);
		leftMaster.setSensorPhase(true);
	}
	
	

	@Override
	public void initialize(double pNow) {
		setMode(new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		leftMaster.set(controlMode, 0);
		rightMaster.set(controlMode, 0);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
	}

	@Override
	public boolean update(double pNow) {
	  DrivetrainMessage driveMessage = driveControl.getDriveMessage();
	  DrivetrainProfilingMessage profilingMessage = driveControl.getProfilingMessage();
	  
	  setMode(driveMessage);
    
    switch(driveMode) {
    case Pathfinder:
      driveMessage = PathFollower.calculateOutputs(profilingMessage.leftFollower, profilingMessage.rightFollower, 
                                    leftPositionTicks, rightPositionTicks,
                                    IMU.clampDegrees(data.pigeon.get(EPigeon.YAW)), 
                                    profilingMessage.isBackwards);
      leftMaster.set(controlMode, driveMessage.leftOutput);
      rightMaster.set(controlMode, driveMessage.rightOutput);
      break;
    default:
      leftMaster.setNeutralMode(driveMessage.neutralMode);
      rightMaster.setNeutralMode(driveMessage.neutralMode);
      leftMaster.set(controlMode, driveMessage.leftOutput);
      rightMaster.set(controlMode, driveMessage.rightOutput);
      break;
    }
    
    leftMaxVelocityTicks = Math.max(leftMaxVelocityTicks, leftMaster.getSelectedSensorVelocity(0));
    rightMaxVelocityTicks = Math.max(rightMaxVelocityTicks, rightMaster.getSelectedSensorVelocity(0));
    
    SmartDashboard.putNumber("Highest Left Velocity", Utils.ticksToFPS(leftMaxVelocityTicks));
    SmartDashboard.putNumber("Highest Right Velocity", Utils.ticksToFPS(rightMaxVelocityTicks));
    SmartDashboard.putString("Drive Mode", driveMode.name());
    
		return false;
	}
	
	@Override
	public void shutdown(double pNow) {
		leftMaster.neutralOutput();
		rightMaster.neutralOutput();
	}
	
	public void setMode(DrivetrainMessage driveMessage)
	{
	  if(driveMessage.driveMode == driveMode && driveMessage.initMode != true) return;
	  this.driveMode = driveMessage.driveMode;
		switch(driveMode)
		{
		case PercentOutput:
		  controlMode = ControlMode.PercentOutput;
			break;
		case MotionMagic:
		  controlMode = ControlMode.MotionMagic;
			leftMaster.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
			leftMaster.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			leftMaster.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			leftMaster.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			leftMaster.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			
			leftMaster.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			leftMaster.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			
			leftMaster.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			
			rightMaster.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
			rightMaster.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			rightMaster.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			rightMaster.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			rightMaster.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			
			rightMaster.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			rightMaster.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			
			rightMaster.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
			break;
		case Pathfinder:
		  controlMode = ControlMode.PercentOutput;
		  break;
		default:
			break;
		}
		
		
		
	}
	@Override
	public void loop(double pNow) {
	  update(pNow);
	}
	
	public DrivetrainMode getDriveMode() {
	  return driveMode;
	}
	
	public ControlMode getControlMode() {
	  return controlMode;
	}

}
	
	



	



  

	