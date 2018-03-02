package org.ilite.frc.robot.modules;

import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriverInput implements IModule{

	
  private DriveControl driveControl;
  private Carriage mCarriage;
  private ElevatorModule mElevatorModule;
  private Intake mIntake;
  
	private Data mData;
	
	public DriverInput(DriveControl pDriveControl, Intake pIntake, Data pData)
	{
	  this.driveControl = pDriveControl;
	  this.mIntake = pIntake;
		this.mData = pData;
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		updateDriveTrain();
		updateIntake();
		updateElevator();
		return false;
	}
	
	private void updateDriveTrain() {
		double desiredLeftOutput, desiredRightOutput;
	  
		double rotate = mData.driverinput.get(ELogitech310.RIGHT_X_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		double throttle = -mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		
		if(mData.driverinput.get(ELogitech310.RIGHT_TRIGGER_AXIS) > 0.5) {
	      rotate /= 3;
	      rotate /= 3;
		}
		
		desiredLeftOutput = throttle + rotate;
		desiredRightOutput = throttle - rotate;
		
		int leftScalar = desiredLeftOutput < 0 ? -1 : 1;
		int rightScalar = desiredRightOutput < 0 ? -1 : 1;
		desiredLeftOutput =  leftScalar * Math.min(Math.abs(desiredLeftOutput), 1);
		desiredRightOutput = rightScalar * Math.min(Math.abs(desiredRightOutput), 1);
		
		driveControl.setDriveMessage(new DriveMessage(desiredLeftOutput, desiredRightOutput, DriveMode.PercentOutput, NeutralMode.Brake));
		
	}
	
	private void updateIntake() {
	    double intakeSpeed = mData.operator.get(ELogitech310.RIGHT_Y_AXIS);
	    
	    if (mData.operator.get(ELogitech310.DPAD_DOWN) != null)
	      mIntake.setIntakePneumaticsOut(false);
	    if (mData.operator.get(ELogitech310.DPAD_UP) != null)
	      mIntake.setIntakePneumaticsOut(true);
	    
	    if(intakeSpeed > 0) {
	      mIntake.intakeIn(intakeSpeed);
	    } else {
	      mIntake.intakeOut(intakeSpeed);
	    }
	}
	
	private void updateElevator() {
		
	}
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}