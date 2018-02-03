
package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.sensors.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake implements IModule{
	
	private final TalonSRX leftIntakeTalon;
	private final TalonSRX rightIntakeTalon;
	private ElevatorModule mElevator;
	private BeamBreakSensor backBeamBreak;
	private BeamBreakSensor frontBeamBreak;
	private double rightCurrent;
	private double rightVoltage;
	private double leftVoltage;
	private double leftCurrent;
	private final double intakeRatioNoCube = .3;
	private boolean isElevatorDown;
	private boolean intakeExtended;
	private double power;
	public Intake(ElevatorModule pElevator)
	{
		leftIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIntakeTalon = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		mElevator = pElevator;
		backBeamBreak = new BeamBreakSensor(SystemSettings.BEAM_BREAK_BACK);
		frontBeamBreak = new BeamBreakSensor(SystemSettings.BEAM_BREAK_FRONT);
		
	}


	@Override
	public void initialize(double pNow) {
		isElevatorDown = mElevator.isDown();
		
	}

	@Override
	public boolean update(double pNow) {
		isElevatorDown = mElevator.isDown();
		
		leftIntakeTalon.set(ControlMode.PercentOutput, power );
		rightIntakeTalon.set(ControlMode.PercentOutput, power);	

		return true;
	}
	public void retractIntake() {
		if(!isElevatorDown && intakeExtended) {
			intakeExtended = false;
		}
	}
	public void extendIntake() {
		if(isElevatorDown && !intakeExtended) {
			intakeExtended = true;
		}
	}
	
	public void intakeIn(double inPower) {
		
		//BeamBreak
		if(isElevatorDown && intakeExtended && !backBeamBreak.isBroken())
			if (frontBeamBreak.isBroken())
				power = inPower;
			else
				power = inPower * intakeRatioNoCube;
		
		/*Current Limiting
		double rightRatio = rightCurrent/rightVoltage;
	    double leftRatio = leftCurrent/leftVoltage;
	    if (rightRatio > 5 || leftRatio > 5)
	      power = -inPower;
	    if(isElevatorDown && intakeExtended && !backBeamBreak.isBroken()) 
	      power = inPower;
		*/
		
	}
	public void intakeOut(double inPower) {
		if(isElevatorDown)
			power = inPower;
		
	}
	@Override
	public void shutdown(double pNow) {
		
	}
	

}