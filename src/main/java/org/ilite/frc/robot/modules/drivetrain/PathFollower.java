package org.ilite.frc.robot.modules.drivetrain;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.followers.EncoderFollower;

public class PathFollower {
  
  public static DriveMessage calculateOutputs(EncoderFollower leftFollower, EncoderFollower rightFollower, int leftPosition, int rightPosition, double angle, boolean isBackwards) {
    
    double turnOutput = calculateAngleOutput(leftFollower, angle, isBackwards);
    double leftOutput = (leftFollower.calculate(leftPosition) + turnOutput);
    double rightOutput = (rightFollower.calculate(rightPosition) - turnOutput);
    
    if(isBackwards) {
      leftOutput *= -1;
      rightOutput *= -1;
    }
    
    return new DriveMessage(leftOutput, rightOutput, DriveMode.Pathfinder, NeutralMode.Brake);
  }
  
  public static double calculateAngleOutput(EncoderFollower follower, double angle, boolean isBackwards) {
    double mActualHeading = (isBackwards) ? Pathfinder.boundHalfDegrees(angle + 180) : Pathfinder.boundHalfDegrees(angle);
    double mDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(follower.getHeading())); //Only need to use 1 side because both sides are parallel
    mDesiredHeading = (isBackwards) ? Pathfinder.boundHalfDegrees(mDesiredHeading + 180) : mDesiredHeading;
    double mHeadingError = Pathfinder.boundHalfDegrees(mDesiredHeading - mActualHeading);
    double mOutput = SystemSettings.DRIVETRAIN_ANGLE_kP * mHeadingError;
    System.out.printf("Actual Heading: %s Desired Heading: %s Heading Error: %s Heading Output: %s\n", mActualHeading, mDesiredHeading, mHeadingError, mOutput);
    return -mOutput;
  }
  
}