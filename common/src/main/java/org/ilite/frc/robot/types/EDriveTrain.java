package org.ilite.frc.robot.types;

import com.flybotix.hfr.codex.CodexOf;

public enum EDriveTrain implements CodexOf<Double> {
  DESIRED_LEFT_POWER,
  DESIRED_RIGHT_POWER,
  VOLTAGE_RAMP_RATE,
  LEFT_POSITION,
  RIGHT_POSITION,
  LEFT_VELOCITY,
  RIGHT_VELOCITY,
  IS_SHIFT,
  
  OPEN_LOOP_THROTTLE,
  OPEN_LOOP_TURN,
  OPEN_LOOP_CALC_LEFT_POWER,
  OPEN_LOOP_CALC_RIGHT_POWER,
  
}
