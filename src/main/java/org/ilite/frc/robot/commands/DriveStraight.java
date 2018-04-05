package org.ilite.frc.robot.commands;

import static org.ilite.frc.common.types.EDriveTrain.LEFT_POSITION_TICKS;
import static org.ilite.frc.common.types.EDriveTrain.RIGHT_POSITION_TICKS;
import static org.ilite.frc.common.types.EPigeon.YAW;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DriverStation;

public class DriveStraight implements ICommand{
	
	private ILog mLog = Logger.createLog(DriveStraight.class);
  
	// % Power per degree
  private static final double TURN_PROPORTION = 0.03;
  
  //If error > ~3 feet, then use max power (1.0)
  // If error < ~3 feet, then 
  // therefore we want to slow down when there are (3ft / wheel diameter) * ticks per rotation ticks remaining
  // Therefore P = (1 - default power) / (# of wheel rotations to start slowing down * # of ticks per rotation)
  // TODO - verify it's 4096 ticks per rotation for this encoder
  private static final double NUM_TICKS_FOR_SLOWDOWN = Utils.inchesToTicks(10 * 12);
  // Units are % power per remaining encoder tick
  private double kP = 0.4 / (Utils.inchesToTicks(36));
  // % motor output
  private double mPower;
  
  private final DriveTrain driveTrain;
  private final Data mData;
  
  // In encoder ticks
  private final double distanceToTravel;
  private double remainingDistance;
  private double initialLeftPosition;
  private double initialRightPosition;
  private final boolean mIgnoreGyro;
  private boolean useHoldPosition;
  
  // In degrees
  private double mDesiredHeadingYaw;

  public DriveStraight(DriveTrain dt, Data pData, double inches, double power, boolean ignoreGyro, boolean useHoldPosition) {
    this.driveTrain = dt;
    this.mData = pData;
    this.distanceToTravel = Utils.inchesToTicks(inches);
    this.mPower = power;
    kP = (1-power) / NUM_TICKS_FOR_SLOWDOWN;
    mIgnoreGyro = ignoreGyro;
    this.useHoldPosition = useHoldPosition;
  }
  
  public DriveStraight(DriveTrain dt, Data pData, double inches, double power){
    this(dt, pData, inches, 0.6, false, true);
  }
  
  public DriveStraight(DriveTrain dt, Data pData, double inches){
     this(dt, pData, inches, 0.6);
  }
  
  public DriveStraight(DriveTrain dt, Data pData, double inches, boolean ignoreGyro, boolean useHoldPosition){
     this(dt, pData, inches, 0.6, ignoreGyro, true);
  }
  
  public void initialize(double pNow){
    mDesiredHeadingYaw = IMU.clampDegrees(mData.pigeon.get(YAW));
    initialLeftPosition = mData.drivetrain.get(LEFT_POSITION_TICKS);
    initialRightPosition = mData.drivetrain.get(RIGHT_POSITION_TICKS);
    mLog.debug("Initial Yaw:" + mDesiredHeadingYaw);
  }
  
  public boolean update(double pNow){
    double currentDistance = getAverageDistanceTravel();
    if( currentDistance >= distanceToTravel){
      // We hold our current position (where we ended the drive straight) using the Talon's closed-loop position mode to avoid overshooting the target distance
      driveTrain.holdPosition();
//      driveTrain.setDriveMessage(new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
      DriverStation.reportError("I AM STOPPING " + Utils.ticksToInches(currentDistance), false);
      return true;
    }
    else
    {
      //set drivetrain output to 0
    }

    remainingDistance = distanceToTravel - currentDistance;
    
    if(mIgnoreGyro) {
      // negative error = turn left; positive error = turn right
      // We negate angle beacuse pigeon angle goes counter-clockwise
      // If desired = 30 and current = 60, then error = -30. Turn left 30 degrees.
      // If desired = 0 and current = -2, then error = 2.  Turn right 2 degrees.
      driveTrain.setDriveMessage(DrivetrainMessage.fromThrottleAndTurn(
          // Clamp the mPower + kP * distance so we have headroom for the turn proportion to work
          // Turn proportion is in units of % power per degree.  So 2 * TURN_PROPORTION gives us
          // 2 degrees of correction before % power is saturated
          mPower + kP * remainingDistance,
          0d,
           NeutralMode.Brake));
      
    } else {
      // negative error = turn left; positive error = turn right
      // We negate angle beacuse pigeon angle goes counter-clockwise
      double yawError = IMU.getAngleDistance(IMU.clampDegrees(mData.pigeon.get(YAW)), mDesiredHeadingYaw);
      // If desired = 30 and current = 60, then error = -30. Turn left 30 degrees.
      // If desired = 0 and current = -2, then error = 2.  Turn right 2 degrees.
      driveTrain.setDriveMessage(DrivetrainMessage.fromThrottleAndTurn(
          // Clamp the mPower + kP * distance so we have headroom for the turn proportion to work
          // Turn proportion is in units of % power per degree.  So 2 * TURN_PROPORTION gives us
          // 2 degrees of correction before % power is saturated
          Utils.clamp(mPower + kP * remainingDistance, 1 - 2*TURN_PROPORTION),
           yawError * TURN_PROPORTION, 
           NeutralMode.Brake));
      
    }
    
    
//    driveTrain.setDriveMessage(new DrivetrainMessage(
//                               mPower + (yawError * TURN_PROPORTION), 
//                               mPower - (yawError * TURN_PROPORTION),
//                               DrivetrainMode.PercentOutput, NeutralMode.Brake));
    
    return false;
  }
  
  public void shutdown(double pNow) {
    
  }
  
  /**
   * @return average # of ticks traveled per encoder
   */
  private double getAverageDistanceTravel(){
    return (Math.abs(mData.drivetrain.get(LEFT_POSITION_TICKS) - initialLeftPosition) + 
            (Math.abs(mData.drivetrain.get(RIGHT_POSITION_TICKS) - initialRightPosition))) / 2;
  }
  
  public void adjustBearing(double angleDiff){
    mDesiredHeadingYaw = IMU.getAngleSum(mDesiredHeadingYaw, angleDiff);
  }
	
}
