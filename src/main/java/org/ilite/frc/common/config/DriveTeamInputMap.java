package org.ilite.frc.common.config;

import org.ilite.frc.common.types.ELogitech310;

public class DriveTeamInputMap {
  public static final ELogitech310
  
    DRIVER_TURN_AXIS = ELogitech310.RIGHT_X_AXIS,
    DRIVER_THROTTLE_AXIS = ELogitech310.LEFT_Y_AXIS,

    DRIVER_SUB_WARP_AXIS = ELogitech310.RIGHT_TRIGGER_AXIS,
    
    DRIVER_SEARCH_CUBE_LEFT_BTN = ELogitech310.L_BTN,
    DRIVER_SEARCH_CUBE_RIGHT_BTN = ELogitech310.R_BTN,
    DRIVER_SEARCH_EXCHANGE_LEFT_BTN = ELogitech310.DPAD_LEFT,
    DRIVER_SERACH_EXCHANGE_RIGHT_BTN = ELogitech310.DPAD_RIGHT,
  
//    DRIVE_SNAIL_MODE = ELogitech310.LEFT_TRIGGER_AXIS,
    
    OPERATOR_CARRIAGE_RESET = ELogitech310.L_BTN,
    OPERATOR_CARRIAGE_GRAB = ELogitech310.X_BTN,
    OPERATOR_CARRIAGE_KICK = ELogitech310.R_BTN,
    OPERATOR_INTAKE_OUT_BTN = ELogitech310.DPAD_UP,
    OPERATOR_INTAKE_IN_BTN = ELogitech310.DPAD_DOWN,
    OPERATOR_OPEN_LOOP_INTAKE_AXIS_1 = ELogitech310.RIGHT_Y_AXIS,
    OPERATOR_OPEN_LOOP_INTAKE_AXIS_2 = ELogitech310.LEFT_Y_AXIS,
    OPERATOR_INTAKE_CLAW_BTN = ELogitech310.Y_BTN,
    OPERATOR_HOLD_INTAKE_OUT = ELogitech310.DPAD_RIGHT,
    
//    OPERATOR_ELEVATOR_OPEN_LOOP_CONTROL_AXIS = ELogitech310.LEFT_Y_AXIS,
    OPERATOR_ZERO_ELEVATOR_INPUTS = ELogitech310.START,
    OPERATOR_ELEVATOR_UP_AXIS = ELogitech310.RIGHT_TRIGGER_AXIS,
    OPERATOR_ELEVATOR_DOWN_AXIS = ELogitech310.LEFT_TRIGGER_AXIS,
    OPERATOR_CLIMBER_AXIS = ELogitech310.LEFT_Y_AXIS,
    OPERATOR_ELEVATOR_SETPOINT_GROUND_BTN = ELogitech310.A_BTN,
    OPERATOR_ELEVATOR_SETPOINT_SWITCH_BTN = ELogitech310.B_BTN,
    OPERATOR_ELEVATOR_SETPOINT_SCALE = ELogitech310.Y_BTN
    ;
}
