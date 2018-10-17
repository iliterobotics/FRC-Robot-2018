package org.ilite.frc.robot.commands;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team254.lib.util.DriveSignal;
import com.team254.lib.util.ReflectingCSVWriter;
import com.team254.lib.util.Util;
import edu.wpi.first.wpilibj.Timer;
import lib.physics.DriveCharacterization;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import java.util.List;

public class CollectAccelerationData implements ICommand {
    private static final double kPower = 0.5;
    private static final double kTotalTime = 2.0; //how long to run the test for
    private final DriveTrain mDrive;

    private final ReflectingCSVWriter<DriveCharacterization.AccelerationDataPoint> mCSVWriter;
    private final List<DriveCharacterization.AccelerationDataPoint> mAccelerationData;
    private final boolean mTurn;
    private final boolean mReverse;
    private final boolean mHighGear;

    private double mStartTime = 0.0;
    private double mPrevVelocity = 0.0;
    private double mPrevTime = 0.0;

    /**
     * @param data     reference to the list where data points should be stored
     * @param highGear use high gear or low
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */
    public CollectAccelerationData(DriveTrain pDriveTrain, List<DriveCharacterization.AccelerationDataPoint> data, boolean highGear, boolean reverse, boolean turn) {
        mDrive = pDriveTrain;
        mAccelerationData = data;
        mHighGear = highGear;
        mReverse = reverse;
        mTurn = turn;
        mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/ACCEL_DATA.csv", DriveCharacterization.AccelerationDataPoint.class);
    }

    @Override
    public void initialize(double pNow) {
        mDrive.setDriveMessage(new DrivetrainMessage((mReverse ? -1.0 : 1.0) * kPower, (mReverse ? -1.0 : 1.0) * (mTurn ? -1.0 : 1.0) * kPower, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mStartTime = Timer.getFPGATimestamp();
        mPrevTime = mStartTime;
    }

    @Override
    public boolean update(double pNow) {
        double averageTicksPer100Ms = (Math.abs(mDrive.getLeftMaster().getSelectedSensorVelocity(0)) + Math.abs(mDrive.getRightMaster().getSelectedSensorVelocity(0))) / 2;
        double currentVelocity = Utils.ticksToRads(averageTicksPer100Ms);
        double currentTime = Timer.getFPGATimestamp();

        //don't calculate acceleration until we've populated prevTime and prevVelocity
        if (mPrevTime == mStartTime) {
            mPrevTime = currentTime;
            mPrevVelocity = currentVelocity;
            return false;
        }

        double acceleration = (currentVelocity - mPrevVelocity) / (currentTime - mPrevTime);

        //ignore accelerations that are too small
        if (acceleration < 1E-9) {
            mPrevTime = currentTime;
            mPrevVelocity = currentVelocity;
            return false;
        }

        mAccelerationData.add(new DriveCharacterization.AccelerationDataPoint(
                currentVelocity, //convert to radians per second
                kPower * 12.0, //convert to volts
                acceleration
        ));

        mCSVWriter.add(mAccelerationData.get(mAccelerationData.size() - 1));

        mPrevTime = currentTime;
        mPrevVelocity = currentVelocity;

        if(Timer.getFPGATimestamp() - mStartTime > kTotalTime) return true;

        return false;
    }

    @Override
    public void shutdown(double pNow) {
        mDrive.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mCSVWriter.flush();
    }
}
