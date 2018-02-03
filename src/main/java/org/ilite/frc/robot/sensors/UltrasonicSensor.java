package org.ilite.frc.robot.sensors;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.IModule;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Ultrasonic;

public class UltrasonicSensor implements IModule {
	private AnalogInput mUltrasonicSensor;
	private double mDistanceInch;
	
	public UltrasonicSensor() {
		//mUltrasonicSensor = new AnalogInput(SystemSettings.ULTRASONIC_PORT);
	}
	
	public double getDistanceInches() {
		return mDistanceInch;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		
		return false;
	}
	
	
}
