package org.ilite.frc.robot;

import org.ilite.frc.common.config.DriveTeamInputMap;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriverInput implements IModule{

	
  protected final DriveTrain driveControl;
  private final Carriage mCarriage;
  private final Elevator mElevatorModule;
  private final Intake mIntake;
  
	private Data mData;
	
	public DriverInput(DriveTrain pDriveControl, Intake pIntake, Carriage pCarriage, Elevator pElevator, Data pData)
	{
	  this.driveControl = pDriveControl;
	  this.mIntake = pIntake;
		this.mData = pData;
		mCarriage = pCarriage;
		mElevatorModule = pElevator;
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
		updateCarriage();
		return false;
	}
	
	private void updateDriveTrain() {
		double desiredLeftOutput, desiredRightOutput;
	  
		double rotate = mData.driverinput.get(DriveTeamInputMap.DRIVER_TURN_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		double throttle = -mData.driverinput.get(DriveTeamInputMap.DRIVER_THROTTLE_AXIS);
		throttle = EInputScale.EXPONENTIAL.map(throttle, 2);
		
		if(mData.driverinput.get(DriveTeamInputMap.DRIVER_SUB_WARP_AXIS) > 0.5) {
	      rotate /= 3;
	      rotate /= 3;
		}
		
		desiredLeftOutput = throttle + rotate;
		desiredRightOutput = throttle - rotate;
		
		int leftScalar = desiredLeftOutput < 0 ? -1 : 1;
		int rightScalar = desiredRightOutput < 0 ? -1 : 1;
		desiredLeftOutput =  leftScalar * Math.min(Math.abs(desiredLeftOutput), 1);
		desiredRightOutput = rightScalar * Math.min(Math.abs(desiredRightOutput), 1);
		
		if(Math.abs(desiredRightOutput) > 0.01 || Math.abs(desiredLeftOutput) > 0.01) {
			System.out.println("LEFT: " + desiredLeftOutput +"\tRIGHT: " +  desiredRightOutput + "");
		}
		
		driveControl.setDrivetrainMessage(new DrivetrainMessage(desiredLeftOutput, desiredRightOutput, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		
	}
	
	private void updateIntake() {
	    double intakeSpeed = mData.operator.get(DriveTeamInputMap.OPERATOR_OPEN_LOOP_INTAKE_AXIS);
	    
	    if (mData.operator.isSet(DriveTeamInputMap.OPERATOR_INTAKE_IN_BTN))
	      mIntake.setIntakeRetracted(false);
	    if (mData.operator.isSet(DriveTeamInputMap.OPERATOR_INTAKE_OUT_BTN))
	      mIntake.setIntakeRetracted(true);
	    
	    if(intakeSpeed > 0) {
	      mIntake.intakeIn(intakeSpeed);
	    } else {
	      mIntake.intakeOut(intakeSpeed);
	    }
	}
	
	private void updateElevator() {
	  mElevatorModule.setPower(-mData.operator.get(DriveTeamInputMap.OPERATOR_ELEVATOR_DOWN_AXIS) + 
	                            mData.operator.get(DriveTeamInputMap.OPERATOR_ELEVATOR_UP_AXIS));
	}
  
  private void updateCarriage() {
//    if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_KICK)) {
//      mCarriage.kick();
//    }
  }
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
