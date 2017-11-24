package org.ilite.frc.robot.types;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public enum EPowerDistPanel implements CodexOf<Double> {
  CURRENT0,
  CURRENT1,
  CURRENT2,
  CURRENT3,
  CURRENT4,
  CURRENT5,
  CURRENT6,
  CURRENT7,
  CURRENT8,
  CURRENT9,
  CURRENT10,
  CURRENT11,
  CURRENT12,
  CURRENT13,
  CURRENT14,
  CURRENT15,
  VOLTAGE,
  TEMPERATURE;
  
  public static void map(Codex<Double, EPowerDistPanel> pCodex, PowerDistributionPanel pPDP) {
    pCodex.reset();
    for(int i = 0; i < 16; i++) {
      pCodex.set(i, pPDP.getCurrent(i));
    }
    pCodex.set(VOLTAGE, pPDP.getVoltage());
    pCodex.set(TEMPERATURE, pPDP.getTemperature());
  }
}
