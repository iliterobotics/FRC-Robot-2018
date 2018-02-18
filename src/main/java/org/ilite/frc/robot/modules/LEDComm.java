package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.DriverInput;

public class LEDComm implements IModule{

	
	private DriveTrain driveTrain;
	private DriverInput driverInput;
	private LEDControl controller;
	
	public LEDComm(DriveTrain driveTrain, DriverInput driverInput, LEDControl controller)
	{
		this.driveTrain = driveTrain;
		this.driverInput = driverInput;
		this.controller = controller;
	}
	@Override
	public void initialize(double pNow) {
		
	}
	
	//Need to implement all conditions with the enumerations for LEDControl.Message
	@Override
	public boolean update(double pNow) {
		
		
		return true;
	}
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
}
