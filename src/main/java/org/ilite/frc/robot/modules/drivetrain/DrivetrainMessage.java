package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DrivetrainMessage {

  public final double leftOutput, rightOutput;
  public final DrivetrainMode leftDriveMode, rightDriveMode;
  public DemandType leftDemandType = DemandType.ArbitraryFeedForward;
  public DemandType rightDemandType = DemandType.ArbitraryFeedForward;
  public double leftDemand = 0.0;
  public double rightDemand = 0.0;
  public final NeutralMode leftNeutralMode, rightNeutralMode;
  public final boolean initMode;

  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode drivetrainMode, NeutralMode neutralMode) {
    this(leftOutput, rightOutput, drivetrainMode, drivetrainMode, neutralMode, neutralMode);
  }

  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode drivetrainMode, NeutralMode neutralMode, boolean initMode) {
    this(leftOutput, rightOutput, drivetrainMode, drivetrainMode, neutralMode, neutralMode, initMode);
  }

  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode leftDriveMode, DrivetrainMode rightDriveMode, NeutralMode neutralMode) {
    this(leftOutput, rightOutput, leftDriveMode, rightDriveMode, neutralMode, neutralMode, false);
  }

  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode leftDriveMode, DrivetrainMode rightDriveMode, NeutralMode leftNeutralMode, NeutralMode rightNeutralMode) {
    this(leftOutput, rightOutput, leftDriveMode, rightDriveMode, leftNeutralMode, rightNeutralMode, false);
  }

  public DrivetrainMessage(double leftOutput, double rightOutput, DrivetrainMode leftDriveMode, DrivetrainMode rightDriveMode, NeutralMode leftNeutralMode, NeutralMode rightNeutralMode, boolean initMode) {
    this.leftOutput = leftOutput;
    this.rightOutput = rightOutput;
    this.leftDriveMode = leftDriveMode;
    this.rightDriveMode = rightDriveMode;
    this.leftNeutralMode = leftNeutralMode;
    this.rightNeutralMode = rightNeutralMode;
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

  public DrivetrainMessage setDemand(DemandType pDemandType, double pLeftDemand, double pRightDemand) {
    this.leftDemandType = pDemandType;
    this.rightDemandType = pDemandType;
    this.leftDemand = pLeftDemand;
    this.rightDemand = pRightDemand;
    return this;
  }

}
