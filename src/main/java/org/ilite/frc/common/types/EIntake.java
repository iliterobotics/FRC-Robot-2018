package org.ilite.frc.common.types;

import org.ilite.frc.robot.modules.Intake;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum EIntake implements CodexOf<Double> {

  LEFT_BUS_VOLTAGE, RIGHT_BUS_VOLTAGE,
  LEFT_OUT_VOLTAGE, RIGHT_OUT_VOLTAGE,
  LEFT_OUT_CURRENT, RIGHT_OUT_CURRENT,
  LEFT_CURRENT_RATIO, RIGHT_CURRENT_RATIO,
  LEFT_DESIRED_POWER, RIGHT_DESIRED_POWER,
  IS_CURRENT_LIMITING,
  IS_RETRACTED;
  
  public static void map(Codex<Double, EIntake> pCodex, Intake pIntake) {
    
    pCodex.set(LEFT_BUS_VOLTAGE, pIntake.getLeftBusVoltage());
    pCodex.set(RIGHT_BUS_VOLTAGE, pIntake.getRightBusVoltage());
    
    pCodex.set(LEFT_OUT_VOLTAGE, pIntake.getLeftOutVoltage());
    pCodex.set(RIGHT_OUT_VOLTAGE, pIntake.getRightOutVoltage());
    
    pCodex.set(LEFT_OUT_CURRENT, pIntake.getLeftOutputCurrent());
    pCodex.set(RIGHT_OUT_CURRENT, pIntake.getRightOutputCurrent());
    
    pCodex.set(LEFT_CURRENT_RATIO, pIntake.getLeftCurrentRatio());
    pCodex.set(RIGHT_CURRENT_RATIO, pIntake.getRightCurrentRatio());
    
    pCodex.set(LEFT_DESIRED_POWER, pIntake.getLeftDesiredPower());
    pCodex.set(RIGHT_DESIRED_POWER, pIntake.getRightDesiredPower());
    
    pCodex.set(IS_CURRENT_LIMITING, pIntake.isCurrentLimiting() ? 1.0 : 0.0);
    
    pCodex.set(IS_RETRACTED, pIntake.isRetracted() ? 1.0 : 0.0);
    
  }
  
}
