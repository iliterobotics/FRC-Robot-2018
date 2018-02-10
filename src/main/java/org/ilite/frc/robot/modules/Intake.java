
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements IModule{
	
	private final TalonSRX leftIntakeTalon;
	private final TalonSRX rightIntakeTalon;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	private double maxRatio;
	public Solenoid leftExtender;
	public Solenoid rightExtender;
	public boolean solOut;
	private double leftPower;
	private double rightPower;
	private boolean startCurrentLimiting;
	private DigitalInput limitSwitch;
	
	public Intake(ElevatorModule pElevator)
	{
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		leftExtender = new Solenoid(0);
		rightExtender = new Solenoid(1);
		limitSwitch = new DigitalInput(SystemSettings.INTAKE_LIMIT_SWITCH);
	}


	@Override
	public void initialize(double pNow) {
		
	}

	@Override
	public boolean update(double pNow) {
		
		
		
		rightCurrent = rightIntakeTalon.getOutputCurrent();
		leftCurrent = leftIntakeTalon.getOutputCurrent();
		rightVoltage = rightIntakeTalon.getBusVoltage();
		leftVoltage = leftIntakeTalon.getBusVoltage();
		leftExtender.set(solOut);
		rightExtender.set(solOut);
		leftIntakeTalon.set(ControlMode.PercentOutput, -leftPower );
		rightIntakeTalon.set(ControlMode.PercentOutput, rightPower);	
		
		

		return true;
	}

	public void intakeIn(double inPower) {
		
		
		double rightRatio = rightCurrent/rightVoltage;
		double leftRatio = leftCurrent/leftVoltage;
		if(leftRatio > maxRatio)
			maxRatio = leftRatio;
		if(rightRatio > maxRatio)
			maxRatio = rightRatio;
		SmartDashboard.putNumber("MaxRatio", maxRatio);
		
		System.out.println("L: " + leftRatio +" R: " + rightRatio);
		if(!limitSwitch.get())
		{
			if ( rightRatio >  3 || leftRatio > 3 )
			{
				startCurrentLimiting = true;
				leftPower = -inPower * .7;
				rightPower = -inPower * .2;
			}
			else if (rightRatio < .7 && leftRatio < .7)
			{
				startCurrentLimiting = false;
				leftPower = inPower;
				rightPower = inPower;
			}
			else if (startCurrentLimiting)
			{
				leftPower = -inPower * .7;
				rightPower = -inPower * .2;
			}
			else
			{
				leftPower = inPower;
				rightPower = inPower;
			}
		}
		else
		{
			leftPower = 0;
			rightPower = 0;
		}

		
	}
	public void setIntakePneumaticsOut(boolean out)
	{
		solOut = out;
	}
	public boolean limitSwitch()
	{
		return limitSwitch.get();
	}
	public void intakeOut(double inPower) {
		leftPower = inPower;
		rightPower= inPower;
	}
	@Override
	public void shutdown(double pNow) {
		
	}
	

}