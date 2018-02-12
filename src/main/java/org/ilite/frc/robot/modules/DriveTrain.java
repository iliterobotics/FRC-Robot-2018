package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);

	private DriverControl driverControl;
	//private PDM g;
	
	private final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	private ControlMode controlMode;
	private double desiredLeft, desiredRight;
	private double maxVelocity;
	
	public DriveTrain(DriverControl driverControl)
	{
		this.driverControl = driverControl;
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
		
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(true);
		
		this.maxVelocity = 0;

		}
	
	

	@Override
	public void initialize(double pNow) {
		leftMaster.set(controlMode, desiredLeft);
		rightMaster.set(controlMode, desiredRight);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
		
	}

	@Override
	public boolean update(double pNow) {
	  ControlMode newMode = driverControl.getDesiredControlMode();
    if(newMode != controlMode) {
      controlMode = newMode;
      initMode(controlMode);
    }
    leftMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
    rightMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
    leftMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredLeftOutput());
    rightMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredRightOutput());
		
		maxVelocity = Math.max(maxVelocity, (getLeftVelocityFeetPerSecond() + getRightVelocityFeetPerSecond()) / 2);
		
		SmartDashboard.putNumber("Highest Velocity", maxVelocity);
		SmartDashboard.putNumber("Left Velocity (Ticks)", getLeftVelocity());
		SmartDashboard.putNumber("Right Velocity (Ticks)", getRightVelocity());
		SmartDashboard.putNumber("Left Velocity (FPS)", getLeftVelocityFeetPerSecond());
		SmartDashboard.putNumber("Right Velocity (FPS)", getRightVelocityFeetPerSecond());
		SmartDashboard.putNumber("Left Position (Ticks)", getLeftPosition());
		SmartDashboard.putNumber("Right Position (Ticks)", getRightPosition());
		SmartDashboard.putNumber("Left Position (Inches)", getLeftPositionInches());
    SmartDashboard.putNumber("Right Position (Unches)", getRightPositionInches());
		
		return false;
	}
	
	public void set(double l, double r)
	{
		desiredLeft = l;
		desiredRight = r;
	}
	
	@Override
	public void shutdown(double pNow) {
		leftMaster.neutralOutput();
		rightMaster.neutralOutput();
	}
	
	public void initMode(ControlMode controlMode)
	{
		switch(controlMode)
		{
		case Velocity:
			break;
		case PercentOutput:
			desiredLeft = 0;
			desiredRight = 0;
			break;
		case Current:	
			break;
		case Disabled:
			break;
		case Follower:
			break;
		case MotionMagic:
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
		case MotionMagicArc:
			break;
		case MotionProfile:
			break;
		case MotionProfileArc:
			break;
		case Position:
			break;
		default:
			break;
		}
	}
	@Override
	public void loop(double pNow) {
	  ControlMode newMode = driverControl.getDesiredControlMode();
	  if(newMode != controlMode) {
	    controlMode = newMode;
	    initMode(controlMode);
	  }
		leftMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		rightMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		leftMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredLeftOutput());
		rightMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredRightOutput());
	}
	
	public int getLeftVelocity()
	{
		return leftMaster.getSelectedSensorVelocity(0);
	}
	
	public int getRightVelocity()
	{
		return rightMaster.getSelectedSensorVelocity(0);
	}
	
	public double getLeftVelocityFeetPerSecond() {
	  return (double)(getLeftVelocity() * (1.0 / SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN) * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * (1.0 / 12.0) * 10.0);
	}
	
	public double getRightVelocityFeetPerSecond() {
    return (double)(getRightVelocity() * (1.0 / SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN) * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * (1.0 / 12.0) * 10.0);
  }
	
	public int getLeftPosition()
	{
		return leftMaster.getSelectedSensorPosition(0);
	}
	
	public int getRightPosition()
	{
		return rightMaster.getSelectedSensorPosition(0);
	}
	
	public double getLeftPositionInches() {
	  return (getLeftPosition() / SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN) * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE;
	}
	
	public double getRightPositionInches() {
    return getRightPosition() / SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE;
  }
	
}
	
	



	



  

	