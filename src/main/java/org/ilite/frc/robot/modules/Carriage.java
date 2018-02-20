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

  Solenoid solenoidKicker, solenoidGrabberRelease;
  private double kickTimer;
  private static final double KICK_DELAY = .1;
  private static final double  RELEASE_DELAY = .2;
  private double releaseTimer;
  private double currentTime;
  private Data mData;
  private boolean isScheduled;
  private DigitalInput beamBreak;
  private carriageState currentState;
  private double kickStartTime;
  private boolean kickerState, grabberState;
  
  private static final ILog log = Logger.createLog(Carriage.class);
    
  
  
  public Carriage(Data pData, Hardware mHardware)
  {
    beamBreak = mHardware.getCarriageBeamBreak();
    mData = pData;
    isScheduled = false;
    solenoidKicker = new Solenoid(SystemSettings.SOLENOID_GRAB);
    solenoidGrabberRelease = new Solenoid(SystemSettings.SOLENOID_POP);
    //setNoCube();
    currentState = carriageState.CUBE;
  }
  
  public enum carriageState
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
//    setHaveCube();
    kickerState = false;
    grabberState = false;
    solenoidKicker.set(kickerState);
    solenoidGrabberRelease.set(grabberState);
  }
  
  //kicker true = out
  //grabber true = closed
  @Override
  public boolean update(double pNow)
  {
    log.debug(currentState.toString());
    currentTime = pNow;
   //When we get the cube/beam break breaks
    if(mData.operator.isSet(ELogitech310.A_BTN))
    {
      solenoidGrabberRelease.set(false);
      solenoidKicker.set(false);
    }
    //switch
    else if (mData.operator.isSet(ELogitech310.B_BTN))
    {
      solenoidGrabberRelease.set(true);
      solenoidKicker.set(true);
    }  
    //scale
    else if(mData.operator.isSet(ELogitech310.Y_BTN))
    {
      solenoidGrabberRelease.set(true);
      solenoidKicker.set(false); 
    }
    //kicker should not kick unless button is clicked
    else
    {
      solenoidKicker.set(false);
    }
    System.out.println(beamBreak.get());
//    switch(currentState)
//    {
//    case CUBE:
//    setHaveCube();
//    if(mData.operator.isSet(ELogitech310.DPAD_LEFT) || isScheduled)
//    {
//      kickStartTime = pNow;
//      currentState = carriageState.KICKING;
//    }
//    break;
//    
//    case KICKING:
//      isKicking(pNow);
//    break;
//    
//    case NOCUBE:
//      setNoCube();
//      if(getBeamBreak() || mData.operator.isSet(ELogitech310.A_BTN))
//      {
//        currentState = carriageState.CUBE;
//      }
//    }
//    System.out.println(beamBreak.get());
    return false;
  } 
    
//  public void isKicking(double pNow)
//  {
//    if(!isScheduled)
//    {
//      kickTimer = pNow + KICK_DELAY;
//      releaseTimer = pNow + RELEASE_DELAY;
//      isScheduled = true;
//    }
//    else
//    {
//      log.debug("CurrTime: " + currentTime + " kickStartTime= " + kickStartTime + " kickTimer= " + kickTimer + " releaseTime= " + releaseTimer);
//      if((currentTime - kickStartTime) >= kickTimer)
//      {
//        //kick
//        solenoidGrabberRelease.set(false);
//      }
//      if((currentTime - kickStartTime) >= releaseTimer)
//      {
//        //release
//        setNoCube();
//        reset();
//        currentState = carriageState.NOCUBE;
//        kickStartTime = 0;
//      }
//    }
//    
//  }
//  public void reset()
//  {
//    setNoCube();
//    //undo kick and release
//  }
//  
//  public boolean getBeamBreak()
//  {
//    boolean returnVal = false;
//    
//    if(beamBreak != null) {
//    returnVal = beamBreak.get(); 
//    }
//    
//    return returnVal;
//    
//  }
//  
//  private void setHaveCube()
//  {
//    solenoidKicker.set(true);
//    solenoidGrabberRelease.set(false);
//  }
//  
//  private void setNoCube()
//  {
//    solenoidKicker.set(false);
//    solenoidGrabberRelease.set(true);
//  }
//  
//  public void kick() {
//    currentState = carriageState.KICKING;
//  }
	
}
