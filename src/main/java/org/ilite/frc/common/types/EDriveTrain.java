package org.ilite.frc.common.types;

import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum EDriveTrain implements CodexOf<Double> {
  LEFT_DRIVE_MODE,
  RIGHT_DRIVE_MODE,
  DESIRED_LEFT_OUTPUT,
  DESIRED_RIGHT_OUTPUT,
  
  OPEN_LOOP_VOLTAGE_RAMP_RATE,
  
  LEFT_POSITION_TICKS,
  RIGHT_POSITION_TICKS,
  RIGHT_POSITION_ROT,
  LEFT_POSITION_ROT,
  LEFT_POSITION_INCHES,
  RIGHT_POSITION_INCHES,
  
  LEFT_VELOCITY_TICKS,
  RIGHT_VELOCITY_TICKS,
  LEFT_VELOCITY_RPM,
  RIGHT_VELOCITY_RPM,
  LEFT_VELOCITY_FPS,
  RIGHT_VELOCITY_FPS,
  
  OPEN_LOOP_THROTTLE, 
  OPEN_LOOP_TURN,
  
  LEFT_TALON_MASTER_VOLTAGE,
  LEFT_TALON_FOLLOW1_VOLTAGE,
  LEFT_TALON_FOLLOW2_VOLTAGE,
  
  LEFT_TALON_MASTER_CURRENT,
  LEFT_TALON_FOLLOW1_CURRENT,
  LEFT_TALON_FOLLOW2_CURRENT,
  
  RIGHT_TALON_MASTER_VOLTAGE,
  RIGHT_TALON_FOLLOW1_VOLTAGE,
  RIGHT_TALON_FOLLOW2_VOLTAGE,
  
  RIGHT_TALON_MASTER_CURRENT,
  RIGHT_TALON_FOLLOW1_CURRENT,
  RIGHT_TALON_FOLLOW2_CURRENT,
  
  TALON_VBUS,
  LEFT_CONTROL_MODE,
  RIGHT_CONTROL_MODE;
  
  public static void map(Codex<Double, EDriveTrain> pCodex, DriveTrain driveTrain, DrivetrainMessage driveMessage) {
    double leftPositionTicks = driveTrain.getLeftMaster().getSelectedSensorPosition(0);
    double rightPositionTicks = driveTrain.getRightMaster().getSelectedSensorPosition(0);
    double leftVelocityTicks = driveTrain.getLeftMaster().getSelectedSensorVelocity(0);
    double rightVelocityTicks = driveTrain.getRightMaster().getSelectedSensorVelocity(0);
    pCodex.set(LEFT_DRIVE_MODE, (double)driveTrain.getLeftDriveMode().ordinal());
    pCodex.set(RIGHT_DRIVE_MODE, (double)driveTrain.getRightDriveMode().ordinal());
    
    pCodex.set(DESIRED_LEFT_OUTPUT, driveMessage.leftOutput);
    pCodex.set(DESIRED_RIGHT_OUTPUT, driveMessage.rightOutput);
    
    pCodex.set(LEFT_POSITION_TICKS, (double)leftPositionTicks);
    pCodex.set(LEFT_POSITION_ROT, Utils.ticksToRotations(leftPositionTicks));
    pCodex.set(LEFT_POSITION_INCHES, Utils.ticksToInches(leftPositionTicks));
    
    pCodex.set(RIGHT_POSITION_TICKS, (double)rightPositionTicks);
    pCodex.set(RIGHT_POSITION_ROT, Utils.ticksToRotations(rightPositionTicks));
    pCodex.set(RIGHT_POSITION_INCHES, Utils.ticksToInches(rightPositionTicks));
    
    pCodex.set(LEFT_VELOCITY_TICKS, (double)leftVelocityTicks);
    pCodex.set(LEFT_VELOCITY_RPM, Utils.ticksToRPM(leftVelocityTicks));
    pCodex.set(LEFT_VELOCITY_FPS, Utils.ticksToFPS(leftVelocityTicks));
    
    pCodex.set(RIGHT_VELOCITY_TICKS, (double)rightVelocityTicks);
    pCodex.set(RIGHT_VELOCITY_RPM, Utils.ticksToRPM(rightVelocityTicks));
    pCodex.set(RIGHT_VELOCITY_FPS, Utils.ticksToFPS(rightVelocityTicks));
    
    pCodex.set(LEFT_TALON_MASTER_VOLTAGE, driveTrain.getLeftMaster().getMotorOutputVoltage());
    pCodex.set(LEFT_TALON_FOLLOW1_VOLTAGE, driveTrain.getLeftFollower1().getMotorOutputVoltage());
    pCodex.set(LEFT_TALON_FOLLOW2_VOLTAGE, driveTrain.getLeftFollower2().getMotorOutputVoltage());
    
    pCodex.set(RIGHT_TALON_MASTER_VOLTAGE, driveTrain.getRightMaster().getMotorOutputVoltage());
    pCodex.set(RIGHT_TALON_FOLLOW1_VOLTAGE, driveTrain.getRightFollower1().getMotorOutputVoltage());
    pCodex.set(RIGHT_TALON_FOLLOW2_VOLTAGE, driveTrain.getRightFollower2().getMotorOutputVoltage());
    
    pCodex.set(LEFT_CONTROL_MODE, (double)driveTrain.getLeftControlMode().ordinal());
    pCodex.set(RIGHT_CONTROL_MODE, (double)driveTrain.getRightControlMode().ordinal());
  }
  
  public static void testMap(Codex<Double, EDriveTrain> pCodex) {
    for(EDriveTrain e : values()) {
      pCodex.set(e, Math.random() * 10);
    }
  }
  
}
