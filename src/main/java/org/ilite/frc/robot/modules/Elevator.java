package org.ilite.frc.robot.modules;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {
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
	private DigitalInput bottomLimit;
	private ElevatorControlMode elevControlMode;
	private ElevDirection elevatorDirection;
  private static final ILog log = Logger.createLog(Elevator.class);
  public static final double TOP_LIMIT = 30d/12d, BOTTOM_LIMIT = 10d/12d;


  public Elevator(Hardware pHardware) {
		mHardware = pHardware;
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		followerElevator.follow(masterElevator);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		isDirectionUp = true;
		//beamBreak = new DigitalInput(SystemSettings.BEAM_BREAK_FRONT);
		elevatorState = ElevatorState.STOP;
		elevatorPosition = ElevatorPosition.BOTTOM;
		elevGearState = ElevatorGearState.NORMAL;

		bottomLimit = new DigitalInput(SystemSettings.DIO_ELEVATOR_BOTTOM_LIMIT_SWITCH);

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
		FIRST_TAPE(0.3, 1),
		SECOND_TAPE(0.3, 2),
		THIRD_TAPE(0.3, 3);

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
		UP(true, 30d/12d, 3),
		DOWN(false, 11d/12d, 0);

		boolean isPositiveDirection;
		double mCurrentLimitRatio;
		int tapeMark;

		ElevDirection(boolean isPositiveDirection, double pCurrentLimitRatio, int tapeMark)
		{
			this.isPositiveDirection = isPositiveDirection;
			mCurrentLimitRatio = pCurrentLimitRatio;
			this.tapeMark = tapeMark;
		}

		public static ElevDirection getDirection(double pDesiredPower)
		{
			return pDesiredPower > 0 ? UP : DOWN;
		}

		public boolean isCurrentLimited(TalonSRX pMasterTalon)
		{
			return pMasterTalon.getOutputCurrent() / pMasterTalon.getMotorOutputVoltage() >= mCurrentLimitRatio;
		}

		public boolean isDecelerated(int currentTapeMark)
		{
			return currentTapeMark == tapeMark;
		}
	}

	public enum ElevatorControlMode
  {
    MANUAL,
    POSITION,
    CURRENT_LIMITING;
  }

  public enum ElevatorGearState
  {
    NORMAL(false),
    CLIMBING(true);

    boolean gearState;
    ElevatorGearState(boolean gearState)
    {
      this.gearState = gearState;
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

      case MANUAL:

//      	switch(elevatorDirection)
//				{
//					case UP:
//						if(elevatorDirection.isDecelerated(currentTachLevel))
//						{
//							elevatorState = ElevatorState.DECELERATE_TOP;
//						}
//						if(elevatorDirection.isCurrentLimited(masterElevator))
//						{
//							elevatorState = ElevatorState.STOP;
//						}
//						break;
//					case DOWN:
//						if(elevatorDirection.isDecelerated(currentTachLevel))
//						{
//							elevatorState = ElevatorState.DECELERATE_BOTTOM;
//						}
//						if(elevatorDirection.isCurrentLimited(masterElevator))
//						{
//							elevatorState = ElevatorState.STOP;
//						}
//				}
        //bottom
        if (!isDirectionUp && currentTachLevel == 0) {
          elevatorState = ElevatorState.DECELERATE_BOTTOM;
        }
        //top
        if (isDirectionUp && currentTachLevel == 3) {
          elevatorState = ElevatorState.DECELERATE_TOP;
        }
        if ((isDirectionUp && currentTachLevel == 0) || (!isDirectionUp && currentTachLevel == 3)) {
          elevatorState = ElevatorState.NORMAL;
        }
        if (mAtBottom) {
          if (mDesiredPower > 0)
          {
            elevatorState = ElevatorState.NORMAL;
          }
          else {
            elevatorState = ElevatorState.STOP;
          }
        }
        if (mAtTop)
        {
          if (mDesiredPower < 0)
          {
            elevatorState = ElevatorState.NORMAL;
          }
          else {

            elevatorState = ElevatorState.STOP;
          }
        }
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
    }
    return currentTachLevel;
  }

  public void setPower(double power)
  {
		mDesiredPower = power;
	}

	public void setElevControlMode(ElevatorControlMode elevControlMode)
  {
    this.elevControlMode = elevControlMode;
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
	private boolean isTopCurrentTripped() {
		return masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage() >= TOP_LIMIT;
	}

	private boolean isBottomCurrentTripped()
  {

    return masterElevator.getOutputCurrent() / masterElevator.getMotorOutputVoltage() >= BOTTOM_LIMIT;
  }
}

