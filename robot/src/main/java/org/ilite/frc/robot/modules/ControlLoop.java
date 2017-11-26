package org.ilite.frc.robot.modules;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.ENavX;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

/**
 * A class which uses the WPILIB Notifier mechanic to run our Modules on
 * a set time.  Tune SystemSettings.CONTROL_LOOP_PERIOD to the desired,
 * but monitor CPU usage.
 */
public class ControlLoop implements Runnable{
  private ILog mLog = Logger.createLog(ControlLoop.class);
  private final Notifier mWpiNotifier;
  private final Object mTaskLock = new Object();
  private boolean mIsRunning = false;
  private final List<IModule> mModules = new ArrayList<>();
  
  private final Data mData;
  private final Hardware mHardware;
  
  private double mLatestTime = 0d;
  
  public ControlLoop(Data pRobotData, Hardware pRobotHardware) {
    mWpiNotifier = new Notifier(this);
    mHardware = pRobotHardware;
    mData = pRobotData;
  }
  
  public synchronized void setRunningModules(IModule... pModules) {
    boolean restart = mIsRunning;
    
  }
  
  public synchronized void start() {
    if(!mIsRunning) {
      mLog.info("Starting control loop");
      synchronized(mTaskLock) {
        mLatestTime = Timer.getFPGATimestamp();
        for(IModule module : mModules) {
          module.initialize(mLatestTime);
        }
        mIsRunning = true;
      }
      mWpiNotifier.startPeriodic(SystemSettings.CONTROL_LOOP_PERIOD);
    }
  }
  
  public synchronized void stop() {
    if(mIsRunning) {
      mLog.info("Stopping control loop");
      mWpiNotifier.stop();
      synchronized(mTaskLock) {
        mIsRunning = false;
        mLatestTime = Timer.getFPGATimestamp();
        for(IModule module : mModules) {
          module.shutdown(mLatestTime);
        }
      }
    }
  }

  @Override
  public void run() {
    synchronized(mTaskLock) {
      try {
        if(mIsRunning) {
          mLatestTime = Timer.getFPGATimestamp();
          mapSensors();
          for(IModule module : mModules) {
            module.update(mLatestTime);
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
  
  private void mapSensors() {
    //TODO change tiemstamp to mLatestTime
    ENavX.map(mData.navx, mHardware.getNavX());
  }
}
