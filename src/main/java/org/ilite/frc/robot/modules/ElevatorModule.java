package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorModule implements IModule {
	private TalonSRX leftElevatorTalon, rightElevatorTalon;
	private double mDesiredPower;
	private boolean isDown;
	private DigitalInput bottomLimitSwitch;

	public ElevatorModule() {
		leftElevatorTalon = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		rightElevatorTalon = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		bottomLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_ELEVATION_LIMIT_SWITCH);
	}

	@Override
	public void initialize(double pNow) {

	}

	@Override
	public boolean update(double pNow) {
		if ((!bottomLimitSwitch.get())) {
			isDown = true;
		} else {
			isDown = false;
		}

		leftElevatorTalon.set(ControlMode.PercentOutput, mDesiredPower);
		rightElevatorTalon.set(ControlMode.PercentOutput, mDesiredPower);
		
		
		return true;
	}

	public void setDesiredPower(double power) {
		mDesiredPower = power;
	}

	public boolean isDown() {
		return isDown;
	}

	@Override
	public void shutdown(double pNow) {

	}

}
