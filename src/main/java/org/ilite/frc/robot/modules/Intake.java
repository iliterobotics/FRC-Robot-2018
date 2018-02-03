
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements IModule{
	private static final double REVERSE_TIME = .1;
	private final TalonSRX leftIntakeTalon;
	private final TalonSRX rightIntakeTalon;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	private double maxRatio;
	public Solenoid extender;
	private double power;
	private double startReverseTime;
	private double currentTime; 
	private boolean startCurrentLimiting;
	private DigitalInput limitSwitch;
	public Intake(ElevatorModule pElevator)
	{
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		extender = new Solenoid(0);
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
		
		leftIntakeTalon.set(ControlMode.PercentOutput, -power );
		rightIntakeTalon.set(ControlMode.PercentOutput, power);	
		
		

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
				power = -inPower * .5;
			}
			else if (rightRatio < 1 && leftRatio < 1)
			{
				startCurrentLimiting = false;
				power = inPower;
			}
			else if (startCurrentLimiting)
			{
				power = -inPower * .5;
			}
			else
			{
				power = inPower;
			}
		}

		
	}
	public void setIntakePneumatics(boolean out)
	{
		extender.set(out);
	}
	public boolean limitSwitch()
	{
		return limitSwitch.get();
	}
	public void intakeOut(double inPower) {
		power = inPower;
			
	}
	@Override
	public void shutdown(double pNow) {
		
	}
	

}