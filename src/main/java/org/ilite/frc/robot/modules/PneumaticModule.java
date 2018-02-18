package org.ilite.frc.robot.modules;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

/**
 * Class which controls the compressor.  Uses the module framework.
 */
public class PneumaticModule implements IModule {
  private boolean mIsCompressorOn = false;
  private boolean mOverrideCompressor = false;

  private Relay mCompressorRelay = null;
  private DigitalInput mCutoffSwitch = null;

  /**
   * Create a Pneumatic Module that is controlled by a relay and switch plugged
   * into DIO
   * 
   * @param pRelayPortId
   *          - The relay port id
   * @param pPressureCutoffSwitchDIOPortId
   *          - the DIO port that has the pressure switch
   */
  public PneumaticModule(int pRelayPortId, int pPressureCutoffSwitchDIOPortId) {
    mCompressorRelay = new Relay(pRelayPortId);
    mCutoffSwitch = new DigitalInput(pPressureCutoffSwitchDIOPortId);
  }

  @Override
  public void shutdown(double pNow) {
    forceCompressorOff();
  }

  @Override
  public void initialize(double pNow) {
  }

  @Override
  public boolean update(double pNow) {

    // In an override state, the compressor can turn off but it cannot turn on.
    if (mOverrideCompressor) {
      System.out.println("Overriding compressor off");
      mIsCompressorOn = false;
    } else {
      System.out.println("Pressure switch state: " + mCutoffSwitch.get());
      // This sensor returns true if pressure >= its threshold.
      mIsCompressorOn = !mCutoffSwitch.get();
    }

    if (mIsCompressorOn) {
      mCompressorRelay.set(Relay.Value.kForward);
      System.out.println("ON");
    } else {
      mCompressorRelay.set(Relay.Value.kOff);
      System.out.println("OFF");
    }
    // System.out.println("Voltage: " + aio.getVoltage() + "v");
    // System.out.println("Pressure: " + ((250 * ( voltageReadout/5 )) - 25));
    return true;
  }

  /**
   * Resumes normal operation of the compressor.
   */
  public void clearCompressorOverride() {
    mOverrideCompressor = false;
  }

  /**
   * Useful to force the compressor off under high-power-consumption scenarios
   */
  public void forceCompressorOff() {
    mOverrideCompressor = true;
    mIsCompressorOn = false;
  }

  /**
   * @return whether or not the compressor is on as of the last update cycle
   */
  public boolean isCompressorOn() {
    return mIsCompressorOn;
  }

  public void forceCompressorOn() {
    mOverrideCompressor = false;
    mIsCompressorOn = true;
  }

}
