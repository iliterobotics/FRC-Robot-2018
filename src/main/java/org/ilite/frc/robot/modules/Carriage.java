package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;

import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Carriage implements IModule{

  public Solenoid solenoidGrabber, solenoidKicker;
  public Hardware mHardware;
  private static final double RELEASE_DELAY = .02; // KICK first, then release after this amount of time (seconds)
  private static final double RESET_DELAY = 0.1; // reset after all cylinders have fully extended.
  private Data mData;
  private boolean isScheduled;
  private DigitalInput beamBreak;
  private CarriageState mCurrentState, mDesiredState;
  private double releaseTime;
  private double resetTime;

  private static final ILog log = Logger.createLog(Carriage.class);

  //constructs necessary variables and sets default state to cube
  public Carriage(Data pData, Hardware pHardware)
  {
    mData = pData;
    mHardware = pHardware;
    isScheduled = false;
    solenoidGrabber = new Solenoid(SystemSettings.CARRIAGE_GRABBER_ID);
    solenoidKicker = new Solenoid(SystemSettings.CARRIAGE_KICKER_ID);
    mDesiredState = mCurrentState = CarriageState.GRAB_CUBE;
  }

  public enum GrabberState
  { 
    GRAB_EXTEND(false),
    RELEASE_RETRACT(true);

    private boolean grabber;

    private GrabberState(boolean grabber)
    {
      this.grabber = grabber;
    }
  }

  public enum KickerState
  {
    KICK_EXTEND(true),
    RESET(false);

    private boolean kicker;

    private KickerState(boolean kicker)
    {
      this.kicker = kicker;
    }
  }

  //creates new enum with states for no cube, cube, and kicking
  public enum CarriageState
  {
    RESET(GrabberState.RELEASE_RETRACT, KickerState.RESET),
    GRAB_CUBE(GrabberState.GRAB_EXTEND, KickerState.RESET),
    KICKING(null, null); // this is a sequence

    GrabberState mGrab;
    KickerState mKick;
    private CarriageState(GrabberState pGrab, KickerState pKick) {
      mGrab = pGrab;
      mKick = pKick;
    }
  }

  @Override
  public void shutdown(double pNow) {

  }

  public void setDesiredState(CarriageState pDesiredState)
  {
    mDesiredState = pDesiredState;
  }
  
  public void setCurrentState(CarriageState pCurrentState) {
    mCurrentState = pCurrentState;
    isScheduled = false;
  }

  @Override
  //makes sure that the kick sequence has not started, gets the correct beamBreak, and sets the current state to cube
  public void initialize(double pNow) {
    isScheduled = false;
    beamBreak = mHardware.getCarriageBeamBreak();
    mDesiredState = mCurrentState = CarriageState.GRAB_CUBE;
  }
  @Override
  public boolean update(double pNow)
  {
    if(mCurrentState != CarriageState.KICKING) {
      // If we have a cube and we aren't overriding the beam break, grab the cube
      if(getBeamBreak() && mDesiredState == null) setCurrentState(CarriageState.GRAB_CUBE);
      // If we haven't already set the desired state, set it
      if(mDesiredState != null) {
        mCurrentState = mDesiredState; 
        mDesiredState = null; // Reset our desired state
      }
    }
    //cycles through enum states
    switch(mCurrentState)
    {
    case KICKING:
      isKicking(pNow);
      break;
    case RESET:
      solenoidKicker.set(mCurrentState.mKick.kicker);
      solenoidGrabber.set(mCurrentState.mGrab.grabber);
      break;
    case GRAB_CUBE:
    default:
      solenoidKicker.set(mCurrentState.mKick.kicker);
      solenoidGrabber.set(mCurrentState.mGrab.grabber);
      break;
    }
    log.debug(String.format("State: %s Scheduled: %s Desired State: %s", mCurrentState.toString(), isScheduled, mDesiredState));
    return false;
  } 

  private void isKicking(double pNow)
  {
    //if the kick sequence is not scheduled to start, set the boolean value to true and record the start time
    if(!isScheduled)
    {
      releaseTime = pNow + RELEASE_DELAY;
      resetTime = pNow + RESET_DELAY;
      isScheduled = true;
    }

    solenoidKicker.set(KickerState.KICK_EXTEND.kicker);
    
    //if the kick sequence is scheduled to start, wait the specified amount of time and then kick
    if(pNow >= releaseTime)
    {
      solenoidGrabber.set(GrabberState.RELEASE_RETRACT.grabber);
    }
    //after kicking, wait the specified amount of time to allow for release and then reset
    if(pNow >= resetTime)
    {
      reset();
    }
  }
  //set the current state to no cube, reset the start time of the kick sequence
  //to 0, and make sure that the kick sequence is not scheduled to start
  private void reset()
  {
    setCurrentState(CarriageState.RESET);
    releaseTime = 0d;
    resetTime = 0d;
    isScheduled = false;
    //undo kick and release
  }
  //verify that the beamBreak is functioning
  public boolean getBeamBreak()
  {
    boolean returnVal = false;

    if(beamBreak != null) {
      returnVal = beamBreak.get(); 
    }

    return returnVal;
  }

  public CarriageState getCurrentState() {
    return mCurrentState;
  }
  
}
