
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Intake implements IModule{
	
	private final TalonSRX leftIntakeTalon;
	private final TalonSRX rightIntakeTalon;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	private final double leftPowerLimiter = .7;
	private final double rightPowerLimiter = .2;
	private final double maxCurrentRatio = 3;
	private final double minCurrentRatio = .7;	
	public Solenoid leftExtender;
	public Solenoid rightExtender;
	public boolean solOut;
	private double leftPower;
	private double rightPower;
	private boolean startCurrentLimiting;
	private DigitalInput beamBreak;
	
	public Intake(ElevatorModule pElevator)
	{
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		leftExtender = new Solenoid(0);
		rightExtender = new Solenoid(1);
		beamBreak = new DigitalInput(SystemSettings.INTAKE_BEAM_BREAK);
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
		
		System.out.println("L: " + leftRatio +" R: " + rightRatio);
		if(!beamBreak.get())
		{
			if ( rightRatio >  maxCurrentRatio || leftRatio > maxCurrentRatio )
			{
				startCurrentLimiting = true;
				leftPower = -inPower * leftPowerLimiter;
				rightPower = -inPower * rightPowerLimiter;
			}
			else if (rightRatio < minCurrentRatio && leftRatio < minCurrentRatio)
			{
				startCurrentLimiting = false;
				leftPower = inPower;
				rightPower = inPower;
			}
			else if (startCurrentLimiting)
			{
				leftPower = -inPower * leftPowerLimiter;
				rightPower = -inPower * rightPowerLimiter;
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
		return beamBreak.get();
	}
	public void intakeOut(double inPower) {
		leftPower = inPower;
		rightPower= inPower;
	}
	@Override
	public void shutdown(double pNow) {
		
	}
	

}