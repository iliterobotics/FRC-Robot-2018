	package org.ilite.frc.robot;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.common.sensors.TalonTach;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera;
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
  private VideoCamera mVisionCamera;
  private Data data;
  private TalonTach mTalonTach;
  
  Hardware() {
    
  }
  
  void init(
      Executor pInitializationPool,
      Joystick pDriverJoystick,
      Joystick pOperatorJoystick,
      PowerDistributionPanel pPDP,
      PigeonIMU pPigeon,
      TalonTach pTalonTach,
      CANifier pCanifier,
      VideoCamera pVisionCamera
  ) {
    mDriverJoystick = pDriverJoystick;
    mOperatorJoystick = pOperatorJoystick;
    mPDP = pPDP;
    mPigeon = pPigeon;
    mPigeonWrapper = new Pigeon(mPigeon, data, SystemSettings.PIGEON_COLLISION_THRESHOLD);
    mVisionCamera = pVisionCamera;
    mVisionCamera.setFPS(30);
    mTalonTach = pTalonTach;

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
  
  public VideoCamera getVisionCamera() {
    return mVisionCamera;
  }
  public TalonTach getTalonTach()
  {
	  return mTalonTach;
  }

  public CANifier getCanifier()
  {
	  return mCanifier;
  }
}

