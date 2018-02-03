package org.ilite.frc.robot.modules.drivetrain;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.followers.EncoderFollower;

public class PathFollower {
  
  public static DriveMessage calculateOutputs(EncoderFollower leftFollower, EncoderFollower rightFollower, int leftPosition, int rightPosition, double angle, boolean isBackwards) {
    double leftProfileOutput = leftFollower.calculate(leftPosition);
    double rightProfileOutput = rightFollower.calculate(rightPosition);
    
    double turnOutput = calculateAngleOutput(leftFollower, angle, isBackwards);
    
    double desiredLeftOutput = (leftProfileOutput + turnOutput);
    double desiredRightOutput = (rightProfileOutput - turnOutput);
    
    double actualLeftOutput = (isBackwards) ? desiredRightOutput : -desiredLeftOutput;
    double actualRightOutput = (isBackwards) ? desiredLeftOutput : -desiredRightOutput;
    return new DriveMessage(actualLeftOutput, actualRightOutput, DriveMode.Pathfinder, NeutralMode.Brake);
  }
  
  public static double calculateAngleOutput(EncoderFollower follower, double angle, boolean isBackwards) {
    double mActualHeading = (isBackwards) ? Pathfinder.boundHalfDegrees(angle + 180): angle;
    double mDesiredHeading = Pathfinder.r2d(follower.getHeading()); //Only need to use 1 side because both sides are parallel
    return SystemSettings.DRIVETRAIN_ANGLE_kP * Pathfinder.boundHalfDegrees(mDesiredHeading - mActualHeading);
  }
  
}
