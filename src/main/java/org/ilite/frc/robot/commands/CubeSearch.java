package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.DriverInput;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;


public class CubeSearch implements ICommand{

	public static final double TURN_POWER = 0.4;
	
	private DriveTrain mDriveTrain;
	private DriverInput mDriverInput;
	private Data mData;
	private VisionTurn mVisionTurn;
	private CubeSearchType cubeSearchType;
	
	
	public enum CubeSearchType {
		LEFT(-1), RIGHT(1);
		int turnScalar;
		private CubeSearchType(int turnScalar) {
			this.turnScalar = turnScalar;
		}
	}
	
	public CubeSearch(DriveTrain pDriveTrain, DriverInput pDriverInput, Data pData, CubeSearchType cubeSearchType){
		
		this.mDriveTrain = pDriveTrain;
		this.mDriverInput = pDriverInput;
		this.mData = pData;
		this.cubeSearchType = cubeSearchType;
		
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		search();
		System.out.println("updating cube search");
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	public void search() {
		
		System.out.println("IS SEARCHING \n\n\n\n\n");
		
		boolean seesCube = (SystemSettings.limelight.getInstance().getTable("limelight").getEntry("tv").getNumber(-1)).equals(1.0) ? true:false;
		
		if(!seesCube) {
			mDriveTrain.setDriveMessage(new DrivetrainMessage((cubeSearchType.turnScalar * TURN_POWER) + mDriverInput.getDesiredLeftOutput(), (cubeSearchType.turnScalar * -TURN_POWER) + mDriverInput.getDesiredRightOutput(), DrivetrainMode.PercentOutput, NeutralMode.Brake));	
		}
		
		else if(seesCube){
			mVisionTurn = new VisionTurn(mDriveTrain, mDriverInput, mData, SystemSettings.ALLOWABLE_ERROR);
			mVisionTurn.update(0);
			
		}
	
	}
	
	
	

}
