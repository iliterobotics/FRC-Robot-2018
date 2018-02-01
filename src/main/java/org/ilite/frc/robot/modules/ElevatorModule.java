package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorModule implements IModule {
	private TalonSRX leftElevator, rightElevator;
	private double mPower;
	private boolean mAtBottom;
	private boolean mAtTop;
	private DigitalInput bottomLimitSwitch;
	private DigitalInput topLimitSwitch;

	public ElevatorModule() {
		leftElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_LEFT);
		rightElevator = TalonFactory.createDefault(SystemSettings.ELEVATOR_TALONID_RIGHT);
		bottomLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_BOTTOM_ELEVATION_LIMIT_SWITCH);
		topLimitSwitch = new DigitalInput(SystemSettings.DIO_PORT_TOP_ELEVATION_LIMIT_SWITCH);
	}

	@Override
	public void initialize(double pNow) {

	}

	@Override
	public boolean update(double pNow) {
		if ((bottomLimitSwitch.get())) {
			mAtBottom = true;
		} else {
			mAtBottom = false;
		}
		if ((topLimitSwitch.get())) {
			mAtTop = true;
		} else {
			mAtTop = false;
		}
		
		

		leftElevator.set(ControlMode.PercentOutput, mPower);
		rightElevator.set(ControlMode.PercentOutput, mPower);
		return true;
	}

	public void setPower(double power) {
		if(mAtBottom)
			if(power < 0)
				mPower = 0;
			else
				mPower = power;
		else if(mAtTop)
			if(power > 0)
				mPower = 0;
			else
				mPower = power;
		else
			mPower = power;
		
	}

	public boolean isDown() {
		return mAtBottom;
	}

	@Override
	public void shutdown(double pNow) {

	}

}
