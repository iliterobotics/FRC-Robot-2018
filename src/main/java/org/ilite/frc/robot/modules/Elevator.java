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
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;

public class Elevator implements IModule {

  public static final int POS_DECREMENT_AMOUNT = 0;
  public static final int POS_INCREMENT_AMOUNT = 0;
  public static final int ENCODER_POS_TOLERANCE = 0; // The error we should tolerate for closed-loop control
  public static final int ENCODER_PANIC_THRESHOLD = 0; // If we are this many ticks further up or down than the lower and upper limits, we should panic and stop the elevator
  public static final int UPPER_ENC_POS_LIMIT = 0;
  public static final int BOTTOM_ENC_POS_LIMIT = 0;
  
  public static final double TOP_LIMIT = 30d/12d, BOTTOM_LIMIT = 10d/12d;
  public static final int DEFAULT_CONTINOUS_LIMIT_AMPS = 20;
  public static final double RAMP_OPEN_LOOP = 1d;
  
  private Data mData;
  private final Hardware mHardware;
  Solenoid shiftSolenoid;
	TalonSRX masterElevator, followerElevator;
	
	private boolean hasInitialized;
	private double mDesiredPower;
	private int currentEncoderTicks, desiredEncoderTicks;
	private boolean mAtBottom = true, mAtTop = false;
	
	EElevatorState elevatorState = EElevatorState.STOP;
	EElevatorPosition elevatorPosition = EElevatorPosition.BOTTOM;
	EElevatorGearState elevGearState = EElevatorGearState.NORMAL;
	ElevatorControlMode elevControlMode = ElevatorControlMode.MANUAL;
	ElevDirection elevatorDirection = ElevDirection.UP;
	
  private static final ILog log = Logger.createLog(Elevator.class);

  public Elevator(Hardware pHardware, Data pData) {
		mHardware = pHardware;
		mData = pData;
		
		masterElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_MASTER);
		followerElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_FOLLOWER);
		shiftSolenoid = new Solenoid(SystemSettings.SOLENOID_ELEVATOR_SHIFTER);
		elevGearState = EElevatorGearState.NORMAL;
    
		hasInitialized = false;
		
    followerElevator.follow(masterElevator);
    
    zeroEncoder();
    
		masterElevator.configContinuousCurrentLimit(DEFAULT_CONTINOUS_LIMIT_AMPS, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.enableCurrentLimit(true);
		masterElevator.configOpenloopRamp(RAMP_OPEN_LOOP, 0);

		masterElevator.selectProfileSlot(SystemSettings.ELEVATOR_PID_SLOT, SystemSettings.ELEVATOR_LOOP_SLOT);
		masterElevator.config_kP(SystemSettings.ELEVATOR_PID_SLOT, SystemSettings.ELEVATOR_MOTION_MAGIC_P, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kI(SystemSettings.ELEVATOR_PID_SLOT, SystemSettings.ELEVATOR_MOTION_MAGIC_I, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kD(SystemSettings.ELEVATOR_PID_SLOT, SystemSettings.ELEVATOR_MOTION_MAGIC_D, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.config_kF(SystemSettings.ELEVATOR_PID_SLOT, SystemSettings.ELEVATOR_MOTION_MAGIC_F, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configMotionCruiseVelocity(SystemSettings.ELEVATOR_MOTION_MAGIC_V, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		masterElevator.configMotionAcceleration(SystemSettings.ELEVATOR_MOTION_MAGIC_A, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
		
		// TODO set voltage ramp & current limit
	}

  public enum ElevatorControlMode
	{
		MANUAL(0.9),
		CLIMBER(0.9),
		POSITION(0.9);
    
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
	    
	    mDesiredPower = 0;
	    
	    currentEncoderTicks = 0;
	    desiredEncoderTicks = 0;
	    
	    elevatorState = EElevatorState.STOP;
	    elevatorPosition = EElevatorPosition.BOTTOM;
	    elevGearState = EElevatorGearState.NORMAL;
	    elevControlMode = ElevatorControlMode.MANUAL;
	    
	    hasInitialized = true;
		}
    
		setGearState(EElevatorGearState.NORMAL);
		elevatorState = EElevatorState.STOP;
	}

	@Override
	public boolean update(double pNow) {
	  
    currentEncoderTicks = masterElevator.getSelectedSensorPosition(0);
    
    elevatorDirection = ElevDirection.getDirection(masterElevator.getMotorOutputVoltage(), elevControlMode);
    
    boolean isCurrentLimited = isCurrentLimiting();
		mAtTop = isCurrentLimited && elevatorDirection == ElevDirection.UP;
		mAtBottom = isCurrentLimited && elevatorDirection == ElevDirection.DOWN;
//		if(mAtTop) {
//		  resetTop();
//		} else if
		if(mAtBottom) {
		  resetBottom();
		}

//    masterElevator.configContinuousCurrentLimit(elevatorDirection.getCurrentLimit(), 0); // Don't wait to check for config success so we don't delay loop
//		masterElevator.enableCurrentLimit(true);   
		
		if(isCurrentLimited) {
		  elevatorState = EElevatorState.STOP;
		} else {
		  updateElevatorControl();
		}
    
		double actualTicks = currentEncoderTicks;

		switch(elevatorState){
			case NORMAL:
			  actualTicks = Utils.clamp(actualTicks, BOTTOM_ENC_POS_LIMIT, UPPER_ENC_POS_LIMIT);
				actualTicks = desiredEncoderTicks;
				break;
			case HOLD:
		    masterElevator.set(ControlMode.PercentOutput, elevGearState.holdVoltage / masterElevator.getBusVoltage());
				break;
			case STOP:
			default:
			  masterElevator.set(ControlMode.PercentOutput, 0.0);
				break;
		}

		// TODO
		// Create Elevator Codex
		// Replace system outs with Log
		//log.warn(elevatorState + " dPow=" + mDesiredPower + " aPow=" + actualPower + " dir=" + isDesiredDirectionUp +  "talonTach=" + currentTachLevel + "Amps: " + masterElevator.getOutputCurrent());
//		log.warn("ElevState:" + elevatorState +  " talonTach=" + currentTachLevel + " talonTachState=" + currentTachState);
//		log.warn(masterElevator.getOutputCurrent() + "/" + masterElevator.getMotorOutputVoltage());
		masterElevator.set(ControlMode.MotionMagic, Utils.clamp(desiredEncoderTicks, BOTTOM_ENC_POS_LIMIT, UPPER_ENC_POS_LIMIT));
//		masterElevator.set(ControlMode.PercentOutput, actualPower);
		//System.out.println(mDesiredPower + "POST CHECK");
		shiftSolenoid.set(elevGearState.gearState);
		return true;
	}
	
	private void updateElevatorControl() {

    switch(elevControlMode) {
      case POSITION:
        if(shouldPanic()) elevatorState = EElevatorState.STOP;
        if(isPosNear(currentEncoderTicks, desiredEncoderTicks)) elevatorState = EElevatorState.HOLD;
        if(!isPosNear(currentEncoderTicks, desiredEncoderTicks)) elevatorState = EElevatorState.NORMAL;
        break;        
      case CLIMBER:
      case MANUAL:
        //if no input, idle at hold power
        if(Math.abs(mDesiredPower) == 0d && !mAtBottom)
        {
          elevatorState = EElevatorState.HOLD;
        } else {
          elevatorState = EElevatorState.NORMAL;
        }
        break;
    }
//    if(shouldstop) {
//      elevatorState = ElevatorState.STOP;
//    }
//    double power = ElevatorState.HOLD.power / 12 * masterElevator.getBusVoltage();

	}
	
	private void resetTop() {
	  currentEncoderTicks = UPPER_ENC_POS_LIMIT;
	  zeroEncoder(UPPER_ENC_POS_LIMIT);
	}
	
	private void resetBottom() {
    currentEncoderTicks = 0;
    zeroEncoder(BOTTOM_ENC_POS_LIMIT);
	}

  public void setPower(double power) {
			mDesiredPower = power;
	}
  
	public void setElevControlMode(ElevatorControlMode elevControlMode)
  {
    this.elevControlMode = elevControlMode;
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
	  zeroEncoder(0);
	}
	
	public void zeroEncoder(int pTicks) {
	  masterElevator.setSelectedSensorPosition(SystemSettings.ELEVATOR_PID_SLOT, pTicks, SystemSettings.TALON_CONFIG_TIMEOUT_MS);
	}

	@Override
	public void shutdown(double pNow) {

	}

	public EElevatorPosition getElevatorPosition()
	{
	  return elevatorPosition;
	}
	
	public void setPosition(EElevatorPosition desiredPosition)
	{
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
  
  public void up() {
    desiredEncoderTicks = currentEncoderTicks + POS_INCREMENT_AMOUNT;
  }
	
	public void down() {
	  desiredEncoderTicks = currentEncoderTicks - POS_DECREMENT_AMOUNT;
	}
	
	private boolean isPosNear(int pCurrentTicks, int pGoalTicks) {
	  return Math.abs(pGoalTicks - pCurrentTicks) < ENCODER_POS_TOLERANCE;
	}
	
	private boolean shouldPanic() {
	  boolean panicTop = Math.abs(currentEncoderTicks - UPPER_ENC_POS_LIMIT) > ENCODER_PANIC_THRESHOLD;
	  boolean panicBottom = Math.abs(currentEncoderTicks - BOTTOM_ENC_POS_LIMIT) > ENCODER_PANIC_THRESHOLD;
	  return panicTop || panicBottom;
	}
	
}

