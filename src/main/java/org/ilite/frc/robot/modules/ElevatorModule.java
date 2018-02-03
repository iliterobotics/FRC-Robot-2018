package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorModule implements IModule {
	private TalonSRX leftElevator, rightElevator;
	private double mPower;
	private boolean mState;
	private DigitalInput limitSwitch;

	public ElevatorModule() {
		leftElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		rightElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		//limitSwitch = new DigitalInput(SystemSettings.DIO_PORT_ELEVATION_LIMIT_SWITCH);
	}

	@Override
	public void initialize(double pNow) {

	}

	@Override
	public boolean update(double pNow) {
		if ((!limitSwitch.get())) {
			mState = true;
		} else {
			mState = false;
		}

		leftElevator.set(ControlMode.PercentOutput, mPower);
		rightElevator.set(ControlMode.PercentOutput, mPower);
		return true;
	}

	public void setPower(double power) {
		mPower = power;
	}

	public boolean isDown() {
		return mState;
	}

	@Override
	public void shutdown(double pNow) {

	}

}
