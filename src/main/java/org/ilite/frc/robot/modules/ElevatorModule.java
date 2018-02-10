package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class ElevatorModule implements IModule {
	private TalonSRX leftElevator, rightElevator;
	private double mPower;
	private boolean mAtBottom;
	private boolean mAtTop;
	private boolean topTripped;
	private boolean bottomTripped;
	private boolean direction; //up = true down = false
	private DigitalInput bottomLimitSwitch;
	private DigitalInput topLimitSwitch;
	private DigitalInput topTripSwitch;
	private DigitalInput bottomTripSwitch;
	private Solenoid solenoid;
	private DigitalInput beamBreak;

	public ElevatorModule() {
		leftElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		rightElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		bottomLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_BOTTOM_ELEVATION_LIMIT_SWITCH);
		topLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_TOP_ELEVATION_LIMIT_SWITCH);
		rightElevator.follow(leftElevator);
		solenoid = new Solenoid(SystemSettings.SHIFT_SOLENOID);
		direction = true;
		beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		
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

	
	public enum ElevatorState
	{
		
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
		if(bottomLimitSwitch.get() == topLimitSwitch.get())
		{
			mAtBottom = bottomLimitSwitch.get();
			mAtTop = topLimitSwitch.get();
			leftElevator.set(ControlMode.PercentOutput, mPower);
			rightElevator.set(ControlMode.PercentOutput, mPower);
			return true;
		}
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
		System.out.println("Bottom " + mAtBottom + " Top " + mAtTop);
		System.out.println("Power: " + mPower);
		leftElevator.set(ControlMode.PercentOutput, mPower);
		rightElevator.set(ControlMode.PercentOutput, mPower);
		return true;
		
//		topTripped = topTripSwitch.get();
//		bottomTripped = bottomTripSwitch.get();
//		mAtTop = topLimitSwitch.get();
//		mAtBottom = bottomLimitSwitch.get();
		
//		leftElevator.set(ControlMode.PercentOutput, mPower);
//		rightElevator.set(ControlMode.PercentOutput, mPower);
//	
//		return true;
	}

	public void setPower(double power) {
//		if(mAtBottom == mAtTop)
//		{
//			mPower = power;
//			return;
//		}
//		if(mAtBottom)
//			if(power < 0)
//				mPower = 0;
//			else
//				mPower = power;
//		else if(mAtTop)
//			if(power > 0)
//				mPower = 0;
//			else
//				mPower = power;
//		else
//			mPower = power;
		
		direction = power > 0 ? true : false;
		if(!mAtTop && !mAtBottom && !bottomTripped && !topTripped)
		{
			mPower = power;
		}
		if(mAtBottom)
		{
			if(!direction)
			{
				mPower = 0;
			}
			else
				mPower = power;
		}
		
		if(mAtTop)
		{
			if(direction)
			{
				mPower = 0;
			}
			else 
				mPower = power;
		}
		if(!mAtBottom && bottomTripped)
		{
			if(!direction)
			{
				mPower = Math.min(power, SystemSettings.ELEV_BOTTOM_SPEED_LIMIT);
			}
			else mPower = power;
		}
		if(!mAtTop && topTripped)
		{
			if(direction)
			{
				mPower = Math.min(power, SystemSettings.ELEV_TOP_SPEED_LIMIT);
			}
			else mPower = power;
			
		}
		
	}

	public void shiftGear(boolean gear)
	{
		solenoid.set(gear);
	}
	
	public boolean isDown() {
		return mAtBottom;
	}

	@Override
	public void shutdown(double pNow) {

	}

}
