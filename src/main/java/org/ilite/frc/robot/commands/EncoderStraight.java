package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class EncoderStraight implements ICommand{

  private double mSetpointInches;
  private double mLeftTargetPosition, mRightTargetPosition;
  private double mLeftPosition, mRightPosition;
  
  private DriveTrain mDrivetrain;
  private Data mData;
  
  public EncoderStraight(double pInches, DriveTrain pDrivetrain, Data pData) {
    this.mDrivetrain = pDrivetrain;
    this.mSetpointInches = pInches;
    this.mData = pData;
  }
  
  public void initialize(double pNow) {
	mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
	mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
	
    mLeftTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    mRightTargetPosition = mSetpointInches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
    
    System.out.println("Right Target: " + mRightTargetPosition + " Left Target: " + mLeftTargetPosition);
    mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPosition + mLeftTargetPosition, mRightPosition + mRightTargetPosition, DrivetrainMode.MotionMagic, NeutralMode.Brake));
  }
  
  public boolean update(double pNow) {
    mLeftPosition = mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
    mRightPosition = mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
    
    if(isFinished()) {
    	System.out.println("EncoderStraight Finished");
    	mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPosition, mRightPosition, DrivetrainMode.MotionMagic, NeutralMode.Brake));
    	return true;
    }
    
    return false;
  }
  
  private boolean isFinished() {
	  boolean leftSideFinished = Math.abs(mLeftTargetPosition - mLeftPosition) < SystemSettings.AUTO_STRAIGHT_POS_TOLERANCE;
	  boolean rightSideFinished = Math.abs(mRightTargetPosition - mRightPosition) < SystemSettings.AUTO_STRAIGHT_POS_TOLERANCE;
	  return leftSideFinished && rightSideFinished;
  }
  
  public void shutdown(double pNow) {
    
  }
  
}
