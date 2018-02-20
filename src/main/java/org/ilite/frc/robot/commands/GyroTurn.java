package org.ilite.frc.robot.commands;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainControl;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;
import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class GyroTurn implements ICommand {
  
  private DrivetrainControl mDriveControl;
  private Data mData;	
  
  private static final int kMIN_ALIGNED_COUNT = 5;
  private static final Double kP = 0.017;
  private static final Double kI = 0.0000008;
  private static final Double kD = 0.08;
  private static final Double kMIN_POWER = 0.05;
  private static final Double kMAX_POWER = 0.3;
  //private static Double visionAngle;
  
  private Double mSetpointDegrees, mTargetYaw, mCurrentYaw, mVisionAngle;
  private Double mError, mLastError, mTotalError;
  private Double mAlignedCount;
  private final Double mAllowableError;
  private Double mLeftPower, mRightPower, mOutput = 0.0;
  private final Double turnAngle;
  private final String mDirection;

//  public GyroTurn(DriveControl pDriveControl, Data pData, double pAllowableError) {
//    this.mDriveControl = pDriveControl;
//    this.mData = pData;
//    Double visionAngle = mData.vision.get(ECubeTarget.DELTA_ANGLE);
//    this.mSetpointDegrees = visionAngle == null ? 0 : visionAngle;
//    this.mAlignedCount = 0.0;
//    this.mAllowableError = pAllowableError;
//  }
  
  public GyroTurn(DrivetrainControl pDriveControl, Data pData, double angle, double pAllowableError, String pDirection) {
	    this.mDriveControl = pDriveControl;
	    this.mData = pData;
	    turnAngle = angle;
	    this.mSetpointDegrees = turnAngle;//== null ? 0 : visionAngle;
	    this.mAlignedCount = 0.0;
	    this.mAllowableError = pAllowableError;
	    this.mDirection = pDirection;
  }

  @Override
  public void initialize(double pNow) {
    updateValues();
	    this.mError = getError();
	    this.mLastError = mError; // Calculate the initial error value
	    this.mTotalError = this.mError;
  }

  public boolean update(double pNow) {
    updateValues();
    
    mError = getError(); // Update error value
    this.mTotalError += this.mError; // Update running error total
    if ((Math.abs(mError) < mAllowableError)) mAlignedCount++;
    
    // if(mAlignedCount >= kMIN_ALIGNED_COUNT) return true;
    // if(System.currentTimeMillis() - mStartTime > TIMEOUT) return true;
    mOutput = ((kP * mError) + (kI * mTotalError) + (kD * (mError - mLastError)));
    
    if (Math.abs(mOutput) < kMIN_POWER) {
      double scalar = mOutput > 0 ? 1 : -1;
      mOutput = kMIN_POWER * scalar;
    }
    
    if(Math.abs(mOutput) > kMAX_POWER)
    {
    	double scalar = mOutput > 0 ? 1 : -1;
    	mOutput = kMAX_POWER * scalar;
    }
    
    mLeftPower = mOutput;
    mRightPower = -mOutput;
    
    mDriveControl.setDriveMessage(new DrivetrainMessage(mLeftPower, mRightPower, DrivetrainMode.PercentOutput, NeutralMode.Brake));
    
    mLastError = mError;
    
    if(mAlignedCount >= kMIN_ALIGNED_COUNT) {
      mDriveControl.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
      return true;
    }
    
    System.out.printf("Target: %s Yaw: %s\n", mSetpointDegrees, mData.pigeon.get(EPigeon.YAW));
    return false;
  }

  public double getError() {
    return IMU.getAngleDistance(IMU.clampDegrees(mCurrentYaw), mTargetYaw);
  }

  private void updateValues() {
    mCurrentYaw = mData.pigeon.get(EPigeon.YAW);
    mCurrentYaw = mCurrentYaw == null ? 0 : mCurrentYaw;
    //mVisionAngle = mData.vision.get(ECubeTarget.DELTA_ANGLE);
    //mTargetYaw = mVisionAngle == null ? mCurrentYaw : -mVisionAngle;
    mTargetYaw = mDirection.equalsIgnoreCase("Left") ? mSetpointDegrees : (Math.abs(360-mSetpointDegrees));
  }
  
  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
  }
}