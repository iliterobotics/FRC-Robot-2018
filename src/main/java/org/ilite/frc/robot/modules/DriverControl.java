package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick mGamepad;
	private DriveTrain m_dt;
		
	public DriverControl(DriveTrain p_dt)
	{
		this.m_dt = p_dt;
		mGamepad = new Joystick(SystemSettings.kCONTROLLER_ID);
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		double rotate = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_LEFT_Y);
		double throttle = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_RIGHT_X);
		double l = throttle - rotate;
		double r = throttle + rotate;
		m_dt.set(l, r);
		System.out.printf("Input Left: %s Input Right: %s\n", l, r);
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
