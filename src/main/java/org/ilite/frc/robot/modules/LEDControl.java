package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.Hardware;
import org.ilite.frc.robot.modules.Carriage.CarriageState;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LEDControl implements IModule {

	private CANifier mCanifier;
	private long blinkStartTime;
	private boolean isOn;
	private Message mCurrentMessage;
	private Elevator mElevator;
	private Carriage mCarriage;
	private Intake mIntake;
	private Hardware mHardware;
	
	public enum LEDColor {
		PURPLE(255, 0, 200), 
		RED(255, 0, 0), 
		LIGHT_BLUE(0, 100, 220),
		WHITE(255, 255, 255), 
		GREEN(0, 255, 0),
		YELLOW(255, 255, 0),
		DEFAULT_COLOR(0, 0, 0), 
		GREEN_HSV(84, 255, 255),
		BLUE(0, 0, 255),
		RED_HSV(0, 255, 255),
		YELLOW_HSV(20, 255, 255),
		PURPLE_HSV(212, 255, 255),
		NONE(0, 0, 0);
		
		final int r, g, b;
		LEDColor(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
		
	//pulse speed = 100, slow flash = 300, solid = 0
	public enum Message{
		HAS_CUBE(LEDColor.PURPLE, 100),
	  ELEV_DECEL(LEDColor.YELLOW, 100),
	  CURRENT_LIMITING(LEDColor.RED, 0),
	  KICKING_CUBE(LEDColor.LIGHT_BLUE, 0),
	  NONE(LEDColor.NONE, 0);

		final LEDColor color;
		final int delay;
		Message(LEDColor color, int delay)
		{
			this.color = color;
			this.delay = delay;
		}
	}

	
	public LEDControl(Intake pIntake, Elevator pElevator, Carriage pCarriage, Hardware pHardware)
	{
	  mIntake = pIntake;
	  mElevator = pElevator;
	  mCarriage = pCarriage;
		mHardware = pHardware;
		this.isOn = true;
	}
	public void initialize(double pNow) {
    mCanifier = mHardware.getCanifier();
	  mCurrentMessage = Message.NONE;
		blinkStartTime = System.currentTimeMillis();
	}
	
	/**
	 * Updates LED strip based on mechanism states. We check mechanisms in order of lowest to highest priority.
	 */
	@Override
	public boolean update(double pNow) {
	  mCurrentMessage = Message.NONE;
	  if(mIntake.isCurrentLimiting()) mCurrentMessage = Message.CURRENT_LIMITING;
	  if(mCarriage.getBeamBreak()) mCurrentMessage = Message.HAS_CUBE;
	  if(mElevator.elevatorState == EElevatorState.DECELERATE_TOP) mCurrentMessage = Message.ELEV_DECEL;
	  if(mElevator.isCurrentLimiting()) mCurrentMessage = Message.CURRENT_LIMITING;
	  if(mCarriage.getCurrentState() == CarriageState.KICKING) mCurrentMessage = Message.KICKING_CUBE;
	  setLED(mCurrentMessage);
		return false;
	}
	

	// A = Green B = Red C = Blue
	private double[] colorCreator(LEDColor color)
	{
		//order = grb
		double[] rgb = new double[3];
		rgb[0] = (double)color.g / 256;
		rgb[1] = (double)color.r / 256;
		rgb[2] = (double)color.b / 256;
		return rgb;
	}
	
	//will be obsolete
	public void setLED(double r, double g, double b)
	{
		setLED(new double[] {g, r, b});
	}
	
	public void setLED(Message m)
	{
		setLED(colorCreator(m.color), m.delay);
	}
	private void setLED(double[] rgb)
	{
		mCanifier.setLEDOutput(rgb[0], CANifier.LEDChannel.LEDChannelA);
		mCanifier.setLEDOutput(rgb[1], CANifier.LEDChannel.LEDChannelB);
		mCanifier.setLEDOutput(rgb[2], CANifier.LEDChannel.LEDChannelC);
	}
	
	private void setLED(double[] rgb, long blinkDelay)
	{
		if(blinkDelay == 0)
		{
			isOn = true;
		}
		else if(System.currentTimeMillis() - blinkStartTime > blinkDelay) {
			isOn = !isOn;
			blinkStartTime = System.currentTimeMillis();
		}
		if(isOn) {
			setLED(rgb);
		} else {
			turnOffLED();
		}
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
