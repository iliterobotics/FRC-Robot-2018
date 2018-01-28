package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
//import org.usfirst.frc.team1885.robot.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IControlLoop {
	//private final ILog mLog = Logger.createLog(DriveTrain.class);

	private DriverControl driverControl;
	//private PDM g;
	
	private final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower, leftFollower2, rightFollower2;
	private ControlMode controlMode;
	private double desiredLeft, desiredRight;
	
	public DriveTrain(DriverControl driverControl)
	{
		this.driverControl = driverControl;
		leftMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT1);
    leftFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT2);
    leftFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT3);
    
		rightMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT1);
		rightFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT2);
		rightFollower2 = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT3);
		
		rightFollower.follow(rightMaster);
		rightFollower2.follow(rightMaster);
    leftFollower.follow(leftMaster);
		leftFollower2.follow(leftMaster);
		
		controlMode = ControlMode.PercentOutput;
		
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(false);

		}
	
	

	@Override
	public void initialize(double pNow) {
		leftMaster.set(controlMode, desiredLeft);
		rightMaster.set(controlMode, desiredRight);
		leftMaster.setSelectedSensorPosition(0, 0, 10);
		rightMaster.setSelectedSensorPosition(0, 0, 10);
		
	}

	@Override
	public boolean update(double pNow) {
		//updateSpeed(desiredLeft, desiredRight);
		leftMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		rightMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		leftMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredLeftOutput());
		rightMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredRightOutput());
		SmartDashboard.putNumber("Left Position", leftMaster.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("Right Position", rightMaster.getSelectedSensorPosition(0));
		return false;
	}	
	
	/*private void updateSpeed(double l, double r)
	{
	
	}*/
	
	public void set(ControlMode pMode, double l, double r)
	{
		desiredLeft = l;
		desiredRight = r;
	}
	
	public void setPower(double l, double r) {
		set(ControlMode.PercentOutput, l, r);
	}
	
	@Override
	public void shutdown(double pNow) {
		leftMaster.neutralOutput();
		rightMaster.neutralOutput();
		
	}
	
	public void changeModes(ControlMode controlMode)
	{
		switch(controlMode)
		{
		case Velocity:
			break;
		case PercentOutput:
			desiredLeft = 0;
			desiredRight = 0;
			break;
		case Current:	
			break;
		case Disabled:
			break;
		case Follower:
			break;
		case MotionMagic:
			break;
		case MotionMagicArc:
			break;
		case MotionProfile:
			break;
		case MotionProfileArc:
			break;
		case Position:
			break;
		default:
			break;
		}
	}
	@Override
	public void loop(double pNow) {
		leftMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		rightMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		leftMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredLeftOutput());
		rightMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredRightOutput());
	}
	
	public int getLeftVelocity()
	{
		return leftMaster.getSelectedSensorVelocity(0);
	}
	
	public int getRightVelocity()
	{
		return rightMaster.getSelectedSensorVelocity(0);
	}
	
	public double getLeftPosition()
	{
		return leftMaster.getSelectedSensorPosition(0);
	}
	
	public double getRightPosition()
	{
		return rightMaster.getSelectedSensorPosition(0);
	}
	
}
	
	



	



  

	