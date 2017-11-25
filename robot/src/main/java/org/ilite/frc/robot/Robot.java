package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.robot.commands.Command;
import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.modules.ControlLoop;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControlSplitArcade;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.ctre.CANTalon;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexSender;
import com.flybotix.hfr.io.CodexNetworkTables;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;
import com.kauailabs.navx.frc.AHRS;
import com.team254.lib.util.MovingAverage;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends SampleRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private double mCurrentTimeNanos = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
    

  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
  private CodexSender mCodexSender = new CodexSender();

  private static long INPUT_LOOP_PERIOD_MS = 20;
  
  private final MovingAverage timeAverage = new MovingAverage(100);
  private final CodexNetworkTables nt = CodexNetworkTables.getInstance();
  NetworkTable codextable = null;
  
  private final ControlLoop mControlLoop;
  private Queue<Command> mCommandQueue = new LinkedList<>();
  private Command mCurrentCommand;
  
  // Temporary...
  private final DriveTrain dt;
  private final DriverControlSplitArcade stick;

  public Robot() {
    mControlLoop = new ControlLoop(mData, mHardware);
    dt = new DriveTrain(mData);
    stick = new DriverControlSplitArcade(mData, dt);
    Logger.setLevel(ELevel.WARN);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
    mHardware.init(
        mExecutor,
        new Joystick(0), 
        new Joystick(1), 
        new PowerDistributionPanel(), 
        new AHRS(SerialPort.Port.kMXP)
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );
    
    mExecutor.execute(() -> {
      mCodexSender.initConnection(
          SystemSettings.CODEX_DATA_PROTOCOL, 
          SystemSettings.ROBOT_CODEX_DATA_SENDER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_HOST);
      mLog.info("Finished initializing protocol " + SystemSettings.CODEX_DATA_PROTOCOL);
    });
    
    NetworkTable.setUpdateRate(INPUT_LOOP_PERIOD_MS);
    NetworkTable.initialize();
    nt.registerCodex(ELogitech310.class);
    nt.registerCodex(ENavX.class);
    nt.registerCodex(EPowerDistPanel.class);
    
    
    
//    schedule(() -> {
//      // This particular task is just to read the PDP for posterity.  We aren't
//      // using it at the moment since we have the CANTalon class.
//      EPowerDistPanel.map(mData.pdp, mPDP);
//      mData.pdp.meta().setTimeNanos(mCurrentTimeNanos);
//      mCodexSender.send(mData.pdp);
//    });
  }

  public void autonomous() {
    mLog.info("AUTONOMOUS");
    mControlLoop.setRunningModules();
    mControlLoop.start();
    
    long start = 0;
    while(isEnabled() && isAutonomous()) {
      if(mHardware.isNavXReady()) {
        if(!updateCommandQueue()) break; //Break out of auto if there are no available commands
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(start);
    }
  }

  private int count = 0;
  public void operatorControl() {
    mLog.info("TELEOP");
//    mControlLoop.setRunningModules();
//    mControlLoop.start();
    long start = 0;
    
    while(isEnabled() && isOperatorControl()) {
      start = System.nanoTime();
      mapInputs();
      dt.update(mCurrentTimeNanos);
      
//      time();
      pauseUntilTheNextCycle(start);
    }
  }
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   */
  private void mapInputs() {
    mData.driverinput.meta().setTimeNanos((long)mCurrentTimeNanos);
    ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), null, false);
    nt.send(mData.driverinput);
    
    stick.update();
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
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    mCurrentTimeNanos = Timer.getFPGATimestamp() * 1e9;
  }
  
  private final void time() {
    double start = Timer.getFPGATimestamp();
    
    for(int i = 0; i < mData.talons.size(); i++) {
      ETalonSRX.map(mData.talons.get(i), mHardware.getTalon(i));
//      mCodexSender.send(mData.talons.get(i));
    }
    EPowerDistPanel.map(mData.pdp, mHardware.getPDP());
    mData.pdp.meta().setTimeNanos((long)mCurrentTimeNanos);
    ENavX.map(mData.navx, mHardware.getNavX(), 0);
    mapInputs();
//    mData.navx.encode();
//    mData.driver.encode();
//    mData.pdp.encode();
//    mLog.info("Sending navx");
//    mCodexSender.send(mData.navx);
    mCodexSender.send(mData.driverinput);
    mCodexSender.send(mData.pdp);
    
//    nt.send(mData.navx);
//    nt.send(mData.driver);
//    nt.send(mData.pdp);
    
    timeAverage.addNumber(Timer.getFPGATimestamp() - start);
    count++;
    if(count > 100) {
      count = 0; 
      mLog.warn("Over 100 cycles, action took  " + Utils.df.format(timeAverage.getAverage() * 1000d * 1000d) + "us per cycle");
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
    mControlLoop.stop();
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
      mCodex.meta().setTimeNanos((long)mCurrentTimeNanos);
    }
  }
}