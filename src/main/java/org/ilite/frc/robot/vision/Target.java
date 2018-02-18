package org.ilite.frc.robot.vision;

/**
 * Represents the change in angle, x, and y position in order to reach the target.
 * It also provides methods to find these values.
 * @author Stephen Welch
 *
 */
public class Target {

  public final double centerX, centerY, deltaX, deltaY, deltaAngle, deltaDistance;
  
  public Target(double centerX, double centerY, double deltaX, double deltaY, double deltaAngle) {
    this.centerX = centerX;
    this.centerY = centerY;
    this.deltaX = deltaX;
    this.deltaY = deltaY;
    this.deltaDistance = Math.hypot(deltaX, deltaY);
    this.deltaAngle = deltaAngle;
  }
  
  public static Target fromDeltaPosition(double centerX, double centerY, double deltaX, double deltaY) {
    double deltaDistance = Math.hypot(deltaX, deltaY);
    double deltaAngle = Math.acos((Math.pow(deltaY, 2) + Math.pow(deltaDistance, 2) - Math.pow(deltaX, 2)) / (2 * deltaX * deltaDistance));
    return new Target(centerX, centerY, deltaX, deltaY, deltaAngle);
  }
  
  public static Target fromDeltaX(double centerX, double centerY, double deltaX, double deltaAngle) {
    double deltaY = deltaX / Math.tan(deltaAngle);
    return new Target(centerX, centerY, deltaX, deltaY, deltaAngle);
  }
  
  public Target fromDeltaY(double centerX, double centerY, double deltaY, double deltaAngle) {
    double deltaX = deltaY * Math.tan(deltaAngle);
    return new Target(centerX, centerY, deltaX, deltaY, deltaAngle);
  }
  
}
