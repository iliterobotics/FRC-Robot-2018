package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Joystick;

public class DriverControlSplitArcade extends DriverControl {
	private Joystick mGamepad;
	private Data mData;
	
	private Object desiredValueLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private NeutralMode desiredNeutralMode;
	private ControlMode desiredControlMode; 
	
	public DriverControlSplitArcade(Data pData) {
		super(pData);
		
	}
	
	@Override
	public boolean update(double pNow) {
        //double rotate = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_LEFT_Y);
        //double throttle = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_RIGHT_X);
		double rotate = mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		rotate = getInputScaling().map(rotate, 2);
		
		
		double throttle1 = mData.driverinput.get(ELogitech310.LEFT_TRIGGER_AXIS);
		double throttle2 = mData.driverinput.get(ELogitech310.RIGHT_TRIGGER_AXIS);
		double throttle = (throttle1 + throttle2 == 2)? 1 : 0;
		
		desiredLeftOutput = throttle - rotate;
		desiredRightOutput = throttle + rotate;
		setDesiredLeft(desiredLeftOutput);
		setDesiredRight(desiredRightOutput);
		return false;
	}
	

}