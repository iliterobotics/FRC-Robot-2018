package org.ilite.frc.robot;

import org.ilite.frc.common.input.EInputScale;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Intake;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Joystick;

public class DriverControlSplitArcade extends DriverInput {
	private Joystick mGamepad;
	private Data mData;
	
	private Object desiredValueLock = new Object();
	private double desiredLeftOutput, desiredRightOutput;
	private NeutralMode desiredNeutralMode;
	private ControlMode desiredControlMode; 
	
	public DriverControlSplitArcade(DriveTrain pDrivetrain, Intake intake, Carriage pCarriage, Elevator pElevator, Data pData) {
		super(pDrivetrain, intake, pCarriage, pElevator, pData);
		this.mData = pData;
	}
	
	@Override
	public boolean update(double pNow) {
        //double rotate = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_LEFT_Y);
        //double throttle = mGamepad.getRawAxis(SystemSettings.kGAMEPAD_RIGHT_X);
		double rotate = mData.driverinput.get(ELogitech310.LEFT_Y_AXIS);
		rotate = EInputScale.EXPONENTIAL.map(rotate, 2);
		
		
		double throttle1 = mData.driverinput.get(ELogitech310.LEFT_TRIGGER_AXIS);
		double throttle2 = mData.driverinput.get(ELogitech310.RIGHT_TRIGGER_AXIS);
		double throttle = (throttle1 + throttle2 == 2)? 1 : 0;
		
		desiredLeftOutput = throttle - rotate;
		desiredRightOutput = throttle + rotate;
		driveTrain.setDriveMessage(new DrivetrainMessage(desiredLeftOutput, desiredRightOutput, DrivetrainMode.PercentOutput, NeutralMode.Brake));
		return false;
	}
	

}