package org.ilite.frc.common.sensors;

import org.ilite.frc.robot.controlloop.IControlLoop;

import edu.wpi.first.wpilibj.DigitalInput;

public class TalonTach implements IControlLoop {

	private DigitalInput talonTachSensor;
	private boolean currentState;
	
	public TalonTach(int port)
	{
		talonTachSensor = new DigitalInput(port);
		currentState = true;
	}
	
	//true for reflective surfaces (powdercoat) false for non-reflective (tape)
	public boolean getSensor()
	{
	  return currentState;
	}
	
//	public boolean getSensor() {
//    if(talonTachSensor == null) {
//      System.err.println("talon tach is null...");
//      return false;
//    } else {
//      return talonTachSensor.get();
//    }
//  }
	
	private boolean getState() {
	  if(talonTachSensor == null) {
      System.err.println("talon tach is null...");
      return false;
    } else {
      return talonTachSensor.get();
    }
	}

  @Override
  public void initialize(double pNow) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean update(double pNow) {
    currentState = getState();
    return false;
  }

  @Override
  public void loop(double pNow) {
    update(pNow);
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }
	
}
