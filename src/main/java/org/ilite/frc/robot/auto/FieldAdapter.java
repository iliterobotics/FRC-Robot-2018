package org.ilite.frc.robot.auto;

/**
 * Allows us to plan paths from the front of the robot and automatically adjust them to the center of the robot.
 * Please note that it ASSUMES certain headings for certain locations. That is because it is the last day of build season, and I don't want to do MATH.
 * @author Stephen Welch
 *
 */
public class FieldAdapter {

  private FieldProfile mField;
  
  public FieldAdapter(FieldProfile pField) {
    this.mField = pField;
  }
 
  public FieldAdapter() {
    
  }
  
  public FieldAdapter() {
    this(new DefaultField());
  }
  
  public double getLeftStartingPosX() {
    return FieldDimensions.LEFT_STARTING_POS_X;
  }
  
  public double getLeftStartingPosY() {
    return FieldDimensions.LEFT_STARTING_POS_Y + mField.getLeftStartingPosYOffset();
  }
  
  public double getMiddleStartingPosX() {
    return FieldDimensions.MIDDLE_STARTING_POS_X;
  }
  
  public double getMiddleStartingPosY() {
    return FieldDimensions.MIDDLE_STARTING_POS_Y + mField.getMiddleStartingPosYOffset();
  }
  
  public double getRightStartingPosX() {
    return FieldDimensions.RIGHT_STARTING_POS_X;
  }
  
  public double getRightStartingPosY() {
    return FieldDimensions.RIGHT_STARTING_POS_Y + mField.getRightStartingPosYOffset();
  }
  
  
  public double getLeftFrontSwitchX() {
    return FieldDimensions.LEFT_SWITCH_FRONT_X;
  }
  
  public double getLeftFrontSwitchY() {
    return FieldDimensions.LEFT_SWITCH_FRONT_Y + mField.getFrontLeftSwitchYOffset();
  }
  
  public double getRightFrontSwitchX() {
    return FieldDimensions.RIGHT_SWITCH_FRONT_X;
  }
  
  public double getRightFrontSwitchY() {
    return FieldDimensions.RIGHT_SWITCH_FRONT_Y + mField.getFrontRightSwitchYOffset();
  }
  
  
  public double getLeftSideSwitchX() {
    return FieldDimensions.LEFT_SWITCH_SIDE_X + mField.getSideLeftSwitchXOffset();
  }
  
  public double getLeftSideSwitchY() {
    return FieldDimensions.LEFT_SWITCH_SIDE_Y;
  }
  
  public double getRightSideSwitchX() {
    return FieldDimensions.RIGHT_SWITCH_SIDE_X + mField.getSideRightSwitchXOffset();
  }
  
  public double getRightSideSwitchY() {
    return FieldDimensions.RIGHT_SWITCH_SIDE_Y;
  }
  
  
  public double getRightScaleX() {
    return FieldDimensions.RIGHT_SCALE_X + mField.getRightScaleXOffset();
  }
  
  public double getRightScaleY() {
    return FieldDimensions.RIGHT_SCALE_Y + mField.getRightScaleYOffset();
  }
  
  public double getLeftScaleX() {
    return FieldDimensions.LEFT_SCALE_X + mField.getLeftScaleXOffset();
  }
  
  public double getLeftScaleY() {
    return FieldDimensions.LEFT_SCALE_Y + mField.getLeftScaleYOffset();
  }
  
}
