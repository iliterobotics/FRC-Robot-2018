package org.ilite.frc.robot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.math.AverageLong;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.ctre.CANTalon;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexMetadata;
import com.flybotix.hfr.codex.CodexSender;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TalonSRX;

public class Robot extends SampleRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private final long mRobotStartUS;
  private long mCurrentTimeNanos = 0;
  
  // ================================
  // WPILib Objects
  // ================================
  private Joystick mDriverJoystick;
  private Joystick mOperatorJoystick;
  private PowerDistributionPanel mPDP;
  private AHRS mAHRS;
  private final AtomicBoolean mNavxReady = new AtomicBoolean(false);
  private CANTalon[] mTalons = new CANTalon[4];
  private final ScheduledExecutorService mScheduledTasks = Executors.newScheduledThreadPool(mTalons.length + 1);
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
    

  // ================================
  // Codexes
  // ================================
  public class Data {
    private Codex<Double, ELogitech310> driver = Codex.of.thisEnum(ELogitech310.class);
    private Codex<Double, ELogitech310> operator = Codex.of.thisEnum(ELogitech310.class);
    private Codex<Double, EPowerDistPanel> pdp = Codex.of.thisEnum(EPowerDistPanel.class);
    private Codex<Double, ENavX> navx = Codex.of.thisEnum(ENavX.class);
    private Codex<Double, ETalonSRX>[] talons = new Codex[mTalons.length];
    private Data() {}
  }
  
  private final Data mData = new Data();
  
  private CodexSender mCodexSender = new CodexSender();

  private static long CONTROL_LOOP_PERIOD_MS = 20;
  
  private boolean mLastTrigger = false;
  private boolean mLastBtn2 = false;
  private final AverageLong timeAverage = new AverageLong(100);

  public Robot() {
    mRobotStartUS = System.nanoTime();
    Logger.setLevel(ELevel.WARN);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
    mAHRS = new AHRS(SerialPort.Port.kMXP);
    mDriverJoystick = new Joystick(0);
    mOperatorJoystick = new Joystick(1);
    mPDP = new PowerDistributionPanel();
    
    for(int i = 0; i < mTalons.length; i++) {
      mTalons[i] = new CANTalon(i + 1);
      
      //TODO - modify codexes to allow a key input as an option to the 'thisEnum' method.
      mData.talons[i] = Codex.of.thisEnum(ETalonSRX.class, i+1, true);
      CodexMetadata<ETalonSRX> meta = new CodexMetadata<>(ETalonSRX.class, 0, 0, i+1);
      mData.talons[i].setMetadata(meta);
      
      schedule(new CANTalonReader(mTalons[i], mData.talons[i]));
    }
    
    mExecutor.execute(() -> {
      while(mAHRS.isCalibrating()) {
        pauseForOneLoop();
      }
      mNavxReady.set(true);
      mLog.info(System.currentTimeMillis() + " NAVX Calibrated");
    });
    
    mExecutor.execute(() -> {
      mCodexSender.initConnection(
          SystemSettings.CODEX_DATA_PROTOCOL, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, 
          SystemSettings.ROBOT_CODEX_DATA_SENDER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_HOST);
    });
    
    pauseForOneLoop();
    
    schedule(() -> {
      // This particular task is just to read the PDP for posterity.  We aren't
      // using it at the moment since we have the CANTalon class.
      EPowerDistPanel.map(mData.pdp, mPDP);
      mData.pdp.meta().setTimeNanos(mCurrentTimeNanos);
      mCodexSender.send(mData.pdp);
    });
  }
  
  private void schedule(Runnable pTask) {
    mScheduledTasks.scheduleAtFixedRate(
      pTask, 
      (long)(CONTROL_LOOP_PERIOD_MS * Math.random()), 
      CONTROL_LOOP_PERIOD_MS, 
      TimeUnit.MILLISECONDS
    );
  }

  public void autonomous() {
    mLog.info("AUTONOMOUS");
    long start = 0;
    while(isEnabled() && isAutonomous()) {
      mCurrentTimeNanos = System.nanoTime() - mRobotStartUS;
      mapInputs();
      mapSensors();
      
      if(mNavxReady.get()) {
        
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(start);
    }
  }

  public void operatorControl() {
    mLog.info("TELEOP");
    timeAverage.addRolloverListener(() -> {
      mLog.debug("Over 100 cycles, action took  " + (timeAverage.getAverage() / 1000) + "us per cycle");
    });
    long start = 0;
    
    while(isEnabled() && isOperatorControl()) {
      start = System.nanoTime();
      
      time();
      for(int i = 0; i < mTalons.length; i++) {
        mCodexSender.send(mData.talons[i]);
      }
      pauseUntilTheNextCycle(start);
    }
  }
  
  private void mapInputs() {
    mData.driver.meta().setTimeNanos(mCurrentTimeNanos);
    ELogitech310.map(mData.driver, mDriverJoystick, null, false);
  }

  private void mapSensors() {
    ENavX.map(mData.navx, mAHRS, mCurrentTimeNanos);
  }
  
  private void pauseUntilTheNextCycle(long pCycleStart) {
    long sleepTime = Math.max(CONTROL_LOOP_PERIOD_MS - (System.nanoTime()-pCycleStart)/1000000, 1);
    if(sleepTime <= 1) {
      CONTROL_LOOP_PERIOD_MS++;
      // When this happens, it's often because the control loop thread is being used to read
      // many sensors at once.  Sensors need a period of time to do the sensing, which means
      // that a thread is in the WAIT state for a while.  If it spends too much time waiting,
      // then the total loop time increases.
      mLog.warn("Increased period to " + CONTROL_LOOP_PERIOD_MS + "ms");
    }
    edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
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
  
  private final void time() {
    long start = System.nanoTime();
    mapInputs();
    mapSensors();
    timeAverage.add(System.nanoTime() - start);
  }

  public void test() {
    mLog.info("TEST");
  }

  public void disabled() {
    mLog.info("DISABLED");
  }
  
  private class CANTalonReader implements Runnable{
    private final CANTalon mCANTalon;
    private final Codex<Double, ETalonSRX> mCodex;
    private CANTalonReader(CANTalon pTalon, Codex<Double, ETalonSRX> pCodex) {
      mCANTalon = pTalon;
      mCodex = pCodex;
    }
  

    @Override
    public void run() {
      if(isEnabled()) {
        ETalonSRX.map(mCodex, mCANTalon);
      } else {
        mCodex.reset();
      }
      mCodex.meta().setTimeNanos(mCurrentTimeNanos);
    }
  }
}