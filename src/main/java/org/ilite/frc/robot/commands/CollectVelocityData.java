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

    private final ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> mLeftCSVWriter, mRightCSVWriter;
    private final List<DriveCharacterization.VelocityDataPoint> mLeftVelocityData, mRightVelocityData;
    private final boolean mTurn;
    private final boolean mReverse;

    private boolean isFinished = false;
    private double mStartTime = 0.0;

    /**
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */

    public CollectVelocityData(DriveTrain pDriveTrain, List<DriveCharacterization.VelocityDataPoint> leftData, List<DriveCharacterization.VelocityDataPoint> rightData, boolean reverse, boolean turn) {
        mDriveTrain = pDriveTrain;
        mLeftVelocityData = leftData;
        mRightVelocityData = rightData;
        mReverse = reverse;
        mTurn = turn;
        mLeftCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/LEFT_VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);
        mRightCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/RIGHT_VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);
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

        updateData(mLeftVelocityData, mLeftCSVWriter, percentPower, mDriveTrain.getLeftVelTicks());
        updateData(mRightVelocityData, mRightCSVWriter, percentPower, mDriveTrain.getRightVelTicks());

        return isFinished;
    }

    public void updateData(List<DriveCharacterization.VelocityDataPoint> pVelocityDataPoints, ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> pCSVWriter, double pCurrentPercentPower, double pVelocityTicks) {
        pVelocityDataPoints.add(new DriveCharacterization.VelocityDataPoint(
                Utils.ticksToRads(pVelocityTicks), //convert velocity to radians per second
                pCurrentPercentPower * 12.0 //convert to volts
        ));
        pCSVWriter.add(pVelocityDataPoints.get(pVelocityDataPoints.size() - 1));
    }

    @Override
    public void shutdown(double pNow) {
        mDriveTrain.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mLeftCSVWriter.flush();
        mRightCSVWriter.flush();
    }
}
