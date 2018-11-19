package org.ilite.frc.robot.modules.drivetrain;

import lib.util.Units;
import org.ilite.frc.common.config.SystemSettings;
import profiles.RobotProfile;

public class MikeyProfile implements RobotProfile {
    @Override
    public double getLeftVoltPerAccel() {
        return 0.03217677393640587 * 1.5;
    }

    @Override
    public double getLeftVoltPerSpeed() {
        return 0.2145841808650226;
    }

    @Override
    public double getLeftFrictionVoltage() {
        return 0.7892487640968108;
    }

    @Override
    public double getRightVoltPerAccel() {
        return 0.02757180183116733 * 1.5;
    }

    @Override
    public double getRightVoltPerSpeed() {
        return 0.20865527274973875;
    }

    @Override
    public double getRightFrictionVoltage() {
        return 0.8438511952245853;
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
        return 60.0;
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
