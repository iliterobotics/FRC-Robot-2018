package org.ilite.frc.robot.modules;

/**
 * 
 * @author ilite
 * Defines possible shifter states and the carriage hold voltages associated with each.
 */
public enum EElevatorGearState
{
	NORMAL(true, 0.8),
	CLIMBING(false, 1.1);

	boolean gearState;
	double holdVoltage;
	EElevatorGearState(boolean gearState, double holdVoltage)
  {
    this.gearState = gearState;
    this.holdVoltage = holdVoltage;
  }
}