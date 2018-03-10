package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.common.types.EElevator;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {

  public static final double TOP_LIMIT = 30d/12d, BOTTOM_LIMIT = 10d/12d;
  public static final int DEFAULT_CONTINOUS_LIMIT_AMPS = 20;
  public static final double RAMP_OPEN_LOOP = 0.5;
  
  private Data mData;
  private final Hardware mHardware;
  private TalonTach talonTach;
  Solenoid shiftSolenoid;
	TalonSRX masterElevator, followerElevator;
	
	private boolean hasInitialized;
	private boolean lastTachState, currentTachState;
	private int currentTachLevel, currentEncoderTicks;
	private double mDesiredPower = 0;
	private boolean mAtBottom, mAtTop, isDesiredDirectionUp;
	
	private ElevatorState elevatorState = ElevatorState.STOP;
	private ElevatorPosition elevatorPosition = ElevatorPosition.BOTTOM;
	private ElevatorGearState elevGearState = ElevatorGearState.NORMAL;
	private ElevatorControlMode elevControlMode = ElevatorControlMode.MANUAL;
	private ElevDirection elevatorDirection = ElevDirection.UP;
	
  private static final ILog log = Logger.createLog(Elevator.class);

  public Elevator(Hardware pHardware, Data pData) {
		mHardware = pHardware;
		mData = pData;
		
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		elevGearState = ElevatorGearState.NORMAL;
    
		hasInitialized = false;
		
    followerElevator.follow(masterElevator);
    masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.configContinuousCurrentLimit(DEFAULT_CONTINOUS_LIMIT_AMPS, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//		masterElevator.enableCurrentLimit(true);
		masterElevator.configOpenloopRamp(RAMP_OPEN_LOOP, 0);
		// TODO set voltage ramp & current limit
	}

  /**
   * 
   * @author ilite
   * Defines the state of the elevator as it moves up and down. Also defines the default power for each state, should we need it.
   */
	public enum ElevatorState
	{
		NORMAL(0.3),
		DECELERATE_TOP(0.2),
		DECELERATE_BOTTOM(-0.15),
		HOLD(0),
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
	
	/**
	 * 
	 * @author ilite
	 * Defines the possible positions of the elevator. Defines the power needed to get to a position and the tape mark associated with that position
	 */
	public enum ElevatorPosition
	{
		BOTTOM(0, 0),
		FIRST_TAPE(0.5, 1),
		SECOND_TAPE(0.5, 2),
		THIRD_TAPE(0.5, 3);

		double inches;
		double power; // Power to apply in order to servo to position
		int tapeMark;
		ElevatorPosition(double power, int tapeMark)
		{
			this.power = power;
			this.tapeMark = tapeMark;
		}
//		private boolean isTapeMarker()
//		{
//			return power != 0;
//		}
	}

	/**
	 * 
	 * @author ilite
	 * Defines the possible directions the elevator can move in. Stores the direction, the current limit for the given direction, the tape mark to begin deceleration, and the continuous current limit.
	 */
	public enum ElevDirection
	{
		UP(true, 30d/12d, 3, 20),
		DOWN(false, 1d/12d, 0, 10),
		OFF(false, 0d, 0, 0);

		boolean isPositiveDirection;
		double mCurrentLimitRatio;
		int decelerationTapeMark;
		int continuousCurrentLimit;

		ElevDirection(boolean isPositiveDirection, double pCurrentLimitRatio, int decelerationTapeMark, int continuousCurrentLimit)
		{
			this.isPositiveDirection = isPositiveDirection;
			mCurrentLimitRatio = pCurrentLimitRatio;
			this.decelerationTapeMark = decelerationTapeMark;
			this.continuousCurrentLimit = continuousCurrentLimit;
		}

		public static ElevDirection getDirection(double pDesiredPower)
		{
			return pDesiredPower > 0 ? UP : DOWN;
		}

		public boolean isCurrentRatioLimited(TalonSRX pMasterTalon)
		{
		  if(pMasterTalon.getMotorOutputVoltage() != 0)
		  {
	      return pMasterTalon.getOutputCurrent() / pMasterTalon.getMotorOutputVoltage() >= mCurrentLimitRatio;
		  }
		  else return false;
		}

		public int getCurrentLimit()
		{
		  return continuousCurrentLimit;
		}
		/**
		 * 
		 * @param currentTapeMark
		 * @return whether we should be decelerated at this position
		 */
		public boolean shouldDecelerate(int currentTapeMark)
		{
			return currentTapeMark == decelerationTapeMark;
		}
		
	}

	public enum ElevatorControlMode
	{
		MANUAL,
		CLIMBER,
		POSITION;
	}
	
	/**
	 * 
	 * @author ilite
	 * Defines possible shifter states and the carriage hold voltages associated with each.
	 */
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

	@Override
	public void initialize(double pNow) {
		masterElevator.setNeutralMode(NeutralMode.Brake);

		// Only initialize state variables once, since the elevator may be in a different position at the end of auto
		if(!hasInitialized) {
	    mAtBottom = true;
	    mAtTop = false;
	    isDesiredDirectionUp = true;
	    
	    currentTachLevel = 0;
	    
	    elevatorState = ElevatorState.STOP;
	    elevatorPosition = ElevatorPosition.BOTTOM;
	    elevGearState = ElevatorGearState.NORMAL;
	    elevControlMode = ElevatorControlMode.MANUAL;
	    
	    hasInitialized = true;
		}
		
    talonTach = mHardware.getTalonTach();

    currentTachState = talonTach.getSensor();
    lastTachState = currentTachState;
    
		setGearState(ElevatorGearState.NORMAL);
		elevatorState = ElevatorState.STOP;
	}

	@Override
	public boolean update(double pNow) {
	  
		currentTachState = talonTach.getSensor();
    currentEncoderTicks = masterElevator.getSelectedSensorPosition(0);
		
		isDesiredDirectionUp = mDesiredPower > 0 ? true : false;
		mAtTop = isCurrentLimiting() && currentTachLevel == ElevatorPosition.THIRD_TAPE.tapeMark;
		mAtBottom = isCurrentLimiting() && currentTachLevel == ElevatorPosition.BOTTOM.tapeMark;

		elevatorDirection = ElevDirection.getDirection(mDesiredPower);

//    masterElevator.configContinuousCurrentLimit(elevatorDirection.getCurrentLimit(), 0); // Don't wait to check for config success so we don't delay loop
//		masterElevator.enableCurrentLimit(true);
		
    currentTachLevel = getTachLevel(currentTachState, lastTachState); // Calculates current tape mark based on last/current tach state
    
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
						if(elevatorDirection.isCurrentRatioLimited(masterElevator))
						{
							elevatorState = ElevatorState.STOP;
						}
						else if(elevatorDirection.shouldDecelerate(currentTachLevel))
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
						if(elevatorDirection.isCurrentRatioLimited(masterElevator) || currentTachLevel == ElevatorPosition.BOTTOM.tapeMark)
						{
							elevatorState = ElevatorState.STOP;
						}
						else if(elevatorDirection.shouldDecelerate(currentTachLevel))
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
		    actualPower = elevatorState.getPower() / masterElevator.getBusVoltage();
				break;

			case STOP:
			default:
				actualPower = ElevatorState.STOP.getPower();
				break;
		}

		// TODO
		// Create Elevator Codex
		// Replace system outs with Log
		log.debug(elevatorState + " dPow=" + mDesiredPower + " aPow=" + actualPower + " dir=" + isDesiredDirectionUp +  "talonTach=" + currentTachLevel + "Amps: " + masterElevator.getOutputCurrent());
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
      if(isDesiredDirectionUp)
      {
        currentTachLevel += (int)Math.ceil(mDesiredPower);
      }
      if(!isDesiredDirectionUp)
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
		return isDesiredDirectionUp;
	}

	public ElevatorPosition getElevatorPosition()
	{
	  return elevatorPosition;
	}
	
	public void setPosition(ElevatorPosition desiredPosition)
	{
	  elevatorPosition = desiredPosition;
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
	public boolean isCurrentLimiting() {
		return elevatorDirection.isCurrentRatioLimited(masterElevator);
	}
	
}

