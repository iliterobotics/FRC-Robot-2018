package org.ilite.frc.common.types;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import org.ilite.frc.robot.modules.Elevator;

public enum EElevator implements CodexOf<Double> {
  TACH_LEVEL,
  GEAR_STATE,
  CONTROL_MODE,
  STATE,
  DESIRED_POWER,
  CURRENT_ENCODER_TICKS,
  CURRENT_TOP_RATIO,
  CURRENT_BOTTOM_RATIO;

  public static void map(Codex<Double, EElevator> pCodex, Elevator pElevator, double pTimestampNow) {
    pCodex.reset();
    pCodex.set(TACH_LEVEL, (double)pElevator.getCurrentTachLevel());
    pCodex.set(GEAR_STATE, (double)pElevator.getGearState().ordinal());
    pCodex.set(CONTROL_MODE, (double)pElevator.getElevControlMode().ordinal());
    pCodex.set(STATE, (double)pElevator.getElevatorState().ordinal());
    pCodex.set(DESIRED_POWER, pElevator.getDesiredPower());
    pCodex.set(CURRENT_ENCODER_TICKS, (double)pElevator.getCurrentEncoderTicks());
    pCodex.set(CURRENT_TOP_RATIO, 30d/12d);
    pCodex.set(CURRENT_BOTTOM_RATIO, 10d/12d);
  }
}