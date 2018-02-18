
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements IModule{
	
	final TalonSRX leftIntakeTalon;
	final TalonSRX rightIntakeTalon;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	public DoubleSolenoid extender;
	public boolean solOut;
	private double leftPower;
	private double rightPower;
	private boolean startCurrentLimiting;
	private DigitalInput beamBreak;
	private final double LEFT_LIMITER = .7;
	private final double RIGHT_LIMITER = .2;
	private final double MAX_RATIO = 3;
	private final double MIN_RATIO = .7;
	
	
	public Intake(Elevator pElevator){
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_RIGHT);
		beamBreak = new DigitalInput(SystemSettings.DIO_INTAKE_BEAM_BREAK);
		extender = new DoubleSolenoid(SystemSettings.SOLENOID_INTAKE_A, SystemSettings.SOLENOID_INTAKE_B);
		
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
		extender.set(Value.kReverse);
		leftIntakeTalon.set(ControlMode.PercentOutput, -leftPower );
		rightIntakeTalon.set(ControlMode.PercentOutput, rightPower);
		return true;
		
	}

	public void intakeIn(double inPower) {
			
		double rightRatio = rightCurrent/rightVoltage;
		double leftRatio = leftCurrent/leftVoltage;

		
		
		if(beamBreak.get())
		{
			if ( rightRatio >  MAX_RATIO || leftRatio > MAX_RATIO )
			{
				startCurrentLimiting = true;
				leftPower = -inPower * LEFT_LIMITER;
				rightPower = -inPower * RIGHT_LIMITER;
			}
			else if (rightRatio < MIN_RATIO && leftRatio < MIN_RATIO)
			{
				startCurrentLimiting = false;
				leftPower = inPower;
				rightPower = inPower;
			}
			else if (startCurrentLimiting)
			{
				leftPower = -inPower * LEFT_LIMITER;
				rightPower = -inPower * RIGHT_LIMITER;
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
	public boolean beamBreak(){
		return beamBreak.get();
	}
	public void intakeOut(double inPower) 
	{
		leftPower = inPower;
		rightPower= inPower;
	}
	@Override
	public void shutdown(double pNow) {	
	}
	

}