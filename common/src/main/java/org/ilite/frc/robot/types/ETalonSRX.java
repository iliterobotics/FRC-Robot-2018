package org.ilite.frc.robot.types;

import com.ctre.CANTalon;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum ETalonSRX implements CodexOf<Double>{
  RAW_GET,
  OUTPUT_CURRENT,
  OUTPUT_VOLTAGE,
  BUS_VOLTAGE;
  
  public static void map(Codex<Double, ETalonSRX> pCodex, CANTalon pTalon) {
    pCodex.set(BUS_VOLTAGE, pTalon.getBusVoltage());
    pCodex.set(OUTPUT_CURRENT, pTalon.getOutputCurrent());
    //pCodex.set(OUTPUT_VOLTAGE, pTalon.getOutputVoltage());
//    pCodex.set(RAW_GET, pTalon.get());
  }
}
