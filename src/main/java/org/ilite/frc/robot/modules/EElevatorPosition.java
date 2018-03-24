package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Utils;

/**
	 * 
	 * @author ilite
	 * Defines the possible positions of the elevator. Defines the power needed to get to a position and the tape mark associated with that position
	 */
	public enum EElevatorPosition
	{
		BOTTOM(0.4, 0),
		FIRST_TAPE(0.5, 2),
		SECOND_TAPE(0.6, 4),
		THIRD_TAPE(0.6, 6);

		double inches;
		double mSetpointPower; // Power to apply in order to servo to position
		public int encoderThreshold;
		EElevatorPosition(double power, int encoderThreshold)
		{
			this.mSetpointPower = power;
			this.encoderThreshold = encoderThreshold;
		}
		
		//checks if passed encoderTick is within the range of the threshold, to determine whether the elevator needs to hold
		public boolean inRange(int currentEncoderTick)
		{
      return Utils.inRange(currentEncoderTick, encoderThreshold, SystemSettings.ELEVATOR_ENCODER_DEADBAND_RANGE);

		}
//		private boolean isencoderThresholder()
//		{
//			return power != 0;
//		}
	}