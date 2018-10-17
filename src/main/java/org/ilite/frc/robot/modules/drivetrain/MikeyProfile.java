package org.ilite.frc.robot.modules.drivetrain;

import lib.util.Units;
import profiles.RobotProfile;

public class MikeyProfile implements RobotProfile {
    @Override
    public double getVoltPerAccel() {
        return 0.05827783679246295;
    }

    @Override
    public double getVoltPerSpeed() {
        return 0.7290574561714328;
    }

    @Override
    public double getFrictionVoltage() {
        return 0.9289097438974405;
    }

    @Override
    public double getWheelRadiusMeters() {
        return Units.inches_to_meters(5.875) / 2.0;
    }

    @Override
    public double getWheelbaseRadiusMeters() {
        return Units.inches_to_meters(24.0) / 2.0;
    }

    @Override
    public double getWheelbaseScrubFactor() {
        return 1.0;
    }

    @Override
    public double getLinearInertia() {
        return 1.0;
    }

    @Override
    public double getAngularInertia() {
        return 1.0;
    }

    @Override
    public double getAngularDrag() {
        return 1.0;
    }
}
