package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.DriverInput;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class VisionTurn implements ICommand {
  
  private DriveTrain mDriveTrain;
  private DriverInput mDriverInput;
  private Data mData;
  
  private static final double kMIN_POWER = 0.06;
  private static final double kP = 0.017;
  
  private double mError, mTotalError, mAllowableError;
  private Double mLeftPower, mRightPower, mOutput = 0.0;

  public VisionTurn(DriveTrain pDriveControl, DriverInput pDriverInput, Data pData, double pAllowableError) {
    this.mDriveTrain = pDriveControl;
    this.mDriverInput = pDriverInput;
    this.mData = pData;
    this.mAllowableError = pAllowableError;
  }

  @Override
  public void initialize(double pNow) {
  }

  public boolean update(double pNow) {
    
    if(isFinished()) { 
      System.out.println("YER DON!");
      return true;
    
    }
    mError = SystemSettings.limelight.getEntry("tx").getDouble(-99);
    mOutput = (kP * mError);
    
    if (Math.abs(mOutput) < kMIN_POWER) {
      double scalar = mOutput > 0 ? 1 : -1;
      mOutput = kMIN_POWER * scalar;
    }
    
    mLeftPower = mDriverInput.getDesiredLeftOutput() + mOutput;
    mRightPower = mDriverInput.getDesiredRightOutput() + -mOutput;
    
    mDriveTrain
        .setDriveMessage(new DrivetrainMessage(mLeftPower, mRightPower, DrivetrainMode.PercentOutput, NeutralMode.Brake));
    
    //System.out.printf("Target: %s Yaw: %s\n", mSetpointDegrees, mData.pigeon.get(EPigeon.YAW));
    return false;
  }
  
  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
  }
  
  public boolean isFinished() {
    return Math.abs( SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tx").getDouble(-99) )<= mAllowableError;
  }
}
