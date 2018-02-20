
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
	public boolean mExtendIntake;
	private double leftDesiredPower;
	private double rightDesiredPower;
	private boolean startCurrentLimiting;
	private DigitalInput beamBreak;
	private final double LEFT_LIMITER = .8;
	private final double RIGHT_LIMITER = .2;
	private final double MAX_RATIO = 3;
	private final double MIN_RATIO = .40;
	
	
	public Intake(Elevator pElevator){
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_RIGHT);
		beamBreak = new DigitalInput(SystemSettings.DIO_INTAKE_BEAM_BREAK);
		extender = new DoubleSolenoid(SystemSettings.SOLENOID_INTAKE_A, SystemSettings.SOLENOID_INTAKE_B);
		mExtendIntake = false;
	}

	@Override
	public void initialize(double pNow) {
		extender.set(Value.kReverse);
	}

	@Override
	public boolean update(double pNow) {
		if(mExtendIntake) {
		  extender.set(Value.kForward);
		} else {
	    extender.set(Value.kReverse);
		}
		leftIntakeTalon.set(ControlMode.PercentOutput, -leftDesiredPower);
		rightIntakeTalon.set(ControlMode.PercentOutput, rightDesiredPower);
		return true;
		
	}

	public void intakeIn(double inPower) {
    rightCurrent = rightIntakeTalon.getOutputCurrent();
    leftCurrent = leftIntakeTalon.getOutputCurrent();
    rightVoltage = rightIntakeTalon.getBusVoltage();
    leftVoltage = leftIntakeTalon.getBusVoltage();
		double rightRatio = rightCurrent/rightVoltage;
		double leftRatio = leftCurrent/leftVoltage;
		
		if(beamBreak.get() && !mExtendIntake)
		{
			if ( rightRatio >  MAX_RATIO || leftRatio > MAX_RATIO )
			{
				startCurrentLimiting = true;
				leftDesiredPower = -inPower * LEFT_LIMITER;
				rightDesiredPower = -inPower * RIGHT_LIMITER;
			}
			else if (rightRatio < MIN_RATIO && leftRatio < MIN_RATIO)
			{
				startCurrentLimiting = false;
				leftDesiredPower = inPower;
				rightDesiredPower = inPower;
			}
			else if (startCurrentLimiting)
			{
				leftDesiredPower = -inPower * LEFT_LIMITER;
				rightDesiredPower = -inPower * RIGHT_LIMITER;
			}
			else
			{
				leftDesiredPower = inPower;
				rightDesiredPower = inPower;
			}
		}
		else
		{
			leftDesiredPower = 0;
			rightDesiredPower = 0;
		}	
	}
	public void setIntakeRetracted(boolean out)
	{
		mExtendIntake = out;
	}
	
	public boolean beamBreak(){
		return beamBreak.get();
	}
	
	public boolean isCurrentLimiting() {
	  return startCurrentLimiting;
	}
	
	public void intakeOut(double inPower) 
	{
		if (!mExtendIntake)
		{
			leftDesiredPower = inPower;
			rightDesiredPower= inPower;
		}
	}
	
	@Override
	public void shutdown(double pNow) {	
	}
	

}