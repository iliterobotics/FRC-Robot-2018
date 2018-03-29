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

  /**
   * Tell the drive train to go and turn.  Both are scalars from -1.0 to 1.0.
   * @param pThrottle - positive = forward, negative = reverse
   * @param pTurn - positive = right, negative = left
   * @param pMode - Brake or Coast
   * @return an open loop drivetrain message
   */
  public static DrivetrainMessage fromThrottleAndTurn(double pThrottle, double pTurn, NeutralMode pMode) {
    return new DrivetrainMessage(pThrottle + pTurn, pThrottle - pTurn, DrivetrainMode.PercentOutput, pMode);
  }
}
