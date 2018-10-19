package org.ilite.frc.robot.modules;

import edu.wpi.first.wpilibj.Talon;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.drivetrain.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
	private DrivetrainMode leftDriveMode, rightDriveMode;
	private ControlMode leftControlMode, rightControlMode;
	
	private int leftPositionTicks, rightPositionTicks, leftVelocityTicks, rightVelocityTicks, leftMaxVelocityTicks, rightMaxVelocityTicks = 0;
	
	public DriveTrain(Data data, Hardware hardware)
	{
		this.data = data;
		this.hardware = hardware;
		leftMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_MASTER);
		leftFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_FOLLOW1);
    	leftFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT_FOLLOW2);

    	// Temporary workaround for this robot
		rightMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_FOLLOW2);
		rightFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_FOLLOW1);
		rightFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT_MASTER);
		
		rightFollower.follow(rightMaster);
		rightFollower2.follow(rightMaster);
		leftFollower.follow(leftMaster);
		leftFollower2.follow(leftMaster);
		
		leftControlMode = ControlMode.PercentOutput;
		rightControlMode = ControlMode.PercentOutput;
		leftDriveMode = DrivetrainMode.PercentOutput;
		rightDriveMode = DrivetrainMode.PercentOutput;

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
		leftMaster.set(leftControlMode, 0);
		rightMaster.set(rightControlMode, 0);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
	}

	@Override
	public boolean update(double pNow) {
	  
	  setMode(currentDrivetrainMessage);

	  if(leftDriveMode.equals(DrivetrainMode.Pathfinder) && rightDriveMode.equals(DrivetrainMode.Pathfinder)) {
		  currentDrivetrainMessage = PathFollower.calculateOutputs(currentProfilingMessage.leftFollower, currentProfilingMessage.rightFollower,
				  leftPositionTicks, rightPositionTicks,
				  IMU.clampDegrees(hardware.getPigeon().getYaw()),
				  currentProfilingMessage.isBackwards);
	  }

	  leftMaster.setNeutralMode(currentDrivetrainMessage.leftNeutralMode);
	  rightMaster.setNeutralMode(currentDrivetrainMessage.rightNeutralMode);

	  leftMaster.set(leftControlMode, currentDrivetrainMessage.leftOutput);
	  rightMaster.set(rightControlMode, currentDrivetrainMessage.rightOutput);

	  leftMaxVelocityTicks = Math.max(leftMaxVelocityTicks, leftMaster.getSelectedSensorVelocity(0));
	  rightMaxVelocityTicks = Math.max(rightMaxVelocityTicks, rightMaster.getSelectedSensorVelocity(0));
    
	  return false;
	}
	
	@Override
	public void shutdown(double pNow) {
		leftMaster.neutralOutput();
		rightMaster.neutralOutput();
	}

	public void setMode(DrivetrainMessage driveMessage) {
		if(driveMessage.leftDriveMode != leftDriveMode || driveMessage.initMode == true) {
			this.leftDriveMode = driveMessage.leftDriveMode;
			leftControlMode = configForMode(leftMaster, leftDriveMode);
		}
		if(driveMessage.rightDriveMode != rightDriveMode || driveMessage.initMode == true) {
			this.rightDriveMode = driveMessage.rightDriveMode;
			rightControlMode = configForMode(rightMaster, rightDriveMode);
		}
	}

	private ControlMode configForMode(TalonSRX talon, DrivetrainMode mode) {
		ControlMode controlMode = ControlMode.PercentOutput;
		switch(mode) {
			case PercentOutput:
				controlMode = ControlMode.PercentOutput;
				break;
			case Position:
				controlMode = ControlMode.Position;
				configTalonForPosition(talon, SystemSettings.POSITION_PID_SLOT, SystemSettings.POSITION_TOLERANCE,
						SystemSettings.POSITION_P, SystemSettings.POSITION_I, SystemSettings.POSITION_D, SystemSettings.POSITION_F);
				break;
			case MotionMagic:
				controlMode = ControlMode.MotionMagic;
				configTalonForMotionMagic(talon, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT,
						SystemSettings.MOTION_MAGIC_P, SystemSettings.MOTION_MAGIC_I, SystemSettings.MOTION_MAGIC_D, SystemSettings.MOTION_MAGIC_F,
						SystemSettings.MOTION_MAGIC_V, SystemSettings.MOTION_MAGIC_A);
				break;
			case Pathfinder:
				controlMode = ControlMode.PercentOutput;
				break;
			default:
				break;
		}
		return controlMode;
	}

	private void configTalonForPosition(TalonSRX talon, int pidSlot, int errorTolerance, double p, double i, double d, double f) {
		talon.configAllowableClosedloopError(pidSlot, errorTolerance, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kP(pidSlot, p, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kI(pidSlot, i, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kD(pidSlot, d, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kF(pidSlot, f, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}
	
	private void configTalonForMotionMagic(TalonSRX talon, int pidSlot, int loopSlot, double p, double i, double d, double f, int v, int a) {
		talon.selectProfileSlot(pidSlot, loopSlot);
		talon.config_kP(pidSlot, p, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kI(pidSlot, i, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kD(pidSlot, d, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.config_kF(pidSlot, f, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		talon.configMotionCruiseVelocity(v, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		talon.configMotionAcceleration(a, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		talon.setSelectedSensorPosition(0, pidSlot, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}

	@Override
	public void loop(double pNow) {
	  update(pNow);
	}
	
	public synchronized void zeroOutputs() {
		setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
	}
	
	public synchronized void setDriveMessage(DrivetrainMessage drivetrainMessage) {
	  this.currentDrivetrainMessage = drivetrainMessage;
	}

	public synchronized void setLeftDriveMessage() {

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
	
	public DrivetrainMode getLeftDriveMode() {
	  return leftDriveMode;
	}

	public DrivetrainMode getRightDriveMode() {
		return rightDriveMode;
	}
	
	public ControlMode getLeftControlMode() {
	  return leftControlMode;
	}

	public ControlMode getRightControlMode() {
		return rightControlMode;
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

  public double getLeftInches() {
		return Utils.ticksToInches(leftMaster.getSelectedSensorPosition(0));
  }

  public double getRightInches() {
		return Utils.ticksToInches(rightMaster.getSelectedSensorPosition(0));
  }

  public double getLeftVelInches() {
		return Utils.ticksToInches(leftMaster.getSelectedSensorVelocity(0));
  }

  public double getRightVelInches() {
		return Utils.ticksToInches(rightMaster.getSelectedSensorVelocity(0));
  }

}
	
	



	



  

	