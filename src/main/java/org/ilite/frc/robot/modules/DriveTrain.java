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
	
	private final TalonSRX leftMaster, rightMaster, leftFollower, rightFollower; /*leftFollower2, rightFollower2;*/
	private ControlMode controlMode;
	private double desiredLeft, desiredRight;
	
	public DriveTrain(DriverControl driverControl)
	{
		this.driverControl = driverControl;
		//leftMaster = new TalonSRX(SystemSettings.kDRIVETRAIN_TALONID_LEFT1);
		leftMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT1);
		rightMaster = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT1);
		leftFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_LEFT2);
		rightFollower = TalonFactory.createDefault(SystemSettings.kDRIVETRAIN_TALONID_RIGHT2);
		//leftFollower2 = new TalonSRX(SystemSettings.DRIVETRAIN_TALONID_LEFT3);
		//rightFollower2 = new TalonSRX(SystemSettings.DRIVETRAIN_TALONID_RIGHT3);
		rightFollower.follow(rightMaster);
		//rightFollower2.follow(rightMaster);
		//leftFollower2.follow(leftMaster);
		leftFollower.follow(leftMaster);
		controlMode = ControlMode.PercentOutput;
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, (int)MotorSafety.DEFAULT_SAFETY_EXPIRATION);
		rightMaster.setSensorPhase(false);
		leftMaster.setSensorPhase(false);
		//rightMaster.setStatusFramePeriod(frameValue, periodMs, timeoutMs)

		}
	
	@Override
	public void initialize(double pNow) {
		leftMaster.set(controlMode, desiredLeft);
		rightMaster.set(controlMode, desiredRight);
		
	}

	@Override
	public boolean update(double pNow) {
		//updateSpeed(desiredLeft, desiredRight);
		leftMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		rightMaster.setNeutralMode(driverControl.getDesiredNeutralMode());
		leftMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredLeftOutput());
		rightMaster.set(driverControl.getDesiredControlMode(), driverControl.getDesiredRightOutput());
		System.out.printf("Left: %s Right: %s\n", desiredLeft, desiredRight);
		System.out.println("Left Motor poition: " + getLeftPosition() + "\nRight Motor poition: " + getRightPosition());
		SmartDashboard.putNumber("Left Position", getLeftPosition());
		SmartDashboard.putNumber("Right Position", getRightPosition());

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
	
	public int getLeftPosition()
	{
		return leftMaster.getSelectedSensorPosition(0);
	}
	
	public int getRightPosition()
	{
		return rightMaster.getSelectedSensorPosition(0);
	}
	
}
	
	



	



  

	