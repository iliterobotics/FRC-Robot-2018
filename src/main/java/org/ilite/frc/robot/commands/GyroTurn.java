package org.ilite.frc.robot.commands;

import org.ilite.frc.common.sensors.IMU;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class GyroTurn implements ICommand {

private static final int TIMEOUT = 3000;
	
	private org.ilite.frc.robot.modules.drivetrain.DriveControl driveControl;
	private Data data;
	
	private static final int MIN_ALIGNED_COUNT = 5;
	private static final double KP = 0.0101;
	private static final double KD = 0.0105;
	private static final double KI = 0.0;
	private static final double MINIMUM_POWER = 0.05;
	
	private double degrees, targetYaw;
	private double error, lastError, totalError;
	private double alignedCount;
	private final double allowableError;
	
	private long startTime;
	
	double leftPower, rightPower, output = 0;
	
	public GyroTurn(DriveControl driveControl, Data data, double degrees, double allowableError)
	{
		this.driveControl = driveControl;
		this.data = data;
		this.degrees = degrees;
		this.alignedCount = 0;
		this.allowableError = allowableError;
	}

	@Override
	public void initialize() {
		this.targetYaw = degrees;  //Calculate the target heading off of # of degrees to turn
		this.lastError = this.error = getError(); //Calculate the initial error value
		this.totalError += this.error;
		startTime = System.currentTimeMillis();
	}
	
	public boolean update()
	{
		error = getError(); //Update error value
		System.out.println(error);
		this.totalError += this.error; //Update running error total
		
		if((Math.abs(error) < allowableError)) alignedCount++;
		if(alignedCount >= MIN_ALIGNED_COUNT) return true;
		if(System.currentTimeMillis() - startTime > TIMEOUT) return true;
		
		output = ((KP * error) + (KI * totalError) + (KD * (error - lastError)));
		if(Math.abs(output) < MINIMUM_POWER){
			double scalar = output>0?1:-1;
			output = MINIMUM_POWER * scalar;
		}
		leftPower = output; 
		rightPower = -output;
		
		driveControl.setDriveMessage(new DriveMessage(leftPower, rightPower, DriveMode.PercentOutput, NeutralMode.Brake));
		
		lastError = error;
		return false;
	}
	
	public double getError(){
		return IMU.getAngleDistance(IMU.convertTo360(IMU.clampDegrees(data.pigeon.get(EPigeon.YAW))), targetYaw);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
