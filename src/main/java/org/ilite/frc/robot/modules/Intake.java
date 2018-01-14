package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake implements IModule{
	
	private final TalonSRX frontLeftIn;
	private final TalonSRX frontRightIn;
	private final TalonSRX backLeftIn;
	private final TalonSRX backRightIn;
	private double power;
	private boolean cubeIn;
	private boolean intakeOut;
	private Elevator elevator;
	
	public Intake(Elevator elevator)
	{
		this.elevator = elevator;
		frontLeftIn = new TalonSRX(SystemSettings.TALON_ADDR_INTAKE_FRONT_LEFT);
		backLeftIn = new TalonSRX(SystemSettings.TALON_ADDR_INTAKE_BACK_LEFT);
		frontRightIn = new TalonSRX(SystemSettings.TALON_ADDR_INTAKE_FRONT_RIGHT);
		backRightIn = new TalonSRX(SystemSettings.TALON_ADDR_INTAKE_BACK_RIGHT);
		intakeOut = false;
		cubeIn = false;
	}


	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double pNow) {
		
		/*if (intakeOut)
			cubeIn = sensorValue;
			*/
		
		if ( elevator.isDown() )
		{
			frontLeftIn.set( ControlMode.PercentOutput, power );
			frontRightIn.set( ControlMode.PercentOutput, -power );
		}
		backRightIn.set( ControlMode.PercentOutput, -power );
		backLeftIn.set( ControlMode.PercentOutput, power );
		
		if ( elevator.intakeSafeRetract() )
			retractIntake();
	}
	
	public void spinIn()
	{
		if ( !intakeOut && elevator.intakeSafeExtend() )
		{
			extendIntake();
			if ( cubeIn )
			{
				setPower(0);
			}
			setPower(1);
		}
		

		
	}
	public void spinOut()
	{
		setPower(-1);
	}
	public void setPower(double power)
	{
		this.power = power;
	}
	
	public void extendIntake()
	{
		intakeOut = true;
	}
	public void retractIntake()
	{
		intakeOut = false;
	}

	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	

}