package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Elevator implements IModule{
	
	private final TalonSRX leftElevator;
	private final TalonSRX rightElevator;
	private double power;

	public Elevator()
	{
		leftElevator = new TalonSRX(SystemSettings.TALON_ADDR_LEFT_ELEVATOR);
		rightElevator = new TalonSRX(SystemSettings.TALON_ADDR_RIGHT_ELEVATOR);
	}
	
	@Override
	public void initialize(double pNow) {

	}

	@Override
	public void update(double pNow) {
		intakeSafeRetract();
		intakeSafeExtend();
		
		leftElevator.set( ControlMode.PercentOutput, power );
		rightElevator.set( ControlMode.PercentOutput, -power );
	}



	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	public boolean intakeSafeExtend()
	{
		if ( this.isDown() )
		{
			return true;
		}
		return false;
	}
	public boolean intakeSafeRetract()
	{

			return true;
	}
	
	public boolean isDown()
	{
		return true;
	}
	public void setPower( double power )
	{
		this.power = power;
	}

}
