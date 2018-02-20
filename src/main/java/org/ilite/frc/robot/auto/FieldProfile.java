package org.ilite.frc.robot.auto;

public interface FieldProfile {
  
  public double getLeftStartingPosYOffset();
  public double getMiddleStartingPosYOffset();
  public double getRightStartingPosYOffset();
  
  public double getFrontLeftSwitchYOffset();
  public double getFrontRightSwitchYOffset();
  
  public double getSideLeftSwitchXOffset();
  public double getSideRightSwitchXOffset();
  
  public double getBackLeftSwitchXOffset();
  public double getBackRightSwitchXOffset();
  public double getBackLeftSwitchYOffset();
  public double getBackRightSwitchYOffset();
  
  public double getLeftScaleXOffset();
  public double getLeftScaleYOffset();
  public double getRightScaleXOffset();
  public double getRightScaleYOffset();
  
}
