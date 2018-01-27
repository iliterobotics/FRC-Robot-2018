package org.ilite.frc.common.sensors;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogPressureSensor {

  public static final double PSI_PER_VOLTAGE = 1;

  public static final double LOW_VOLTAGE = 2.25;

  private AnalogInput mAnalogPort;
  private double mSensorVoltage;

  public AnalogPressureSensor(int pAnalogPortId) {
    mAnalogPort = new AnalogInput(pAnalogPortId);
  }

  public double getPressure() {
    return getVoltage() * PSI_PER_VOLTAGE;
    //TODO - convert this into a pressure via calibration
    //It may turn into a lookup table + interpolation
  }

  public double getVoltage() {
    return mAnalogPort.getVoltage();
  }

  public boolean isCompressorLow(){
    return mSensorVoltage <= LOW_VOLTAGE;
  }

}
