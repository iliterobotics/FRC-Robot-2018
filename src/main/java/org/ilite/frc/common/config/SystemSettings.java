package org.ilite.frc.common.config;

import java.util.concurrent.TimeUnit;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.robot.SimpleNetworkTable;
import org.ilite.frc.robot.auto.FieldAdapter;

import com.flybotix.hfr.io.MessageProtocols.EProtocol;
import com.team254.lib.util.ConstantsBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;


public class SystemSettings extends ConstantsBase {
  
  
public static double CONTROL_LOOP_PERIOD = 0.015; // seconds
  public static TimeUnit SYSTEM_TIME_UNIT = TimeUnit.SECONDS;
  
  // =============================================================================
  // Comms
  // =============================================================================
  public static SimpleNetworkTable AUTON_TABLE = new SimpleNetworkTable("AUTON_TABLE") {
    @Override
    public void initKeys() {
      getInstance().getEntry(ECross.class.getSimpleName()).setDefaultNumber(-1); 
      getInstance().getEntry(EStartingPosition.class.getSimpleName()).setDefaultNumber(-1);
      getInstance().getEntry(ECubeAction.class.getSimpleName()).setDefaultNumberArray(new Number[] {0}); 
      getInstance().getEntry("Chosen Autonomous").setDefaultString(""); 
      getInstance().getEntry("Delay").setDefaultDouble(-1); 
    }
  };
  public static SimpleNetworkTable DRIVER_CONTROL_TABLE = new SimpleNetworkTable("DRIVER_CONTROL_TABLE") {
	  @Override
	  public void initKeys() {
		  getInstance().getEntry("Driver Control Mode").setDefaultString("ARCADE");
	  }
  };
  public static SimpleNetworkTable SMART_DASHBOARD = new SimpleNetworkTable("SmartDashboard");
  public static double NETWORK_TABLE_UPDATE_RATE = 0.01;
  
  public static EProtocol CODEX_DATA_PROTOCOL = EProtocol.UDP;
  public static int     DRIVER_STATION_CODEX_DATA_RECEIVER_PORT = 7777;
  public static String  DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "10.18.85.10";
//  public static String  DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "172.22.11.1";
  public static String[]  DRIVER_STATION_CODEX_DATA_RECEIVER_HOSTS = new String[]{
      "10.18.85.10",
      "172.22.11.1",
  };
  
  public static int     ROBOT_CODEX_DATA_SENDER_PORT = 7778;
  
  public static int SOLENOID_INTAKE_A = 1;
  public static int SOLENOID_INTAKE_B= 2;
  public static int SOLENOID_ELEVATOR_SHIFTER= 0;
  
  // =============================================================================
  // Talon Addresses
  // =============================================================================
  public static  int kDRIVETRAIN_TALONID_LEFT_MASTER = 10;
  public static  int kDRIVETRAIN_TALONID_LEFT_FOLLOW1 = 11;
  public static  int kDRIVETRAIN_TALONID_LEFT_FOLLOW2 = 12;
  public static  int kDRIVETRAIN_TALONID_RIGHT_MASTER = 5;
  public static  int kDRIVETRAIN_TALONID_RIGHT_FOLLOW1 = 6;
  public static  int kDRIVETRAIN_TALONID_RIGHT_FOLLOW2 = 7;
  public static int ELEVATOR_TALONID_MASTER = 8;
  public static int ELEVATOR_TALONID_FOLLOWER = 9;
  public static int INTAKE_TALONID_RIGHT = 14;
  public static int INTAKE_TALONID_LEFT = 13;
	
	public static int TALON_CONFIG_TIMEOUT_MS = 50;
	public static int kCONTROLLER_ID = 0;
  // =============================================================================
  // CANifier Constants
  // =============================================================================
  public static int 	CANIFIER_DEVICE_ID = 4;

  // =============================================================================
  // Robot Dimensions - These include bumpers, all units in feet
  // =============================================================================
  public static double  ROBOT_LENGTH = 37.75 / 12.0;
  public static double  ROBOT_WIDTH = 34.0 / 12.0;
  public static double  ROBOT_CENTER_TO_SIDE = ROBOT_WIDTH / 2.0;
  public static double  ROBOT_CENTER_TO_FRONT = ROBOT_LENGTH / 2;
  public static double  ROBOT_CENTER_TO_BACK = ROBOT_LENGTH - ROBOT_CENTER_TO_FRONT;
  public static double  ROBOT_CENTER_TO_FRONT_CORNER = Math.hypot(ROBOT_CENTER_TO_FRONT, ROBOT_CENTER_TO_SIDE);
  public static double  ROBOT_CENTER_TO_BACK_CORNER = Math.hypot(ROBOT_CENTER_TO_BACK, ROBOT_CENTER_TO_SIDE);
  
  // =============================================================================
  // Drive Train Constants
  // =============================================================================
  public static double  DRIVETRAIN_WHEEL_DIAMETER = 5.875;
  public static double  DRIVETRAIN_WHEEL_DIAMETER_FEET = DRIVETRAIN_WHEEL_DIAMETER / 12.0;
  public static double  DRIVETRAIN_WHEEL_CIRCUMFERENCE = DRIVETRAIN_WHEEL_DIAMETER * Math.PI;
  public static double  DRIVETRAIN_DEFAULT_RAMP_RATE = 120.0; // in V/sec
  public static double  DRIVETRAIN_HIGH_GEAR_RAMP_RATE = 120.0; // in V/sec
  public static double  DRIVETRAIN_ENC_TICKS_PER_TURN = 1024;
  public static double	DRIVETRAIN_EFFECTIVE_WHEELBASE = 25.5;
  public static double 	DRIVETRAIN_TURN_CIRCUMFERENCE = DRIVETRAIN_EFFECTIVE_WHEELBASE * Math.PI;
  public static double	DRIVETRAIN_INCHES_PER_DEGREE = DRIVETRAIN_TURN_CIRCUMFERENCE / 360;
  public static double	DRIVETRAIN_WHEEL_TURNS_PER_DEGREE = DRIVETRAIN_INCHES_PER_DEGREE / DRIVETRAIN_WHEEL_DIAMETER;
  public static double	DRIVETRAIN_ANGLE_kP = 0.00;
  public static double	DRIVETRAIN_LEFT_VELOCITY_kP = 0;
  public static double	DRIVETRAIN_LEFT_VELOCITY_kI = 0;
  public static double	DRIVETRAIN_LEFT_VELOCITY_kD = 0;
  public static double	DRIVETRAIN_LEFT_kA = 0;
  public static double	DRIVETRAIN_LEFT_kV = 1.0 / 14.239176663548465;
  public static double  DRIVETRAIN_RIGHT_VELOCITY_kP = 0;
  public static double  DRIVETRAIN_RIGHT_VELOCITY_kI = 0;
  public static double  DRIVETRAIN_RIGHT_VELOCITY_kD = 0;
  public static double  DRIVETRAIN_RIGHT_kA = 0;
  public static double  DRIVETRAIN_RIGHT_kV = 1.0 / 13.848650721299247;
  
  // =============================================================================
  // Elevator Constants
  // =============================================================================  
  public static int     DIO_TALON_TACH = 8;
  public static int     DIO_ELEVATOR_BOTTOM_LIMIT_SWITCH = 6;
  public static double ELEVATOR_NORMAL_MAX_POWER = 0.8d;
  public static double ELEVATOR_CLIMBER_MAX_POWER = 0.8d;
  // =============================================================================
  // Carriage Constants
  // =============================================================================  
  public static final int DIO_CARRIAGE_BEAM_BREAK_ID = 7;
  public static final int CARRIAGE_KICKER_ID = 3;
  public static final int CARRIAGE_GRABBER_ID = 4;
  
  // =============================================================================
  // Controllers
  // =============================================================================  
  public static double PIGEON_COLLISION_THRESHOLD = 10;
  public static int PIGEON_DEVICE_ID = 3;
  public static int PCM_DEVICE_ID = 0;
  public static int VRM_DEVICE_ID = 1;
  public static int PDP_DEVICE_ID = 20;
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
  
  public static int ENCODER_MAX_TICKS = 0;
  // =============================================================================
  // Input Constants
  // =============================================================================
  public static double  SNAIL_MODE_THROTTLE_LIMITER = .5;
  public static double  SNAIL_MODE_ROTATE_LIMITER = .4;
  public static double  INPUT_DEADBAND_F310_JOYSTICK = 0.05;
  public static double  INPUT_DEADBAND_F310_TRIGGER = 0.5;
  public static int     JOYSTICK_PORT_DRIVER = 0;
  public static int     JOYSTICK_PORT_OPERATOR = 1;
  public static int     JOYSTICK_PORT_TESTER = 2;

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
  public static int		MOTION_MAGIC_PID_SLOT = 0;
  public static int		MOTION_MAGIC_LOOP_SLOT = 0;
  public static double	MOTION_MAGIC_P = 0.5;
  public static double	MOTION_MAGIC_I = 0.0;
  public static double	MOTION_MAGIC_D = 0.0;
  public static double	MOTION_MAGIC_F = 1023 / 951;
  public static int		MOTION_MAGIC_V = 951;
  public static int		MOTION_MAGIC_A = 951;
  
  // =============================================================================
  // Closed-Loop Position Constants
  // =============================================================================
  public static int    POSITION_TOLERANCE = 0;
  public static int    POSITION_PID_SLOT = 0;
  public static double POSITION_P = 0;
  public static double POSITION_I = 0;
  public static double POSITION_D = 0;
  public static double POSITION_F = 0;
  
  // =============================================================================
  // Autonomous Constants
  // =============================================================================
  public static int		AUTO_STRAIGHT_POS_TOLERANCE = 100;
  public static int		AUTO_TURN_POS_TOLERANCE = 100;
  public static double	AUTO_TURN_TIMEOUT = 5000;
  public static FieldAdapter FIELD_ADAPTER = new FieldAdapter();
  // =============================================================================
  // Pneumatics
  // =============================================================================
  public static int DIO_PRESSURE_SWITCH = 9;
  public static int RELAY_COMPRESSOR_PORT = 0;
  // =============================================================================
  // Sensors
  // =============================================================================

//  public static final int DIO_INTAKE_BEAM_BREAK = 7;

  // =============================================================================
  // Vision Constants
  // =============================================================================
  public static double VISION_CUBE_WIDTH_INCHES = 12; // Average of possible cube widths
  public static int		 VISION_TWO_CUBE_WIDTH = 50;
  public static double VISION_HORIZ_FOV_DEGREES = 60;
  public static double VISION_VERT_FOV_DEGREES = 60;
  public static int    VISION_CAMERA_WIDTH = 160;
  public static int    VISION_CAMERA_HEIGHT = 120;
  public static double VISION_DEGREES_PER_PIXEL_X = VISION_HORIZ_FOV_DEGREES / VISION_CAMERA_WIDTH;
  public static double VISION_DEGREES_PER_PIXEL_Y = VISION_VERT_FOV_DEGREES / VISION_CAMERA_HEIGHT;
  //We calculate this because the coordinate system usually starts from the left, meaning 0 degrees is the leftmost part of the camera's view. We want 0 to be the center of the view.
  public static double VISION_CAMERA_DEGREES_CENTER_X = (VISION_CAMERA_WIDTH / 2) * VISION_DEGREES_PER_PIXEL_X; 
  
  public static double RES_SCALAR = 1.0;
  public static final double ALLOWABLE_ERROR = 0.5;
  public static NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
  
  @Override
  public String getFileLocation() {
    return "~/constants.txt";
  }
}
