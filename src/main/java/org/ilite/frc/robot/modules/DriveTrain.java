package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
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

import edu.wpi.first.wpilibj.MotorSafety;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);
  private long lastUpdate = 0;
	private DrivetrainControl driveControl;

	private Data data;
	//private PDM g;
	
	final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	
	private Hardware hardware;
	private DrivetrainMessage drivetrainMessage;
	private DrivetrainProfilingMessage profilingMessage;
	private DrivetrainMode driveMode;
	private ControlMode controlMode; 
	
	private int leftPositionTicks, rightPositionTicks, leftVelocityTicks, rightVelocityTicks, leftMaxVelocityTicks, rightMaxVelocityTicks = 0;
	
	public DriveTrain(DrivetrainControl driveControl, Hardware hardware, Data data)
	{
	  this.hardware = hardware;
	  this.drivetrainMessage = new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake);
	  this.profilingMessage = new DrivetrainProfilingMessage(null, null, false);
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
		
		rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
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
//	  System.out.println(System.currentTimeMillis() - lastUpdate);
	  lastUpdate = System.currentTimeMillis();
	  setMode(drivetrainMessage);
    
    switch(driveMode) {
    case Pathfinder:
      drivetrainMessage = PathFollower.calculateOutputs(profilingMessage.leftFollower, profilingMessage.rightFollower, 
                                    leftMaster.getSelectedSensorPosition(0), rightMaster.getSelectedSensorPosition(0), 
                                    IMU.clampDegrees(hardware.getPigeon().getYaw()),
                                    profilingMessage.isBackwards);
      leftMaster.set(controlMode, drivetrainMessage.leftOutput);
      rightMaster.set(controlMode, drivetrainMessage.rightOutput);
      break;
    default:
      leftMaster.setNeutralMode(drivetrainMessage.neutralMode);
      rightMaster.setNeutralMode(drivetrainMessage.neutralMode);
      leftMaster.set(controlMode, drivetrainMessage.leftOutput);
      rightMaster.set(controlMode, drivetrainMessage.rightOutput);
      break;
    }
    
//    SmartDashboard.putNumber("Highest Left Velocity", Utils.ticksToFPS(leftMaxVelocityTicks));
//    SmartDashboard.putNumber("Highest Right Velocity", Utils.ticksToFPS(rightMaxVelocityTicks));
//    SmartDashboard.putString("Drive Mode", driveMode.name());
    
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
	
	public TalonSRX getLeftMaster() {
	  return leftMaster;
	}
	
	public TalonSRX getRightMaster() {
    return rightMaster;
  }
	
	public synchronized void setDrivetrainMessage(DrivetrainMessage pDrivetrainMessage) {
	  this.drivetrainMessage = pDrivetrainMessage;
	}
	
	public synchronized void setProfilingMessage(DrivetrainProfilingMessage pProfilingMessage) {
	  this.profilingMessage = pProfilingMessage;
	}

}
	
	



	



  

	