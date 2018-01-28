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
	private Elevator mElevator;
	
	private Object desiredValueLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private NeutralMode desiredNeutralMode;
	private ControlMode desiredControlMode; 
	
	public DriverControl(Data pData, Intake pIntake, Elevator pElevator)
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
		double rotate = mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		double throttle = mData.driverinput.get(ELogitech310.RIGHT_X_AXIS);
		desiredLeftOutput = throttle - rotate;
		desiredRightOutput = throttle + rotate;
	}
	
	private void updateIntake() {
		double intakeSpeed = mData.operator.get(ELogitech310.RIGHT_Y_AXIS);
		System.out.println("Intake Speed:" + intakeSpeed);
		if(mData.operator.get(ELogitech310.DPAD_UP) != null) {
			mIntake.extendIntake();
		} 
		else if(mData.operator.get(ELogitech310.DPAD_DOWN) != null) {
			mIntake.retractIntake();
		}
		if(intakeSpeed > 0) {
			mIntake.spinIn(intakeSpeed);
		} else {
			mIntake.spinOut(intakeSpeed);
		}
		
	}
	
	private void updateElevator() {
		
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
