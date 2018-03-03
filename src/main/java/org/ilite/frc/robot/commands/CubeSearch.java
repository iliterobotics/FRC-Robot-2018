package org.ilite.frc.robot.commands;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.commands.GyroTurn;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.modules.DriverInput;
import org.ilite.frc.robot.modules.drivetrain.DriveControl;
import org.ilite.frc.robot.modules.drivetrain.DriveMessage;
import org.ilite.frc.robot.modules.drivetrain.DriveMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;


public class CubeSearch implements ICommand{

	private DriveControl mDriveControl;
	private Data mData;
	private DriverInput mDriverInput;
	private GyroTurn mGyroTurn;
	
	
	
	@SuppressWarnings("static-access")
	public CubeSearch(DriveControl pDriveControl, Data pData, double pAllowableError){
		
		this.mDriveControl = pDriveControl;
		this.mData = pData;
		
	}
	
	
	
	
	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(double pNow) {
		searchRight();
		searchLeft();
		return false;
	}

	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}
	
	public void searchRight() {
		if(!(SystemSettings.limelight.getInstance().getTable("limelight").getEntry("tv").getNumber(null)).equals(1)) {
			mDriveControl.setDriveMessage(new DriveMessage(0.4, -0.4, DriveMode.PercentOutput, NeutralMode.Brake));		
		}
		
		else{
			mGyroTurn = new GyroTurn(mDriveControl, mData, 3);
			mGyroTurn.update(0);
			
		}
	}
		
		public void searchLeft() {
			if(!(SystemSettings.limelight.getInstance().getTable("limelight").getEntry("tv").getNumber(null)).equals(1)) {
				mDriveControl.setDriveMessage(new DriveMessage(-0.4, 0.4, DriveMode.PercentOutput, NeutralMode.Brake));		
			}
			
			else{
				mGyroTurn = new GyroTurn(mDriveControl, mData, 3);
				mGyroTurn.update(0);
				
			}
		
		
				
		
		}
	
	
	

}
