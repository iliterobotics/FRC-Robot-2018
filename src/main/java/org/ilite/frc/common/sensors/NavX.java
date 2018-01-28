package org.ilite.frc.common.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort.Port;

public class NavX extends IMU {
	
	private double initialAngle;
	private final AHRS iahrs;
  
  //TODO - single value for now - could be VERY noisy
  // others to try: {0.75, 0.25}, {0.6, 0.4}, {0.5, 0.3, 0.2}
  private static final double[] kCollisionGains = {1.0};
	
	public NavX(Port pPort){
	  super(kCollisionGains);
	  iahrs = new AHRS(pPort);
	}

	public double getInitialAngle() {
		return initialAngle;
	}

	public double getYaw() {
		return iahrs.getYaw();
	}

	public double getDisplacementX() {
		return iahrs.getDisplacementX();
	}
	
	public double getDisplacementY() {
		return iahrs.getDisplacementY();
	}
	
	public double getDisplacementZ() {
		return iahrs.getDisplacementZ();
	}

	public void resetDisplacement() {
		iahrs.resetDisplacement();
	}
	
	public boolean isCalibrating(){
		return iahrs.isCalibrating();
	}
		
	public double getAngle(){
		return convertTo360(iahrs.getAngle());
	}
	
	public double getAngleOffStart(){
		return getAngleSum(getAngle(), -initialAngle);
	}
	
	public void setInitialAngle(double yaw){
		initialAngle = yaw;
	}

  @Override
  public double getPitch() {
    return iahrs.getPitch();
  }

  @Override
  public double getRoll() {
    return iahrs.getRoll();
  }

  @Override
  public void zeroAll() {
    iahrs.reset();
  }

  @Override
  protected double getRawAccelX() {
    return iahrs.getRawAccelX();
  }

  @Override
  protected double getRawAccelY() {
    return iahrs.getRawAccelY();
  }

  @Override
  protected void updateSensorCache(double pTimestampNow) {
    // TODO Auto-generated method stub
    
  }

}
