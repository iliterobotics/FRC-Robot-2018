package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {
	TalonSRX masterElevator, followerElevator;
	private final Hardware mHardware;
	private TalonTach talonTach;
	private boolean lastTachState, currentTachState;
	private int currentTachLevel;
	Solenoid shiftSolenoid;
	private double mDesiredPower;
	private boolean mAtBottom, mAtTop, direction; //up = true down = false
	private ElevatorState elevatorState;
	private ElevatorPosition elevatorPosition;
	private boolean gearState;
	private int tickPosition;

	public Elevator(Hardware pHardware) {
	  mHardware = pHardware;
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		followerElevator.follow(masterElevator);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		direction = true;
		//beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		elevatorState = ElevatorState.STOP;
		elevatorPosition = ElevatorPosition.BOTTOM;
		gearState = false;
		
		masterElevator.configContinuousCurrentLimit(20, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.enableCurrentLimit(true);
//		masterElevator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		
//		masterElevator.selectProfileSlot(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_LOOP_SLOT);
//		masterElevator.config_kP(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.config_kI(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.config_kD(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.config_kF(SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//
//		masterElevator.configMotionCruiseVelocity(SystemSettings.MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.configMotionAcceleration(SystemSettings.MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//
//		masterElevator.setSelectedSensorPosition(0, SystemSettings.MOTION_MAGIC_PID_SLOT, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		// TODO set voltage ramp & current limit
	}


	public enum ElevatorState
	{
		NORMAL(0.3),
		DECELERATE_TOP(0.03),
		DECELERATE_BOTTOM(-0.03),
		HOLD(1.1),
		BOTTOM(0),
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
		FIRST_TAPE(0.7, 1),
		SECOND_TAPE(0.7, 2),
		THIRD_TAPE(0.7, 3),
		SCALE(0);
		
		double inches;
		double power;
		int tapeMark;
		private ElevatorPosition(double power, int tapeMark)
		{
		  this.power = power;
		  this.tapeMark = tapeMark;
		}
		private ElevatorPosition(double inches)
		{
			this.inches = inches;
		}
		
		private boolean isTapeMarker()
		{
		  return power != 0;
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
    talonTach = mHardware.getTalonTach();
	  masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	  tickPosition = 0;
	  gearState = shiftSolenoid.get();
	  elevatorState = ElevatorState.STOP;
	  elevatorPosition = ElevatorPosition.BOTTOM;
	  mAtBottom = true;
	  mAtTop = false;
	  direction = true;
	  currentTachLevel = 0;
	  currentTachState = talonTach.getSensor();
	  lastTachState = currentTachState;
	}

	@Override
	public boolean update(double pNow) {
	  currentTachState = talonTach.getSensor();
		direction = mDesiredPower > 0 ? true : false;
		tickPosition = masterElevator.getSelectedSensorPosition(0);
		System.out.println(tickPosition + " ELEVATOR ENCODER TICKS");
		System.out.println((masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage()) + " CURRENT");
		mAtTop = isCurrentLimitTripped() && tickPosition > SystemSettings.ENCODER_MAX_TICKS / 2;
		mAtBottom = isCurrentLimitTripped() && tickPosition < SystemSettings.ENCODER_MAX_TICKS / 2;
		

    boolean shouldstop = mAtBottom || mAtTop || isCurrentLimitTripped();
		
		if(lastTachState == true && currentTachState == false)
		{
		  if(direction)
		  {
		    currentTachLevel += (int)Math.ceil(mDesiredPower);
		  }
		  if(!direction)
		  {
		    currentTachLevel += (int)Math.floor(mDesiredPower);
		  }		  
		}
		
		if(elevatorPosition.isTapeMarker())
		{
		  if(currentTachLevel != elevatorPosition.tapeMark)
		  {
		    mDesiredPower = elevatorPosition.power;
		    elevatorState = ElevatorState.NORMAL;
		  }
		  else
		  {
		    elevatorState = ElevatorState.STOP;
		  }
		}
		else {
		  //bottom
		  if(!direction && tickPosition < (SystemSettings.ENCODER_MAX_TICKS / 2) && currentTachLevel == 1 )
		  {
		    elevatorState = ElevatorState.DECELERATE_BOTTOM;
		  }
		  //top
		  if(direction && tickPosition > (SystemSettings.ENCODER_MAX_TICKS / 2) && currentTachLevel == 3)
		  {
		    elevatorState = ElevatorState.DECELERATE_TOP;
		  }
		  if((direction && tickPosition < (SystemSettings.ENCODER_MAX_TICKS / 2)) ||  (!direction && tickPosition > (SystemSettings.ENCODER_MAX_TICKS / 2)))
		  {
		    elevatorState = ElevatorState.NORMAL;
		  }
		  if(mAtBottom)
		  {
		    if(mDesiredPower > 0)
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
		    if(mDesiredPower < 0)
		    {
		      elevatorState = ElevatorState.NORMAL;
		    }
		    else {

		      elevatorState = ElevatorState.STOP;
		    }
		  }
		}
//		if(shouldstop) {
//		  elevatorState = ElevatorState.STOP;
//		}

//		double power = ElevatorState.HOLD.power / 12 * masterElevator.getBusVoltage();
		
		double actualPower = 0;
		
		switch(elevatorState){

		case NORMAL: 
		  actualPower = mDesiredPower;
			break;

		case DECELERATE_BOTTOM: 
		  actualPower = Math.max(mDesiredPower, elevatorState.getPower());
//			masterElevator.set(ControlMode.PercentOutput, Math.max(mDesiredPower, elevatorState.getPower()));
			break;

		case DECELERATE_TOP: 
      actualPower = Math.min(mDesiredPower, elevatorState.getPower());
//			masterElevator.set(ControlMode.PercentOutput, Math.min(mDesiredPower, elevatorState.getPower()));
			break;
			
		case HOLD:
//		  actualPower = elevatorState.power / masterElevator.getBusVoltage()

		case BOTTOM:
		  actualPower = Math.max(mDesiredPower, elevatorState.getPower());
//			 masterElevator.set(ControlMode.PercentOutput, Math.max(mDesiredPower, elevatorState.getPower()));
			 break;
			 
		case STOP: 
		  actualPower = elevatorState.getPower();
//			masterElevator.set(ControlMode.PercentOutput, elevatorState.getPower());
			break;

		default: 
			actualPower = ElevatorState.STOP.getPower();
			break;
		}
//    System.out.println("elevatorState=" + elevatorState + " dPow=" + mDesiredPower + " aPow=" + actualPower + " dir=" + direction + " stop=" + shouldstop + " talonTach=" + currentTachLevel);
    
    masterElevator.set(ControlMode.PercentOutput, Utils.clamp(actualPower, 0.3d));
		//System.out.println(mDesiredPower + "POST CHECK");
		lastTachState = currentTachState;
		return true;
	}
	
	public void setPower(double power) {
		mDesiredPower = power;
	}

	public void shiftGear(boolean gear)
	{
		shiftSolenoid.set(gear);
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
	
	private boolean isCurrentLimitTripped() {
	  return masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage() >= 2d;
	}
}

