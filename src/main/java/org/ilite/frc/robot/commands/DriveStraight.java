package org.ilite.frc.robot.commands;

import static org.ilite.frc.common.types.EDriveTrain.LEFT_POSITION_TICKS;
import static org.ilite.frc.common.types.EDriveTrain.RIGHT_POSITION_TICKS;
import static org.ilite.frc.common.types.EPigeon.YAW;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.DriverStation;

public class DriveStraight implements ICommand{
	
	private ILog mLog = Logger.createLog(DriveStraight.class);
  
  private static final double TURN_PROPORTION = 0.03;
  private static final double INITIAL_POWER = 0.6 ;
  
  private final DriveTrain driveTrain;
  private final Data mData;
  private final double distanceToTravel;
  
  private double initialLeftPosition;
  private double initialRightPosition;
  
  private double initialYaw;

  
  public DriveStraight(DriveTrain dt, Data pData, double inches){
    this.driveTrain = dt;
    this.mData = pData;
    this.distanceToTravel = (int)Utils.inchesToTicks(inches);
  }
  
  public void initialize(double pNow){
    initialYaw = IMU.clampDegrees(mData.pigeon.get(YAW));
    initialLeftPosition = mData.drivetrain.get(LEFT_POSITION_TICKS);
    initialRightPosition = mData.drivetrain.get(RIGHT_POSITION_TICKS);
    mLog.debug("Initial Yaw:" + IMU.clampDegrees(mData.pigeon.get(YAW)));
//    System.out.printf("InitL:%s InitR:%s\n", initialLeftPosition, initialRightPosition);
  }
  
  public boolean update(double pNow){
    
    if( getAverageDistanceTravel() >= distanceToTravel){
      driveTrain.setDriveMessage(new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
      DriverStation.reportError("I AM STOPPING", false);
//      System.out.printf("FinalL:%s FinalR:%s DistTravelled:%s Target:%s\n", mData.drivetrain.get(LEFT_POSITION_TICKS), mData.drivetrain.get(RIGHT_POSITION_TICKS), getAverageDistanceTravel(), distanceToTravel);
      return true;
    }

    double yawError = IMU.getAngleDistance(IMU.clampDegrees(mData.pigeon.get(YAW)), initialYaw);
    driveTrain.setDriveMessage(new DrivetrainMessage(
                               INITIAL_POWER + (yawError * TURN_PROPORTION), 
                               INITIAL_POWER - (yawError * TURN_PROPORTION),
                               DrivetrainMode.PercentOutput, NeutralMode.Brake));
    
    return false;
  }
  
  public void shutdown(double pNow) {
    
  }
  
  private double getAverageDistanceTravel(){
    return (Math.abs(mData.drivetrain.get(LEFT_POSITION_TICKS) - initialLeftPosition) + 
            (Math.abs(mData.drivetrain.get(RIGHT_POSITION_TICKS) - initialRightPosition))) / 2;
  }
  
  public void adjustBearing(double angleDiff){
    initialYaw = IMU.getAngleSum(initialYaw, angleDiff);
  }
	
}
