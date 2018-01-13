package org.ilite.frc.common.frc.robot.types.copy;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;

public enum ELogitechAttack3 implements CodexOf<Double>{
  TRIGGER,
  THUMB_BTN2,
  THUMB_BTN3,
  THUMB_BTN4,
  THUMB_BTN5,
  BASE_LEFT_6,
  BASE_LEFT_7,
  BASE_MID_8,
  BASE_MID_9,
  BASE_RIGHT_10,
  BASE_RIGHT_11,
  X_AXIS,
  Y_AXIS,
  THROTTLE,
  ID;

  
  public static void map(int pID, Codex<Double, ELogitechAttack3> pCodex, Joystick pJoystick) {
    pCodex.reset();
    for(int i = 0; i < 11; i++) {
      pCodex.set(i, pJoystick.getRawButton(i+1) ? 1d : null);
    }
    pCodex.set(X_AXIS, pJoystick.getAxis(AxisType.kX));
    pCodex.set(Y_AXIS, pJoystick.getAxis(AxisType.kY));
    pCodex.set(THROTTLE, pJoystick.getAxis(AxisType.kZ));
    pCodex.set(ID, Double.valueOf(pID));
  }
  
  public static void map(Codex<Double, ELogitechAttack3> pCodex, Joystick pJoystick) {
    map(0, pCodex, pJoystick);
  }
}
