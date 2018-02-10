package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class ElevatorModule implements IModule {
	private TalonSRX masterElevator, followerElevator;
	private double mPower;
	private boolean mAtBottom, mAtTop, topSpeedLimitTripped, bottomSpeedLimitTripped, direction; //up = true down = false
	private DigitalInput bottomLimitSwitch, topLimitSwitch, topTripSwitch, bottomTripSwitch, beamBreak;
	private Solenoid solenoid;
	private ElevatorState elevatorState;

	public ElevatorModule() {
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		bottomLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_BOTTOM_ELEVATION_LIMIT_SWITCH);
		topLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_TOP_ELEVATION_LIMIT_SWITCH);
		followerElevator.follow(masterElevator);
		solenoid = new Solenoid(SystemSettings.SHIFT_SOLENOID);
		direction = true;
		beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		elevatorState = ElevatorState.STOP;

		masterElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
		masterElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		masterElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		masterElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

//		followerElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
//		followerElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		followerElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		followerElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		followerElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//
//		followerElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		followerElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//
//		followerElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

	}


	public enum ElevatorState
	{
		NORMAL(1),
		DECELERATE_TOP(0.3),
		DECELERATE_BOTTOM(-0.2),
		HOLD(1.1),
		STOP(0);

		double power;
		private ElevatorState(double power)
		{
			this.power = power;
		}

		private double getPower()
		{
			return power;
		}
	}


	public void setPosition(double ticks)
	{
		double currentTick = masterElevator.getSelectedSensorPosition(SystemSettings.MOTION_MAGIC_PID_SLOT);

		double targetTick = (currentTick < ticks) ? (currentTick - (ticks)) : -(currentTick - ticks);


		masterElevator.set(ControlMode.MotionMagic, targetTick);
//		followerElevator.set(ControlMode.MotionMagic, targetTick);
	}

	@Override
	public void initialize(double pNow) {

	}

	@Override
	public boolean update(double pNow) {
		//		if(bottomLimitSwitch.get() == topLimitSwitch.get())
		//		{
		//			mAtBottom = bottomLimitSwitch.get();
		//			mAtTop = topLimitSwitch.get();
		//			leftElevator.set(ControlMode.PercentOutput, mPower);
		//			rightElevator.set(ControlMode.PercentOutput, mPower);
		//			return true;
		//		}
		//		if ((bottomLimitSwitch.get())) {
		//			mAtBottom = true;
		//		} else {
		//			mAtBottom = false;
		//		}
		//		if ((topLimitSwitch.get())) {
		//			mAtTop = true;
		//		} else {
		//			mAtTop = false;
		//		}
		//		System.out.println("Bottom " + mAtBottom + " Top " + mAtTop);
		//		System.out.println("Power: " + mPower);
		//		leftElevator.set(ControlMode.PercentOutput, mPower);
		//		return true;

		topSpeedLimitTripped = topTripSwitch.get();
		bottomSpeedLimitTripped = bottomTripSwitch.get();
		mAtTop = topLimitSwitch.get();
		mAtBottom = bottomLimitSwitch.get();
		direction = mPower > 0 ? true : false;

		if(!direction && bottomSpeedLimitTripped)
		{
			elevatorState = ElevatorState.DECELERATE_BOTTOM;
		}

		if(direction && topSpeedLimitTripped)
		{
			elevatorState = ElevatorState.DECELERATE_TOP;
		}
		if((direction && bottomSpeedLimitTripped) ||  (!direction && topSpeedLimitTripped))
		{
			elevatorState = ElevatorState.NORMAL;
		}
		if(mAtBottom)
		{
			if(mPower > 0)
			{
				elevatorState = ElevatorState.NORMAL;
			}
			else
			{
				elevatorState = ElevatorState.STOP;
			}
		}
		if(mAtTop)
		{
			if(mPower < 0)
			{
				elevatorState = ElevatorState.NORMAL;
			}
			else {

				elevatorState = ElevatorState.STOP;
			}
		}

		double power = ElevatorState.HOLD.power / 12 * masterElevator.getBusVoltage();
		
		switch(elevatorState){

		case NORMAL: 
			masterElevator.set(ControlMode.PercentOutput, mPower);
			break;

		case DECELERATE_BOTTOM: 
			masterElevator.set(ControlMode.PercentOutput, Math.max(mPower, elevatorState.getPower()));
			break;

		case DECELERATE_TOP: 
			masterElevator.set(ControlMode.PercentOutput, Math.min(mPower, elevatorState.getPower()));
			break;

		case STOP: 
			masterElevator.set(ControlMode.PercentOutput, elevatorState.getPower());
			break;

		default: 
			masterElevator.set(ControlMode.PercentOutput, ElevatorState.STOP.getPower());
			break;
		}

		return true;
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


		mPower = power;

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
