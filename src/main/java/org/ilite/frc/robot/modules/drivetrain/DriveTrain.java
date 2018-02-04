package org.ilite.frc.robot.modules.drivetrain;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.TalonFactory;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.MotorSafety;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);

	private DriveControl driveControl;
	private Data data;
	//private PDM g;
	
	private final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	
	private DriveMode driveMode;
	private ControlMode controlMode; 
	
	private int leftPositionTicks, rightPositionTicks, leftVelocityTicks, rightVelocityTicks;
	
	public DriveTrain(DriveControl driveControl, Data data)
	{
		this.driveControl = driveControl;
		this.data = data;
		leftMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT1);
    leftFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT2);
    leftFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT3);
    
		rightMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT1);
		rightFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT2);
		rightFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT3);
		
		rightFollower.follow(rightMaster);
		rightFollower2.follow(rightMaster);
    leftFollower.follow(leftMaster);
		leftFollower2.follow(leftMaster);
		
		controlMode = ControlMode.PercentOutput;
		driveMode = DriveMode.PercentOutput;
		
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(true);
	}
	
	

	@Override
	public void initialize(double pNow) {
	  updateDriveMode(driveMode);
		leftMaster.set(controlMode, 0);
		rightMaster.set(controlMode, 0);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
		
	}

	@Override
	public boolean update(double pNow) {
	  DriveMessage driveMessage = driveControl.getDriveMessage();
	  ProfilingMessage profilingMessage = driveControl.getProfilingMessage();
	  
	  setMode(driveMessage.driveMode);
    
    switch(driveMode) {
    case Pathfinder:
      driveMessage = PathFollower.calculateOutputs(profilingMessage.leftFollower, profilingMessage.rightFollower, 
                                    leftPositionTicks, rightPositionTicks,
                                    data.pigeon.get(EPigeon.YAW), 
                                    profilingMessage.isBackwards);
      updateDriveMode(driveMessage.driveMode);
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
		
		return false;
	}
	
	private void updateDriveMode(DriveMode newMode) {
	  if(newMode != driveMode) {
	    driveMode = newMode;
	    setMode(driveMode);
	  }
	}
	
	@Override
	public void shutdown(double pNow) {
		leftMaster.neutralOutput();
		rightMaster.neutralOutput();
	}
	
	public void setMode(DriveMode driveMode)
	{
	  if(this.driveMode == driveMode) return;
	  this.driveMode = driveMode;
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
	
	public TalonSRX getLeftMaster() {
	  return leftMaster;
	}
	
	public TalonSRX getRightMaster() {
	  return rightMaster;
	}
	
	public DriveMode getDriveMode() {
	  return driveMode;
	}
	
	public ControlMode getControlMode() {
	  return controlMode;
	}
	
}
	
	



	



  

	