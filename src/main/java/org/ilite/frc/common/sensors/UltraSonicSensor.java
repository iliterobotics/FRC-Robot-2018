package org.ilite.frc.common.sensors;

import org.ilite.frc.common.util.FilteredAverage;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Ultrasonic;

public class UltraSonicSensor {
	private Ultrasonic mWpiUltrasonic;

  //TODO - single value for now - could be VERY noisy
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
	public UltraSonicSensor(int pPingChannelDIOid, int pEchoChannelDIOid) {
	  DigitalOutput ping = new DigitalOutput(pPingChannelDIOid);
	  DigitalInput echo = new DigitalInput(pEchoChannelDIOid);
    mWpiUltrasonic = new Ultrasonic(ping,echo);
    mWpiUltrasonic.setEnabled(false);
    mWpiUltrasonic.setAutomaticMode(true);
    mDistanceFilter = new FilteredAverage(kFilterGains);
	}
	
	/**
	 * Provides a means to turn this on & off.
	 * @param pEnabled - True = on, False  = off
	 */
	public void setEnabled(boolean pEnabled) {
	  if(!mWpiUltrasonic.isEnabled() == pEnabled) {
	    mWpiUltrasonic.setEnabled(pEnabled);
	  }
	  if(!pEnabled) {
	    mDistanceFilter.clear();
	  }
	}
	
	/**
	 * @return The filtered average of the most recent range measurements, or 0 if
	 * the sensor is 
	 */
	public double getInches() {
	  if(!mWpiUltrasonic.isRangeValid() || !mWpiUltrasonic.isEnabled()) {
	    mDistanceFilter.clear();
	    return 0d;
	  }
    mDistanceFilter.addNumber(mWpiUltrasonic.getRangeInches());
		return mDistanceFilter.getAverage();
	}

}