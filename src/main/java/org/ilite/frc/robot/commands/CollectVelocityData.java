package org.ilite.frc.robot.commands;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team254.lib.util.ReflectingCSVWriter;
import edu.wpi.first.wpilibj.Timer;
import lib.physics.DriveCharacterization;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import java.util.List;

public class CollectVelocityData implements ICommand {
    private static final double kMaxPower = 0.25;
    private static final double kRampRate = 0.02;
    private final DriveTrain mDriveTrain;

    private final ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> mCSVWriter;
    private final List<DriveCharacterization.VelocityDataPoint> mVelocityData;
    private final boolean mTurn;
    private final boolean mReverse;
    private final boolean mHighGear;

    private boolean isFinished = false;
    private double mStartTime = 0.0;

    /**
     * @param data     reference to the list where data points should be stored
     * @param highGear use high gear or low
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */

    public CollectVelocityData(DriveTrain pDriveTrain, List<DriveCharacterization.VelocityDataPoint> data, boolean highGear, boolean reverse, boolean turn) {
        mDriveTrain = pDriveTrain;
        mVelocityData = data;
        mHighGear = highGear;
        mReverse = reverse;
        mTurn = turn;
        mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);

    }

    @Override
    public void initialize(double pNow) {
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public boolean update(double pNow) {
        double percentPower = kRampRate * (Timer.getFPGATimestamp() - mStartTime);
        if (percentPower > kMaxPower) {
            isFinished = true;
            return true;
        }
        mDriveTrain.setDriveMessage(new DrivetrainMessage((mReverse ? -1.0 : 1.0) * percentPower, (mReverse ? -1.0 : 1.0) * (mTurn ? -1.0 : 1.0) * percentPower, DrivetrainMode.PercentOutput, NeutralMode.Coast));

        double averageTicksPer100Ms = (Math.abs(mDriveTrain.getLeftMaster().getSelectedSensorVelocity(0)) + Math.abs(mDriveTrain.getRightMaster().getSelectedSensorVelocity(0))) / 2;
        mVelocityData.add(new DriveCharacterization.VelocityDataPoint(
                Utils.ticksToRads(averageTicksPer100Ms), //convert velocity to radians per second
                percentPower * 12.0 //convert to volts
        ));
        mCSVWriter.add(mVelocityData.get(mVelocityData.size() - 1));

        return isFinished;
    }

    @Override
    public void shutdown(double pNow) {
        mDriveTrain.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mCSVWriter.flush();
    }
}
