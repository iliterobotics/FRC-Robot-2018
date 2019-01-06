package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {
  public static final double TOP_LIMIT = 30d/12d, BOTTOM_LIMIT = 10d/12d;
  public static final int DEFAULT_CONTINOUS_LIMIT_AMPS = 25;
  public static final double RAMP_OPEN_LOOP = 0.4d;
  
  private Data mData;
  private final Hardware mHardware;
  TalonTach talonTach;
  Solenoid shiftSolenoid;
	TalonSRX masterElevator, followerElevator;
	
	private boolean hasInitialized;
	private boolean lastTachState, currentTachState;
	private int currentTachLevel, currentEncoderTicks;
	private double mDesiredPower = 0;
	private boolean mAtBottom = true, mAtTop = false, isDesiredDirectionUp = true;
	private boolean isSetpointAboveIntialPosition = false;
	private boolean isAtPosition = false;
	
	EElevatorState elevatorState = EElevatorState.STOP;
	EElevatorPosition elevatorPosition = EElevatorPosition.BOTTOM, lastElevPosition = EElevatorPosition.BOTTOM;
	EElevatorGearState elevGearState = EElevatorGearState.NORMAL;
	ElevatorControlMode elevControlMode = ElevatorControlMode.MANUAL;
	ElevDirection elevatorDirection = ElevDirection.UP;
	ElevatorControlMode currentElevControlMode, lastElevControlMode;
	
  private static final ILog log = Logger.createLog(Elevator.class);

   
  
  public Elevator(Hardware pHardware, Data pData) {
		mHardware = pHardware;
		mData = pData;
		
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		masterElevator.setInverted(true);
		followerElevator.setInverted(true);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		elevGearState = EElevatorGearState.NORMAL;
    
		hasInitialized = false;
		
    followerElevator.follow(masterElevator);
    masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configContinuousCurrentLimit(DEFAULT_CONTINOUS_LIMIT_AMPS, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.enableCurrentLimit(true);
		masterElevator.setSensorPhase(false);
		masterElevator.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configOpenloopRamp(RAMP_OPEN_LOOP, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
    followerElevator.configOpenloopRamp(RAMP_OPEN_LOOP, SystemSettings.TALON_CONFIG_TIMEOUT_MS);

    masterElevator.setNeutralMode(NeutralMode.Brake);
    followerElevator.setNeutralMode(NeutralMode.Brake);
	}

  public enum ElevatorControlMode
	{
		MANUAL(1.0),
		CLIMBER(1.0),
		POSITION(1.0);
    
    private double maxPower;
    
    private ElevatorControlMode(double maxPower) {
      this.maxPower = maxPower;
    }
    
    public double getMaxPower() {
      return maxPower;
    }
    
	}
	
	@Override
	public void initialize(double pNow) {
	  Logger.setLevel(ELevel.WARN);
		masterElevator.setNeutralMode(NeutralMode.Brake);

		// Only initialize state variables once, since the elevator may be in a different position at the end of auto
		if(!hasInitialized) {
	    mAtBottom = true;
	    mAtTop = false;
	    isDesiredDirectionUp = true;
	    
	    currentEncoderTicks = 0;
	    
	    elevatorState = EElevatorState.STOP;
	    elevatorPosition = EElevatorPosition.BOTTOM;
	    elevGearState = EElevatorGearState.NORMAL;
	    elevControlMode = ElevatorControlMode.MANUAL;
	    
	    currentElevControlMode = elevControlMode;
	    lastElevControlMode = elevControlMode;
	    
	    hasInitialized = true;
	    masterElevator.getSensorCollection().setQuadraturePosition(0, 0);
		}
		
    talonTach = mHardware.getTalonTach();

    currentTachState = talonTach.getSensor();
//    lastTachState = currentTachState;
    
		setGearState(EElevatorGearState.NORMAL);
		elevatorState = EElevatorState.STOP;
	}

	@Override
	public boolean update(double pNow) {
	  
//		currentTachState = talonTach.getSensor();
    currentEncoderTicks = masterElevator.getSelectedSensorPosition(0);
    isDesiredDirectionUp = mDesiredPower > 0 ? true : false;
//    currentTachLevel = getTachLevel(currentTachState);//, lastTachState); // Calculates current tape mark based on last/current tach state

    elevatorDirection = ElevDirection.getDirection(mDesiredPower, elevControlMode);
    boolean isCurrentLimited = isCurrentLimiting();

		mAtTop = isCurrentLimited && elevatorDirection == ElevDirection.UP;
		mAtBottom = isCurrentLimited && elevatorDirection == ElevDirection.DOWN;
//		if(mAtTop) {
//		  resetTop();
//		} else if
//		  if(mAtBottom) {
//		  resetBottom();
//		}

//    masterElevator.configContinuousCurrentLimit(elevatorDirection.getCurrentLimit(), 0); // Don't wait to check for config success so we don't delay loop
//		masterElevator.enableCurrentLimit(true);   
		
		if(isCurrentLimited) {
		  log.warn("================================= CURRENT LIMITED");
		  elevatorState = EElevatorState.STOP;
		} else {
		  updateElevatorControl(pNow);
		}
    
		double actualPower = 0;

		switch(elevatorState){

			case NORMAL:
			  if(elevatorDirection == ElevDirection.DOWN) {
			    actualPower = -elevatorState.power;
			  } else {
			    actualPower = mDesiredPower;
			  }
				break;

			case DECELERATE_BOTTOM:
			  actualPower = Math.max(mDesiredPower, elevatorState.power);
			  break;
			case DECELERATE_TOP:
			  actualPower = Math.min(mDesiredPower, elevatorState.power);
				break;

			case HOLD:
		    actualPower = elevGearState.holdVoltage / masterElevator.getBusVoltage();
				break;

			case STOP:
			default:
				actualPower = EElevatorState.STOP.getPower();
				break;
		}

		//log.warn(elevatorState + " dPow=" + mDesiredPower + " aPow=" + actualPower + " dir=" + isDesiredDirectionUp +  "talonTach=" + currentTachLevel + "Amps: " + masterElevator.getOutputCurrent());
//		log.warn("ElevState:" + elevatorState +  " talonTach=" + currentTachLevel + " talonTachState=" + currentTachState);
//		log.warn(masterElevator.getOutputCurrent() + "/" + masterElevator.getMotorOutputVoltage());
		masterElevator.set(ControlMode.PercentOutput, Utils.clamp(actualPower, elevControlMode.getMaxPower()));

		shiftSolenoid.set(elevGearState.gearState);
		return true;
	}
	
  double lastError = 0d;
  double lastTime = 0d;
  double startTime = 0d;
	boolean hasStarted = false;
	private void updateElevatorControl(double now) {
	  currentElevControlMode = elevControlMode;
	  /*
	   * Check if our position setpoint has been updated. If it has, reset our start time so we don't exit before the motors begin moving.
	   */
	  if(currentElevControlMode == ElevatorControlMode.POSITION && elevatorPosition.encoderThreshold != lastElevPosition.encoderThreshold)
    {
      hasStarted = true;
    } else {
      hasStarted = false;
    }
	  
    switch(elevControlMode) {
      case POSITION:
        
        if(hasStarted)
        {
          startTime = now;
        }
        double error = elevatorPosition.encoderThreshold - currentEncoderTicks;
        // 1.0 power = 1000 ticks
        double kp = 1d / 2000d * 1.2;
        log.warn(currentEncoderTicks + "============ CURRENT TICKS");
        log.warn("Time going to setpoint: " + (lastTime - startTime));
//        int directionScalar = 0;
        // If we are past the setpoint, hold position
        if(Math.abs(error - lastError) <= SystemSettings.ELEVATOR_ERROR_DEADBAND_TICKS && lastTime - startTime >= SystemSettings.ELEVATOR_ENCODER_TIMEOUT)
//        if(isFinishedGoingToPosition())
        {
          elevatorState = EElevatorState.HOLD;
          isAtPosition = true;
        // If the setpoint is above us, and we are below it, go up
        // This is redundant
//        } else if (isSetpointAboveIntialPosition) {
//          directionScalar = (elevatorPosition.isBelowSetpoint(currentEncoderTicks)) ? 1 : -1;
//          elevatorState = EElevatorState.NORMAL;
//        } else if(!isSetpointAboveIntialPosition && elevatorPosition.isAboveSetpoint(currentEncoderTicks)) {
//          directionScalar = (elevatorPosition.isAboveSetpoint(currentEncoderTicks)) ? -1 : 1;
//          elevatorState = EElevatorState.NORMAL;
//        }
          
//        mDesiredPower = elevatorPosition.mSetpointPower * directionScalar;
          
        } else {
          elevatorState = EElevatorState.NORMAL;
          System.out.println("ERROR=" + error + " \tkP = " + kp);
          
          mDesiredPower = Utils.clamp(kp * error, elevatorPosition.mSetpointPower);
          elevatorDirection = ElevDirection.getDirection(mDesiredPower, elevControlMode);
          
//          if(mDesiredPower <= EElevatorState.HOLD.power / masterElevator.getBusVoltage()){
//              isAtPosition = true;
//            }
//          else {
//            isAtPosition = false;
//          }
//          if(elevatorDirection == ElevDirection.DOWN) {
//            mDesiredPower /= 1.25d;
//          }
//          if(elevatorDirection.shouldDecelerate(currentEncoderTicks, elevatorDirection.isPositiveDirection)) {
//            mDesiredPower = Utils.clamp(mDesiredPower, -1*EElevatorState.DECELERATE_BOTTOM.power);
//          } else if (currentEncoderTicks > ElevDirection.UP.decelerationEncoderThreshold) {
//            mDesiredPower = Utils.clamp(mDesiredPower, EElevatorState.DECELERATE_TOP.power);
//          }
          //check if we should decelerate based on whether we are near the top of bottom
//          if(elevatorDirection.shouldDecelerate(currentEncoderTicks, elevatorDirection.isPositiveDirection))
//          {
//            //clamp power by checking if going up or down and setting to deceleration power accordingly
//            if(elevatorDirection.isPositiveDirection)
//            {
//              mDesiredPower = Utils.clamp(mDesiredPower, EElevatorState.DECELERATE_TOP.power);
//            }
//            else
//              mDesiredPower = Utils.clamp(mDesiredPower, EElevatorState.DECELERATE_BOTTOM.power);
//          }
          lastError = error;
          lastTime = now;
          
        }
        
//        log.debug("TAPE MARKER " + elevatorPosition);
        break;

        
      case CLIMBER:
      case MANUAL:
        //if no input, idle at hold power
        if(Math.abs(mDesiredPower) == 0d && !mAtBottom)
        {
          elevatorState = EElevatorState.HOLD;
        }
        //else, decide state based on direction enum
        else switch(elevatorDirection)
        {
          case UP:
            
            if(elevatorDirection.shouldDecelerate(currentEncoderTicks, elevatorDirection.isPositiveDirection))
            {
              elevatorState = EElevatorState.DECELERATE_TOP;
            } else {
              elevatorState = EElevatorState.NORMAL;
            }
            break;
          case DOWN:
            if(elevatorDirection.shouldDecelerate(currentEncoderTicks, elevatorDirection.isPositiveDirection))
            {
              elevatorState = EElevatorState.DECELERATE_BOTTOM;
            } else {
              elevatorState = EElevatorState.NORMAL;
            }
            break;
          case CLIMBER_UP:
            elevatorState = EElevatorState.NORMAL;
            break;
          case CLIMBER_DOWN:
            elevatorState = EElevatorState.NORMAL;
            break;
          default:
            elevatorState = EElevatorState.STOP;
        }
        break;
    }
    
    lastElevControlMode = currentElevControlMode;
    lastElevPosition = elevatorPosition;
//    if(shouldstop) {
//      elevatorState = ElevatorState.STOP;
//    }
//    double power = ElevatorState.HOLD.power / 12 * masterElevator.getBusVoltage();

	}
	
	private void resetTop() {

	  masterElevator.setSelectedSensorPosition(SystemSettings.ELEVATOR_TOP_ENCODER_TICK, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
//    currentTachLevel = 6;
	}
	
	private void resetBottom() {
    currentTachLevel = 0;
    currentEncoderTicks = 0;
    masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}

	/*
	  Changes the tach level by taking the currentTachState and lastTachState every cycle.
	  Increases by one when first encounters non-reflective material

	  true = reflective material (powdercoat) false = non-reflective (tape)
	 */
	
	//OBSOLETE
  private int getTachLevel(boolean currentTachState)//, boolean pLastTachState)
  {
    //if it hits or leaves a tape, changes tachlevel based on direction
    if(currentTachState)
    {
      if(isDesiredDirectionUp)
      {
        currentTachLevel++;
      }
      if(!isDesiredDirectionUp)
      {
        currentTachLevel--;
      }
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
	
	public void setGearState(EElevatorGearState newState)
  {
    elevGearState = newState;
  }
	
	public EElevatorGearState getGearState()
	{
	  return elevGearState;
	}

	public boolean isDown() {

	  return mAtBottom;
	}

	public void zeroEncoder()
	{
    masterElevator.setSelectedSensorPosition(0, 0, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}

	@Override
	public void shutdown(double pNow) {

	}

	public void goToBottom()
	{
		elevatorPosition = EElevatorPosition.BOTTOM;
	}

	public boolean decelerateHeight()
	{
		return currentTachLevel >= 2;
	}
	public boolean getDirection()
	{
		return isDesiredDirectionUp;
	}

	public EElevatorPosition getElevatorPosition()
	{
	  return elevatorPosition;
	}
	
	public void setPosition(EElevatorPosition desiredPosition)
	{
	  log.warn("SETTING POSITION: " + desiredPosition.name());
	  // Sets a flag telling whether the setpoint was above or below the current position when we called setPosition
	  if(currentEncoderTicks < desiredPosition.encoderThreshold) {
	    isSetpointAboveIntialPosition = true;
	  } else if (currentEncoderTicks > desiredPosition.encoderThreshold){
	    isSetpointAboveIntialPosition = false;
	  }
	  elevatorPosition = desiredPosition;
	}

	public EElevatorState getElevatorState()
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
  
  public double getMasterVoltage()
  {
    return masterElevator.getMotorOutputVoltage();
  }
  public double getFollowerVoltage()
  {
    return followerElevator.getMotorOutputVoltage();
  }
  
  public double getMasterCurrrent()
  {
    return masterElevator.getOutputCurrent();
  }
  public double getFollowerCurrent()
  {
    return followerElevator.getOutputCurrent();
  }
  //30/12 and 10/12 = amps / voltage
	public boolean isCurrentLimiting() {
		return elevatorDirection.isCurrentRatioLimited(mData.elevator);
	}
	
	public boolean isFinishedGoingToPosition() {
	  return isAtPosition;
//	  return Math.abs(error - lastError) <= SystemSettings.ELEVATOR_ERROR_DEADBAND_TICKS && lastTime - startTime >= SystemSettings.ELEVATOR_ENCODER_TIMEOUT;
	}
	
}

