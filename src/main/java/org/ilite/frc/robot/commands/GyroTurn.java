package org.ilite.frc.robot.commands;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.ECubeTarget;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;

public class GyroTurn implements ICommand {
  
  private DriveControl mDriveControl;
  private Data mData;
  
  private static final int kMIN_ALIGNED_COUNT = 5;
  private static final Double kP = 0.010;
  private static final Double kD = 0.0;
  private static final Double kI = 0.0;
  private static final Double kMIN_POWER = 0.05;
  
  private Double mSetpointDegrees, mTargetYaw, mCurrentYaw, mVisionAngle;
  private Double mError, mLastError, mTotalError;
  private Double mAlignedCount;
  private final Double mAllowableError;
  private Double mLeftPower, mRightPower, mOutput = 0.0;

  public GyroTurn(DriveControl pDriveControl, Data pData, double pAllowableError) {
    this.mDriveControl = pDriveControl;
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

  @SuppressWarnings("static-access")
  public boolean update(double pNow) {
    
    if(isFinished()) { 
      System.out.println("YER DON!");
      return true;
    
    }
//    System.out.println("tx: " +SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99));
    System.out.println("area: " + SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("ta").getDouble(-99));
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
    
    mLeftPower = mOutput;
    mRightPower = -mOutput;
    
    mDriveControl
        .setDriveMessage(new DriveMessage(mLeftPower, mRightPower, DriveMode.PercentOutput, NeutralMode.Brake));
    
    mLastError = mError;
    System.out.println("vision: " + mVisionAngle);
    //System.out.printf("Target: %s Yaw: %s\n", mSetpointDegrees, mData.pigeon.get(EPigeon.YAW));
    return false;
  }

  public double getError() {
    return IMU.getAngleDistance(IMU.clampDegrees(mCurrentYaw), mTargetYaw);
  }

  @SuppressWarnings("static-access")
  private void updateValues() {
    mCurrentYaw = mData.pigeon.get(EPigeon.YAW);
    mCurrentYaw = mCurrentYaw == null ? 0 : mCurrentYaw;
    System.out.println(SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99) + "");
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
  
  public boolean isFinished() {
    return mVisionAngle <= 12.0;
  }
}
