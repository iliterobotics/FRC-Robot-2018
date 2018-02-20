package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

public class TalonTach {

	private DigitalInput talonTachSensor;
	public TalonTach(int port)
	{
		talonTachSensor = new DigitalInput(port);
	}
	
	//true for reflective surfaces (powdercoat) false for non-reflective (tape)
	public boolean getSensor()
	{
	  if(talonTachSensor == null) {
	    System.err.println("talon tach is null...");
	    return false;
	  } else {
	    return talonTachSensor.get();
	  }
	}
	
}
