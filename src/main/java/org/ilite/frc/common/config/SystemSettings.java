package org.ilite.frc.common.config;

import java.util.concurrent.TimeUnit;

import org.ilite.frc.common.types.ELogitech310;

import com.flybotix.hfr.io.MessageProtocols.EProtocol;
import com.team254.lib.util.ConstantsBase;

import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;

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
	public static  int kDRIVETRAIN_TALONID_RIGHT1 = 6;
	public static  int kDRIVETRAIN_TALONID_RIGHT2 = 5;
	public static  int kDRIVETRAIN_TALONID_RIGHT3 = 7;
	public static int INTAKE_TALONID_FRONT_LEFT = 13;
	public static int INTAKE_TALONID_FRONT_RIGHT = 14;
	public static int ELEVATOR_TALONID_LEFT = 11;
	public static int ELEVATOR_TALONID_RIGHT = 12;
	
	public static int TALON_CONFIG_TIMEOUT_MS = 50;
	public static int kCONTROLLER_ID = 0;
  // =============================================================================
  // Drive Train Constants
  // =============================================================================
  public static double  DRIVETRAIN_WHEEL_DIAMETER = 4.875;
  public static double  DRIVETRAIN_WHEEL_CIRCUMFERENCE = DRIVETRAIN_WHEEL_DIAMETER * Math.PI;
  public static int     DRIVETRAIN_SHIFT_SOLENOID_ID = 2;
  public static double  DRIVETRAIN_DEFAULT_RAMP_RATE = 120.0; // in V/sec
  public static double  DRIVETRAIN_HIGH_GEAR_RAMP_RATE = 120.0; // in V/sec
  public static int		  DRIVETRAIN_ENC_TICKS_PER_TURN = 1024;
  public static double	DRIVETRAIN_EFFECTIVE_WHEELBASE = 0;
  public static double 	DRIVETRAIN_TURN_CIRCUMFERENCE = DRIVETRAIN_EFFECTIVE_WHEELBASE * Math.PI;
  public static double	DRIVETRAIN_INCHES_PER_DEGREE = DRIVETRAIN_TURN_CIRCUMFERENCE / 360;
  public static double	DRIVETRAIN_WHEEL_TURNS_PER_DEGREE = DRIVETRAIN_INCHES_PER_DEGREE / DRIVETRAIN_WHEEL_DIAMETER;
  public static double	DRIVETRAIN_ANGLE_kP = 0;
  public static double	DRIVETRAIN_VELOCITY_kP = 0;
  public static double	DRIVETRAIN_VELOCITY_kI = 0;
  public static double	DRIVETRAIN_VELOCITY_kD = 0;
  public static double	DRIVETRAIN_kA = 0;
  public static double	DRIVETRAIN_kV = 0.00104166666666666666666666666667;
  
  
  // =============================================================================
  // Pigeon
  // =============================================================================  
  public static int PIGEON_DEVICE_ID = 5;
  // =============================================================================
  // Motion Profiling Constants
  // =============================================================================
  public static String 	MP_WRITE_DIRECTORY = "";
  public static FitMethod 	MP_FIT_METHOD = FitMethod.HERMITE_QUINTIC;
  public static int		MP_SAMPLES = Config.SAMPLES_HIGH;
  public static double 	MP_DELTA_TIME = 0;
  public static double 	MP_MAX_VEL = 0;
  public static double 	MP_MAX_ACC = 0;
  public static double 	MP_MAX_JERK = 0;
  
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
  
  // =============================================================================
  // Motion Magic Constants
  // =============================================================================
  public static double 	MOTION_MAGIC_TURN_DEGREE_TOLERANCE = 3;
  public static int		MOTION_MAGIC_PID_SLOT;
  public static int		MOTION_MAGIC_LOOP_SLOT;
  public static int		MOTION_MAGIC_P;
  public static int		MOTION_MAGIC_I;
  public static int		MOTION_MAGIC_D;
  public static int		MOTION_MAGIC_F;
  public static int		MOTION_MAGIC_V;
  public static int		MOTION_MAGIC_A;
  // =============================================================================
  // Sensors
  // =============================================================================
  public static final int INTAKE_LIMIT_SWITCH = 0;
  
  @Override
  public String getFileLocation() {
    return "~/constants.txt";
  }
}
