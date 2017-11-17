package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.modules.Module;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexSender;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;

public class Robot extends SampleRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  
  // ================================
  // WPILib Objects
  // ================================
  private Joystick mDriverJoystick;
  private Joystick mOperatorJoystick;
  private PowerDistributionPanel mPDP;
  private AHRS mAHRS;
  private final AtomicBoolean mNavxReady = new AtomicBoolean(false);

  // ================================
  // Codexes
  // ================================
  private Codex<Double, ELogitech310> mDriverInput = Codex.of.thisEnum(ELogitech310.class);
  private Codex<Double, ELogitech310> mOperatorInput = Codex.of.thisEnum(ELogitech310.class);
  private Codex<Double, EPowerDistPanel> mPowerDist = Codex.of.thisEnum(EPowerDistPanel.class);
  private Codex<Double, ENavX> mNavxData = Codex.of.thisEnum(ENavX.class);
  
  private CodexSender mCodexSender = new CodexSender();

  private static long CONTROL_LOOP_PERIOD_MS = 20;
  
  private boolean mLastTrigger = false;
  private boolean mLastBtn2 = false;
  
  private List<Module> mRunningModules;

  public Robot() {
  }

  public void robotInit() {
    System.out.println(System.currentTimeMillis() + " INIT");
    mAHRS = new AHRS(SerialPort.Port.kMXP);
    mDriverJoystick = new Joystick(0);
    mOperatorJoystick = new Joystick(1);
    mPDP = new PowerDistributionPanel();
    
    mRunningModules = new ArrayList<>();
    
    new Thread(() -> {
      while(mAHRS.isCalibrating()) {
        pauseForOneLoop();
      }
      mNavxReady.set(true);
      System.out.println(System.currentTimeMillis() + " NAVX Calibrated");
    }).start();
    
    new Thread(() -> {
      mCodexSender.initConnection(
          SystemSettings.CODEX_DATA_PROTOCOL, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, 
          SystemSettings.ROBOT_CODEX_DATA_SENDER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_HOST);
    }).start();
  }

  public void autonomous() {
    System.out.println("AUTONOMOUS");
    setRunningModules();
    long start = 0;
    while(isEnabled() && isAutonomous()) {
      start = System.nanoTime();
      
      if(mNavxReady.get()) {
        updateRunningModules();
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(start);
    }
  }

  public void operatorControl() {
    System.out.println("TELEOP");
    setRunningModules();
    long start = 0;
    Logger.setLevel(ELevel.DEBUG);
    
    while(isEnabled() && isOperatorControl()) {
      start = System.nanoTime();
      
      time();
      updateRunningModules();
//      Utils.print(mNavxData);
      pauseUntilTheNextCycle(start);
    }
  }
  
  private void mapInputs() {
    ELogitech310.map(mDriverInput, mDriverJoystick, null, false);
  }

  private void mapSensors() {
    ENavX.map(mNavxData, mAHRS);
//    EPowerDistPanel.map(mPowerDist, mPDP);
  }
  
  private void pauseUntilTheNextCycle(long pCycleStart) {
    long sleepTime = Math.max(CONTROL_LOOP_PERIOD_MS - (System.nanoTime()-pCycleStart)/1000000, 1);
    if(sleepTime <= 1) {
      CONTROL_LOOP_PERIOD_MS++;
      mLog.warn("Increased period to " + CONTROL_LOOP_PERIOD_MS);
    }
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  private void pauseForOneLoop() {
    try {
      Thread.sleep(CONTROL_LOOP_PERIOD_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  private long count = 0;
  private long total = 0;
  private final void time() {
    count++;
    long start = System.nanoTime();
    mapInputs();
    mapSensors();
    total += (System.nanoTime() - start);
    if(count > 100) {
      System.out.println("Over 100 cycles, action took  " + (total / count / 1000) + "us per read");
      count = 0;
      total = 0;
    }
  }

  private void initializeRunningModules() {
	  for(Module m : mRunningModules) {
		  m.initialize();
	  }
  }
  
  private void setRunningModules(Module...modules) {
	 mRunningModules.clear();
	 for(Module m : mRunningModules) mRunningModules.add(m);
	 initializeRunningModules();
  }
  
  private void updateRunningModules() {
	  for(Module m : mRunningModules) m.update();
  }
  
  public void test() {
    System.out.println("TEST");
  }

  public void disabled() {
    System.out.println("DISABLED");
  }
}