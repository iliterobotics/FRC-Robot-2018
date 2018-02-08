package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.robot.commands.EncoderTurn;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

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
  private final GetAutonomous getAutonomous;
  private Queue<ICommand> mCommandQueue;
  private ICommand mCurrentCommand;
  
  // Temporary...
  private final DriveControl driveControl;
  private final DriveTrain dt;
  private final DriverInput drivetraincontrol;
  public Robot() {
  	getAutonomous = new GetAutonomous(SystemSettings.AUTON_TABLE);
	driveControl = new DriveControl();
	dt = new DriveTrain(driveControl, mData);
	drivetraincontrol = new DriverInput(driveControl, mData);
	mControlLoop = new ControlLoopManager(mData, mHardware);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
       mHardware.init(
        mExecutor,
        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
        new PowerDistributionPanel(), 
        new PigeonIMU(SystemSettings.PIGEON_DEVICE_ID),
        mData
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );

  }

  public void autonomousInit() {
    System.out.println("Default autonomousInit() method... Overload me!");
    mCommandQueue = getAutonomous.getAutonomousCommands();
    mLog.info("AUTONOMOUS");
    mHardware.getPigeon().zeroAll();
    setRunningModules(dt);
    mCommandQueue.clear();
    updateCommandQueue(true);
  }
  public void autonomousPeriodic() {
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    //mControlLoop.setRunningControlLoops();
    //mControlLoop.start();
    
    //TODO put updateCommandQueue into autoninit
    updateCommandQueue(false);
    updateRunningModules();
      
  }
  
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	  setRunningModules(dt, drivetraincontrol);
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
      ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), 1.0, true);
      ELogitech310.map(mData.operator, mHardware.getOperatorJoystick(), 1.0, true);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
      EPigeon.map(mData.pigeon, mHardware.getPigeon(), mCurrentTime);
      EDriveTrain.map(mData.drivetrain, dt, driveControl.getDriveMessage(), mCurrentTime);

      SystemUtils.writeCodexToSmartDashboard(mData.drivetrain);
      SystemUtils.writeCodexToSmartDashboard(mData.pigeon);
//      SystemUtils.writeCodexToSmartDashboard(mData.pigeon);
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
	    }
	    if(mCommandQueue.peek() != null) {
	      mCommandQueue.peek().initialize(mCurrentTime);
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