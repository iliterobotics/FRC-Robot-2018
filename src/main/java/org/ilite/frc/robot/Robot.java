package org.ilite.frc.robot;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import control.DriveMotionPlanner;
import lib.geometry.Pose2d;
import lib.geometry.Pose2dWithCurvature;
import lib.geometry.Rotation2d;
import lib.physics.DriveCharacterization;
import lib.trajectory.Trajectory;
import lib.trajectory.timing.CentripetalAccelerationConstraint;
import lib.trajectory.timing.TimedState;
import lib.trajectory.timing.TimingConstraint;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EDriverControlMode;
import org.ilite.frc.common.sensors.TalonTach;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EElevator;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.util.SystemUtils;
import org.ilite.frc.robot.commands.*;
import org.ilite.frc.robot.controlloop.ControlLoopManager;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.LEDControl;
import org.ilite.frc.robot.modules.PneumaticModule;
import org.ilite.frc.robot.modules.TestingInputs;
import org.ilite.frc.robot.modules.drivetrain.TrajectoryFollower;
import org.ilite.frc.robot.sensors.BeamBreakSensor;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import paths.TrajectoryGenerator;

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
  private final BeamBreakSensor mBeamBreak;
  private DriverInput mDriverInput;
  private Joystick testJoystick;
  private  LEDControl mLedController;

  private TrajectoryFollower mTrajectoryFollower;

  private GetAutonomous getAutonomous;

  List<DriveCharacterization.AccelerationDataPoint> leftAccelData = new ArrayList<>();
    List<DriveCharacterization.VelocityDataPoint> leftVelData = new ArrayList<>();
    List<DriveCharacterization.AccelerationDataPoint> rightAccelData = new ArrayList<>();
    List<DriveCharacterization.VelocityDataPoint> rightVelData = new ArrayList<>();

  public Robot() {
    System.out.println("Hardware init");
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
    System.out.println("Finished hardware init");
    
    mBeamBreak = new BeamBreakSensor(mHardware.getCarriageBeamBreak());
    System.out.println("Beam break init finished");
    mElevator = new Elevator(mHardware, mData);
    mIntake = new Intake(mElevator, mHardware, mBeamBreak);
  	mPneumaticControl = new PneumaticModule(SystemSettings.RELAY_COMPRESSOR_PORT, SystemSettings.DIO_PRESSURE_SWITCH);
    mCarriage = new Carriage(mData, mHardware, mBeamBreak);
  	mDrivetrain = new DriveTrain(mData, mHardware);
    mControlLoop = new ControlLoopManager(mDrivetrain, mData, mHardware);
  	testJoystick = new Joystick(SystemSettings.JOYSTICK_PORT_TESTER);
  	mDriverInput = new DriverInput(mDrivetrain, mIntake, mCarriage, mElevator, mData);
  	mLedController = new LEDControl(mIntake, mElevator, mCarriage, mHardware);
  	mTrajectoryFollower = new TrajectoryFollower(mDrivetrain, mHardware);
  	getAutonomous = new GetAutonomous(SystemSettings.AUTON_TABLE, mIntake, mElevator, mCarriage, mHardware.getPigeon(), mDrivetrain, mData);
  	System.out.println("Modules instantiateds");
  	Logger.setLevel(ELevel.DEBUG);
  }

  public void robotInit() {
    mLog.info(System.currentTimeMillis() + " INIT");
    SystemSettings.limelight.getEntry("ledMode").setNumber(1.0);
  }

  public void autonomousInit() {
    mLog.info("AUTONOMOUS");
    
    double start = Timer.getFPGATimestamp();
    mHardware.getPigeon().zeroAll();
    System.out.println("Pigeon init took " + (Timer.getFPGATimestamp() - start) + " seconds");
    mapInputsAndCachedSensors();

      // mTrajectoryFollower.getDriveController().setPlannerMode(DriveMotionPlanner.PlannerMode.FEEDBACK);
      // mTrajectoryFollower.getDriveController().getController().setGains(0.65, 0.175);
      TrajectoryGenerator mTrajectoryGenerator = new TrajectoryGenerator(mTrajectoryFollower.getDriveController());
      List<TimingConstraint<Pose2dWithCurvature>> kTrajectoryConstraints = Arrays.asList(new CentripetalAccelerationConstraint(70.0));
      List<Pose2d> waypoints = Arrays.asList(new Pose2d[] {
           new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)),
              // new Pose2d(7.0 * 12.0, -7.0 * 12.0, Rotation2d.fromDegrees(-90.0))
              new Pose2d(20.0 * 12.0, 0.0, Rotation2d.fromDegrees(0.0))
      });
      Trajectory<TimedState<Pose2dWithCurvature>> trajectory = mTrajectoryGenerator.generateTrajectory(false, waypoints, kTrajectoryConstraints, 60.0, 30.0, 12.0);

    mCommandQueue.clear();

    System.out.println("Loops took " + (Timer.getFPGATimestamp() - start) + " seconds");
    // mCommandQueue = getAutonomous.getAutonomousCommands();
    mCommandQueue.clear();
    // mCommandQueue.add(new CollectVelocityData(mDrivetrain, leftVelData, rightVelData, false, false));
    // mCommandQueue.add(new Delay(3));
    // mCommandQueue.add(new CollectAccelerationData(mDrivetrain, leftAccelData, rightAccelData, false, false));
     mCommandQueue.add(new FollowTrajectory(trajectory, mTrajectoryFollower, true));
      System.out.println("Get auton commands init took " + (Timer.getFPGATimestamp() - start) + " seconds");

      setRunningModules(/*mIntake, mElevator, mCarriage, mBeamBreak, mLedController*/);
      mControlLoop.setRunningControlLoops(/*mHardware.getTalonTach(),*/ mTrajectoryFollower, mDrivetrain);
      mControlLoop.start();

    // Add commands here
    updateCommandQueue(true);
    
  }
  public void autonomousPeriodic() {
    mapInputsAndCachedSensors();
    
    //TODO put updateCommandQueue into autoninit
    updateCommandQueue(false);
    updateRunningModules();

    // if(mCommandQueue.isEmpty()) {
    //     DriveCharacterization.CharacterizationConstants leftConstants = DriveCharacterization.characterizeDrive(leftVelData, leftAccelData);
    //     DriveCharacterization.CharacterizationConstants rightConstants = DriveCharacterization.characterizeDrive(rightVelData, rightAccelData);

    //     System.out.println("Left:\n" + leftConstants.toString());
    //     System.out.println("Right:\n" + rightConstants.toString());
    // }

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
		  switchDriverControlModes(new DriverControlSplitArcade(mDrivetrain, mIntake, mCarriage, mElevator, mData));
		  break;
	  }
	  
  }
  public void teleopInit()
  {
	  mLog.info("TELEOP");
	  receiveDriverControlMode();

    mHardware.getPigeon().zeroAll();
    mapInputsAndCachedSensors();
	   
	  setRunningModules(/*mBeamBreak,*/ mDriverInput, mDrivetrain, mTrajectoryFollower/*, mIntake, mCarriage, mPneumaticControl, mElevator, mLedController*/);
    
	  mControlLoop.setRunningControlLoops(/* mHardware.getTalonTach()*/);
    mControlLoop.start();
  }

  public void teleopPeriodic() {
    mapInputsAndCachedSensors();
    
    if(mDriverInput.shouldInitializeCommandQueue()) mCommandQueue = mDriverInput.getDesiredCommandQueue();
    if(mDriverInput.canRunCommandQueue()) updateCommandQueue(mDriverInput.shouldInitializeCommandQueue());
    
    updateRunningModules();
    
//      mData.resetAll(mCurrentTime);
    }
  
  
  /**
   * 1. Map joysticks to codexes
   * 2. Perform any input filtering (such as split the split arcade re-map and squaring of the turn)
   * 3. Sets DriveTrain outputs based on processed input
   */
  private void mapInputsAndCachedSensors() {
      mCurrentTime = Timer.getFPGATimestamp();
    
      ELogitech310.map(mData.driverinput, mHardware.getDriverJoystick(), 1.0, true);
      ELogitech310.map(mData.operator, mHardware.getOperatorJoystick(), 1.0, true);
//      ELogitech310.map(mData.tester, testJoystick);
    // Any input processing goes here, such as 'split arcade driver'
    // Any further input-to-direct-hardware processing goes here
    // Such as using a button to reset the gyros
      // EPigeon.map(mData.pigeon, mHardware.getPigeon(), mCurrentTime);
      // EDriveTrain.map(mData.drivetrain, mDrivetrain, mDrivetrain.getDriveMessage());
      // EElevator.map(mData.elevator, mElevator, mCurrentTime);
      // SystemUtils.writeCodexToSmartDashboard(EDriveTrain.class, mData.drivetrain, mCurrentTime);
      // SystemUtils.writeCodexToSmartDashboard(EElevator.class, mData.elevator, mCurrentTime);
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
	        mCurrentCommand.shutdown(mCurrentTime);
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
      mCurrentTime = Timer.getFPGATimestamp();
      mControlLoop.stop();
	  mRunningModules.forEach(m -> m.shutdown(mCurrentTime));
	  // Stop blinking if we don't have a cube
  }
  
  public void disabledPeriodic() {
//    System.out.println(getAutonomous.getAutonomousCommands());
  }
  
  
  
  
}