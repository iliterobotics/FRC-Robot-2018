package org.ilite.frc.robot.modules;

/**
 * 
 * @author ilite
 * Defines the state of the elevator as it moves up and down. Also defines the default power for each state, should we need it.
 */
public enum EElevatorState
{
	NORMAL(0.35),
	DECELERATE_TOP(0.22),
	DECELERATE_BOTTOM(-0.25),
	HOLD(0),
	STOP(0);

	double power;
	EElevatorState(double power)
	{
		this.power = power;
	}

	double getPower()
	{
	  return power;
	}
}