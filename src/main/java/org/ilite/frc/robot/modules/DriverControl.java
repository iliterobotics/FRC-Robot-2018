package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick gamepad;
	private DriveTrain dt;
		
	public DriverControl(DriveTrain dt)
	{
		this.dt = dt;
		gamepad = new Joystick(SystemSettings.CONTROLLER_ID);
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		double rotate = gamepad.getRawAxis(SystemSettings.GAMEPAD_LEFT_Y);
		double throttle = gamepad.getRawAxis(SystemSettings.GAMEPAD_RIGHT_X);
		double l = throttle - rotate;
		double r = throttle + rotate;
		dt.set(l, r);
		System.out.printf("Input Left: %s Input Right: %s\n", l, r);
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
