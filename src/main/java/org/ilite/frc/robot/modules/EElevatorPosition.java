package org.ilite.frc.robot.modules;

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
		public int tapeMark;
		EElevatorPosition(double power, int tapeMark)
		{
			this.mSetpointPower = power;
			this.tapeMark = tapeMark;
		}
//		private boolean isTapeMarker()
//		{
//			return power != 0;
//		}
	}