package org.ilite.frc.robot;

import java.util.concurrent.Executor;
import com.ctre.phoenix.CANifier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.Pigeon;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
public class Hardware {
  private ILog mLog = Logger.createLog(Hardware.class);

  private Joystick mDriverJoystick;
  private Joystick mOperatorJoystick;
  private PowerDistributionPanel mPDP;
  public final AtomicBoolean mNavxReady = new AtomicBoolean(false);
  private PigeonIMU mPigeon;
  private Pigeon mPigeonWrapper;
  private CANifier mCanifier;
  private UsbCamera mVisionCamera;
  private Data data;
  
  Hardware() {
    
  }
  
  void init(
      Executor pInitializationPool,
      Joystick pDriverJoystick,
      Joystick pOperatorJoystick,
      PowerDistributionPanel pPDP,
      CANifier pCanifier
      PigeonIMU pPigeon,
      UsbCamera pVisionCamera,
      Data data
  ) {
    mDriverJoystick = pDriverJoystick;
    mOperatorJoystick = pOperatorJoystick;
    mPDP = pPDP;
    mPigeon = pPigeon;
    mPigeonWrapper = new Pigeon(mPigeon, data, SystemSettings.PIGEON_COLLISION_THRESHOLD);
    mVisionCamera = pVisionCamera;
    mVisionCamera.setFPS(30);

//    pInitializationPool.execute(() -> {
//      while(mAHRS.isCalibrating()) {
//        try {
//          Thread.sleep(20);
//        } catch (InterruptedException e) {
//          Thread.currentThread().interrupt();
//        }
//      }
//      mNavxReady.set(true);
//      mLog.info(System.currentTimeMillis() + " NAVX Calibrated");
//    });
    mCanifier = pCanifier;
  }
  
  public Joystick getDriverJoystick() { 
    return mDriverJoystick;
  }
  
  public Joystick getOperatorJoystick() {
    return mOperatorJoystick;
  }
  
  public PowerDistributionPanel getPDP() {
    return mPDP;
  }
  
  public Pigeon getPigeon()
  {
	  return mPigeonWrapper;
  }
  
  public UsbCamera getVisionCamera() {
    return mVisionCamera;
  }

  public CANifier getCanifier()
  {
	  return mCanifier;
  }
}

