package org.ilite.frc.common.sensors;

import org.ilite.frc.robot.controlloop.IControlLoop;

import edu.wpi.first.wpilibj.DigitalInput;

public class TalonTach implements IControlLoop {

	private DigitalInput talonTachSensor;
	private boolean mCurrentState, lastState, hasChanged, hasBeenPolled;
	private TapeState lastTapeState, mCurrentTapeState;
	
	public TalonTach(int port)
	{
		talonTachSensor = new DigitalInput(port);
		mCurrentState = true;
		lastState = true;
		hasChanged = false;
		hasBeenPolled = false;
		mCurrentTapeState = TapeState.NON_TAPE;
		lastTapeState = TapeState.NON_TAPE;
	}

	public enum TapeState
	{
	  TAPE,
	  NON_TAPE;
	  
	}
	//true for reflective surfaces (powdercoat) false for non-reflective (tape)
	public boolean getSensor()
	{
	  boolean actualHasChanged = hasChanged;
	  hasChanged = false;
	  return actualHasChanged;
	}
	
//	?public boolean getSensor() {
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
  
  //calculates whether elevator is at a tape or not
  public void setTapeState(boolean pCurrentState)
  {
    if(pCurrentState == false && lastState == true) mCurrentTapeState = TapeState.TAPE;
    if(pCurrentState == true && lastState == false) mCurrentTapeState = TapeState.NON_TAPE;
  }

  
  public TapeState getTapeState()
  {
    return mCurrentTapeState;
  }
  
  @Override
  public boolean update(double pNow) {
    mCurrentState = getState();
    setTapeState(mCurrentState);
    if(mCurrentTapeState != lastTapeState) hasChanged = true;
    
    System.out.println("ON TAPE ? " + mCurrentTapeState);
    lastTapeState = mCurrentTapeState;
    lastState = mCurrentState;
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
