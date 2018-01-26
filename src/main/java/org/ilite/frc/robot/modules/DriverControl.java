package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick mGamepad;
	private Data mData;
	
	private Object desiredValueLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private NeutralMode desiredNeutralMode;
	private ControlMode desiredControlMode; 
	
	public DriverControl(Data pData)
	{
		this.mGamepad = new Joystick(SystemSettings.kCONTROLLER_ID);
		this.mData = pData;
		this.desiredNeutralMode = NeutralMode.Brake;
		this.desiredControlMode = ControlMode.PercentOutput;
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
//		double rotate = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_LEFT_Y);
//		double throttle = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_RIGHT_X);
		double rotate = mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		double throttle = mData.driverinput.get(ELogitech310.RIGHT_X_AXIS);
		desiredLeftOutput = throttle - rotate;
		desiredRightOutput = throttle + rotate;
		return false;
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
