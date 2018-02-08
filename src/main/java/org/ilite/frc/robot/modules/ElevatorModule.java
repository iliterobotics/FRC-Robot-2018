package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorModule implements IModule {
	private TalonSRX leftElevator, rightElevator;
	private double mPower;
	private boolean mAtBottom;
	private boolean mAtTop;
	private DigitalInput bottomLimitSwitch;
	private DigitalInput topLimitSwitch;

	public ElevatorModule() {
		leftElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		rightElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		bottomLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_BOTTOM_ELEVATION_LIMIT_SWITCH);
		topLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_TOP_ELEVATION_LIMIT_SWITCH);
		rightElevator.follow(leftElevator);

		leftElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
		leftElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		leftElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		leftElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		leftElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		rightElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
		rightElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		rightElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		rightElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		rightElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		rightElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		rightElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		rightElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

	}

	
	public void setPosition(double ticks)
	{
		double currentTick = leftElevator.getSelectedSensorPosition(SystemSettings.MOTION_MAGIC_PID_SLOT);

		double targetTick = (currentTick < ticks) ? (currentTick - (ticks)) : -(currentTick - ticks);
		
		
		leftElevator.set(ControlMode.MotionMagic, targetTick);
		rightElevator.set(ControlMode.MotionMagic, targetTick);
	}
	
	@Override
	public void initialize(double pNow) {
		
	}

	@Override
	public boolean update(double pNow) {
		if ((bottomLimitSwitch.get())) {
			mAtBottom = true;
		} else {
			mAtBottom = false;
		}
		if ((topLimitSwitch.get())) {
			mAtTop = true;
		} else {
			mAtTop = false;
		}
		
		leftElevator.set(ControlMode.PercentOutput, mPower);
		rightElevator.set(ControlMode.PercentOutput, mPower);
		return true;
	}

	public void setPower(double power) {
		if(mAtBottom)
			if(power < 0)
				mPower = 0;
			else
				mPower = power;
		else if(mAtTop)
			if(power > 0)
				mPower = 0;
			else
				mPower = power;
		else
			mPower = power;
		
	}

	public boolean isDown() {
		return mAtBottom;
	}

	@Override
	public void shutdown(double pNow) {

	}

}
