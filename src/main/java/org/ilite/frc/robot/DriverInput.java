package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.Queue;

import org.ilite.frc.common.config.DriveTeamInputMap;
import org.ilite.frc.common.input.DriverInputUtils;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.commands.CubeSearch;
import org.ilite.frc.robot.commands.CubeSearch.CubeSearchType;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Carriage.CarriageState;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Elevator.ElevatorPosition;
import org.ilite.frc.robot.modules.IModule;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriverInput implements IModule{

	
  protected final DriveTrain driveTrain;
  private final Carriage mCarriage;
  private final Elevator mElevatorModule;
  private final Intake mIntake;
  private boolean scaleInputs;
  
  private Queue<ICommand> desiredCommandQueue;
  private boolean lastCanRunCommandQueue;
  private boolean canRunCommandQueue;
  
  
	private Data mData;
	
	public DriverInput(DriveTrain pDrivetrain, Intake pIntake, Carriage pCarriage, Elevator pElevator, Data pData)
	{
	    this.driveTrain = pDrivetrain;
	    this.mIntake = pIntake;
		this.mData = pData;
		mCarriage = pCarriage;
		mElevatorModule = pElevator;
		this.desiredCommandQueue = new LinkedList<>();
		scaleInputs = false;
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
		canRunCommandQueue = lastCanRunCommandQueue == false;
		
	}

	@Override
	public boolean update(double pNow) {
		if(mData.driverinput.get(DriveTeamInputMap.DRIVE_SNAIL_MODE) > 0.5)
		  scaleInputs = true;
		else
		  scaleInputs = false;
		if(!canRunCommandQueue) updateDriveTrain();
		updateIntake();
		updateElevator();
		updateCarriage();
		updateCommands();
		return false;
	}
	
	private void updateCommands() {
		boolean leftButton = mData.driverinput.isSet(DriveTeamInputMap.DRIVER_SEARCH_CUBE_LEFT_BTN);
		boolean rightButton = mData.driverinput.isSet(DriveTeamInputMap.DRIVER_SEARCH_CUBE_RIGHT_BTN);
		
		canRunCommandQueue = leftButton || rightButton;

		if(shouldInitializeCommandQueue()) {
			System.out.println("shouldInitializeCommandQueue() = true \\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\nsearching...!");
			desiredCommandQueue.clear();
			if(leftButton) {
				desiredCommandQueue.add(new CubeSearch(driveTrain, mData, CubeSearchType.LEFT));
			} else if(rightButton) {
				desiredCommandQueue.add(new CubeSearch(driveTrain, mData, CubeSearchType.RIGHT));
			}
		}
		lastCanRunCommandQueue = canRunCommandQueue;
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
	      throttle /= 4;
	      rotate /= 4;
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
		
		driveTrain.setDriveMessage(new DrivetrainMessage(desiredLeftOutput, desiredRightOutput, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		
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
	  
	  if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_ZERO_ELEVATOR_INPUTS))
	  {
	    mElevatorModule.setPower(0);
	  }
	  
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
      mCarriage.setDesiredState(CarriageState.KICKING);
    }  else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_RESET)) {
      mCarriage.setDesiredState(CarriageState.RESET);
    } else if(mData.operator.isSet(DriveTeamInputMap.OPERATOR_CARRIAGE_GRAB)) {
      mCarriage.setDesiredState(CarriageState.GRAB_CUBE);
    }
  }
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	

	public boolean shouldInitializeCommandQueue() {
		return lastCanRunCommandQueue == false && canRunCommandQueue == true;
	}
	
	public boolean canRunCommandQueue() {
		return canRunCommandQueue;
	}
	
	public Queue<ICommand> getDesiredCommandQueue() {
		return desiredCommandQueue;
	}
	

}
