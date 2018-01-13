package org.ilite.frc.robot.modules;

import java.util.ArrayList;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
//import org.usfirst.frc.team1885.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IModule {
  //private final ILog mLog = Logger.createLog(DriveTrain.class);

	//private Solenoid gearShifter;
	private final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	private ControlMode controlMode;
	private double desiredLeft, desiredRight;
	
	public DriveTrain()
	{
		leftMaster = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_LEFT1);
		rightMaster = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_RIGHT1);
		leftFollower = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_LEFT2);
		rightFollower = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_RIGHT2);
		leftFollower2 = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_LEFT3);
		rightFollower2 = new TalonSRX(RobotMap.DRIVETRAIN_TALONID_RIGHT3);
		rightFollower.follow(rightMaster);
		rightFollower2.follow(rightMaster);
		leftFollower.follow(leftMaster);
		leftFollower.follow(leftMaster);
		controlMode = ControlMode.Velocity;


		}
	@Override
	public void init() {
		
		
	}

	@Override
	public boolean update() {
		//updateSpeed(desiredLeft, desiredRight);
		leftMaster.set(controlMode, desiredLeft);
		rightMaster.set(controlMode, desiredRight);
		return false;
	}
	
	/*private void updateSpeed(double l, double r)
	{
	
	}*/
	
	public void set(double l, double r)
	{
		desiredLeft = l;
		desiredRight = r;
	}
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	public void changeModes(ControlMode controlMode)
	{
		this.controlMode = controlMode;
	}
	
}
	
	



	



  

	