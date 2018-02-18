package org.ilite.frc.robot.modules;

public class LEDComm implements IModule{

	
	private DriveTrain driveTrain;
	private DriverControl driverControl;
	private LEDControl controller;
	
	public LEDComm(DriveTrain driveTrain, DriverControl driverControl, LEDControl controller)
	{
		this.driveTrain = driveTrain;
		this.driverControl = driverControl;
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
