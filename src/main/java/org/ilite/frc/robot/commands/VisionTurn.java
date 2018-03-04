package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class VisionTurn implements ICommand {
  
  private DriveTrain mDriveTrain;
  private Data mData;
  
  private static final int kMIN_ALIGNED_COUNT = 5;
  private static final Double kP = 0.017;
  private static final Double kD = 0.0;
  private static final Double kI = 0.0;
  private static final Double kMIN_POWER = 0.05;
  
  private Double mSetpointDegrees, mTargetYaw, mCurrentYaw, mVisionAngle;
  private Double mError, mLastError;
  private Double mTotalError = 0.0;
  private Double mAlignedCount;
  private final Double mAllowableError;
  private Double mLeftPower, mRightPower, mOutput = 0.0;

  public VisionTurn(DriveTrain pDriveControl, Data pData, double pAllowableError) {
    this.mDriveTrain = pDriveControl;
    this.mData = pData;
//    NetworkTable table = System
    Double visionAngle = mData.vision.get(ECubeTarget.DELTA_ANGLE);
    this.mSetpointDegrees = visionAngle == null ? 0 : visionAngle;
    this.mAlignedCount = 0.0;
    this.mAllowableError = pAllowableError;
  }

  @Override
  public void initialize(double pNow) {
    updateValues();
    this.mError = getError();
    this.mLastError = mError; // Calculate the initial error value
    this.mTotalError = this.mError;
  }

  public boolean update(double pNow) {
    
    if(isFinished()) { 
      System.out.println("YER DON!");
      return true;
    
    }
    
    System.out.println("Not Done Yet....!!");
//    System.out.println("tx: " +SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99));
    updateValues();
    mError = getError(); // Update error value
    System.out.println("mError: " + mError);
    System.out.println("mTotalError: " + mTotalError);
    this.mTotalError += this.mError; // Update running error total
    if ((Math.abs(mError) < mAllowableError)) mAlignedCount++;
    
    mLastError = mError;
    // if(mAlignedCount >= kMIN_ALIGNED_COUNT) return true;
    // if(System.currentTimeMillis() - mStartTime > TIMEOUT) return true;
    mOutput = ((kP * mError) + (kI * mTotalError) + (kD * (mError - mLastError)));
    
    if (Math.abs(mOutput) < kMIN_POWER) {
      double scalar = mOutput > 0 ? 1 : -1;
      mOutput = kMIN_POWER * scalar;
    }
    
    mLeftPower = mOutput;
    mRightPower = -mOutput;
    
    mDriveTrain
        .setDriveMessage(new DrivetrainMessage(mLeftPower, mRightPower, DrivetrainMode.PercentOutput, NeutralMode.Brake));
    
    
    System.out.println("vision: " + mVisionAngle);
    //System.out.printf("Target: %s Yaw: %s\n", mSetpointDegrees, mData.pigeon.get(EPigeon.YAW));
    return false;
  }

  public double getError() {
    Double error = IMU.getAngleDistance(IMU.clampDegrees(mCurrentYaw), mTargetYaw);
    return (error == null) ? 0.0 : error;
  }

  @SuppressWarnings("static-access")
  private void updateValues() {
    mCurrentYaw = mData.pigeon.get(EPigeon.YAW);
    mCurrentYaw = mCurrentYaw == null ? 0 : mCurrentYaw;
    double modifiedAngle = SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99);
//    if(modifiedAngle<0)
//    {
//      modifiedAngle -= 30.0;
//    }
    mVisionAngle = modifiedAngle;//SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99);//mData.vision.get(ECubeTarget.DELTA_ANGLE);
    mTargetYaw = mVisionAngle == null ? mCurrentYaw : -mVisionAngle;
  }
  
  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
  }
  
  @SuppressWarnings("static-access")
public boolean isFinished() {
    return Math.abs( SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99) )<= mAllowableError;
  }
}
