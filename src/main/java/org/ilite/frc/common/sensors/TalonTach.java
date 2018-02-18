package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

public class TalonTach {

	private DigitalInput talonTachSensor;
	public TalonTach(int port)
	{
		talonTachSensor = new DigitalInput(port);
	}
	
	//true for reflective surfaces (powdercoat) false for non-reflective (tape)
	public boolean getSensor()
	{
		//return talonTachSensor.get();
	  return false;
	}
	
	
}
