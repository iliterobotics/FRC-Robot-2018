package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControl;
import org.ilite.frc.robot.modules.ElevatorModule;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.vision.GripPipeline;
import org.ilite.frc.robot.vision.Processing;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Robot extends IterativeRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private double mCurrentTime = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
  private final ControlLoopManager mControlLoop;
  
  private List<IModule> mRunningModules = new LinkedList<>();
  private final GetAutonomous getAutonomous;
  private Queue<ICommand> mCommandQueue;
  private ICommand mCurrentCommand;
  
  private VisionThread visionThread;
  private GripPipeline pipeline;
  private Processing processing;
  
  // Temporary...
  private final Intake intake;
  private final ElevatorModule elevator;
  private final DriveTrain dt;
  private final DriverControl drivetraincontrol;

  public Robot() {
    elevator = new ElevatorModule();
    intake = new Intake(elevator);
  	mControlLoop = new ControlLoopManager(mData, mHardware);
  	drivetraincontrol = new DriverControl(mData, intake, elevator);
  	dt = new DriveTrain(drivetraincontrol);
  	getAutonomous = new GetAutonomous(SystemSettings.AUTON_TABLE);
  	Logger.setLevel(ELevel.INFO);
  }
  
  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
       mHardware.init(
        mExecutor,
        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
        new PowerDistributionPanel(), 
        new PigeonIMU(SystemSettings.PIGEON_DEVICE_ID),
        CameraServer.getInstance().startAutomaticCapture()
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );
       
    pipeline = new GripPipeline();
    processing = new Processing(mHardware.getVisionCamera());
    visionThread = new VisionThread(mHardware.getVisionCamera(), pipeline, processing);
    try {
    	visionThread.start();
    } catch (Exception e) {
    	System.err.println("Vision Thread Error");
    }
//    while(visionThread.isAlive()) System.out.println("Vision started");
    
//    trackingCamera.setBrightness(0);
//    trackingCamera.setExposureManual(0);
//    trackingCamera.setFPS(0);
//    trackingCamera.setPixelFormat(PixelFormat.kBGR);
//    trackingCamera.setResolution(0, 0);
//    trackingCamera.setWhiteBalanceManual(0);
    
  }

  public void autonomousInit() {
    System.out.println("Default autonomousInit() method... Overload me!");
    mCommandQueue = getAutonomous.getAutonomousCommands();
    mLog.info("AUTONOMOUS");
    mHardware.getPigeon().zeroAll();
  }
  public void autonomousPeriodic() {
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    //mControlLoop.setRunningControlLoops();
    //mControlLoop.start();
    
    //TODO put updateCommandQueue into autoninit
    updateCommandQueue(true);
    updateRunningModules();
      
  }
  
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	  setRunningModules(dt, drivetraincontrol, intake);
	  initializeRunningModules();
	  mHardware.getPigeon().zeroAll();
	  
	  mControlLoop.setRunningControlLoops();
	  mControlLoop.start();
  }

  public void teleopPeriodic() {
    // Remember that DriverControl classes don't go here. They aren't Modules.
    
      mCurrentTime = Timer.getFPGATimestamp();
//      mData.resetAll(mCurrentTime);
      mapInputsAndCachedSensors();
      updateRunningModules();
    }
  
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputsAndCachedSensors() {
    ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), 1.0, false);
    ELogitech310.map(mData.operator, mHardware.getOperatorJoystick(), 1.0, false);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
    EPigeon.map(mData.pigeon, mHardware.getPigeon(), mCurrentTime);
    ECubeTarget.map(mData.vision, processing);
    SystemUtils.writeCodexToSmartDashboard(mData.pigeon);
    SystemUtils.writeCodexToSmartDashboard(mData.vision);
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
	    if(mCurrentCommand.update()) {
	      mCommandQueue.poll(); //Discard the command and initialize the next one
	    }
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
	  for(IModule m : mRunningModules) {
		  m.update(mCurrentTime);
	  }
	  
  }
  
  /**
   * Clears the list of running modules and sets new ones
   * @param modules An arbitrary amount of modules to set to run
   */
  private void setRunningModules(IModule...modules) {
	  mRunningModules.clear();
	  for(IModule m : modules) {
	    mRunningModules.add(m);
	  }
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

  public void disabledInit() {
	  mLog.info("DISABLED");
	  mControlLoop.stop();
  }
  
  public void disabledPeriodic() {
	  System.out.println("Getting autonomous...");
	  getAutonomous.getAutonomousCommands();
	  Timer.delay(1);
  }
  
  
  
  
}