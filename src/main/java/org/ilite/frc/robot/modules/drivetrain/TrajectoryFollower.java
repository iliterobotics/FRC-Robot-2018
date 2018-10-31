package org.ilite.frc.robot.modules.drivetrain;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import control.DriveController;
import control.DriveMotionPlanner;
import control.DriveOutput;
import lib.geometry.Pose2d;
import lib.geometry.Rotation2d;
import lib.util.ReflectingCSVWriter;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.DriveTrain;

import java.util.Set;

public class TrajectoryFollower implements IControlLoop {

    // These must be OUR implementation
    private ReflectingCSVWriter<Pose2d> mOdometryWriter = new ReflectingCSVWriter<>( "/home/lvuser/ODOMETRY.csv", Pose2d.class);
    private ReflectingCSVWriter<DriveMotionPlanner> mTrajectoryWriter = new ReflectingCSVWriter<>("/home/lvuser/TRAJECTORY.csv", DriveMotionPlanner.class);
    private ReflectingCSVWriter<SetpointInfo> mSetpointWriter = new ReflectingCSVWriter<>("/home/lvuser/SETPOINT.csv", SetpointInfo.class);

    private DriveController mDriveController = new DriveController(new MikeyProfile(), SystemSettings.CONTROL_LOOP_PERIOD);
    private DriveOutput mCurrentDriveOutput = new DriveOutput();

    private final DriveTrain mDriveTrain;
    private final Hardware mHardware;

    private double mLastTimeUpdated = 0.0;
    private final double kP = 0.2;

    private double leftVelRads = 0.0;
    private double rightVelRads = 0.0;

    private boolean enabled = false;

    public TrajectoryFollower(DriveTrain pDriveTrain, Hardware pHardware) {
        mDriveTrain = pDriveTrain;
        mHardware = pHardware;
    }

    @Override
    public void initialize(double pNow) {

    }

    @Override
    public boolean update(double pNow) {

        writeToCsv(pNow);

        leftVelRads = Utils.ticksToRads(mDriveTrain.getLeftVelTicks());
        rightVelRads = Utils.ticksToRads(mDriveTrain.getRightVelTicks());
        // Invert heading later
        mCurrentDriveOutput = mDriveController.getOutput(pNow, mDriveTrain.getLeftInches(), mDriveTrain.getRightInches(), Rotation2d.fromDegrees(IMU.clampDegrees(mHardware.getPigeon().getHeading())).getDegrees());


        // Hopefully this will bring the robot to a full stop at the end of the path
        if(mDriveController.isDone() && enabled) {
            mDriveTrain.zeroOutputs();
            System.out.println("DONE");
        } else if (!mDriveController.isDone() && enabled){
            // Since we aren't trying to deal with Talon velocity control yet - correct for error manually
            // mCurrentDriveOutput = velocityFeedbackCorrection(mCurrentDriveOutput);
            DrivetrainMessage driveMessage = new DrivetrainMessage(
                radiansPerSecondToTicksPer100ms(mCurrentDriveOutput.left_velocity),
                radiansPerSecondToTicksPer100ms(mCurrentDriveOutput.right_velocity),
                DrivetrainMode.Velocity, NeutralMode.Brake);

            driveMessage.setDemand(
             DemandType.ArbitraryFeedForward,
             (mCurrentDriveOutput.left_feedforward_voltage / 12.0) + (SystemSettings.DRIVE_VELOCITY_D * (radiansPerSecondToTicksPer100ms(mCurrentDriveOutput.left_accel) / 1000.0) / 1023.0),
             (mCurrentDriveOutput.right_feedforward_voltage / 12.0) + (SystemSettings.DRIVE_VELOCITY_D * (radiansPerSecondToTicksPer100ms(mCurrentDriveOutput.right_accel) / 1000.0) / 1023.0));

            mDriveTrain.setDriveMessage(driveMessage);
        }
        System.out.println(pNow - mLastTimeUpdated);

        mLastTimeUpdated = pNow;
        return false;
    }

    private static double rotationsToInches(double rotations) {
        return rotations * (SystemSettings.DRIVETRAIN_WHEEL_DIAMETER * Math.PI);
    }

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    private static double inchesToRotations(double inches) {
        return inches / (SystemSettings.DRIVETRAIN_WHEEL_DIAMETER * Math.PI);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 1024.0 / 10.0;
    }

    @Override
    public void shutdown(double pNow) {
        mOdometryWriter.flush();
        mTrajectoryWriter.flush();
        mSetpointWriter.flush();
    }

    private double getLeftVelError(DriveOutput pOutputToCorrect) {
        return pOutputToCorrect.left_velocity - leftVelRads;
    }

    private double getRightVelError(DriveOutput pOutputToCorrect) {
        return pOutputToCorrect.right_velocity - rightVelRads;
    }

    private DriveOutput velocityFeedbackCorrection(DriveOutput pOutputToCorrect) {
        DriveOutput correctedOutput = new DriveOutput();
        correctedOutput.left_accel = pOutputToCorrect.left_accel;
        correctedOutput.right_accel = pOutputToCorrect.right_accel;
        correctedOutput.left_velocity = pOutputToCorrect.left_velocity;
        correctedOutput.right_velocity = pOutputToCorrect.right_velocity;
        correctedOutput.left_feedforward_voltage = pOutputToCorrect.left_feedforward_voltage + kP * getLeftVelError(pOutputToCorrect);
        correctedOutput.right_feedforward_voltage = pOutputToCorrect.right_feedforward_voltage + kP * getRightVelError(pOutputToCorrect);

        return correctedOutput;
    }

    private void writeToCsv(double time) {
        Pose2d latestPose = getDriveController().getRobotStateEstimator().getRobotState().getLatestFieldToVehiclePose();

        mSetpointWriter.add(new SetpointInfo(time, leftVelRads, rightVelRads, mCurrentDriveOutput.left_velocity, mCurrentDriveOutput.right_velocity));
        mOdometryWriter.add(latestPose);
        mTrajectoryWriter.add(getDriveController().getDriveMotionPlanner());
    }

    public DriveController getDriveController() {
        return mDriveController;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void loop(double pNow) {
        update(pNow);
    }
}
