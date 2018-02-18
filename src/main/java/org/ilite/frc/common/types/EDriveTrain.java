package org.ilite.frc.common.types;

import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum EDriveTrain implements CodexOf<Double> {
  TIME_SECONDS,
  
  DRIVE_MODE,
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
  OPEN_LOOP_CALC_LEFT_POWER,
  OPEN_LOOP_CALC_RIGHT_POWER,
  
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
  TALON_CONTROL_MODE;
  
  public static void map(Codex<Double, EDriveTrain> pCodex, DriveTrain driveTrain, DrivetrainMessage driveMessage, double pTimestampNow, TalonSRX rightMaster, TalonSRX leftMaster) {
    double leftPositionTicks = leftMaster.getSelectedSensorPosition(0);
    double rightPositionTicks = rightMaster.getSelectedSensorPosition(0);
    double leftVelocityTicks = leftMaster.getSelectedSensorVelocity(0);
    double rightVelocityTicks = rightMaster.getSelectedSensorVelocity(0);
    pCodex.set(DESIRED_LEFT_OUTPUT, driveMessage.leftOutput);
    pCodex.set(DESIRED_RIGHT_OUTPUT, driveMessage.rightOutput);
    pCodex.set(DRIVE_MODE, (double)driveTrain.getDriveMode().ordinal());
    
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
    
    pCodex.set(TALON_CONTROL_MODE, (double)driveTrain.getControlMode().ordinal());  
  }
  
}
