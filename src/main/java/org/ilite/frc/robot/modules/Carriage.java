package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DigitalInput;
import wrappers.IDigitalInput;
import wrappers.ISolenoid;
import wrappers.SolenoidWrapper;

public class Carriage implements IModule{

  public ISolenoid solenoidGrabber, solenoidKicker;
  public IDigitalInput beamBreak;
  public Hardware mHardware;
  private double kickTimer;
  private static final double KICK_DELAY = .02;
  private static final double RELEASE_DELAY = .01;
  private Data mData;
  private boolean isScheduled;
  private CarriageState currentState;
  private GrabberState grabberState;
  private KickerState kickerState;
  private double kickStartTime;
  
  private static final ILog log = Logger.createLog(Carriage.class);
    
  //constructs necessary variables and sets default state to cube
  public Carriage(Data pData, Hardware pHardware)
  {
    mData = pData;
    mHardware = pHardware;
    isScheduled = false;
    solenoidGrabber = new Solenoid(SystemSettings.CARRIAGE_GRABBER_ID);
    solenoidKicker = new Solenoid(SystemSettings.CARRIAGE_KICKER_ID);
    setHaveCube();
    currentState = CarriageState.CUBE;
    grabberState = GrabberState.ISGRABBING;
    kickerState = KickerState.ISNOTKICKING;
  }
  
  //creates new enum with states for no cube, cube, and kicking
  public enum CarriageState
  {
    NOCUBE,
    CUBE,
    KICKING;
  }
  
  public enum GrabberState
  { 
    ISGRABBING(false),
    ISNOTGRABBING(true);
    
    private boolean grabber;

    private GrabberState(boolean grabber)
    {
      this.grabber = grabber;
    }
  }
  
  public enum KickerState
  {
    ISKICKING(true),
    ISNOTKICKING(false);
    
    private boolean kicker;
    
    private KickerState(boolean kicker)
    {
      this.kicker = kicker;
    }
  }
  
  @Override
  public void shutdown(double pNow) {
    
    
  }
  
//  public void setGrabberState(GrabberState grabberState)
//  {
//    if(grabberState == GrabberState.
//  }
  
  @Override
  //makes sure that the kick sequence has not started, gets the correct beamBreak, and sets the current state to cube
  public void initialize(double pNow) {
    beamBreak = mHardware.getCarriageBeamBreak();
    isScheduled = false;
    currentState = CarriageState.CUBE;
  }
  @Override
  public boolean update(double pNow)
  {
    //displays the current state
    log.debug(currentState.toString());
    
    //cycles through enum states
    switch(currentState)
    {
    case CUBE:
    setHaveCube();
    //if pressed, start kick sequence
    if(mData.driverinput.isSet(ELogitech310.DPAD_LEFT) || isScheduled)
    {
      currentState = CarriageState.KICKING;
    }
    break;
    
    case KICKING:
      isKicking(pNow);
    break;
    
    //if the beamBreak is broken, set the current state to cube
    case NOCUBE:
      if(!getBeamBreak())
      {
        currentState = CarriageState.CUBE;
        break;
      }
      setNoCube();
      break;
    }
    System.out.println(getBeamBreak());
    return false;
  } 
    
  public void isKicking(double pNow)
  {
    //if the kick sequence is not scheduled to start, set the boolean value to true and record the start time
    if(!isScheduled)
    {
      kickStartTime = pNow;
      isScheduled = true;
    }
    else
    {
      //if the kick sequence is scheduled to start, wait the specified amount of time and then kick
      if((pNow - kickStartTime) >= KICK_DELAY)
      {
        solenoidKicker.set(true);
      }
      //after kicking, wait the specified amount of time to allow for release and then reset
      if((pNow - kickStartTime) >= RELEASE_DELAY)
      {
        reset();
      }
    }
  }
  //set the current state to no cube, reset the start time of the kick sequence
  //to 0, and make sure that the kick sequence is not scheduled to start
  public void reset()
  {
    setNoCube();
    currentState = CarriageState.NOCUBE;
    kickStartTime = 0;
    isScheduled = false;
    //undo kick and release
  }
  //verify that the beamBreak is functioning
  public boolean getBeamBreak()
  {
    return beamBreak.get();
  }
  //set the solenoids to the state that they will be in when the robot holds a cube
  public void setHaveCube()
  {
    solenoidGrabber.set(false);
    solenoidKicker.set(false);
  }
//set the solenoids to the state that they will be in when the robot does not hold a cube
  public void setNoCube()
  {
    solenoidGrabber.set(true);
    solenoidKicker.set(false);
  }
}
