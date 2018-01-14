package org.ilite.frc.robot.modules;

public class Elevator implements IModule{

	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double pNow) {
		intakeSafeRetract();
		intakeSafeExtend();
	}



	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	public boolean intakeSafeExtend()
	{
		return true;
	}
	public boolean intakeSafeRetract()
	{
		return true;
	}
	
	public boolean isDown()
	{
		return true;
	}

}
