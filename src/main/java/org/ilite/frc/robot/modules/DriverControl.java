package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.ProfilingMessage;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick mGamepad;
	private Data mData;
	private Intake mIntake;
	private Elevator mElevator;
	
	private Object messageLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private DriveMessage driveMessage;
	private ProfilingMessage profilingMessage;
	
	public DriverControl(Data pData, Intake pIntake, Elevator pElevator)
	{
		this.mGamepad = new Joystick(SystemSettings.kCONTROLLER_ID);
		this.mData = pData;
		this.mIntake = pIntake;
		this.mElevator = pElevator;
		this.driveMessage = new DriveMessage(0, 0, DriveMode.PercentOutput, NeutralMode.Brake);
		this.profilingMessage = new ProfilingMessage(null, null, Double.NaN, false);
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
	
	public void setDriveMessage(DriveMessage driveMessage) {
	  synchronized(messageLock) {
	    this.driveMessage = driveMessage;
	  }
	}
	
	public void setProfilingMessage(ProfilingMessage profilingMessage) {
	  synchronized(messageLock) {
	    this.profilingMessage = profilingMessage;
	  }
  }
	
	public DriveMessage getDriveMessage() {
	  synchronized(messageLock) {
	    return driveMessage;
	  }
	}
	
	public ProfilingMessage getProfilingMessage() {
	  synchronized(messageLock) {
	    return profilingMessage;
	  }
    
  }
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
