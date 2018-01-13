package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

public class DriverControlSplitArcade extends DriverControl{
	
	private final Data data;
	
	public DriverControlSplitArcade(Data pData, DriveTrain driveTrain) {
		super(driveTrain);
		data = pData;
	}
	
	double throttle, turn;
	int scalar;
	
	@Override
	public void updateDriveTrain() {
		throttle = data.driverinput.get(SystemSettings.DRIVER_MAP_THROTTLE_AXIS);
    turn = data.driverinput.get(SystemSettings.DRIVER_MAP_TURN_AXIS);
    scalar = (turn < 0) ? -1 : 1;
    boolean override = false;
		if(data.driverinput.isSet(ELogitech310.DPAD_UP)) {
		  throttle = 0.1;
		  override = true;
		} else if (data.driverinput.isSet(ELogitech310.DPAD_LEFT)) {
      throttle = 2.0*0.1;
      override = true;
		} else if (data.driverinput.isSet(ELogitech310.DPAD_DOWN)) {
      throttle = 3.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.DPAD_RIGHT)) {
      throttle = 4.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.L_BTN)) {
      throttle = 5.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.R_BTN)) {
      throttle = 6.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.A_BTN)) {
      throttle = 7.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.B_BTN)) {
      throttle = 8.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.X_BTN)) {
      throttle = 9.0*0.1;
      override = true;
    } else if (data.driverinput.isSet(ELogitech310.Y_BTN)) {
      throttle = 10.0*0.1;
      override = true;
    }
    if(override && Math.abs(turn) > SystemSettings.INPUT_DEADBAND_F310_JOYSTICK) {
      turn = Math.min(Math.abs(turn), Math.abs(throttle));
    }
		turn = Math.pow(turn, 2) * scalar;
		
		// TODO Cheezy Drive goes here....
		
    data.drivetrain.set(EDriveTrain.OPEN_LOOP_THROTTLE , throttle);
    data.drivetrain.set(EDriveTrain.OPEN_LOOP_TURN , turn);
    data.drivetrain.set(EDriveTrain.OPEN_LOOP_CALC_LEFT_POWER , throttle + turn);
    data.drivetrain.set(EDriveTrain.OPEN_LOOP_CALC_RIGHT_POWER , throttle - turn);
		setSpeeds(throttle + turn, throttle - turn);
		
		data.drivetrain.map(data.driverinput, ELogitech310.R_BTN, EDriveTrain.IS_SHIFT);
	}
}
