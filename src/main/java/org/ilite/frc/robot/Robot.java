package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.ENavX;
import org.ilite.frc.robot.commands.Command;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControlSplitArcade;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.vision.GripPipeline;
import org.ilite.frc.robot.vision.Processing;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.flybotix.hfr.codex.CodexSender;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Robot extends SampleRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private double mCurrentTime = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(2);
  private final java.util.Timer mTimer = new java.util.Timer("Robot Alarms and Delays");
  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
  private CodexSender mCodexSender = new CodexSender();

  private static long INPUT_LOOP_PERIOD_MS = 20;
  
//  private final CodexNetworkTables nt = CodexNetworkTables.getInstance();
  
  private final ControlLoopManager mControlLoop;
  
  private List<IModule> mRunningModules = new LinkedList<>();
  private Queue<Command> mCommandQueue = new LinkedList<>();
  private Command mCurrentCommand;
  
  private VisionThread visionThread;
  
  // Temporary...
  private final DriveTrain dt;
  private final DriverControlSplitArcade drivetraincontrol;

  public Robot() {
    mControlLoop = new ControlLoopManager(mData, mHardware);
    dt = new DriveTrain(mData);
    drivetraincontrol = new DriverControlSplitArcade(mData, dt);
    Logger.setLevel(ELevel.WARN);
  }
  
  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
    
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setFPS(30);
//    camera.setResolution(320, 240);
    GripPipeline pipeline = new GripPipeline();
    Processing processing = new Processing(camera);
    visionThread = new VisionThread(camera, pipeline, processing);
    visionThread.start();
//    while(visionThread.isAlive()) System.out.println("Vision started");
    
//    trackingCamera.setBrightness(0);
//    trackingCamera.setExposureManual(0);
//    trackingCamera.setFPS(0);
//    trackingCamera.setPixelFormat(PixelFormat.kBGR);
//    trackingCamera.setResolution(0, 0);
//    trackingCamera.setWhiteBalanceManual(0);
    
//    mHardware.init(
//        mExecutor,
//        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
//        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
//        new PowerDistributionPanel(), 
//        new AHRS(SerialPort.Port.kMXP)
//        // Sensors
//        // Custom hw
//        // Spike relays
//        // etc
//        
//        // Talons TBD ... they're somewhat picky.
//    );
    
//    mExecutor.execute(() -> {
//      mCodexSender.initConnection(
//          SystemSettings.CODEX_DATA_PROTOCOL, 
//          SystemSettings.ROBOT_CODEX_DATA_SENDER_PORT, 
//          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, 
//          SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_HOSTS);
//      mLog.info("Finished initializing protocol " + SystemSettings.CODEX_DATA_PROTOCOL);
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
	setRunningModules();
    mControlLoop.setRunningControlLoops();
    mControlLoop.start();
    
    while(isEnabled() && isAutonomous()) {
    	updateCommandQueue(true);
      if(mHardware.isNavXReady()) {
        updateCommandQueue(false);
        updateRunningModules();
      } else {
        mLog.warn("NavX data is not ready, skipping auton for 1 cycle");
      }
      
      pauseUntilTheNextCycle(mCurrentTime);
    }
  }

  public void operatorControl() {
    mLog.info("TELEOP");
    // Remember that DriverControl classes don't go here. They aren't Modules.
	setRunningModules();
	mControlLoop.setRunningControlLoops();
    mControlLoop.start();
    
    while(isEnabled() && isOperatorControl()) {
      mCurrentTime = Timer.getFPGATimestamp();
      mData.resetAll(mCurrentTime);
      mapInputs();
      
      updateRunningModules();
      
      mCodexSender.send(mData.driverinput);
      mCodexSender.send(mData.drivetrain);
      ENavX.map(mData.navx, mHardware.getNavX());
      mCodexSender.send(mData.navx);
      
      pauseUntilTheNextCycle(mCurrentTime);
    }
  }
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputs() {
    ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), null, false);
//    nt.send(mData.driverinput);
    
    // Any input processing goes here, such as 'split arcade driver'
    drivetraincontrol.update();
    
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
  }
  
  /**
   * Pauses for 20 ms. If sensor reads are taking too long, increases delay period to accommodate.
   * @param pCyleStartTime The current time from the onboard FPGA
   */
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
  private boolean updateCommandQueue(boolean firstRun) {
	  //Grab the next command
	  mCurrentCommand = mCommandQueue.peek();
	  if(mCurrentCommand != null) {
		if(firstRun) mCurrentCommand.initialize();
		//If this command is finished executing
		if(mCurrentCommand.update()) mCommandQueue.poll(); //Discard the command and initialize the next one
	    if(mCommandQueue.peek() != null) {
	    	mCommandQueue.peek().initialize();
	    	return true;
	    }
	  }
	  return false;
  }
  
  /**
   * Updates every module in the robot's list of running modules
   */
  private void updateRunningModules() {
	  for(IModule m : mRunningModules) m.update(Timer.getFPGATimestamp());
  }
  
  /**
   * Clears the list of running modules and sets new ones
   * @param modules An arbitrary amount of modules to set to run
   */
  private void setRunningModules(IModule...modules) {
	  mRunningModules.clear();
	  for(IModule m : modules) mRunningModules.add(m);
	  initializeRunningModules();
  }
  
  /**
   * Calls the initialization method of every running modules
   */
  private void initializeRunningModules() {
	  for(IModule m : mRunningModules) {
		  m.initialize(Timer.getFPGATimestamp());
	  }
  }
  
  public void test() {
    mLog.info("TEST");
  }

  public void disabled() {
    mLog.info("DISABLED");
    mControlLoop.stop();
  }
}