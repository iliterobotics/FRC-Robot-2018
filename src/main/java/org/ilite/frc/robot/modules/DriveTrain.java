package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainProfilingMessage;
import org.ilite.frc.robot.modules.drivetrain.PathFollower;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);

	private Data data;
	private Hardware hardware;
	//private PDM g;
	
	final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	
	private DrivetrainMessage currentDrivetrainMessage;
	private DrivetrainProfilingMessage currentProfilingMessage;
	private DrivetrainMode driveMode;
	private ControlMode controlMode; 
	
	private int leftPositionTicks, rightPositionTicks, leftVelocityTicks, rightVelocityTicks, leftMaxVelocityTicks, rightMaxVelocityTicks = 0;
	
	public DriveTrain(Data data, Hardware hardware)
	{
		this.data = data;
		this.hardware = hardware;
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
		
		currentDrivetrainMessage = new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake);
		currentProfilingMessage = new DrivetrainProfilingMessage(null, null, false);
		
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
    leftMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		
		rightMaster.configOpenloopRamp(0.1, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftMaster.configOpenloopRamp(0.1, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		rightMaster.configContinuousCurrentLimit(40, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
    leftMaster.configContinuousCurrentLimit(40, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
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
	  setDriveMessage(new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		setMode(new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		leftMaster.set(controlMode, 0);
		rightMaster.set(controlMode, 0);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
	}

	@Override
	public boolean update(double pNow) {
	  
	  setMode(currentDrivetrainMessage);
    
    switch(driveMode) {
    case Pathfinder:
      currentDrivetrainMessage = PathFollower.calculateOutputs(currentProfilingMessage.leftFollower, currentProfilingMessage.rightFollower, 
                                    leftPositionTicks, rightPositionTicks,
                                    IMU.clampDegrees(hardware.getPigeon().getYaw()), 
                                    currentProfilingMessage.isBackwards);
      leftMaster.set(controlMode, currentDrivetrainMessage.leftOutput);
      rightMaster.set(controlMode, currentDrivetrainMessage.rightOutput);
      break;
    default:
      leftMaster.setNeutralMode(currentDrivetrainMessage.neutralMode);
      rightMaster.setNeutralMode(currentDrivetrainMessage.neutralMode);
      leftMaster.set(controlMode, currentDrivetrainMessage.leftOutput);
      rightMaster.set(controlMode, currentDrivetrainMessage.rightOutput);
      break;
    }
    
    leftMaxVelocityTicks = Math.max(leftMaxVelocityTicks, leftMaster.getSelectedSensorVelocity(0));
    rightMaxVelocityTicks = Math.max(rightMaxVelocityTicks, rightMaster.getSelectedSensorVelocity(0));
    
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
		case Position:
		  controlMode = ControlMode.Position;
		  leftMaster.configAllowableClosedloopError(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_TOLERANCE, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		  leftMaster.config_kP(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		  leftMaster.config_kI(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		  leftMaster.config_kD(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		  leftMaster.config_kF(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		  
		  rightMaster.configAllowableClosedloopError(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_TOLERANCE, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
      rightMaster.config_kP(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
      rightMaster.config_kI(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
      rightMaster.config_kD(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
      rightMaster.config_kF(SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
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
	
	public synchronized void holdPosition() {
	  setDriveMessage(new DrivetrainMessage(getLeftMaster().getSelectedSensorPosition(0), getRightMaster().getSelectedSensorPosition(0),
	                  DrivetrainMode.Position, NeutralMode.Brake));
	}
	
	public synchronized void setDriveMessage(DrivetrainMessage drivetrainMessage) {
	  this.currentDrivetrainMessage = drivetrainMessage;
	}
	
	public synchronized void setProfilingMessage(DrivetrainProfilingMessage profilingMessage) {
	  this.currentProfilingMessage = profilingMessage;
	}
	
	public DrivetrainMessage getDriveMessage() {
	  return currentDrivetrainMessage;
	}
	
	public DrivetrainProfilingMessage getProfilingMessage() {
	  return currentProfilingMessage;
	}
	
	public DrivetrainMode getDriveMode() {
	  return driveMode;
	}
	
	public ControlMode getControlMode() {
	  return controlMode;
	}
	
	public TalonSRX getLeftMaster() {
	  return leftMaster;
	}
	
	public TalonSRX getLeftFollower1() {
	  return leftFollower;
	}
	
	public TalonSRX getLeftFollower2() {
    return leftFollower2;
  }
	
	public TalonSRX getRightMaster() {
    return rightMaster;
  }
  
  public TalonSRX getRightFollower1() {
    return rightFollower;
  }
  
  public TalonSRX getRightFollower2() {
    return rightFollower2;
  }

}
	
	



	



  

	