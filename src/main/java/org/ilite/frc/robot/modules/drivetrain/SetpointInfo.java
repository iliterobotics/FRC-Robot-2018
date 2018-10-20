package org.ilite.frc.robot.modules.drivetrain;

public class SetpointInfo {

    public double time, leftValue, rightValue, leftGoal, rightGoal;

    public SetpointInfo(double pTime, double pLeftValue, double pRightValue, double pLeftGoal, double pRightGoal) {
        time = pTime;
        leftValue = pLeftValue;
        rightValue = pRightValue;
        leftGoal = pLeftGoal;
        rightGoal = pRightGoal;
    }
}
