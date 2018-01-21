package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake implements IModule{
	
	private final TalonSRX leftIn;
	private final TalonSRX rightIn;
	private double power;
	private boolean cubeIn;
	private boolean intakeOut;
	private Elevator elevator;
	
	public Intake(Elevator elevator)
	{
		this.elevator = elevator;
		leftIn = new TalonSRX(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIn = new TalonSRX(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		intakeOut = false;
		cubeIn = false;
	}


	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		
		/*if (intakeOut)
			cubeIn = sensorValue;
			*/
		
		if ( elevator.isDown() )
		{
			leftIn.set( ControlMode.PercentOutput, power );
			rightIn.set( ControlMode.PercentOutput, -power );
		}
		
		//if ( elevator.intakeSafeRetract() )
		//	retractIntake();
		
		System.out.printf("Intake extended: %s Intake Power: %s\n", intakeOut, power);
		return true;
	}
	
	public void spinIn(double pIntakePower)
	{
		/*if ( !intakeOut && elevator.intakeSafeExtend() )
		{
			extendIntake();
		}
		*/
		if ( intakeOut )
		{
			if ( cubeIn )
			{
				setPower(0);
			}
			setPower(pIntakePower);
		}
	}
	public void spinOut(double pIntakePower)
	{
		setPower(pIntakePower);
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