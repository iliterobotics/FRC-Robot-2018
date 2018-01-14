package org.ilite.frc.robot.modules;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;


public class LEDControl implements IModule {

	private CANifier cCanifier;
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
	public void initialize(double pNow) {
		
		cCanifier.setLEDOutput(0, CANifier.LEDChannel.LEDChannelA);
	}

	@Override
	public void update(double pNow) {
		while( )
		{
			cCanifier.setLEDOutput(, LEDChannel.LEDChannelA );
		}
	}

	public float rgbCreator(
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}

}
