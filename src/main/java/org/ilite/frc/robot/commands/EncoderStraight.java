package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.DriverControl;

import com.ctre.phoenix.motorcontrol.ControlMode;

public class EncoderStraight implements ICommand{

  private double mCurrentAngle;
  private double mSetpointInches;
  private double mLeftTargetPosition, mRightTargetPosition;
  private double mLeftPosition, mRightPosition;
  
  private DriverControl mDriverControl;
  private DriveTrain mDrivetrain;
  
  public EncoderStraight(double pInches, DriverControl pDriverControl, DriveTrain pDrivetrain) {
    this.mDriverControl = pDriverControl;
    this.mSetpointInches = pInches;
    this.mDrivetrain = pDrivetrain;
  }
  
  public void initialize() {
    mLeftTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mRightTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mDriverControl.setDesiredControlMode(ControlMode.MotionMagic);
    mDriverControl.setDesiredLeftOutput(mLeftTargetPosition);
    mDriverControl.setDesiredLeftOutput(mRightTargetPosition);
  }
  
  public boolean update() {
    return false;
  }
  
  public void shutdown() {
    
  }
  
}
