package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class CubeStraight implements ICommand{
  
  private double mTargetDist, mCurrentDist;
  
  private DriveControl mDriveControl;
  private Data mData;

  
  public CubeStraight(Data pData, DriveControl pDriveControl) {
    this.mDriveControl = pDriveControl;
    this.mData = pData;
    this.mTargetDist = 36.0;
  }
  
  
  @SuppressWarnings("static-access")
  @Override
  public void initialize(double pNow) {
    mCurrentDist = SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("ta").getDouble(-99);
    
  }
  @SuppressWarnings("static-access")
  @Override
  public boolean update(double pNow) {
    if(isFinished()) {
      mDriveControl.setDriveMessage(new DriveMessage(0.0, 0.0, DriveMode.PercentOutput, NeutralMode.Brake));
      return true;
    }
    
    if(isQuarterway())
      realign();
    mCurrentDist = SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("ta").getDouble(-99);
    mDriveControl.setDriveMessage(new DriveMessage(0.2, 0.2, DriveMode.PercentOutput, NeutralMode.Brake));
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }
  @SuppressWarnings("static-access")
  public boolean isFinished() {
    return (mCurrentDist >= mTargetDist)||(SystemSettings.limelight.getInstance().getDefault().getTable("limelight").getEntry("tv").getDouble(-1) == 0);
    
  }
  
  public boolean isQuarterway() {
    return mCurrentDist == (mTargetDist - mTargetDist*0.5);
  }
  
  public boolean realign()
  {
    GyroTurn gTurn = new GyroTurn(mDriveControl, mData, 3);
    gTurn.update(0);
    return true;
  }
  
  

}
