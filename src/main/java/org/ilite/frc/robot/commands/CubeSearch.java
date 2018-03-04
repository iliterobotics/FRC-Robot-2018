package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMessage;
import org.ilite.frc.robot.modules.drivetrain.DrivetrainMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;


public class CubeSearch implements ICommand{

	public static final double TURN_POWER = 0.2;
	
	private DriveTrain mDriveTrain;
	private Data mData;
	private VisionTurn mVisionTurn;
	private CubeSearchType cubeSearchType;
//	private boolean canSearch;
	
	
	public enum CubeSearchType {
		LEFT(-1), RIGHT(1);
		int turnScalar;
		private CubeSearchType(int turnScalar) {
			this.turnScalar = turnScalar;
		}
	}
	
	public CubeSearch(DriveTrain pDriveTrain, Data pData, CubeSearchType cubeSearchType){
		
		this.mDriveTrain = pDriveTrain;
		this.mData = pData;
		this.cubeSearchType = cubeSearchType;
		
	}
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
//		canSearch = Math.abs(mData.driverinput.get(ELogitech310.LEFT_Y_AXIS)) < 0.8 && Math.abs(mData.driverinput.get(ELogitech310.LEFT_X_AXIS)) < 0.8 ? true:false;
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
		
		if(!(SystemSettings.limelight.getInstance().getTable("limelight").getEntry("tv").getNumber(-1)).equals(1.0)) {
			mDriveTrain.setDriveMessage(new DrivetrainMessage(cubeSearchType.turnScalar * TURN_POWER,cubeSearchType.turnScalar * -TURN_POWER, DrivetrainMode.PercentOutput, NeutralMode.Brake));	
			System.out.println("Made it thru");
		}
		
		else if((SystemSettings.limelight.getInstance().getTable("limelight").getEntry("tv").getNumber(-1)).equals(1.0)){
			mVisionTurn = new VisionTurn(mDriveTrain, mData, SystemSettings.ALLOWABLE_ERROR);
			mVisionTurn.update(0);
			
		}
	
	}
	
	
	

}
