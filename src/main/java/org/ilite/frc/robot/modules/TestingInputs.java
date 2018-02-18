package org.ilite.frc.robot.modules;

import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;
import org.ilite.frc.robot.modules.drivetrain.DriveTrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class TestingInputs implements IModule {

	
	  private DriveTrain mDriveTrain;
	  private Carriage mCarriage;
	  private ElevatorModule mElevatorModule;
	  private Intake mIntake;
	  
		private Data mData;
		
		public TestingInputs(Data pData, Intake intake, Carriage carriage,
							DriveTrain driveTrain, ElevatorModule elevator)
		{
			mDriveTrain = driveTrain;
			mIntake = intake;
			mCarriage = carriage;
			mElevatorModule = elevator;
			this.mData = pData;
		}
		
		@Override
		public void initialize(double pNow) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean update(double pNow) {
			updateValues();
			return false;
		}
		
		private void updateValues() {
			
			if (!mData.operator.isNull(ELogitech310.A_BTN))
				mDriveTrain.getLeftMaster().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.B_BTN))
				mDriveTrain.getRightMaster().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.DPAD_DOWN))
				mDriveTrain.getLeftFollower().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.DPAD_UP))
				mDriveTrain.getLeftFollower2().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.DPAD_LEFT))
				mDriveTrain.getRightFollower().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.DPAD_RIGHT))
				mDriveTrain.getRightFollower2().set(ControlMode.PercentOutput, .5);
			if (!mData.operator.isNull(ELogitech310.DPAD_DOWN))
				mDriveTrain.getLeftMaster().set(ControlMode.PercentOutput, .5);

			mIntake.getRightIntake().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_Y_AXIS));
			mIntake.getLeftIntake().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_Y_AXIS));
			mElevatorModule.getLeftElevator().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_TRIGGER_AXIS));
			mElevatorModule.getRightElevator().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_TRIGGER_AXIS));
			
		}
		
		@Override
		public void shutdown(double pNow) {
			// TODO Auto-generated method stub
			
		}
		
}
