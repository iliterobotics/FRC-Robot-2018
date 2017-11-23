package org.ilite.frclog.display;

import org.ilite.frc.robot.types.ESupportedTypes;

/**
 * This enum is an initial way to do this.  All of the custom data should be put into 
 * a JSON config file so that we can load these at runtime.  This allows us to add 
 * more data constants to the stream without needing to re-compile.  This enum
 * is a way to represent the default ports/modules on the robot.
 * 
 * "robotdata" : [
 *     {display : "Left Encoder Position", comms : "leftpos", type : "LONG" },
 *     {display : "Right Encoder Position", comms : "rightpos", type : "LONG" },
 *     {display : "Left Encoder Velocity", comms : "leftvel", type : "DOUBLE" },
 *     {display : "Right Encoder Velocity", comms : "rightvel", type : "DOUBLE" },
 *     {display : "Right Encoder Velocity", comms : "rightvel", type : "DOUBLE" },
 *     {display : "Drive Train Current (A)", comms : "drive_train_current", type : "DOUBLE" },
 * ]
 * 
 * The "PCM" enum is a special case.  It is an integer that represents the state of 8 flags at once,
 * using OR logic on powers of 2.
 *
 */
public enum ERobotData {
  ROBOT_LOG("robot_log", ESupportedTypes.STRING),
  PCM("pcm", ESupportedTypes.INTEGER),
  GYRO_DEG("gyro_deg", ESupportedTypes.DOUBLE),
  
  LEFT_ENCODER_POS("leftpos", ESupportedTypes.LONG),
  RIGHT_ENCODER_POS("rightpos", ESupportedTypes.LONG),
  LEFT_ENCODER_VELOCITY("leftvel", ESupportedTypes.DOUBLE),
  RIGHT_ENCODER_VELOCITY("rightvel", ESupportedTypes.DOUBLE),
  DRIVETRAIN_CURRENT("drive_train_current", ESupportedTypes.DOUBLE),
  INTAKE_CURRENT("intake_current", ESupportedTypes.DOUBLE),
  CLIMBER_CURRENT("climber_current", ESupportedTypes.DOUBLE);
  
  
  
  public final String comms;
  public final ESupportedTypes type;
  
  public Class<?> clazz() {
    return type.type;
  }
  
  private ERobotData(String pCommsName, ESupportedTypes pType) {
    comms = pCommsName;
    type = pType;
  }
}
