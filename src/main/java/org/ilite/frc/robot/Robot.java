package org.ilite.frc.robot;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EDriverControlMode;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.robot.commands.FollowPath;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.LEDControl;
import org.ilite.frc.robot.modules.LEDControl.Message;
import org.ilite.frc.robot.modules.PneumaticModule;
import org.ilite.frc.robot.modules.TestingInputs;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainControl;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
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
  
  private final DriveTrain mDrivetrain;
  private final Carriage mCarriage;
  private final Elevator mElevator;
  private final Intake mIntake;
  private final PneumaticModule mPneumaticControl;
  private final DrivetrainControl mDrivetrainControl;
  private DriverInput mDriverInput;
  private Joystick testJoystick;
  private  LEDControl mLedController;

  
  private GetAutonomous getAutonomous;

  public Robot() {
    mElevator = new Elevator(mHardware);
    mIntake = new Intake(mElevator);
  	mControlLoop = new ControlLoopManager(mData, mHardware);
    mDrivetrainControl = new DrivetrainControl();
  	mPneumaticControl = new PneumaticModule(SystemSettings.RELAY_COMPRESSOR_PORT, SystemSettings.DIO_PRESSURE_SWITCH);
    mCarriage = new Carriage(mData, mHardware, mIntake);
  	mDrivetrain = new DriveTrain(mDrivetrainControl, mData);
  	testJoystick = new Joystick(SystemSettings.JOYSTICK_PORT_TESTER);
  	mDriverInput = new DriverInput(mDrivetrainControl, mIntake, mCarriage, mElevator, mData);
  	mLedController = new LEDControl(mIntake, mCarriage, mHardware);
  	Logger.setLevel(ELevel.INFO);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
      
       mHardware.init(
        mExecutor,
        new Joystick(SystemSettings.JOYSTICK_PORT_DRIVER), 
        new Joystick(SystemSettings.JOYSTICK_PORT_OPERATOR), 
//        new PowerDistributionPanel(SystemSettisngs.PDP_DEVICE_ID), 
        null,
        new PigeonIMU(SystemSettings.PIGEON_DEVICE_ID),
        new TalonTach(SystemSettings.DIO_TALON_TACH),
        new CANifier(SystemSettings.CANIFIER_DEVICE_ID),
        //CameraServer.getInstance().startAutomaticCapture(),
        new DigitalInput(SystemSettings.DIO_CARRIAGE_BEAM_BREAK_ID)
        // Sensors
        // Custom hw
        // Spike relays
        // etc
        
        // Talons TBD ... they're somewhat picky.
    );
  }

  public void autonomousInit() {
    mLog.info("AUTONOMOUS");
    
    mHardware.getPigeon().zeroAll();
    mapInputsAndCachedSensors();
    
    mCommandQueue = getAutonomous.getAutonomousCommands();
    mCommandQueue.clear();
    mCommandQueue.add(new FollowPath(mDrivetrainControl, mData, 
                      new File("/home/lvuser/paths/to-right-switch-curve_left_detailed.csv"), 
                      new File("/home/lvuser/paths/to-right-switch-curve_right_detailed.csv"), 
                      false));
    // Add commands here
    updateCommandQueue(true);
    
  }
  public void autonomousPeriodic() {
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    
    //TODO put updateCommandQueue into autoninit
    updateCommandQueue(false);
    updateRunningModules();
      
  }
 
  public void switchDriverControlModes(DriverInput dc) {
	  this.mDriverInput = dc;
  }
  
  public void receiveDriverControlMode() {
	  int receivedControlMode = SystemSettings.DRIVER_CONTROL_TABLE.getEntry(EDriverControlMode.class.getSimpleName()).getNumber(0).intValue();
	  EDriverControlMode controlMode = EDriverControlMode.intToEnum(receivedControlMode);
	  switch (controlMode) {
	  case ARCADE:
		  switchDriverControlModes (mDriverInput);
		  break;
	  case SPLIT_ARCADE:
		  switchDriverControlModes(new DriverControlSplitArcade(mDrivetrainControl, mIntake, mCarriage, mElevator, mData));
		  break;
	  }
	  
  }
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	   receiveDriverControlMode();

	   setRunningModules(mDriverInput, mDrivetrain, mIntake, mCarriage, mPneumaticControl, mElevator, mLedController);
	  
	  mHardware.getPigeon().zeroAll();
	  
	  mControlLoop.setRunningControlLoops();
	  mControlLoop.start();
  }

  public void teleopPeriodic() {
    // Remember that DriverControl classes don't go here. They aren't Modules.
    mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    updateRunningModules();
    
//      mData.resetAll(mCurrentTime);
    }
  
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputsAndCachedSensors() {
      ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), 1.0, true);
      ELogitech310.map(mData.operator, mHardware.getOperatorJoystick(), 1.0, true);
      ELogitech310.map(mData.tester, testJoystick);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
      EPigeon.map(mData.pigeon, mHardware.getPigeon(), mCurrentTime);
      EDriveTrain.map(mData.drivetrain, mDrivetrain, mDrivetrainControl.getDriveMessage(), mCurrentTime, mDrivetrain.getLeftMaster(), mDrivetrain.getRightMaster());
      SystemUtils.writeCodexToSmartDashboard(mData.drivetrain);
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
		  m.initialize(Timer.getFPGATimestamp());
	  }
  }
  
  public void testInit() {
    setRunningModules(mDrivetrain, mIntake, mCarriage, mPneumaticControl, mElevator,
                      new TestingInputs(mData, mIntake, mCarriage, mDrivetrain, mElevator, mPneumaticControl));
    mHardware.getPigeon().zeroAll();
    
    mControlLoop.setRunningControlLoops();
    mControlLoop.start();
  }
  
  public void testPeriodic() {
	  mLog.info("TEST");
	  mCurrentTime = Timer.getFPGATimestamp();
    mapInputsAndCachedSensors();
    updateRunningModules();
  }

  public void disabledInit() {
	  mLog.info("DISABLED");
	  mControlLoop.stop();
  }
  
  public void disabledPeriodic() {
  }
  
  
  
  
}