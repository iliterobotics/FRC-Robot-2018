package org.ilite.frc.common.sensors;

import com.ctre.phoenix.sensors.PigeonIMU;

public class Pigeon extends IMU{

	private double[] ypr;
  private short[] xyz;
	private PigeonIMU mPigeon;
  
  //TODO - single value for now - could be VERY noisy
  // others to try: {0.75, 0.25}, {0.6, 0.4}, {0.5, 0.3, 0.2}
  private static final double[] kCollisionGains = {1.0};
  
  /**
   * Creates a Pigeon sensor wrapper from the hardware.  Assumes the hardware has already been
   * initialized on the CAN bus.  Uses 0.5G as the default g-force.
   * @param pHardware - Initialized PigeonIMU
   */
  public Pigeon(PigeonIMU pHardware){
    this(pHardware, 0.5d);
  }
	
	/**
	 * Creates a Pigeon sensor wrapper from the hardware.  Assumes the hardware has already been
	 * initialized on the CAN bus.
   * @param pHardware - Initialized PigeonIMU
   * @param pCollisionThreshold_DeltaG - The threshold to use for the collision g-force
	 */
	public Pigeon(PigeonIMU pHardware, double pCollisionThreshold_DeltaG){
	  super(kCollisionGains);
		ypr = new double[3];
		xyz = new short[3];
		mPigeon = pHardware;
		setCollisionThreshold_DeltaG(pCollisionThreshold_DeltaG);
	}
	
	/**
	 * Pre-populates the filters & calculated values so it's done only once per cycle
	 * @param pTimestampNow
	 */
	protected void updateSensorCache(double pTimestampNow) {
    mPigeon.getYawPitchRoll(ypr);
    for(int i = 0 ; i < ypr.length; i++) {
      ypr[i] = clampDegrees(ypr[i]);
    }
    
    mPigeon.getBiasedAccelerometer(xyz);
	}
	
	public double getHeading() {
	  return mPigeon.getFusedHeading();
	}

	public double getYaw() {
    return ypr[0];
	}
	
	public double getPitch() {
    return ypr[1];
	}
	
	public double getRoll() {
    return ypr[2];
	}

  protected double getRawAccelX() {
    return xyz[0];
  }
  
  protected double getRawAccelY() {
    return xyz[0];
  }
	
	public void zeroAll() {
		for(int i = 0; i < ypr.length; i++) {
			ypr[i] = 0;
		}
    for(int i = 0; i < xyz.length; i++) {
      xyz[i] = 0;
    }
		mPigeon.setFusedHeading(0d, 20); //TODO - figure out CAN timeout defaults
	}
}