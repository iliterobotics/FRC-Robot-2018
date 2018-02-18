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

import edu.wpi.first.wpilibj.Solenoid;

public class TestingInputs implements IModule {

	
	  private DriveTrain mDriveTrain;
	  private Carriage mCarriage;
	  private ElevatorModule mElevatorModule;
	  private Intake mIntake;
	  private Solenoid carriageKicker;
	  private Solenoid carriageHolder;
	  private Solenoid intakeExtender;
	  private Solenoid gearShifter;
	  private final double TALON_POWER = .5;
	  
		private Data mData;
		
		public TestingInputs(Data pData, Intake intake, Carriage carriage,
							DriveTrain driveTrain, ElevatorModule elevator)
		{
			mDriveTrain = driveTrain;
			mIntake = intake;
			mCarriage = carriage;
			mElevatorModule = elevator;
			this.mData = pData;
			carriageKicker = new Solenoid(0);
			carriageHolder = new Solenoid(1);
			intakeExtender = new Solenoid(2);
			gearShifter = new Solenoid(3);

			
		}
		
		@Override
		public void initialize(double pNow) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean update(double pNow) {
			updateTalons();
			updatePneumatics();
			return false;
		}
		
		private void updateTalons() {
			
			if (!mData.tester.isNull(ELogitech310.A_BTN))
				mDriveTrain.getLeftMaster().set(ControlMode.PercentOutput, TALON_POWER);
			if (!mData.tester.isNull(ELogitech310.B_BTN))
				mDriveTrain.getRightMaster().set(ControlMode.PercentOutput, TALON_POWER);
			if (!mData.tester.isNull(ELogitech310.DPAD_DOWN))
				mDriveTrain.getLeftFollower().set(ControlMode.PercentOutput, TALON_POWER);
			if (!mData.tester.isNull(ELogitech310.DPAD_UP))
				mDriveTrain.getLeftFollower2().set(ControlMode.PercentOutput, TALON_POWER);
			if (!mData.tester.isNull(ELogitech310.DPAD_LEFT))
				mDriveTrain.getRightFollower().set(ControlMode.PercentOutput, TALON_POWER);
			if (!mData.tester.isNull(ELogitech310.DPAD_RIGHT))
				mDriveTrain.getRightFollower2().set(ControlMode.PercentOutput, TALON_POWER);;
			

			mIntake.getRightIntake().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_Y_AXIS));
			mIntake.getLeftIntake().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_Y_AXIS));
			mElevatorModule.getLeftElevator().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_TRIGGER_AXIS));
			mElevatorModule.getRightElevator().set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_TRIGGER_AXIS));
			
		}
		
		public void updatePneumatics()
		{
			if (!mData.pTester.isNull(ELogitech310.A_BTN))
			{
				carriageKicker.set(true);
			}
			else
			{
				carriageKicker.set(false);
			}
			
			if (!mData.pTester.isNull(ELogitech310.B_BTN))
			{
				carriageHolder.set(true);
			}
			else
			{
				carriageHolder.set(false);
			}
			
			if (!mData.pTester.isNull(ELogitech310.X_BTN))
			{
				intakeExtender.set(true);
			}
			else
			{
				intakeExtender.set(false);
			}
			
			if (!mData.pTester.isNull(ELogitech310.Y_BTN))
			{
				gearShifter.set(true);
			}
			else
			{
				gearShifter.set(false);
			}
		}
		
		@Override
		public void shutdown(double pNow) {
			// TODO Auto-generated method stub
			
		}
		
}
