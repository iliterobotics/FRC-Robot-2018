package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriveMessage {

  public final double leftOutput, rightOutput;
  public final DriveMode driveMode;
  public final NeutralMode neutralMode;
  public final boolean initMode;
  
  public DriveMessage(double leftOutput, double rightOutput, DriveMode driveMode, NeutralMode neutralMode) {
    this(leftOutput, rightOutput, driveMode, neutralMode, false);
  }
  
  public DriveMessage(double leftOutput, double rightOutput, DriveMode driveMode, NeutralMode neutralMode, boolean initMode) {
    this.leftOutput = leftOutput;
    this.rightOutput = rightOutput;
    this.driveMode = driveMode;
    this.neutralMode = neutralMode;
    this.initMode = initMode;
  }

}
