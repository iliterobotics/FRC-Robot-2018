package org.ilite.frc.common.sensors;
import org.ilite.frc.common.util.FilteredAverage;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Ultrasonic;
public class UltraSonicSensor {
	private Ultrasonic mWpiUltrasonic;
	private AnalogInput analogInput;
	private double distance;
	private static final double MM_CONVERSION = 0.03937007874;
	private static final double ACD_CONVERSION = 1474.358974;
	private static final double SCALE_FACTOR = 0.2;
  //TDO - single value for now - could be VERY noisy
  // others to try: {0.75, 0.25}, {0.6, 0.4}, {0.5, 0.3, 0.2}
	private static final double[] kFilterGains = {1.0};
	private final FilteredAverage mDistanceFilter;
	/**
	 * Creates a ultrasonic sensor using channels that are passed in.  The sensor is
	 * DISABLED by default so it isn't running when the bot turns on.  Call setEnabled(true)
	 * when measurements are needed, and setEnabled(false) when they are not.
	 * @param pPingChannelDIOid - The sending DIO port
	 * @param pEchoChannelDIOid - The receiving DIO port
	 */
//	public UltraSonicSensor(int pPingChannelDIOid, int pEchoChannelDIOid) {
//	  DigitalOutput ping = new DigitalOutput(pPingChannelDIOid);
//	  DigitalInput echo = new DigitalInput(pEchoChannelDIOid);
//    mWpiUltrasonic = new Ultrasonic(ping, echo);
//    System.out.println("built");
//    mWpiUltrasonic.setEnabled(false);
//    mWpiUltrasonic.setAutomaticMode(true);
//    mDistanceFilter = new FilteredAverage(kFilterGains);
//    System.out.println("success");
//	}
	
	
	public UltraSonicSensor(int pUltrasonicID)
	{
		analogInput = new AnalogInput(pUltrasonicID);
		mDistanceFilter = new FilteredAverage(kFilterGains);
		distance = 0;
	}
	/**
	 * Provides a means to turn this on & off.
	 * @param pEnabled - True = on, False  = off
	 */
//	public void setEnabled(boolean pEnabled) {
//	  if(!mWpiUltrasonic.isEnabled() == pEnabled) {
//	    mWpiUltrasonic.setEnabled(pEnabled);
//	  }
//	  if(!pEnabled) {
//	    mDistanceFilter.clear();
//	  }
//	}
	
	/**
	 * @return The filtered average of the most recent range measurements, or 0 if
	 * the sensor is 
	 */
//	public double getInches() {
//	  if(!mWpiUltrasonic.isRangeValid() || !mWpiUltrasonic.isEnabled()) {
//	    mDistanceFilter.clear();
//	    return 0d;
//	  }
//    mDistanceFilter.addNumber(mWpiUltrasonic.getRangeInches());
//		return mDistanceFilter.getAverage();
//	}
	
	public double getInches()
	{
		calculate();
//		mDistanceFilter.addNumber(distance);
//		return mDistanceFilter.getAverage();
		return distance;
	}
	
	private void calculate()
	{
		distance = (analogInput.getVoltage() * ACD_CONVERSION) * MM_CONVERSION;
		
	}
	
	public void zeroDistance()
	{
		distance = 0;
	}
}