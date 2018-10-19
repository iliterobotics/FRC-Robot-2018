package org.ilite.frc.robot.modules.drivetrain;

import lib.util.Units;
import org.ilite.frc.common.config.SystemSettings;
import profiles.RobotProfile;

public class MikeyProfile implements RobotProfile {
    @Override
    public double getVoltPerAccel() {
        return 0.010284174323007975;
    }

    @Override
    public double getVoltPerSpeed() {
        return 0.18533132326271728;
    }

    @Override
    public double getFrictionVoltage() {
        return 0.896048227779587 * 0.99;
    }

    @Override
    public double getWheelRadiusMeters() {
        return Units.inches_to_meters(SystemSettings.DRIVETRAIN_WHEEL_DIAMETER) / 2.0;
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
