package org.ilite.frc.robot.commands;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class GyroTurn implements ICommand {
  
  private DriveTrain mDrivetrain;
  private Pigeon mPigeon;
  
  private static final int kMIN_ALIGNED_COUNT = 5;
  private static final double kTIMEOUT = 1.5;
  private static final double kP = 0.0121;
  private static final double kI = 0.0001;
  private static final double kD = 0.08;
  private static final double kMIN_POWER = 0.05;
  private static final double kMAX_POWER = 1.0;
  
  private double mInitialYaw, mTurnAngle, mTargetYaw;
  private double mError, mLastError, mTotalError;
  private double mLeftPower, mRightPower, mOutput = 0.0;
  private double mStartTime;
  private int mAlignedCount;
  private final double mAllowableError;
  
  public GyroTurn(DriveTrain pDrivetrain, Pigeon pPigeon, double pTurnAngle, double pAllowableError) {
    this.mDrivetrain = pDrivetrain;
    this.mPigeon = pPigeon;
    
    this.mTurnAngle = pTurnAngle;
    this.mAllowableError = pAllowableError;

    this.mAlignedCount = 0;
  }

  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
    
    mInitialYaw = getYaw();
    mTargetYaw = IMU.getAngleSum(mInitialYaw, mTurnAngle);
    
    this.mError = getError();
    this.mLastError = mError; // Calculate the initial error value
    this.mTotalError = mError;
  }

  public boolean update(double pNow) {
    
    mError = getError(); // Update error value
    this.mTotalError += this.mError; // Update running error total
    
    mOutput = ((kP * mError) + (kI * mTotalError) + (kD * (mError - mLastError)));
    
    int scalar = mOutput > 0 ? 1 : -1;
    if(Math.abs(mOutput) <= kMIN_POWER) mOutput = kMIN_POWER * scalar;
    
    if(Math.abs(mOutput) >= kMAX_POWER) mOutput = kMAX_POWER * scalar;
    
    mLeftPower = mOutput;
    mRightPower = -mOutput;
    mLastError = mError;

    if ((Math.abs(mError) <= Math.abs(mAllowableError))) {
      mDrivetrain.holdPosition();
//      mAlignedCount++;
      return true;
    }
    if(pNow - mStartTime > kTIMEOUT) {
//      mDrivetrain.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
      return true;
    }
    mDrivetrain.setDriveMessage(new DrivetrainMessage(mLeftPower, mRightPower, DrivetrainMode.PercentOutput, NeutralMode.Brake));
//    if(mAlignedCount >= kMIN_ALIGNED_COUNT) {
//      mDrivetrain.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Brake));
//      return true;
//    }
    
    System.out.printf("Target: %s Yaw: %s\n", mTargetYaw, getYaw());
    return false;
  }

  public double getError() {
    return IMU.getAngleDistance(mTargetYaw, getYaw());
  }
  
  private double getYaw() {
    return -IMU.clampDegrees(mPigeon.getYaw());
  }
  
  @Override
  public void shutdown(double pNow) {
    
  }
 
}
