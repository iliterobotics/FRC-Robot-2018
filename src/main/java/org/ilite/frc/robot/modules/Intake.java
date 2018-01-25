package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.sensors.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake implements IModule{
	
	private final TalonSRX leftIntake;
	private final TalonSRX rightIntake;
	private Elevator mElevator;
	private BeamBreakSensor backBeam;
	private BeamBreakSensor frontBeam;
	private boolean isElevatorDown;
	private boolean intakeExtended;
	private double power;
	public Intake(Elevator pElevator)
	{
		leftIntake = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_LEFT);
		rightIntake = TalonFactory.createDefault(SystemSettings.INTAKE_TALONID_FRONT_RIGHT);
		mElevator = pElevator;
		backBeam = new BeamBreakSensor(SystemSettings.BEAM_BREAK_BACK);
		frontBeam = new BeamBreakSensor(SystemSettings.BEAM_BREAK_FRONT);
	}


	@Override
	public void initialize(double pNow) {
		isElevatorDown = mElevator.isDown();
		
	}

	@Override
	public boolean update(double pNow) {
		isElevatorDown = mElevator.isDown();
		leftIntake.set(ControlMode.PercentOutput, power );
		rightIntake.set(ControlMode.PercentOutput, power);
		
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
		if(isElevatorDown && intakeExtended && !backBeam.isBroken()) 
			power = inPower;
	}
	public void intakeOut(double inPower) {
		if(isElevatorDown)
			power = inPower;
		
	}
	@Override
	public void shutdown(double pNow) {
		
	}
	

}