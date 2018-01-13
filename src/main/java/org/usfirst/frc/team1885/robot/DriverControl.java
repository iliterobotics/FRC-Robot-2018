package org.usfirst.frc.team1885.robot;

import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Talon;

public class DriverControl implements IModule{

	
	private Joystick gamepad;
	private DriveTrain dt;
		
	public DriverControl(DriveTrain dt)
	{
		this.dt = dt;
		gamepad = new Joystick(RobotMap.CONTROLLER_ID);
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update() {
		double throttle = gamepad.getRawAxis(RobotMap.GAMEPAD_LEFT_Y);
		double rotate = gamepad.getRawAxis(RobotMap.GAMEPAD_RIGHT_X);
		double l = throttle - rotate;
		double r = throttle + rotate;
		dt.set(l, r);
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}
