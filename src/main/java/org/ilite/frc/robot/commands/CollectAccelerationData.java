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

public class CollectAccelerationData implements ICommand {
    private static final double kPower = 0.5;
    private static final double kTotalTime = 2.0; //how long to run the test for
    private final DriveTrain mDrive;

    private final ReflectingCSVWriter<DriveCharacterization.AccelerationDataPoint> mLeftCSVWriter, mRightCSVWriter;
    private final List<DriveCharacterization.AccelerationDataPoint> mLeftAccelerationData, mRightAccelerationData;
    private final boolean mTurn;
    private final boolean mReverse;

    private double mStartTime = 0.0;
    private double mLeftPrevVelocity = 0.0, mRightPrevVelocity = 0.0;
    private double mPrevTime = 0.0;

    /**
     * @param leftData     reference to the list where data points should be stored
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */
    public CollectAccelerationData(DriveTrain pDriveTrain, List<DriveCharacterization.AccelerationDataPoint> leftData, List<DriveCharacterization.AccelerationDataPoint> rightData, boolean reverse, boolean turn) {
        mDrive = pDriveTrain;
        mLeftAccelerationData = leftData;
        mRightAccelerationData = rightData;
        mReverse = reverse;
        mTurn = turn;
        mLeftCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/LEFT_ACCEL_DATA.csv", DriveCharacterization.AccelerationDataPoint.class);
        mRightCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/RIGHT_ACCEL_DATA.csv", DriveCharacterization.AccelerationDataPoint.class);
    }

    @Override
    public void initialize(double pNow) {
        mDrive.setDriveMessage(new DrivetrainMessage((mReverse ? -1.0 : 1.0) * kPower, (mReverse ? -1.0 : 1.0) * (mTurn ? -1.0 : 1.0) * kPower, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mStartTime = Timer.getFPGATimestamp();
        mPrevTime = mStartTime;
    }

    @Override
    public boolean update(double pNow) {
        double currentLeftVelocity = Utils.ticksToRads(mDrive.getLeftVelTicks());
        double currentRightVelocity = Utils.ticksToRads(mDrive.getRightVelTicks());

        double currentTime = Timer.getFPGATimestamp();

        //don't calculate acceleration until we've populated prevTime and prevVelocity
        if (mPrevTime == mStartTime) {
            mPrevTime = currentTime;
            mLeftPrevVelocity = currentLeftVelocity;
            mRightPrevVelocity = currentRightVelocity;
            return false;
        }

        double leftAcceleration = (currentLeftVelocity - mLeftPrevVelocity) / (currentTime - mPrevTime);
        double rightAcceleration = (currentRightVelocity - mRightPrevVelocity) / (currentTime - mPrevTime);


        //ignore accelerations that are too small
        if (leftAcceleration < 1E-9) {
            mPrevTime = currentTime;
            mLeftPrevVelocity = currentLeftVelocity;
            return false;
        }

        //ignore accelerations that are too small
        if (rightAcceleration < 1E-9) {
            mPrevTime = currentTime;
            mRightPrevVelocity = currentRightVelocity;
            return false;
        }

        mLeftAccelerationData.add(new DriveCharacterization.AccelerationDataPoint(
                currentLeftVelocity, //convert to radians per second
                kPower * 12.0, //convert to volts
                leftAcceleration
        ));

        mRightAccelerationData.add(new DriveCharacterization.AccelerationDataPoint(
                currentRightVelocity, //convert to radians per second
                kPower * 12.0, //convert to volts
                rightAcceleration
        ));

        mLeftCSVWriter.add(mLeftAccelerationData.get(mLeftAccelerationData.size() - 1));
        mRightCSVWriter.add(mRightAccelerationData.get(mRightAccelerationData.size() - 1));


        mPrevTime = currentTime;
        mLeftPrevVelocity = currentLeftVelocity;
        mRightPrevVelocity = currentRightVelocity;

        if(Timer.getFPGATimestamp() - mStartTime > kTotalTime) return true;

        return false;
    }

    @Override
    public void shutdown(double pNow) {
        mDrive.setDriveMessage(new DrivetrainMessage(0.0, 0.0, DrivetrainMode.PercentOutput, NeutralMode.Coast));
        mLeftCSVWriter.flush();
        mRightCSVWriter.flush();
    }
}
