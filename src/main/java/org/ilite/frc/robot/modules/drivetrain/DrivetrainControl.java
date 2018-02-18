package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DrivetrainControl {

  private Object messageLock = new Object();
  private DrivetrainMessage driveMessage;
  private DrivetrainProfilingMessage profilingMessage;
  
  public DrivetrainControl() {
    this.driveMessage = new DrivetrainMessage(0, 0, DrivetrainMode.PercentOutput, NeutralMode.Brake);
    this.profilingMessage = new DrivetrainProfilingMessage(null, null, false);
  }
  
  public void setDriveMessage(DrivetrainMessage driveMessage) {
    synchronized(messageLock) {
      this.driveMessage = driveMessage;
    }
  }
  
  public void setProfilingMessage(DrivetrainProfilingMessage profilingMessage) {
    synchronized(messageLock) {
      this.profilingMessage = profilingMessage;
    }
  }
  
  public DrivetrainMessage getDriveMessage() {
    synchronized(messageLock) {
      return driveMessage;
    }
  }
  
  public DrivetrainProfilingMessage getProfilingMessage() {
    synchronized(messageLock) {
      return profilingMessage;
    }
    
  }
  
}
