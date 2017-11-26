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
import org.ilite.frc.robot.types.EDriveTrain;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;

import com.flybotix.hfr.codex.CodexSender;
import com.flybotix.hfr.util.lang.EnumUtils;
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
  private double mCurrentTime = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
  private final java.util.Timer mTimer = new java.util.Timer("Robot Alarms and Delays");
  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
  private CodexSender mCodexSender = new CodexSender();

  private static long INPUT_LOOP_PERIOD_MS = 20;
  
//  private final CodexNetworkTables nt = CodexNetworkTables.getInstance();
  
  private final ControlLoop mControlLoop;
  private Queue<Command> mCommandQueue = new LinkedList<>();
  private Command mCurrentCommand;
  
  // Temporary...
//  private final DriveTrain dt;
//  private final DriverControlSplitArcade drivetraincontrol;

  public Robot() {
    mControlLoop = new ControlLoop(mData, mHardware);
//    dt = new DriveTrain(mData);
//    drivetraincontrol = new DriverControlSplitArcade(mData, dt);
    Logger.setLevel(ELevel.WARN);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
    mHardware.init(
        mExecutor,
        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
        new PowerDistributionPanel(), 
        new AHRS(SerialPort.Port.kMXP)
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );
    
//    mExecutor.execute(() -> {
      mCodexSender.initConnection(
          SystemSettings.CODEX_DATA_PROTOCOL, 
          SystemSettings.ROBOT_CODEX_DATA_SENDER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, 
          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_HOST);
      mLog.info("Finished initializing protocol " + SystemSettings.CODEX_DATA_PROTOCOL);
//    });
    
//    NetworkTable.setUpdateRate(INPUT_LOOP_PERIOD_MS);
//    NetworkTable.initialize();
//    nt.registerCodex(ELogitech310.class);
//    nt.registerCodex(ENavX.class);
//    nt.registerCodex(EPowerDistPanel.class);
    
    
// TODO - monitoring current is time-instensive (due to waits) so re-enabled this once
// Codex thread safety is resolved
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
    
    while(isEnabled() && isAutonomous()) {
      if(mHardware.isNavXReady()) {
        if(!updateCommandQueue()) break; //Break out of auto if there are no available commands
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(mCurrentTime);
    }
  }

  public void operatorControl() {
    mLog.info("TELEOP");
    mControlLoop.setRunningModules();
    mControlLoop.start();
    
    while(isEnabled() && isOperatorControl()) {
      mCurrentTime = Timer.getFPGATimestamp();
      mData.resetAll(mCurrentTime);
      mapInputs();
//      dt.update(mCurrentTime);
      mCodexSender.send(mData.driverinput);
      mCodexSender.send(mData.drivetrain);
      

      Utils.time(() -> EPowerDistPanel.map(mData.pdp, mHardware.getPDP()), "PDP Read of 16 channels");
      
      ENavX.map(mData.navx, mHardware.getNavX());
      mCodexSender.send(mData.navx);
      
      pauseUntilTheNextCycle(mCurrentTime);
    }
  }
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   */
  private void mapInputs() {
    ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), null, false);
//    nt.send(mData.driverinput);
    
    // Any input processing goes here, such as 'split arcade driver'
//    drivetraincontrol.update();
    
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
  }
  
  private void pauseUntilTheNextCycle(double pCyleStartTime) {
    double now = Timer.getFPGATimestamp();
    long sleepTime = INPUT_LOOP_PERIOD_MS;
    if(isEnabled()) {
      sleepTime = (long) Math.max(INPUT_LOOP_PERIOD_MS - (pCyleStartTime-now)/1e3d, 1);
    }
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
}