package org.ilite.frc.robot;

import java.util.Arrays;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.common.input.EDriverControlMode;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.common.sensors.LidarLite;
import org.ilite.frc.common.sensors.Pigeon;


import org.ilite.frc.common.sensors.LidarLite;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.DriverControlSplitArcade;
import org.ilite.frc.robot.modules.ElevatorModule;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private double mCurrentTime = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
  private final ControlLoopManager mControlLoop;
  
  private List<IModule> mRunningModules = new LinkedList<>();
  private Queue<ICommand> mCommandQueue = new LinkedList<>();
  private ICommand mCurrentCommand;
  
  private VisionThread visionThread;
  private GripPipeline pipeline;
  private Processing processing;
  
  private final DriveTrain mDrive;
  private final Carriage mCarriage;
  private final ElevatorModule mElevator;
  private final Intake mIntake;
  private final DriveControl driveControl;
  private DriverInput mDriverInput;
  
  private String controllerMode;
  private int numControlMode;
  
  private LidarLite lidar = new LidarLite();
  
 private final TalonTach talonTach;  
  public Robot() {
	mControlLoop = new ControlLoopManager(mData, mHardware);
	talonTach = new TalonTach(4);
	elevator = new ElevatorModule(talonTach);
	intake = new Intake(elevator);
	drivetraincontrol = new DriverControl(mData, intake, elevator);
	dt = new DriveTrain(drivetraincontrol);
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
        new TalonTach(SystemSettings.T)
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );

  }

  public void autonomousInit() {
    System.out.println("Default autonomousInit() method... Overload me!");
    mLog.info("AUTONOMOUS");
    mHardware.getPigeon().zeroAll();

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    mapInputsAndCachedSensors();
    
    settings.loadFromFile();
    mapInputsAndCachedSensors();
    mCommandQueue = getAutonomous.getAutonomousCommands();
    mCommandQueue.clear();
    mCommandQueue.add(new FollowPath(driveControl, mData, 
                      new File("/home/lvuser/paths/to-right-switch-curve_left_detailed.csv"), 
                      new File("/home/lvuser/paths/to-right-switch-curve_right_detailed.csv"), 
                      false));
    // Add commands here
    updateCommandQueue(true);
    
  }
  public void autonomousPeriodic() {
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
	setRunningModules(drivetraincontrol, dt);
    //mControlLoop.setRunningControlLoops();
    //mControlLoop.start();
    
	//TODO put updateCommandQueue into autoninit
	updateCommandQueue(true);
    updateRunningModules();
      
  }
 
  public void switchDriverControlModes(DriverInput dc) {
	  this.mDriverInput = dc;
  }
  
  public void receiveDriverControlMode() {
	  int receivedControlMode = SystemSettings.DRIVER_CONTROL_TABLE.getEntry(EDriverControlMode.class.getSimpleName()).getNumber(0).intValue();
	  numControlMode = receivedControlMode;
	  EDriverControlMode controlMode = EDriverControlMode.intToEnum(numControlMode);
	  switch (controlMode) {
	  case ARCADE:
		  switchDriverControlModes (new DriverInput(driveControl, mIntake, mData));
		  break;
	  case SPLIT_ARCADE:
		  switchDriverControlModes(new DriverControlSplitArcade(driveControl, mIntake, mData));
		  break;
	  }
	  
  }
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	   receiveDriverControlMode();

	  setRunningModules(mDrive, mDriverInput);
	  
	  initializeRunningModules();
	  mHardware.getPigeon().zeroAll();
	  
	  mControlLoop.setRunningControlLoops();
	  mControlLoop.start();

  }

  public void teleopPeriodic() {
    // Remember that DriverControl classes don't go here. They aren't Modules.
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    updateRunningModules();
    
      mCurrentTime = Timer.getFPGATimestamp();
//      mData.resetAll(mCurrentTime);
      mapInputsAndCachedSensors();
      System.out.println("Yaw: " + mHardware.getPigeon().getYaw());
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
  }
  
  
  
  
}