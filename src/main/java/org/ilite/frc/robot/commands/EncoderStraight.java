package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderStraight implements ICommand{

  private double mSetpointInches;
  private double mLeftTargetPosition, mRightTargetPosition;
  private double mLeftPosition, mRightPosition;
  
  private DriveControl mDriveControl;
  private DriveTrain mDrivetrain;
  private Data mData;
  
  public EncoderStraight(double pInches, DriveControl pDriveControl, Data pData) {
    this.mDriveControl = pDriveControl;
    this.mSetpointInches = pInches;
    this.mData = pData;
  }
  
  public void initialize() {
    mLeftTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mRightTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mDriveControl.setDriveMessage(new DriveMessage(mLeftTargetPosition, mLeftTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
  }
  
  public boolean update() {
    mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
    mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
    if(Math.abs((mLeftPosition + mRightPosition) / 2) >= Math.abs(mLeftTargetPosition)) {
    	System.out.println("Done with straight");
    	return true;
    }
    return false;
  }
  
  public void shutdown() {
    
  }
  
}
