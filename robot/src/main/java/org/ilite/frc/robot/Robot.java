package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.robot.commands.Command;
import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.math.AverageDouble;
import org.ilite.frc.robot.math.AverageLong;
import org.ilite.frc.robot.modules.Module;
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
import edu.wpi.first.wpilibj.Timer;

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

  private static long INPUT_LOOP_PERIOD_MS = 20;
  
  private boolean mLastTrigger = false;
  private boolean mLastBtn2 = false;
  private final AverageDouble timeAverage = new AverageDouble(100);
  
  private List<Module> mRunningModules;
  private Queue<Command> mCommandQueue;
  private Command mCurrentCommand;

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
    
    mRunningModules = new ArrayList<>();
    mCommandQueue = new LinkedList<>();

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
    
//    schedule(() -> {
//      // This particular task is just to read the PDP for posterity.  We aren't
//      // using it at the moment since we have the CANTalon class.
//      EPowerDistPanel.map(mData.pdp, mPDP);
//      mData.pdp.meta().setTimeNanos(mCurrentTimeNanos);
//      mCodexSender.send(mData.pdp);
//    });
  }
  
  private void schedule(Runnable pTask) {
    mScheduledTasks.scheduleAtFixedRate(
      pTask, 
      (long)(INPUT_LOOP_PERIOD_MS * Math.random()), 
      INPUT_LOOP_PERIOD_MS, 
      TimeUnit.MILLISECONDS
    );
  }

  public void autonomous() {
    mLog.info("AUTONOMOUS");
    setRunningModules();
    
    long start = 0;
    while(isEnabled() && isAutonomous()) {
      mCurrentTimeNanos = System.nanoTime() - mRobotStartUS;
      mapInputs();
      mapSensors();
      
      if(mNavxReady.get()) {
		if(!updateCommandQueue()) break; //Break out of auto if there are no available commands
        updateRunningModules();
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(start);
    }
  }

  public void operatorControl() {
    mLog.info("TELEOP");
    timeAverage.addRolloverListener(() -> {
      mLog.debug("Over 100 cycles, action took  " + (timeAverage.getAverage() * 1000d * 1000d) + "us per cycle");
    });
    setRunningModules();
    long start = 0;
    
    while(isEnabled() && isOperatorControl()) {
      start = System.nanoTime();
      
      time();
      
      updateRunningModules();
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
    long sleepTime = Math.max(INPUT_LOOP_PERIOD_MS - (System.nanoTime()-pCycleStart)/1000000, 1);
    if(sleepTime <= 1) {
      INPUT_LOOP_PERIOD_MS++;
      // When this happens, it's often because the control loop thread is being used to read
      // many sensors at once.  Sensors need a period of time to do the sensing, which means
      // that a thread is in the WAIT state for a while.  If it spends too much time waiting,
      // then the total loop time increases.
      mLog.warn("Increased period to " + INPUT_LOOP_PERIOD_MS + "ms");
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
      Thread.sleep(INPUT_LOOP_PERIOD_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  private final void time() {
    mapInputs();
    mapSensors();
    double start = Timer.getFPGATimestamp();
    for(int i = 0; i < mTalons.length; i++) {
      mCodexSender.send(mData.talons[i]);
    }
    timeAverage.add(Timer.getFPGATimestamp() - start);
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
    double now = Timer.getFPGATimestamp();
	  for(Module m : mRunningModules) {
	    m.update(now);
	  }
  }
  
  /**
   * 
   * @return Whether there is another available autonomous command to execute
   */
  private boolean updateCommandQueue() {
	  //Grab the next command
	  mCurrentCommand = mCommandQueue.peek();
	  if(mCurrentCommand != null){
		mCurrentCommand.initialize();
		//If this command is finished executing
		if(mCurrentCommand.update()) mCommandQueue.poll(); //Discard the command and initialize the next one
	    if(mCommandQueue.peek() != null) return true;
	  }
	  return false;
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