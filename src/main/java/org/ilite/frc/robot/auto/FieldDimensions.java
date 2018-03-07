package org.ilite.frc.robot.auto;

import org.ilite.frc.common.config.SystemSettings;

/**
 * Represent field dimensions in feet. The origin is the bottom left corner of the field.
 * @author Stephen Welch
 *
 */
public class FieldDimensions {
  
  public static final double FIELD_LENGTH = 0;
  public static final double FIELD_WIDTH = 0;
  
  public static final double PORTAL_CORNER_TO_FIELD_EDGE = 2.474166666666667;
  public static final double EXCHANGE_TAPE_TO_FIELD_EDGE = 14.5;
  
  public static final double LEFT_STARTING_POS_X = SystemSettings.ROBOT_LENGTH;
  public static final double LEFT_STARTING_POS_Y = FIELD_WIDTH - PORTAL_CORNER_TO_FIELD_EDGE - SystemSettings.ROBOT_CENTER_TO_SIDE;
  public static final double RIGHT_STARTING_POS_X = SystemSettings.ROBOT_LENGTH;
  public static final double RIGHT_STARTING_POS_Y = PORTAL_CORNER_TO_FIELD_EDGE + SystemSettings.ROBOT_CENTER_TO_SIDE;
  public static final double MIDDLE_STARTING_POS_X = SystemSettings.ROBOT_LENGTH;
  public static final double MIDDLE_STARTING_POS_Y = EXCHANGE_TAPE_TO_FIELD_EDGE - SystemSettings.ROBOT_CENTER_TO_SIDE;
  
  public static final double LEFT_SWITCH_FRONT_X = 12;
  public static final double LEFT_SWITCH_FRONT_Y = MIDDLE_STARTING_POS_Y;
  
  public static final double RIGHT_SWITCH_FRONT_X = 12;
  public static final double RIGHT_SWITCH_FRONT_Y = 18;
  
  public static final double LEFT_SWITCH_SIDE_X = 14;
  public static final double LEFT_SWITCH_SIDE_Y = 20;
  
  public static final double RIGHT_SWITCH_SIDE_X = 14;
  public static final double RIGHT_SWITCH_SIDE_Y = 7;
  
  public static final double LEFT_SWITCH_BACK_X = 0;
  public static final double LEFT_SWITCH_BACK_Y = 0;
  
  public static final double RIGHT_SWITCH_BACK_X = 0;
  public static final double RIGHT_SWITCH_BACK_Y = 0;
  
  public static final double LEFT_SCALE_X = 24;
  public static final double LEFT_SCALE_Y = LEFT_STARTING_POS_Y;
  
  public static final double RIGHT_SCALE_X = 24;
  public static final double RIGHT_SCALE_Y = LEFT_STARTING_POS_Y;
  
}
