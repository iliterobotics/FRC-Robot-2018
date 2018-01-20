package org.ilite.frc.common.input;


/**
 * Enumeration that drives input scaling for any joystick.  For example,
 * the following code will produce the same scale that we had in 2017 for turning:<br><br>
 * <code>EInputScale.EXPONENTIAL.map( [joystick input], 2);</code>
 */
public enum EInputScale {
  RAW_SIN,
  PCT_SIN,
  SIN_WITH_FGAIN1,
  SIN_WITH_FGAIN2,
  SIN_WITH_FGAIN3,
  EXPONENTIAL,
  UNIT
  ;

  /**
   * @param pJoystickInput - the joystick, as-read from the codex
   * @param pScalar - the scalar to be applied.  How it is applied depends upon the enumeration itself.  
   *    USE SYSTEM SETTINGS to define constants for these scalars.
   * @return - the scaled equivalent of the joystick input
   */
  public double map(double pJoystickInput, double pScalar) {
    switch(this) {
    case SIN_WITH_FGAIN1 : 
      return Math.sin(Math.PI/2 * pScalar * pJoystickInput) / Math.sin(Math.PI / 2 * pScalar);
    case SIN_WITH_FGAIN2 : 
      return Math.sin(Math.PI/2 * pScalar * SIN_WITH_FGAIN1.map(pJoystickInput, pScalar)) / Math.sin(Math.PI / 2 * pScalar);
    case SIN_WITH_FGAIN3 : 
      return Math.sin(Math.PI/2 * pScalar * SIN_WITH_FGAIN2.map(pJoystickInput, pScalar)) / Math.sin(Math.PI / 2 * pScalar);
    case PCT_SIN : 
      return Math.sin(pJoystickInput) / Math.sin(1);
    case RAW_SIN : 
      return Math.sin(pJoystickInput);
    case EXPONENTIAL : 
      double sign = pJoystickInput > 0 ? 1 : -1;
      return Math.pow(pJoystickInput, pScalar) * sign;
    case UNIT:
    default:
      return pJoystickInput;
      
    }
  }
  
  /**
   * Since not all methods need a scalar, this utility method reduces some boilerplate code.
   * @param pJoystickInput - the joystick, as-read from the codex
   * @return - the scaled equivalent of the joystick input
   */
  public double map(double pJoystickInput) {
    return map(pJoystickInput, 1d);
  }
}
