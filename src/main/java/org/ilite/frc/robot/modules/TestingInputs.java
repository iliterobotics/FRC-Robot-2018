package org.ilite.frc.robot.modules;

import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class TestingInputs implements IModule {

	
	  private DriveTrain mDriveTrain;
	  private Carriage mCarriage;
	  private Elevator mElevator;
	  private Intake mIntake;
	  private PneumaticModule mPneumaticModule;
	  private final double TALON_POWER = .2;
	  
		private Data mData;
		
		public TestingInputs(Data pData, Intake intake, Carriage carriage,
							DriveTrain driveTrain, Elevator elevator, PneumaticModule pPneumatic)
		{
			mDriveTrain = driveTrain;
			mIntake = intake;
			mCarriage = carriage;
			mElevator = elevator;
			this.mData = pData;
			mPneumaticModule = pPneumatic;
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
			
//			mDriveTrain.leftMaster.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.A_BTN) ? TALON_POWER : 0);
//			mDriveTrain.rightMaster.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.B_BTN) ? TALON_POWER : 0);
			mDriveTrain.leftFollower.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.DPAD_DOWN) ? TALON_POWER : 0);
			mDriveTrain.leftFollower2.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.DPAD_UP) ? TALON_POWER : 0);
			mDriveTrain.rightFollower.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.DPAD_LEFT) ? TALON_POWER : 0);
			mDriveTrain.rightFollower2.set(ControlMode.PercentOutput, mData.tester.isSet(ELogitech310.DPAD_RIGHT) ? TALON_POWER : 0);
			
//			if(mData.tester.isSet(ELogitech310.A_BTN)) {
//			  mPneumaticModule.forceCompressorOff();
//			} else if (mData.tester.isSet(ELogitech310.B_BTN)){
//        mPneumaticModule.forceCompressorOn();
//			}
			

			mIntake.rightIntakeTalon.set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_Y_AXIS));
			mIntake.leftIntakeTalon.set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_Y_AXIS));
			mElevator.masterElevator.set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.LEFT_TRIGGER_AXIS));
			mElevator.followerElevator.set(ControlMode.PercentOutput, mData.tester.get(ELogitech310.RIGHT_TRIGGER_AXIS));
			
		}
		
		public void updatePneumatics()
		{
			if (mData.tester.isSet(ELogitech310.A_BTN))
			{
				mCarriage.solenoidPop.set(true);
			}
			else
			{
				mCarriage.solenoidPop.set(false);
			}
			
			if (mData.tester.isSet(ELogitech310.B_BTN))
			{
				mCarriage.solenoidGrab.set(true);
			}
			else
			{
				mCarriage.solenoidGrab.set(false);
			}
			
			if (mData.tester.isSet(ELogitech310.X_BTN))
			{
				mIntake.extender.set(Value.kForward);
			}
			else
			{
				mIntake.extender.set(Value.kReverse);
			}
			
			if (mData.tester.isSet(ELogitech310.Y_BTN))
			{
				mElevator.solenoid.set(true);
			}
			else
			{
				mElevator.solenoid.set(false);
			}
		}
		
		@Override
		public void shutdown(double pNow) {
			// TODO Auto-generated method stub
			
		}
		
}