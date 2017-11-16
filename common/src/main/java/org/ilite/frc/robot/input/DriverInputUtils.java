package org.ilite.frc.robot.input;

import edu.wpi.first.wpilibj.Joystick;

public class DriverInputUtils {
  
  public static final double sMIN_DEAD_BAND_POS = 0.02;
  public static final double sMIN_DEAD_BAND_NEG = -0.02;
  public static final double sMAX_DEAD_BAND_POS = 0.98;
  public static final double sMAX_DEAD_BAND_NEG = -0.98;

  public static double handleDeadband(Joystick pJoystick, int pAxis) {
    double res = pJoystick.getRawAxis(pAxis);
    if(res >= sMIN_DEAD_BAND_NEG && res <= sMIN_DEAD_BAND_POS) return 0d;
    if(res >= sMAX_DEAD_BAND_POS) return 1d;
    if(res <= sMAX_DEAD_BAND_NEG) return -1d;
    return res;
  }
  public static double handleDeadbandOfDifference(Joystick pJoystick, int pAxis1, int pAxis2) {
    double res = pJoystick.getRawAxis(pAxis1) - pJoystick.getRawAxis(pAxis2);
    if(res >= sMIN_DEAD_BAND_NEG && res <= sMIN_DEAD_BAND_POS) return 0d;
    if(res >= sMAX_DEAD_BAND_POS) return 1d;
    if(res <= sMAX_DEAD_BAND_NEG) return -1d;
    return res;
  }
}
