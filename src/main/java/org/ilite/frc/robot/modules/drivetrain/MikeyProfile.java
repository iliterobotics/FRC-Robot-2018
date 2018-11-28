package org.ilite.frc.robot.modules.drivetrain;

import lib.util.Units;
import org.ilite.frc.common.config.SystemSettings;
import profiles.RobotProfile;

public class MikeyProfile implements RobotProfile {
    @Override
    public double getRightVoltPerAccel() {
        return 0.016864324364531374;
    }

    @Override
    public double getRightVoltPerSpeed() {
        return 0.18073618726728796;
    }

    @Override
    public double getRightFrictionVoltage() {
        return 1.1959927471792733;
    }

    @Override
    public double getLeftVoltPerAccel() {
        return 0.013749622791233014;
    }

    @Override
    public double getLeftVoltPerSpeed() {
        return 0.17994777099607262;
    }

    @Override
    public double getLeftFrictionVoltage() {
        return 1.1869717385926033;
    }

    @Override
    public double getWheelRadiusMeters() {
        return Units.inches_to_meters(SystemSettings.DRIVETRAIN_WHEEL_DIAMETER / 2.0);
    }

    @Override
    public double getWheelbaseRadiusMeters() {
        return Units.inches_to_meters(24.5) / 2.0;
    }

    @Override
    public double getWheelbaseScrubFactor() {
        return 1.0;
    }

    @Override
    public double getLinearInertia() {
        return 27.2155;
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
