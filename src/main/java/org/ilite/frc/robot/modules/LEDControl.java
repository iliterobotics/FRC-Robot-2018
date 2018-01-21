package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.Hardware;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LEDControl implements IModule {

	private CANifier mCanifier;
	private boolean isBlinking;
	private long blinkStartTime;
	private boolean isOn;
	
	public enum LEDColor {
		PURPLE(255, 0, 255), 
		RED(255, 0, 0), 
		LIGHT_BLUE(0, 100, 255),
		WHITE(255, 255, 255), 
		GREEN(0, 255, 0),
		YELLOW(255, 255, 0),
		DEFAULT_COLOR(0, 0, 0), 
		GREEN_HSV(84, 255, 255),
		BLUE(0, 0, 255),
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
	
	public enum Condition
	{
		EMERGENCY(LEDColor.RED, false),
		DEFAULT(LEDColor.PURPLE, false),
		BLINK_BLUE(LEDColor.BLUE, true);
		final LEDColor color;
		final boolean blink;
		Condition(LEDColor color, boolean blink)
		{
			this.color = color;
			this.blink = blink;
		}
	}

	
	public LEDControl(Hardware pHardware)
	{
		mCanifier = pHardware.getCanifier();
		this.isBlinking = true;
		this.isOn = true;
	}
	public void initialize(double pNow) {
		turnOffLED();
		blinkStartTime = System.currentTimeMillis();
	}

	@Override
	public void update(double pNow) {
		SmartDashboard.putNumber("time", Timer.getFPGATimestamp());
		if(isBlinking) {
			if(System.currentTimeMillis() - blinkStartTime > 300) {
				isOn = !isOn;
				blinkStartTime = System.currentTimeMillis();
			}
			if(isOn) {
				setLED(255, 255, 255);
			} else {
				setLED(0, 0, 0);
			}
		}
	}
	

	// A = Green B = Red C = Blue
	private double[] colorCreator(LEDColor color)
	{
		//order = grb
		double[] rgb = new double[3];
		rgb[0] = color.g / 256;
		rgb[1] = color.r / 256;
		rgb[2] = color.b / 256;
		return rgb;
	}
	
	public void setLED(double r, double g, double b)
	{
		setLED(new double[] {g, r, b});
	}
	public void setLED(Condition c)
	{
//		if(c.blink)
//			setLEDBlink(colorCreator(c.color));
//		else
			setLED(colorCreator(c.color));
	}
	private void setLED(double[] rgb)
	{
		mCanifier.setLEDOutput(rgb[0], CANifier.LEDChannel.LEDChannelA);
		mCanifier.setLEDOutput(rgb[1], CANifier.LEDChannel.LEDChannelB);
		mCanifier.setLEDOutput(rgb[2], CANifier.LEDChannel.LEDChannelC);
	}
	
	private void setLEDBlink(double[] rgb)
	{
		
		
	}
	
	public void turnOffLED()
	{
		setLED(new double[] {0, 0, 0});
	}
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub
		
	}

}
