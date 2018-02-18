package org.ilite.frc.robot.commands;

import static org.ilite.frc.common.types.EDriveTrain.LEFT_POSITION_TICKS;
import static org.ilite.frc.common.types.EDriveTrain.RIGHT_POSITION_TICKS;
import static org.ilite.frc.common.types.EPigeon.YAW;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DriverStation;

public class DriveStraight implements ICommand{
  
  private static final double PROPORTION = 0.05;
  private static final double INITIAL_POWER = 0.4;
  
  private final DriveControl driveTrain;
  private final Data mData;
  private final double distanceToTravel;
  
  private double initialLeftPosition;
  private double initialRightPosition;
  
  private double initialYaw;

  
  public DriveStraight(DriveControl dt, Data pData, double inches){
    this.driveTrain = dt;
    this.mData = pData;
    this.distanceToTravel = (int)Utils.inchesToTicks(inches);
  }
  
  public void initialize(double pNow){
    initialYaw = IMU.clampDegrees(mData.pigeon.get(YAW));
    initialLeftPosition = mData.drivetrain.get(LEFT_POSITION_TICKS);
    initialRightPosition = mData.drivetrain.get(RIGHT_POSITION_TICKS);
    System.out.println("Initial Yaw:" + IMU.clampDegrees(mData.pigeon.get(YAW)));
    System.out.printf("InitL:%s InitR:%s\n", initialLeftPosition, initialRightPosition);
  }
  
  public boolean update(double pNow){
    
    if( getAverageDistanceTravel() >= distanceToTravel){
      driveTrain.setDriveMessage(new DriveMessage(0, 0, DriveMode.PercentOutput, NeutralMode.Brake));
      DriverStation.reportError("I AM STOPPING", false);
      System.out.printf("FinalL:%s FinalR:%s DistTravelled:%s Target:%s\n", mData.drivetrain.get(LEFT_POSITION_TICKS), mData.drivetrain.get(RIGHT_POSITION_TICKS), getAverageDistanceTravel(), distanceToTravel);
      return true;
    }

    double yawError = IMU.getAngleDistance(IMU.clampDegrees(mData.pigeon.get(YAW)), initialYaw);
    driveTrain.setDriveMessage(new DriveMessage(
                               INITIAL_POWER + (yawError * PROPORTION), 
                               INITIAL_POWER - (yawError * PROPORTION),
                               DriveMode.PercentOutput, NeutralMode.Brake));
    
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
