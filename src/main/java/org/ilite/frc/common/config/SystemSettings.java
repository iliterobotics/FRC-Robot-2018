package org.ilite.frc.common.config;

import java.util.concurrent.TimeUnit;

import org.ilite.frc.common.types.ELogitech310;

import com.flybotix.hfr.io.MessageProtocols.EProtocol;
import com.team254.lib.util.ConstantsBase;

public class SystemSettings extends ConstantsBase {
  
  public static double CONTROL_LOOP_PERIOD = 0.01; // seconds
  public static TimeUnit SYSTEM_TIME_UNIT = TimeUnit.SECONDS;
  
  // =============================================================================
  // Comms
  // =============================================================================
  public static EProtocol CODEX_DATA_PROTOCOL = EProtocol.UDP;
  public static int     DRIVER_STATION_CODEX_DATA_RECEIVER_PORT = 7777;
  public static String  DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "10.18.85.10";
//  public static String  DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "172.22.11.1";
  public static String[]  DRIVER_STATION_CODEX_DATA_RECEIVER_HOSTS = new String[]{
      "10.18.85.10",
      "172.22.11.1",
  };
  
  public static int     ROBOT_CODEX_DATA_SENDER_PORT = 7778;
  
  // =============================================================================
  // Talon Addresses
  // =============================================================================
  public static  int kDRIVETRAIN_TALONID_LEFT1 = 8;
	public static  int kDRIVETRAIN_TALONID_LEFT2 = 9;
  public static  int kDRIVETRAIN_TALONID_LEFT3 = 10;
	public static  int kDRIVETRAIN_TALONID_RIGHT1 = 5;
	public static  int kDRIVETRAIN_TALONID_RIGHT2 = 6;
	public static  int kDRIVETRAIN_TALONID_RIGHT3 = 7;
	public static int INTAKE_TALONID_FRONT_LEFT = 13;
	public static int INTAKE_TALONID_FRONT_RIGHT = 14;
	public static int ELEVATOR_TALONID_LEFT = 11;
	public static int ELEVATOR_TALONID_RIGHT = 12;
	
	public  static int kCONTROLLER_ID = 0;

  // =============================================================================
  // Drive Train Constants
  // =============================================================================
  public static double  DRIVETRAIN_WHEEL_DIAMETER = 3.98;
  public static int     DRIVETRAIN_SHIFT_SOLENOID_ID = 2;
  public static double  DRIVETRAIN_DEFAULT_RAMP_RATE = 120.0; // in V/sec
  public static double  DRIVETRAIN_HIGH_GEAR_RAMP_RATE = 120.0; // in V/sec
  
  // =============================================================================
  // Pigeon
  // =============================================================================  
  public static int PIGEON_DEVICE_ID = 5;
  // =============================================================================
  // Input Constants
  // =============================================================================
  public static double  INPUT_DEADBAND_F310_JOYSTICK = 0.05;
  public static double  INPUT_DEADBAND_F310_TRIGGER = 0.5;
  public static int     JOYSTICK_PORT_DRIVER = 0;
  public static int     JOYSTICK_PORT_OPERATOR = 1;

  // =============================================================================
  // Controller Mapping
  // =============================================================================
  public static ELogitech310  DRIVER_MAP_THROTTLE_AXIS = ELogitech310.LEFT_Y_AXIS;
  public static ELogitech310  DRIVER_MAP_TURN_AXIS = ELogitech310.COMBINED_TRIGGER_AXIS;
  public static ELogitech310  DRIVER_MAP_INTAKE_BUTTON = ELogitech310.A_BTN;
  public static ELogitech310  DRIVER_MAP_OUTTAKE_BUTTON = ELogitech310.B_BTN;
  
  @Override
  public String getFileLocation() {
    return "~/constants.txt";
  }
}
