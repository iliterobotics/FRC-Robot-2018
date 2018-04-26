package org.ilite.frc.robot.commands;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

public class HoldPosition implements ICommand {

    private DriveTrain mDrivetrain;
    private Data mData;
    private HoldType mHoldType;
    private double mHoldTime;
    private double mStartTime;

    public enum HoldType {
        HOLD_LEFT, HOLD_RIGHT, HOLD_BOTH;
    }

    public HoldPosition(DriveTrain pDrivetrain, Data pData, HoldType pHoldType, double pHoldTime) {
        this.mDrivetrain = pDrivetrain;
        this.mData = pData;
        this.mHoldType = pHoldType;
        this.mHoldTime = pHoldTime;
    }

    @Override
    public void initialize(double pNow) {
        mStartTime = pNow;
        System.out.println(mDrivetrain);
        switch (mHoldType) {
            case HOLD_LEFT:
                mDrivetrain.setDriveMessage(new DrivetrainMessage(getLeftPositionTicks(), 0.0, DrivetrainMode.Position, DrivetrainMode.PercentOutput, NeutralMode.Brake));
                break;
            case HOLD_RIGHT:
                mDrivetrain.setDriveMessage(new DrivetrainMessage(0.0, getRightPositionTicks(), DrivetrainMode.PercentOutput, DrivetrainMode.Position, NeutralMode.Brake));
                break;
            case HOLD_BOTH:
                mDrivetrain.setDriveMessage(new DrivetrainMessage(getLeftPositionTicks(), getRightPositionTicks(), DrivetrainMode.Position, DrivetrainMode.Position, NeutralMode.Brake));
                break;
        }
    }

    @Override
    public boolean update(double pNow) {
        if(pNow - mStartTime > mHoldTime) {
            return true;
        }
        return false;
    }

    @Override
    public void shutdown(double pNow) {

    }

    private double getLeftPositionTicks() {
        return mData.drivetrain.get(EDriveTrain.LEFT_POSITION_TICKS);
    }

    private double getRightPositionTicks() {
        return mData.drivetrain.get(EDriveTrain.RIGHT_POSITION_TICKS);
    }

}
