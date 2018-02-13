package org.ilite.frc.robot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;

import org.ilite.frc.common.sensors.LidarLite;
import org.ilite.frc.common.sensors.Pigeon;


import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.robot.commands.GyroTurn;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControl;
import org.ilite.frc.robot.modules.ElevatorModule;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;
import org.ilite.frc.robot.vision.GripPipeline;
import org.ilite.frc.robot.vision.Processing;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
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
  private final DriveControl driveControl;
  private final DriveTrain dt;
  private final DriverInput drivetraincontrol;
  private LidarLite lidar = new LidarLite();
  private Carriage carriage;
  public Robot() {
	  elevator = new ElevatorModule();
	  intake = new Intake(elevator);
    mControlLoop = new ControlLoopManager(mData, mHardware);
	  drivetraincontrol = new DriverInput(driveControl, mData);
    carriage = new Carriage(mData);
	dt = new DriveTrain(driveControl, mData);
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
        CameraServer.getInstance().startAutomaticCapture(),
        mData
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
	mLog.info("AUTONOMOUS");

    setRunningModules(dt, intake, elevator, carriage);
    mControlLoop.setRunningControlLoops();
    mControlLoop.start();
    
    mHardware.getPigeon().zeroAll();
    
    mCommandQueue = getAutonomous.getAutonomousCommands();
    // Add commands here
    updateCommandQueue(true);
  }
  
  public void autonomousPeriodic() {
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
	  setRunningModules(drivetraincontrol, dt);
    //mControlLoop.setRunningControlLoops();
    //mControlLoop.start();
    
    updateCommandQueue(false);
    updateRunningModules();
  }
  
  public void teleopInit()
  {
	  mLog.info("TELEOP");

	  setRunningModules(dt, drivetraincontrol, intake, carriage);
	  
	  mHardware.getPigeon().zeroAll();
	  
	  mControlLoop.setRunningControlLoops();
	  mControlLoop.start();
  }

  public void teleopPeriodic() {
    // Remember that DriverControl classes don't go here. They aren't Modules.
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    updateRunningModules();
  }
  
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputsAndCachedSensors() {
      ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), 1.0, true);
      ELogitech310.map(mData.operator, mHardware.getOperatorJoystick(), 1.0, true);
      EDriveTrain.map(mData.drivetrain, dt, driveControl.getDriveMessage(), mCurrentTime);
      EPigeon.map(mData.pigeon, mHardware.getPigeon(), mCurrentTime);
      ECubeTarget.map(mData.vision, processing);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
      SystemUtils.writeCodexToSmartDashboard(mData.pigeon);
      SystemUtils.writeCodexToSmartDashboard(mData.drivetrain);
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
	    if(firstRun) mCurrentCommand.initialize(mCurrentTime);
	    //If this command is finished executing
	    if(mCurrentCommand.update(mCurrentTime)) {
	      mCommandQueue.poll(); //Discard the command and initialize the next one
	      if(mCommandQueue.peek() != null) {
		      mCommandQueue.peek().initialize(mCurrentTime);
		      return true;
		  }
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
		  m.initialize(mCurrentTime);
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
    SystemUtils.writeCodexToSmartDashboard(mData.drivetrain);
    SystemUtils.writeCodexToSmartDashboard(mData.pigeon);
  }
  
}