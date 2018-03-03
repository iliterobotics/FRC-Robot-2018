package org.ilite.frc.robot.modules.drivetrain;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.controlloop.IControlLoop;
import org.ilite.frc.robot.modules.IModule;

public class SwerveDrive implements IModule, IControlLoop{
	
	
	//private Object dvLock = new Object();
	static double desiredRightOutput;
	static double desiredLeftOutput;
	static double LEFT_SCALAR = 1.0;
	static double RIGHT_SCALAR = -1.0;
	static double OUTER_GAIN = 1.0;
	static double INNER_GAIN = 1.0;
	
	

	@Override
	public void loop(double pNow) {
		 
		//int j = ( (int)(Math.random()+30)>20 ? -1:1);
	}

	@Override
	public void shutdown(double pNow) {
		 
		
	}

	@Override
	public void initialize(double pNow) {
		 
		
	}
	
	public static double calculateRight(double throttle, double rotate, double RIGHT_SCALAR)
	{
		double result;
		double resultb;
		if(rotate > 0 && throttle > 0 || rotate < 0 && throttle < 0)
		{
			result = OUTER_GAIN;
		}
		else
			result = INNER_GAIN;
		if(rotate > 0 && throttle > 0 || rotate < 0 && throttle < 0)
		{
			resultb = 1.0;
		}
		else
			resultb = -1.0;
		
		desiredRightOutput=Math.min(Math.max((throttle+RIGHT_SCALAR*rotate*result+Math.sin(throttle)*RIGHT_SCALAR*Math.sin(rotate)*throttle*resultb) * SystemSettings.RES_SCALAR, -1.0),1);
		return desiredRightOutput;
	}
	
	public static double calculateLeft(double throttle, double rotate, double LEFT_SCALAR)
	{
		double resultc;
		double resultd;
		
		if(rotate<0 && throttle >0 || rotate>0 && throttle<0)
		{
			resultc = OUTER_GAIN;
		}
		else
			resultc = INNER_GAIN;
		
		if(rotate>0 && throttle >0 || rotate<0 && throttle<0)
		{
			resultd = 1.0;
		}
		else
			resultd = -1.0;
		
		desiredLeftOutput=Math.min(Math.max((throttle+LEFT_SCALAR*rotate*resultc+Math.sin(throttle)*LEFT_SCALAR*Math.sin(rotate)*throttle*resultd) * SystemSettings.RES_SCALAR,-1.0),1);
		return desiredLeftOutput;
	}

	@Override
	public boolean update(double pNow) {
		
		
		
//		desiredLeftOutput=Math.min(Math.max((rotate+LEFT_SCALAR*throttle*result+Math.sin(rotate)*LEFT_SCALAR*Math.sin(throttle)*rotate*resultb) * 1.0/*RES_SCALE*/,-1.0),1);
//		desiredRightOutput=Math.min(Math.max((rotate+RIGHT_SCALAR*throttle*resultc+Math.sin(rotate)*LEFT_SCALAR*Math.sin(throttle)*rotate*resultd) * 1.0/*RES_SCALE*/,-1.0),1);
		return false;
	}
	
//	public double getDesiredRight()
//	{
//		synchronized(dvLock)
//		{
//			return desiredRightOutput;
//		}
//	}
//	
//	public double getDesiredLeft()
//	{
//		synchronized (dvLock) 
//		{
//			return desiredLeftOutput;
//		}
//	}
	
	
	
	

}
