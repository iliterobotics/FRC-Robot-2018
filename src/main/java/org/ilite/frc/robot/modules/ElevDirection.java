package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.modules.Elevator.ElevatorControlMode;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * 
 * @author ilite
 * Defines the possible directions the elevator can move in. Stores the direction, the current limit for the given direction, the tape mark to begin deceleration, and the continuous current limit.
 */
public enum ElevDirection
{
  //up = 3 down = 1
	UP(true, 35d/12d, 5, 20),
	DOWN(false, 20d/12d, 2, 10),
	CLIMBER_UP(true, 30d/12d, 5, 20),
	CLIMBER_DOWN(false, 65d/12d, 2, 10),
	OFF(false, 15d/12d, 0, 0);

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

	public static ElevDirection getDirection(double pDesiredPower, ElevatorControlMode pElevatorControlMode)
	{
	  if(pDesiredPower == 0) return OFF;
	  if(pElevatorControlMode == ElevatorControlMode.CLIMBER) {
	    return pDesiredPower > 0 ? CLIMBER_UP : CLIMBER_DOWN;
	  } else {
	    return pDesiredPower > 0 ? UP : DOWN;
	  }
	}

	public boolean isCurrentRatioLimited(TalonSRX pMasterTalon)
	{
	  if(pMasterTalon.getMotorOutputVoltage() != 0)
	  {
//	    System.out.println("LIMIT: " + pMasterTalon.getOutputCurrent() / pMasterTalon.getMotorOutputVoltage());
      return Math.abs(pMasterTalon.getOutputCurrent()) / Math.abs(pMasterTalon.getMotorOutputVoltage()) >= mCurrentLimitRatio;
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
	public boolean shouldDecelerate(int currentTapeMark, boolean isUp)
	{
	  if(isUp) {
      return currentTapeMark >= decelerationTapeMark;
	  } else {
	    return currentTapeMark <= decelerationTapeMark;
	  }
	}
	
}