package org.ilite.frc.robot.modules;

import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;
import edu.wpi.first.wpilibj.Solenoid;

public class Carriage implements IModule{

	private Solenoid solenoid1, solenoid2, solenoid3;
	private double kickTimer;
	private double releaseTimer;
	private double currentTime;
	private Data mData;
	private boolean isScheduled;
	
	
	public Carriage(Data pData)
	{
		mData = pData;
		isScheduled = false;
	}
	
	@Override
	public void shutdown(double pNow) {
		
		
	}
	@Override
	public void initialize(double pNow) {
		solenoid1.set(false);
		solenoid2.set(false);
		solenoid3.set(false);
		
	}
	@Override
	public boolean update(double pNow)
	{
		if(!mData.operator.isSet(ELogitech310.B_BTN))
		{
			reset();
			if(!isScheduled)
			{
				schedule(pNow);
			}
		}
		return false;
	}	
		
	public void schedule(double pNow)
	{
		if(mData.operator.isSet(ELogitech310.B_BTN))
		{
			currentTime = pNow;
			kickTimer = pNow + 1;
			releaseTimer = pNow + 5;
			isScheduled = true;
		}
		if(isScheduled = true)
		{
			if(currentTime >= kickTimer)
			{
				//kick
				//testing code:
				solenoid1.set(true);
				solenoid2.set(true);
				solenoid3.set(true);
			}
			if(currentTime >= releaseTimer)
			{
				//release
				
			}
		}
	}
	public void reset()
	{
		isScheduled = false;
		currentTime = 0;
		//undo kick and release
	}
	
}
