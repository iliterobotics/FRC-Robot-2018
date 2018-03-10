package org.ilite.frc.robot.modules;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import org.ilite.frc.common.config.DriveTeamInputMap;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {
  private Data mData;
	TalonSRX masterElevator, followerElevator;
	private final Hardware mHardware;
	private TalonTach talonTach;
	private boolean lastTachState, currentTachState;
	private int currentTachLevel, currentEncoderTicks;
	Solenoid shiftSolenoid;
	private double mDesiredPower;
	private boolean mAtBottom, mAtTop, isDirectionUp;
	private ElevatorState elevatorState;
	private ElevatorPosition elevatorPosition;
	private ElevatorGearState elevGearState;
	private ElevatorControlMode elevControlMode;
	private ElevDirection elevatorDirection;
  private static final ILog log = Logger.createLog(Elevator.class);
  public static final double TOP_LIMIT = 30d/12d, BOTTOM_LIMIT = 10d/12d;


  public Elevator(Hardware pHardware, Data pData) {
		mHardware = pHardware;
		mData = pData;
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		followerElevator.follow(masterElevator);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		isDirectionUp = true;
		//beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		elevatorState = ElevatorState.STOP;
		elevatorPosition = ElevatorPosition.BOTTOM;
		elevGearState = ElevatorGearState.NORMAL;


		elevControlMode = ElevatorControlMode.MANUAL;

		masterElevator.configContinuousCurrentLimit(20, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.enableCurrentLimit(true);
		masterElevator.configOpenloopRamp(0.5, 0);
		// TODO set voltage ramp & current limit
	}


	public enum ElevatorState
	{
		NORMAL(0.3),
		DECELERATE_TOP(0.2),
		DECELERATE_BOTTOM(-0.15),
		HOLD(1.1),
		STOP(0);

		double power;
		ElevatorState(double power)
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
		BOTTOM(0, 0),
		FIRST_TAPE(1, 1),
		SECOND_TAPE(1, 2),
		THIRD_TAPE(1, 3);

		double inches;
		double power;
		int tapeMark;
		ElevatorPosition(double power, int tapeMark)
		{
			this.power = power;
			this.tapeMark = tapeMark;
		}
		private boolean isTapeMarker()
		{
			return power != 0;
		}
	}

	public enum ElevDirection
	{
		UP(true, 30d/12d, 3, 20),
		DOWN(false, 11d/12d, 0, 10);

		boolean isPositiveDirection;
		double mCurrentLimitRatio;
		int tapeMark;
		int currentLimit;

		ElevDirection(boolean isPositiveDirection, double pCurrentLimitRatio, int tapeMark, int currentLimit)
		{
			this.isPositiveDirection = isPositiveDirection;
			mCurrentLimitRatio = pCurrentLimitRatio;
			this.tapeMark = tapeMark;
			this.currentLimit = currentLimit;
		}

		public static ElevDirection getDirection(double pDesiredPower)
		{
			return pDesiredPower > 0 ? UP : DOWN;
		}

		public boolean isCurrentLimited(TalonSRX pMasterTalon)
		{
		  if(pMasterTalon.getMotorOutputVoltage() != 0)
		  {
	      return pMasterTalon.getOutputCurrent() / pMasterTalon.getMotorOutputVoltage() >= mCurrentLimitRatio;
		  }
		  else return false;
		}

		public int getCurrentLimit()
		{
		  return currentLimit;
		}
		public boolean isDecelerated(int currentTapeMark)
		{
			return currentTapeMark == tapeMark;
		}
		
	}

	public enum ElevatorControlMode
	{
		MANUAL,
		CLIMBER,
		POSITION;
	}

	public enum ElevatorGearState
	{
		NORMAL(true, 3),
		CLIMBING(false, 1.1);

		boolean gearState;
		double holdVoltage;
		ElevatorGearState(boolean gearState, double holdVoltage)
    {
      this.gearState = gearState;
      this.holdVoltage = holdVoltage;
    }
  }

	public void setPosition(ElevatorPosition desiredPosition)
	{
		elevatorPosition = desiredPosition;
	}

	//obsolete
//	private void setPosition(double inches)
//	{
//		double currentTick = masterElevator.getSelectedSensorPosition(SystemSettings.MOTION_MAGIC_PID_SLOT);
//		double desiredTick = some regression to convert inches to ticks
//		masterElevator.set(ControlMode.MotionMagic, desiredTick);
//	}

	@Override
	public void initialize(double pNow) {
		talonTach = mHardware.getTalonTach();
		masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.setNeutralMode(NeutralMode.Brake);
		setGearState(ElevatorGearState.NORMAL);
		elevatorState = ElevatorState.STOP;
		elevatorPosition = ElevatorPosition.BOTTOM;
		mAtBottom = true;
		mAtTop = false;
		isDirectionUp = true;
		currentTachLevel = 0;
		currentTachState = talonTach.getSensor();
		lastTachState = currentTachState;
	}

	@Override
	public boolean update(double pNow) {
		currentTachState = talonTach.getSensor();
		isDirectionUp = mDesiredPower > 0 ? true : false;
		mAtTop = isTopCurrentTripped() && currentTachLevel == 3;
		mAtBottom = isBottomCurrentTripped() && currentTachLevel == 0;

		elevatorDirection = ElevDirection.getDirection(mDesiredPower);
		boolean isCurrentLimited = elevatorDirection.isCurrentLimited(masterElevator);

		currentEncoderTicks = masterElevator.getSelectedSensorPosition(0);

    masterElevator.configContinuousCurrentLimit(elevatorDirection.getCurrentLimit(), SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.enableCurrentLimit(true);
    currentTachLevel = getTachLevel(currentTachState, lastTachState);
		switch(elevControlMode) {

      case POSITION:

        if (currentTachLevel != elevatorPosition.tapeMark) {
          mDesiredPower = currentTachLevel > elevatorPosition.tapeMark ? -elevatorPosition.power : elevatorPosition.power;
          elevatorState = ElevatorState.NORMAL;
        }
        else
        {
					elevatorState = ElevatorState.HOLD;
        }
        log.debug("TAPE MARKER " + elevatorPosition);
        break;

        
      case CLIMBER:
        if(currentTachLevel != 2)
        {
          mDesiredPower = -ElevatorState.NORMAL.power;
          elevatorState = ElevatorState.NORMAL;
        }
        else
        {
         elevatorState = ElevatorState.HOLD;
        }
         
        break;
        
      case MANUAL:
      	switch(elevatorDirection)
				{
					case UP:
						if(elevatorDirection.isCurrentLimited(masterElevator))
						{
							elevatorState = ElevatorState.STOP;
						}
						else if(elevatorDirection.isDecelerated(currentTachLevel))
						{
							elevatorState = ElevatorState.DECELERATE_TOP;
						}
						
						if(mDesiredPower == 0 && !mAtBottom)
						{
						  elevatorState = ElevatorState.HOLD;
						}
						else
						{
						  elevatorState = ElevatorState.NORMAL;
						}
						break;
					case DOWN:
						if(elevatorDirection.isCurrentLimited(masterElevator))
						{
							elevatorState = ElevatorState.STOP;
						}
						else if(elevatorDirection.isDecelerated(currentTachLevel))
						{
							elevatorState = ElevatorState.DECELERATE_BOTTOM;
						}
						if(mDesiredPower == 0 && !mAtBottom)
            {
              elevatorState = ElevatorState.HOLD;
            }
						else
            {
              elevatorState = ElevatorState.NORMAL;
            }
				}
        //bottom
//        if (!isDirectionUp && currentTachLevel == 0) {
//          elevatorState = ElevatorState.DECELERATE_BOTTOM;
//        }
//        //top
//        if (isDirectionUp && currentTachLevel == 3) {
//          elevatorState = ElevatorState.DECELERATE_TOP;
//        }
//        if ((isDirectionUp && currentTachLevel == 0) || (!isDirectionUp && currentTachLevel == 3)) {
//          elevatorState = ElevatorState.NORMAL;
//        }
//        if (mAtBottom) {
//          if (mDesiredPower > 0)
//          {
//            elevatorState = ElevatorState.NORMAL;
//          }
//          else {
//            elevatorState = ElevatorState.STOP;
//          }
//        }
//        if (mAtTop)
//        {
//          if (mDesiredPower < 0)
//          {
//            elevatorState = ElevatorState.NORMAL;
//          }
//          else {
//
//            elevatorState = ElevatorState.STOP;
//          }
//        }
        break;
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
		    actualPower = elevatorState.power / masterElevator.getBusVoltage();
				break;

			case STOP:
				actualPower = elevatorState.getPower();
//			masterElevator.set(ControlMode.PercentOutput, elevatorState.getPower());
				break;

			default:
				actualPower = ElevatorState.STOP.getPower();
				break;
		}

		// TODO
		// Create Elevator Codex
		// Replace system outs with Log
		log.debug(elevatorState + " dPow=" + mDesiredPower + " aPow=" + actualPower + " dir=" + isDirectionUp +  "talonTach=" + currentTachLevel);
    masterElevator.set(ControlMode.PercentOutput, Utils.clamp(actualPower, 0.3d));
//		masterElevator.set(ControlMode.PercentOutput, actualPower);
		//System.out.println(mDesiredPower + "POST CHECK");
		lastTachState = currentTachState;
		shiftSolenoid.set(elevGearState.gearState);
		return true;
	}

	/*
	  Changes the tach level by taking the currentTachState and lastTachState every cycle.
	  Increases by one when first encounters non-reflective material

	  true = reflective material (powdercoat) false = non-reflective (tape)
	 */
  private int getTachLevel(boolean currentTachState, boolean lastTachState)
  {
    if(lastTachState == true && currentTachState == false)
    {
      if(isDirectionUp)
      {
        currentTachLevel += (int)Math.ceil(mDesiredPower);
      }
      if(!isDirectionUp)
      {
        currentTachLevel += (int)Math.floor(mDesiredPower);
      }
      System.out.println("CHANGED LEVEL SUCCESS ");
		}
    return currentTachLevel;
  }

  public void setPower(double power) {
			mDesiredPower = power;
	}
	public void setElevControlMode(ElevatorControlMode elevControlMode)
  {
    this.elevControlMode = elevControlMode;
  }

	
	public void setCurrentTachLevel(int tachLevel)
	{
	  currentTachLevel = tachLevel;
	}
	public void setGearState(ElevatorGearState newState)
  {
    elevGearState = newState;
  }
	
	public ElevatorGearState getGearState()
	{
	  return elevGearState;
	}

	public boolean isDown() {

	  return mAtBottom;
	}

	//obsolete?
	public void zeroEncoder()
	{
	  masterElevator.setSelectedSensorPosition(0, 0, 0);
	}

	@Override
	public void shutdown(double pNow) {

	}

	public void goToBottom()
	{
		elevatorPosition = ElevatorPosition.BOTTOM;
	}

	public boolean decelerateHeight()
	{
		return currentTachLevel >= 2;
	}
	public boolean getDirection()
	{
		return isDirectionUp;
	}

	public ElevatorPosition getElevatorPosition()
	{
	  return elevatorPosition;
	}

	public ElevatorState getElevatorState()
	{

	  return elevatorState;
	}

	public ElevatorControlMode getElevControlMode()
  {
    return elevControlMode;
  }
	public int getCurrentTachLevel()
  {
    return currentTachLevel;
  }

  public int getCurrentEncoderTicks() {
    return currentEncoderTicks;
  }

  public double getDesiredPower()
  {
    return mDesiredPower;
  }
  

  //30/12 and 10/12 = amps / voltage
	public boolean isTopCurrentTripped() {
		return masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage() >= TOP_LIMIT;
	}

	public boolean isBottomCurrentTripped()
  {
    return masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage() >= BOTTOM_LIMIT;
  }
}

