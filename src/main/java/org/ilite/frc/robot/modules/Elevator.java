package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {
	TalonSRX masterElevator, followerElevator;
	private double mPower;
	private boolean mAtBottom, mAtTop, topSpeedLimitTripped, bottomSpeedLimitTripped, direction; //up = true down = false
	private TalonTach talonTach;
	Solenoid solenoid;
	private ElevatorState elevatorState;
	ElevatorPosition elevatorPosition;
	private boolean gearState;
	private int tickPosition;

	public Elevator(Hardware pHardware) {
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		followerElevator.follow(masterElevator);
		solenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		direction = true;
		//beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		elevatorState = ElevatorState.STOP;
		elevatorPosition = ElevatorPosition.BOTTOM;
		gearState = false;
		masterElevator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		talonTach = pHardware.getTalonTach();
		
		masterElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
		masterElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		masterElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

		masterElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}


	public enum ElevatorState
	{
		NORMAL(1),
		DECELERATE_TOP(0.3),
		DECELERATE_BOTTOM(-0.2),
		HOLD(1.1),
		BOTTOM(-0.2),
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

	public enum ElevatorPosition
	{
		CLIMB(0),
		BOTTOM(0),
		SWITCH(0),
		SCALE(0);
		
		double inches;
		private ElevatorPosition(double inches)
		{
			this.inches = inches;
		}
	}
	
	public void setPosition(ElevatorPosition desiredPosition)
	{
		elevatorPosition = desiredPosition;
		setPosition(elevatorPosition.inches);
	}
	
	private void setPosition(double inches)
	{
		double currentTick = masterElevator.getSelectedSensorPosition(SystemSettings.MOTION_MAGIC_PID_SLOT);
		//double desiredTick = some regression to convert inches to ticks
		//masterElevator.set(ControlMode.MotionMagic, desiredTick);
	}
	@Override
	public void initialize(double pNow) {

	}

	@Override
	public boolean update(double pNow) {
//		mAtTop = topLimitSwitch.get();
//		mAtBottom = bottomLimitSwitch.get();
		direction = mPower > 0 ? true : false;
		tickPosition = masterElevator.getSelectedSensorPosition(0);
		if(!talonTach.getSensor())
		{
			elevatorState = ElevatorState.NORMAL;
		}
		if(!direction && tickPosition < (SystemSettings.ENCODER_MAX_TICKS / 2) && !talonTach.getSensor() )
		{
			elevatorState = ElevatorState.DECELERATE_BOTTOM;
		}

		if(direction && tickPosition > (SystemSettings.ENCODER_MAX_TICKS / 2) && !talonTach.getSensor())
		{
			elevatorState = ElevatorState.DECELERATE_TOP;
		}
		if((direction && tickPosition < (SystemSettings.ENCODER_MAX_TICKS / 2)) ||  (!direction && tickPosition > (SystemSettings.ENCODER_MAX_TICKS / 2)))
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
			zeroEncoder();
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

//		double power = ElevatorState.HOLD.power / 12 * masterElevator.getBusVoltage();
		
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
			
		case HOLD:
//			masterElevator.set(ControlMode., demand);

		case BOTTOM:
			 masterElevator.set(ControlMode.PercentOutput, Math.max(mPower, elevatorState.getPower()));
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
		mPower = power;
	}

	public void shiftGear(boolean gear)
	{
		solenoid.set(gear);
		gearState = gear;
	}

	public boolean getGearState()
	{
		return gearState;
	}
	
	public boolean isDown() {
		return mAtBottom;
	}
	
	public void zeroEncoder()
	{
		masterElevator.setSelectedSensorPosition(0, 0, 0);
	}

	@Override
	public void shutdown(double pNow) {

	}
	
	public void goToBottom()
	{
		elevatorState = ElevatorState.BOTTOM;
	}

	public double getHeightInches()
	{
		//convert current ticks to inches for DriveTrain
//		return tickPosition /
		return 0.0;
	}
	
	public boolean getDirection()
	{
		return direction;
	}
	
	public ElevatorPosition getElevatorPosition()
	{
		return elevatorPosition;
	}
	
	public ElevatorState getElevatorState()
	{
		return elevatorState;
	}
}

//		topSpeedLimitTripped = topTripSwitch.get();
//		bottomSpeedLimitTripped = bottomTripSwitch.get();