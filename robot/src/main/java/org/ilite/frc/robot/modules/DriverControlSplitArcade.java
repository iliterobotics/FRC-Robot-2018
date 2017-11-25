package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.EDriveTrain;
import org.ilite.frc.robot.types.ELogitech310;

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
