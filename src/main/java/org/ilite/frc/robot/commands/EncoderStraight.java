package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderStraight implements ICommand{

  private double mSetpointInches;
  private double mLeftTargetPosition, mRightTargetPosition, mAbsoluteTargetPosition;
  private double mLeftPosition, mRightPosition, mAbsolutePosition;
  
  private DriveControl mDriveControl;
  private Data mData;
  
  public EncoderStraight(double pInches, DriveControl pDriveControl, Data pData) {
    this.mDriveControl = pDriveControl;
    this.mSetpointInches = pInches;
    this.mData = pData;
  }
  
  public void initialize() {
    mLeftTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mRightTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mAbsoluteTargetPosition = Utils.absoluteAverage(mLeftTargetPosition, mRightTargetPosition);
    
    mDriveControl.setDriveMessage(new DriveMessage(mLeftTargetPosition, mRightTargetPosition, DriveMode.MotionMagic, NeutralMode.Brake));
  }
  
  public boolean update() {
    mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
    mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
    mAbsolutePosition = Utils.absoluteAverage(mLeftPosition, mRightPosition);
    
    if(mAbsoluteTargetPosition - mAbsolutePosition < SystemSettings.AUTO_POS_TOLERANCE) {
    	mDriveControl.setDriveMessage(new DriveMessage(mLeftPosition, mRightPosition, DriveMode.MotionMagic, NeutralMode.Brake));
    	return true;
    }
    
    return false;
  }
  
  public void shutdown() {
    
  }
  
}
