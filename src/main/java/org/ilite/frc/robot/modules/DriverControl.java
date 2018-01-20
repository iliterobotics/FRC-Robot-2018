package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick mGamepad;
	private DriveTrain m_dt;
	private Data mData;
		
	public DriverControl(DriveTrain p_dt, Data pData)
	{
		this.m_dt = p_dt;
		mGamepad = new Joystick(SystemSettings.kCONTROLLER_ID);
		mData = pData;
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
		double l = throttle - rotate;
		double r = throttle + rotate;
		m_dt.setPower(l, r);
		System.out.printf("Input Left: %s Input Right: %s\n", l, r);
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
