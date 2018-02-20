package org.ilite.frc.robot.modules.drivetrain;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.followers.EncoderFollower;

public class PathFollower {
  
  public static DrivetrainMessage calculateOutputs(EncoderFollower leftFollower, EncoderFollower rightFollower, int left, int right, double angle, boolean isBackwards) {
    
    double turnOutput = calculateAngleOutput(leftFollower, IMU.clampDegrees(angle), isBackwards);
    double leftOutput = (leftFollower.calculate(left) + turnOutput);
    double rightOutput = (rightFollower.calculate(right) - turnOutput);
//    System.out.printf("Left: %s Right: %s\n", leftOutput, rightOutput);
    if(isBackwards) {
      leftOutput *= -1;
      rightOutput *= -1;
    }
    
    return new DrivetrainMessage(leftOutput, rightOutput, DrivetrainMode.Pathfinder, NeutralMode.Brake);
  }
  
  public static double calculateAngleOutput(EncoderFollower follower, double angle, boolean isBackwards) {
    double mActualHeading = (isBackwards) ? Pathfinder.boundHalfDegrees(angle + 180) : Pathfinder.boundHalfDegrees(angle);
    double mDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(follower.getHeading())); //Only need to use 1 side because both sides are parallel
    mDesiredHeading = (isBackwards) ? Pathfinder.boundHalfDegrees(mDesiredHeading + 180) : mDesiredHeading;
    double mHeadingError = Pathfinder.boundHalfDegrees(mDesiredHeading - mActualHeading);
    double mOutput = SystemSettings.DRIVETRAIN_ANGLE_kP * mHeadingError;
//    System.out.printf("Actual Heading: %s Desired Heading: %s Heading Error: %s Heading Output: %s\n", mActualHeading, mDesiredHeading, mHeadingError, mOutput);
    return -mOutput;
  }
  
}