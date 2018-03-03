package org.ilite.frc.robot;

import org.ilite.frc.common.config.DriveTeamInputMap;
import org.ilite.frc.common.input.DriverInputUtils;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Carriage.CarriageState;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Elevator.ElevatorPosition;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainControl;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriverInput implements IModule{

	
  protected final DrivetrainControl driveControl;
  private final Carriage mCarriage;
  private final Elevator mElevatorModule;
  private final Intake mIntake;
  private boolean scaleInputs;
  
	private Data mData;
	
	public DriverInput(DrivetrainControl pDriveControl, Intake pIntake, Carriage pCarriage, Elevator pElevator, Data pData)
	{
	  this.driveControl = pDriveControl;
	  this.mIntake = pIntake;
		this.mData = pData;
		mCarriage = pCarriage;
		mElevatorModule = pElevator;
		scaleInputs = false;
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		if(mData.driverinput.isSet(DriveTeamInputMap.DRIVE_SNAIL_MODE))
		  scaleInputs = true;
		else
		  scaleInputs = false;
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
		
		throttle = scaleInputs ? throttle = DriverInputUtils.scale(throttle, 0.33) : EInputScale.EXPONENTIAL.map(throttle, 2);

		if(mElevatorModule.decelerateHeight())
		{
		  throttle = Utils.clamp(throttle, 0.5);
		}
		if(mData.driverinput.get(DriveTeamInputMap.DRIVER_SUB_WARP_AXIS) > 0.5) {
	      rotate /= 3;
	      rotate /= 3;
		}
		
		System.out.println("ENGINE THROTTLE " + throttle);
		desiredLeftOutput = throttle + rotate;
		desiredRightOutput = throttle - rotate;
		
		int leftScalar = desiredLeftOutput < 0 ? -1 : 1;
		int rightScalar = desiredRightOutput < 0 ? -1 : 1;
		desiredLeftOutput =  leftScalar * Math.min(Math.abs(desiredLeftOutput), 1);
		desiredRightOutput = rightScalar * Math.min(Math.abs(desiredRightOutput), 1);
		
		if(Math.abs(desiredRightOutput) > 0.01 || Math.abs(desiredLeftOutput) > 0.01) {
			System.out.println("LEFT: " + desiredLeftOutput +"\tRIGHT: " +  desiredRightOutput + "");
		}
		
		driveControl.setDriveMessage(new DrivetrainMessage(desiredLeftOutput, desiredRightOutput, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		
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
	  if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_ELEVATOR_SETPOINT_SWITCH_BTN))
	  {
	  	mElevatorModule.setElevControlMode(Elevator.ElevatorControlMode.POSITION);
	    mElevatorModule.setPosition(ElevatorPosition.FIRST_TAPE);
	  }
	  else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_ELEVATOR_SETPOINT_SCALE))
    {
      mElevatorModule.setElevControlMode(Elevator.ElevatorControlMode.POSITION);
      mElevatorModule.setPosition(ElevatorPosition.THIRD_TAPE);
    }
    else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_ELEVATOR_SETPOINT_GROUND_BTN))
    {
      mElevatorModule.setElevControlMode(Elevator.ElevatorControlMode.POSITION);
      mElevatorModule.setPosition(ElevatorPosition.BOTTOM);
    }
	  else
    {
      mElevatorModule.setElevControlMode(Elevator.ElevatorControlMode.MANUAL);
      mElevatorModule.setPower(-mData.operator.get(DriveTeamInputMap.OPERATOR_ELEVATOR_DOWN_AXIS) +
              mData.operator.get(DriveTeamInputMap.OPERATOR_ELEVATOR_UP_AXIS));
    }


	}
  
  private void updateCarriage() {
    if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_KICK)) {
      mCarriage.setCarriageState(CarriageState.KICKING);
    }  else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_RESET)) {
      mCarriage.setCarriageState(CarriageState.RESET);
    } else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_GRAB)) {
      mCarriage.setCarriageState(CarriageState.GRAB_CUBE);
    }
  }
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
