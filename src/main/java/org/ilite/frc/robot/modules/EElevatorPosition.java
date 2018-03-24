package org.ilite.frc.robot.modules;

/**
	 * 
	 * @author ilite
	 * Defines the possible positions of the elevator. Defines the power needed to get to a position and the tape mark associated with that position
	 */
	public enum EElevatorPosition
	{
		BOTTOM(0),
		FIRST_TAPE(2),
		SECOND_TAPE(4),
		THIRD_TAPE(6);
	  
		public int encoderThreshold;
		EElevatorPosition(int encoderThreshold)
		{
			this.encoderThreshold = encoderThreshold;
		}
		
		public boolean isAtPosition(int pCurrentPosition, EElevatorPosition pPosition) {
		  return Math.abs(pPosition.encoderThreshold - pCurrentPosition) < Elevator.ENCODER_POS_TOLERANCE;
		}
		
	}