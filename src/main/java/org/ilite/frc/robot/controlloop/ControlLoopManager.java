package org.ilite.frc.robot.controlloop;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ENavX;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Hardware;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

/**
 * A class which uses the WPILIB Notifier mechanic to run our Modules on
 * a set time.  Tune SystemSettings.CONTROL_LOOP_PERIOD to the desired,
 * but monitor CPU usage.
 */
public class ControlLoopManager implements Runnable{
  private ILog mLog = Logger.createLog(ControlLoopManager.class);
  private final Notifier mWpiNotifier;
  private final Object mTaskLock = new Object();
  private boolean mIsRunning = false;
  private final List<IControlLoop> mControlLoops = new ArrayList<>();
  
  private final Data mData;
  private final Hardware mHardware;
  
  private double mLatestTime = 0d;
  
  public ControlLoopManager(Data pRobotData, Hardware pRobotHardware) {
    mWpiNotifier = new Notifier(this);
    mHardware = pRobotHardware;
    mData = pRobotData;
  }
  
  public synchronized void setRunningControlLoops(IControlLoop... pControlLoops) {
    mControlLoops.clear();
    for(IControlLoop c : pControlLoops) mControlLoops.add(c);
  }
  
  public synchronized void start() {
    if(!mIsRunning) {
      mLog.info("Starting control loop");
      synchronized(mTaskLock) {
        mLatestTime = Timer.getFPGATimestamp();
        for(IControlLoop c : mControlLoops) c.initialize(mLatestTime);
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
    	for(IControlLoop c : mControlLoops) c.shutdown(mLatestTime);
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
          for(IControlLoop c : mControlLoops) {
            c.loop(mLatestTime);
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
  
  private void mapSensors() {
    //TODO change timestamp to mLatestTime
//    ENavX.map(mData.navx, mHardware.getNavX());
  }
}
