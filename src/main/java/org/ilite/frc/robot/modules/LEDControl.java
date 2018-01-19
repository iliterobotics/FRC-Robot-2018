package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LEDControl implements IModule {

	private CANifier mCanifier;
	public enum LEDColor {
		PURPLE(255, 0, 255), 
		RED(255, 0, 0), 
		LIGHT_BLUE(0, 100, 255),
		WHITE(255, 255, 255), 
		GREEN(0, 255, 0),
		YELLOW(255, 255, 0),
		DEFAULT_COLOR(0, 0, 0), 
		GREEN_HSV(84, 255, 255), 
		RED_HSV(0, 255, 255),
		YELLOW_HSV(20, 255, 255),
		PURPLE_HSV(212, 255, 255);
		
		final int r, g, b;
		LEDColor(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
	
	public enum Conditions
	{
		
		final float colorOutput;
		Conditions(float colorOutput)
		{
			this.colorOutput = colorOutput;
		}
	}
	
	public LEDControl(Hardware pHardware)
	{
		mCanifier = pHardware.getCanifier();
	}
	public void initialize(double pNow) {
		
		mCanifier.setLEDOutput(0, CANifier.LEDChannel.LEDChannelA);
		mCanifier.setLEDOutput(0, CANifier.LEDChannel.LEDChannelB);
		mCanifier.setLEDOutput(0, CANifier.LEDChannel.LEDChannelC);
	}

	@Override
	public void update(double pNow) {
		SmartDashboard.putNumber("time", Timer.getFPGATimestamp());
	}
	

	public float colorCreator(int r, int g, int b)
	{
		return 0;
	}
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}

}
