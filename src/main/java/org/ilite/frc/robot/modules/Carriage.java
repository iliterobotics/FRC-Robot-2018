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

  Solenoid solenoidGrab, solenoidPop;
  private double kickTimer;
  private static final int KICK_DELAY = 1;
  private static final int RELEASE_DELAY = 5;
  private double releaseTimer;
  private double currentTime;
  private Data mData;
  private boolean isScheduled;
  private static DigitalInput beamBreak;
  private CarriageState currentState;
  private double kickStartTime;
  
  private static final ILog log = Logger.createLog(Carriage.class);
    
  private Intake intake;
  
  public Carriage(Data pData, Hardware mHardware, Intake pIntake)
  {
    Carriage.beamBreak = mHardware.getCarriageBeamBreak();
    mData = pData;
    intake = pIntake;
    isScheduled = false;
    solenoidGrab = new Solenoid(SystemSettings.SOLENOID_GRAB);
    solenoidPop = new Solenoid(SystemSettings.SOLENOID_POP);
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
    setHaveCube();
  }
  
  @Override
  public boolean update(double pNow)
  {
    log.debug(currentState.toString());
    currentTime = pNow;
    
    if(intake.getAveragePower() > 0 && !getBeamBreak()) setState(CarriageState.NOCUBE);
    
    switch(currentState)
    {
    case CUBE:
    setHaveCube();
    if(mData.driverinput.isSet(ELogitech310.DPAD_LEFT) || isScheduled)
    {
      kickStartTime = pNow;
      currentState = CarriageState.KICKING;
    }
    break;
    
    case KICKING:
      isKicking(pNow);
    break;
    
    case NOCUBE:
      setNoCube();
      if(getBeamBreak())
      {
        currentState = CarriageState.CUBE;
      }
    }
    
    return false;
  } 
    
  public void isKicking(double pNow)
  {
    if(!isScheduled)
    {
      kickTimer = pNow + KICK_DELAY;
      releaseTimer = pNow + RELEASE_DELAY;
      isScheduled = true;
    }
    else
    {
      log.debug("CurrTime: " + currentTime + " kickStartTime= " + kickStartTime + " kickTimer= " + kickTimer + " releaseTime= " + releaseTimer);
      if((currentTime - kickStartTime) >= kickTimer)
      {
        //kick
      }
      if((currentTime - kickStartTime) >= releaseTimer)
      {
        //release
        setNoCube();
        reset();
        currentState = carriageState.NOCUBE;
        kickStartTime = 0;
      }
    }
  }
  public void reset()
  {
    setNoCube();
    currentState = CarriageState.NOCUBE;
    kickStartTime = 0;
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
  
  private void setHaveCube()
  {
    solenoidGrab.set(true);
    solenoidPop.set(false);
  }
  
  private void setNoCube()
  {
    solenoidGrab.set(false);
    solenoidPop.set(true);
  }
  
  public boolean isGrabbing() {
    return solenoidGrab.get();
  }
  
  public boolean isKicking() {
    return solenoidPop.get();
  }
  
  public void setState(CarriageState state) {
    currentState = state;
  }
	
}
