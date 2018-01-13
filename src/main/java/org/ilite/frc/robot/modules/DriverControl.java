package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;

public abstract class DriverControl {

	public static final boolean HIGH_GEAR = true;
	public static final boolean LOW_GEAR = false;
	

	protected final DriveTrain driveTrain;
	
	public DriverControl(DriveTrain driveTrain) {
		this.driveTrain = driveTrain;
	}

	public void setSpeeds(double left, double right){
		
		if(Math.abs(left - right) < SystemSettings.INPUT_DEADBAND_F310_JOYSTICK ){
			left = right = (left + right) / 2;
		}
		if(Math.abs(left) < SystemSettings.INPUT_DEADBAND_F310_JOYSTICK){
			left = 0;
		}
		if(Math.abs(right) < SystemSettings.INPUT_DEADBAND_F310_JOYSTICK){
			right = 0;
		}
		driveTrain.setPower(left, right);
	}
	
	
	public abstract void updateDriveTrain();
	
	public void update(){
		updateDriveTrain();
	}
	
}
