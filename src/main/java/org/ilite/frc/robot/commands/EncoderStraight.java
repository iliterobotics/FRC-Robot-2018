package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderStraight implements ICommand{

  private double mCurrentAngle;
  private double mSetpointInches;
  private double mLeftTargetPosition, mRightTargetPosition;
  private double mLeftPosition, mRightPosition;
  
  private DriveControl mDriveControl;
  private DriveTrain mDrivetrain;
  
  public EncoderStraight(double pInches, DriveControl pDriveControl, DriveTrain pDrivetrain) {
    this.mDriveControl = pDriveControl;
    this.mSetpointInches = pInches;
    this.mDrivetrain = pDrivetrain;
  }
  
  public void initialize() {
    mLeftTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mRightTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    System.out.printf("Left Target: %s Right Target: %s", mLeftTargetPosition, mRightTargetPosition);
    mDriveControl.setDriveMessage(new DriveMessage(mLeftTargetPosition, mLeftTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
  }
  
  public boolean update() {
    System.out.println("Updating command");
    return false;
  }
  
  public void shutdown() {
    
  }
  
}
