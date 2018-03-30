
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.Utils;
import org.ilite.frc.robot.sensors.BeamBreakSensor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intake implements IModule{
	
  private Hardware mHardware;
	final TalonSRX leftIntakeTalon;
	final TalonSRX rightIntakeTalon;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	public DoubleSolenoid extender;
	public boolean mRetractIntake;
	private double leftDesiredPower;
	private double rightDesiredPower;
	private boolean startCurrentLimiting;
	private BeamBreakSensor beamBreak;
	private final double LEFT_LIMITER = .8;
	private final double RIGHT_LIMITER = .2;
	private final double MAX_CURRENT_LIMIT_RATIO = 30.0/12.0;
	private final double MIN_CURRENT_LIMIT_RATIO = .80;
	private final double MAX_OPERATOR_POWER = 0.6d;
	
	
	public Intake(Elevator pElevator, Hardware pHardware, BeamBreakSensor pBeamBreak){
	  mHardware = pHardware;
	  beamBreak = pBeamBreak;
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_RIGHT);
		extender = new DoubleSolenoid(SystemSettings.SOLENOID_INTAKE_A, SystemSettings.SOLENOID_INTAKE_B);
		mRetractIntake = false;
	}

	@Override
	public void initialize(double pNow) {
		setIntakeRetracted(true);
	}

	@Override
	public boolean update(double pNow) {
		if(mRetractIntake) {
		  // Retracted
		  extender.set(Value.kReverse);
      leftIntakeTalon.set(ControlMode.PercentOutput, 0);
      rightIntakeTalon.set(ControlMode.PercentOutput, 0);
		} else {
		  // Extended
	    extender.set(Value.kForward);
      leftIntakeTalon.set(ControlMode.PercentOutput, -leftDesiredPower);
      rightIntakeTalon.set(ControlMode.PercentOutput, rightDesiredPower);
		}
		return true;
		
	}

	public void intakeIn(double inPower) {
    rightCurrent = rightIntakeTalon.getOutputCurrent();
    leftCurrent = leftIntakeTalon.getOutputCurrent();
    rightVoltage = rightIntakeTalon.getBusVoltage();
    leftVoltage = leftIntakeTalon.getBusVoltage();
		double rightRatio = rightCurrent/rightVoltage;
		double leftRatio = leftCurrent/leftVoltage;
		boolean beamBreakTriggered = mHardware.getCarriageBeamBreak().get();
		
		if(!mRetractIntake)
		{
			if ( (rightRatio >  MAX_CURRENT_LIMIT_RATIO || leftRatio > MAX_CURRENT_LIMIT_RATIO) && !beamBreakTriggered)
			{
				startCurrentLimiting = true;
				leftDesiredPower = -inPower * LEFT_LIMITER;
				rightDesiredPower = -inPower * RIGHT_LIMITER;
			}
			else if (rightRatio < MIN_CURRENT_LIMIT_RATIO && leftRatio < MIN_CURRENT_LIMIT_RATIO)
			{
				startCurrentLimiting = false;
				leftDesiredPower = inPower;
				rightDesiredPower = inPower;
			}
			else if (startCurrentLimiting && !beamBreakTriggered)
			{
				leftDesiredPower = -inPower * LEFT_LIMITER;
				rightDesiredPower = -inPower * RIGHT_LIMITER;
			}
			else
			{
				leftDesiredPower = Utils.clamp(inPower, MAX_OPERATOR_POWER);
				rightDesiredPower = Utils.clamp(inPower, MAX_OPERATOR_POWER);
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
		mRetractIntake = out;
	}
	
	public boolean beamBreak(){
	  return beamBreak.isBroken();
	}
	public boolean isCurrentLimiting() {
	  return startCurrentLimiting;
	}
	
	public void intakeOut(double inPower) 
	{
		if (!mRetractIntake)
		{
			leftDesiredPower = inPower;
			rightDesiredPower= inPower;
		}
	}
	
	@Override
	public void shutdown(double pNow) {	
	}
	

}