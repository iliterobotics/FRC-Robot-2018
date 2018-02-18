package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DrivetrainMessage {

  public final double leftOutput, rightOutput;
  public final DrivetrainMode driveMode;
  public final NeutralMode neutralMode;
  public final boolean initMode;
  
  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode driveMode, NeutralMode neutralMode) {
    this(leftOutput, rightOutput, driveMode, neutralMode, false);
  }
  
  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode driveMode, NeutralMode neutralMode, boolean initMode) {
    this.leftOutput = leftOutput;
    this.rightOutput = rightOutput;
    this.driveMode = driveMode;
    this.neutralMode = neutralMode;
    this.initMode = initMode;
  }

}
