package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick mGamepad;
	private Data mData;
	private Intake mIntake;
	private ElevatorModule mElevator;
	
	private Object desiredValueLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private NeutralMode desiredNeutralMode;
	private ControlMode desiredControlMode; 
	
	public DriverControl(Data pData, Intake pIntake, ElevatorModule pElevator)
	{
		this.mGamepad = new Joystick(SystemSettings.kCONTROLLER_ID);
		this.mData = pData;
		this.mIntake = pIntake;
		this.mElevator = pElevator;
		this.desiredNeutralMode = NeutralMode.Brake;
		this.desiredControlMode = ControlMode.PercentOutput;
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
	  mData.driverinput.set(ELogitech310.RUMBLE, 1.0);
	  
		double rotate = mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		double throttle = mData.driverinput.get(ELogitech310.RIGHT_X_AXIS);
		desiredLeftOutput = throttle - rotate;
		desiredRightOutput = throttle + rotate;
		
		int leftScalar = desiredLeftOutput < 0 ? -1 : 1;
		int rightScalar = desiredRightOutput < 0 ? -1 : 1;
		desiredLeftOutput =  leftScalar * Math.min(Math.abs(desiredLeftOutput), 1);
		desiredRightOutput = rightScalar * Math.min(Math.abs(desiredRightOutput), 1);
		
		if(mData.driverinput.get(ELogitech310.RIGHT_TRIGGER_AXIS) > 0.5) {
		  desiredLeftOutput /= 3;
		  desiredRightOutput /= 3;
		}
	}
	
	private void updateIntake() {
		double intakeSpeed = mData.operator.get(ELogitech310.RIGHT_Y_AXIS);
		
		if(mData.operator.isSet(ELogitech310.DPAD_UP)) {
			mIntake.setIntakePneumatics(false);
		} 
		else if(mData.operator.isSet(ELogitech310.DPAD_DOWN)) {
			mIntake.setIntakePneumatics(true);
		}
		
		if(intakeSpeed > 0) {
			mIntake.intakeIn(intakeSpeed);
		} else {
			mIntake.intakeOut(intakeSpeed);
		}
		
	}
	
	private void updateElevator() {
		
	}
	
	public void setDesiredLeftOutput(double desiredLeftOutput) {
    this.desiredLeftOutput = desiredLeftOutput;
  }

  public void setDesiredRightOutput(double desiredRightOutput) {
    this.desiredRightOutput = desiredRightOutput;
  }

  public void setDesiredNeutralMode(NeutralMode desiredNeutralMode) {
    this.desiredNeutralMode = desiredNeutralMode;
  }

  public void setDesiredControlMode(ControlMode desiredControlMode) {
    this.desiredControlMode = desiredControlMode;
  }

  public double getDesiredLeftOutput() {
		synchronized (desiredValueLock) {
			return desiredLeftOutput;
		}
	}
	
	public double getDesiredRightOutput() {
		synchronized(desiredValueLock) {
			return desiredRightOutput;
		}
	}
	
	public ControlMode getDesiredControlMode() {
		synchronized(desiredValueLock) {
			return desiredControlMode;
		}
	}
	
	public NeutralMode getDesiredNeutralMode() {
		synchronized(desiredValueLock) {
			return desiredNeutralMode;
		}
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
