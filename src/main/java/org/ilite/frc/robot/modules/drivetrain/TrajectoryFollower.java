package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import control.DriveController;
import control.DriveMotionPlanner;
import control.DriveOutput;
import lib.geometry.Pose2d;
import lib.util.ReflectingCSVWriter;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.IModule;
import profiles.LockdownProfile;

public class TrajectoryFollower implements IControlLoop {

    private ReflectingCSVWriter<Pose2d> mOdometryWriter = new ReflectingCSVWriter<>( "/home/lvuser/logs/ODOMETRY.csv", Pose2d.class);
    private ReflectingCSVWriter<DriveMotionPlanner> mTrajectoryWriter = new ReflectingCSVWriter<>("/home/lvuser/logs/TRAJECTORY.csv", DriveMotionPlanner.class);

    private DriveController mDriveController = new DriveController(new MikeyProfile(), SystemSettings.CONTROL_LOOP_PERIOD);
    private DriveOutput mCurrentDriveOutput = new DriveOutput();

    private final DriveTrain mDriveTrain;
    private final Hardware mHardware;

    private double mLastTimeUpdated = 0.0;
    private final double kP = 0.0;

    public TrajectoryFollower(DriveTrain pDriveTrain, Hardware pHardware) {
        mDriveTrain = pDriveTrain;
        mHardware = pHardware;
    }

    @Override
    public void initialize(double pNow) {

    }

    @Override
    public boolean update(double pNow) {

        writeToCsv();
        mCurrentDriveOutput = mDriveController.getOutput(pNow, mDriveTrain.getLeftInches(), mDriveTrain.getRightInches(), IMU.clampDegrees(mHardware.getPigeon().getHeading()));


        DrivetrainMessage driveMessage = new DrivetrainMessage(mCurrentDriveOutput.left_feedforward_voltage / 12.0,
                                                                mCurrentDriveOutput.right_feedforward_voltage / 12.0,
                                                                DrivetrainMode.PercentOutput, NeutralMode.Brake);

        // Hopefully this will bring the robot to a full stop at the end of the path
        if(mDriveController.isDone()) {
            mDriveTrain.zeroOutputs();
        } else {
            // Since we aren't trying to deal with Talon velocity control yet - correct for error manually
//            mCurrentDriveOutput = velocityFeedbackCorrection(mCurrentDriveOutput);
            mDriveTrain.setDriveMessage(driveMessage);
        }

        System.out.println("Updating TrajectoryFollower - DT is " + (pNow - mLastTimeUpdated));

        mLastTimeUpdated = pNow;
        return false;
    }

    @Override
    public void shutdown(double pNow) {
        mOdometryWriter.flush();
        mTrajectoryWriter.flush();
    }

    private double getLeftVelError(DriveOutput pOutputToCorrect) {
        return pOutputToCorrect.left_velocity - mDriveTrain.getLeftVelInches();
    }

    private double getRightVelError(DriveOutput pOutputToCorrect) {
        return pOutputToCorrect.right_velocity - mDriveTrain.getRightVelInches();
    }

    private DriveOutput velocityFeedbackCorrection(DriveOutput pOutputToCorrect) {
        DriveOutput correctedOutput = new DriveOutput();
        correctedOutput.left_accel = pOutputToCorrect.left_accel;
        correctedOutput.right_accel = pOutputToCorrect.right_accel;
        correctedOutput.left_velocity = pOutputToCorrect.left_velocity;
        correctedOutput.right_velocity = pOutputToCorrect.right_velocity;
        correctedOutput.left_feedforward_voltage = kP * getLeftVelError(pOutputToCorrect);
        correctedOutput.right_feedforward_voltage = kP * getRightVelError(pOutputToCorrect);

        return correctedOutput;
    }

    private void writeToCsv() {
        Pose2d latestPose = getDriveController().getRobotStateEstimator().getRobotState().getLatestFieldToVehiclePose();

        mOdometryWriter.add(latestPose);
        mTrajectoryWriter.add(getDriveController().getDriveMotionPlanner());
    }

    public DriveController getDriveController() {
        return mDriveController;
    }

    @Override
    public void loop(double pNow) {
        update(pNow);
    }
}
