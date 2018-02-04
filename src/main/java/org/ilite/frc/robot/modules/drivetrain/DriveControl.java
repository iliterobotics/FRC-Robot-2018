package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriveControl {

  private Object messageLock = new Object();
  private double desiredLeftOutput, desiredRightOutput;
  private DriveMessage driveMessage;
  private ProfilingMessage profilingMessage;
  
  public DriveControl() {
    this.driveMessage = new DriveMessage(0, 0, DriveMode.PercentOutput, NeutralMode.Brake);
    this.profilingMessage = new ProfilingMessage(null, null, Double.NaN, false);
  }
  
  public void setDriveMessage(DriveMessage driveMessage) {
    synchronized(messageLock) {
      this.driveMessage = driveMessage;
    }
  }
  
  public void setProfilingMessage(ProfilingMessage profilingMessage) {
    synchronized(messageLock) {
      this.profilingMessage = profilingMessage;
    }
  }
  
  public DriveMessage getDriveMessage() {
    synchronized(messageLock) {
      return driveMessage;
    }
  }
  
  public ProfilingMessage getProfilingMessage() {
    synchronized(messageLock) {
      return profilingMessage;
    }
    
  }
  
}
