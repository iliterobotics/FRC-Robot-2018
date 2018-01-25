package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.commands.Command;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControl;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.common.sensors.Pigeon;


import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.sensors.PigeonIMU;

public class Robot extends IterativeRobot {
  private final ILog mLog = Logger.createLog(Robot.class);
  private double mCurrentTime = 0;
  
  private final Executor mExecutor = Executors.newFixedThreadPool(1);
  private final Hardware mHardware = new Hardware();
  private final Data mData = new Data();
  
//  private final ControlLoopManager mControlLoop;
  
  private List<IModule> mRunningModules = new LinkedList<>();
  private Queue<Command> mCommandQueue = new LinkedList<>();
  private Command mCurrentCommand;
  
  // Temporary...
  private final DriveTrain dt;
  private final DriverControl drivetraincontrol;
  private Pigeon pidgey;

  //Collision Threshold => Temporary Value
  final static double kCollisionThreshold_DeltaG = 0.5f;
  double lastAccelX;
  double lastAccelY;
  double lastAccelZ;
  
   
  public Robot() {
//    mControlLoop = new ControlLoopManager(mData, mHardware);
    dt = new DriveTrain();
    drivetraincontrol = new DriverControl(dt, mData);
    Logger.setLevel(ELevel.INFO);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
       mHardware.init(
        mExecutor,
        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
        new PowerDistributionPanel(), 
        new PigeonIMU(SystemSettings.PIGEON_DEVICE_ID)
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );

       pidgey = new Pigeon(mHardware);

    
  }

  public void autonomousInit() {
    System.out.println("Default autonomousInit() method... Overload me!");
    mLog.info("AUTONOMOUS");
  }
  public void autonomousPeriodic() {
	setRunningModules();
    //mControlLoop.setRunningControlLoops();
    //mControlLoop.start();
    
	//TODO put updateCommandQueue into autoninit
	updateCommandQueue(true);
    updateRunningModules();
      
  }
  
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	  setRunningModules(dt, drivetraincontrol);
	  initializeRunningModules();
	  pidgey.zeroAll();
	  
  }

  public void teleopPeriodic() {
    // Remember that DriverControl classes don't go here. They aren't Modules.
	
//	mControlLoop.setRunningControlLoops();
//    mControlLoop.start();
    
      mCurrentTime = Timer.getFPGATimestamp();
//      mData.resetAll(mCurrentTime);
      mapInputs();
      
      updateRunningModules();
    }
  
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputs() {
      ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), null, false);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
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

  public void disabledInit()
  {
	    mLog.info("DISABLED");	  
  }
  public void disabledPeriodic() {
    //mControlLoop.stop();
  }
  
  
  public void collisionDetection(){
      while(true){
          boolean collisionDetected = false;
          double currentAccelX = pidgey.getAccelX();
          double currentJerkX = currentAccelX - lastAccelX;
          lastAccelX = currentAccelX;
          double currentAccelY = pidgey.getAccelY();
          double currentJerkY = currentAccelY - lastAccelY;
          lastAccelY = currentAccelY;
//          double currentAccelZ = pidgey.getAccelZ();
//          double currentJerkZ = currentAccelZ - lastAccelZ;
//          lastAccelZ = currentAccelZ;
          if ( ( Math.abs(currentJerkX) > kCollisionThreshold_DeltaG ) ||
                  ( Math.abs(currentJerkY) > kCollisionThreshold_DeltaG) ) {
                 collisionDetected = true;
          }
          
      }
  }
  
  
  
  
}