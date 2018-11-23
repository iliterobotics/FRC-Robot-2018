package org.ilite.frc.robot.commands;

import com.team254.lib.util.ReflectingCSVWriter;
import control.DriveMotionPlanner;
import lib.geometry.Pose2d;
import lib.geometry.Pose2dWithCurvature;
import lib.trajectory.Trajectory;
import lib.trajectory.timing.TimedState;
import org.ilite.frc.robot.modules.drivetrain.TrajectoryFollower;

public class FollowTrajectory implements ICommand {

    private Trajectory<TimedState<Pose2dWithCurvature>> mTrajectory;
    private TrajectoryFollower mTrajectoryFollower;
    private boolean mResetPose;

    private boolean mWriteToCsv = false;

    public FollowTrajectory(Trajectory<TimedState<Pose2dWithCurvature>> pTrajectory, TrajectoryFollower pTrajectoryFollower, boolean pResetPose) {
        mTrajectory = pTrajectory;
        mTrajectoryFollower = pTrajectoryFollower;
        mResetPose = pResetPose;
    }

    @Override
    public void initialize(double pNow) {
        mTrajectoryFollower.getDriveController().setTrajectory(mTrajectory, mResetPose);
        mTrajectoryFollower.enable();
    }

    @Override
    public boolean update(double pNow) {
        if(mTrajectoryFollower.getDriveController().isDone()) {
            return true;
        }
        return false;
    }

    @Override
    public void shutdown(double pNow) {
        mTrajectoryFollower.disable();
    }

    public FollowTrajectory setWriteToCsv() {
        mWriteToCsv = true;
        return this;
    }

}
