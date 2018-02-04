package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalBeamSensor  {

	private final DigitalInput mBeamInput;
	
	public DigitalBeamSensor(int pInputChannel) {
		mBeamInput = new DigitalInput(pInputChannel);
	}
	public boolean isBroken() {
	  // NOTE - if the beam is noisy, we can do some filtered average based upon the leading
	  // edge of the detection rather than just a 'get'.  This effectively debounces the signal.
	  //	  mBeamInput.readRisingTimestamp()
		return mBeamInput.get();
	}
	
}
