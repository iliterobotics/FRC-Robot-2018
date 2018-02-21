package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Carriage implements IModule{

  public Solenoid solenoidGrabber, solenoidKicker;
  public Hardware mHardware;
  private double kickTimer;
  private static final double KICK_DELAY = .02;
  private static final double RELEASE_DELAY = .01;
  private Data mData;
  private boolean isScheduled;
  private static DigitalInput beamBreak;
  private CarriageState currentState;
  private double kickStartTime;
  
  private static final ILog log = Logger.createLog(Carriage.class);
    
  
  
  public Carriage(Data pData, Hardware pHardware)
  {
    mData = pData;
    mHardware = pHardware;
    isScheduled = false;
    solenoidGrabber = new Solenoid(SystemSettings.CARRIAGE_GRABBER_ID);
    solenoidKicker = new Solenoid(SystemSettings.CARRIAGE_KICKER_ID);
    setNoCube();
    currentState = CarriageState.CUBE;
  }
  
  public enum CarriageState
  {
    NOCUBE,
    CUBE,
    KICKING;
  }
  
  @Override
  public void shutdown(double pNow) {
    
    
  }
  @Override
  public void initialize(double pNow) {
    isScheduled = false;
    beamBreak = mHardware.getCarriageBeamBreak();
    currentState = CarriageState.CUBE;
  }
  @Override
  public boolean update(double pNow)
  {
    log.debug(currentState.toString());
    
    switch(currentState)
    {
    case CUBE:
    setHaveCube();
    if(mData.driverinput.isSet(ELogitech310.DPAD_LEFT) || isScheduled)
    {
      currentState = CarriageState.KICKING;
    }
    break;
    
    case KICKING:
      isKicking(pNow);
    break;
    
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
    if(!isScheduled)
    {
      kickStartTime = pNow;
      isScheduled = true;
    }
    else
    {
      if((pNow - kickStartTime) >= KICK_DELAY)
      {
        solenoidKicker.set(true);
      }
      if((pNow - kickStartTime) >= RELEASE_DELAY)
      {
        reset();
      }
    }
  }
  public void reset()
  {
    setNoCube();
    currentState = CarriageState.NOCUBE;
    kickStartTime = 0;
    isScheduled = false;
    //undo kick and release
  }
  
  public boolean getBeamBreak()
  {
    boolean returnVal = false;
    
    if(beamBreak != null) {
      returnVal = beamBreak.get(); 
    }
    
    return returnVal;
  }
  
  public void setHaveCube()
  {
    solenoidGrabber.set(false);
    solenoidKicker.set(false);
  }
  
  public void setNoCube()
  {
    solenoidGrabber.set(true);
    solenoidKicker.set(false);
  }
  
  public void kick() {
    currentState = CarriageState.KICKING;
  }
  
}
