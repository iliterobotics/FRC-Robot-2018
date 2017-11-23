package org.ilite.frc.robot.config;

import com.flybotix.hfr.io.MessageProtocols.EProtocol;
import com.team254.lib.util.ConstantsBase;

public class SystemSettings extends ConstantsBase {
  
  public static double CONTROL_LOOP_PERIOD = 0.005; // seconds
  
  public static EProtocol CODEX_DATA_PROTOCOL = EProtocol.UDP;
  public static int DRIVER_STATION_CODEX_DATA_RECEIVER_PORT = 7777;
  public static String DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "172.22.11.1";
  public static int ROBOT_CODEX_DATA_SENDER_PORT = 7778;
  
  @Override
  public String getFileLocation() {
    return "~/constants.txt";
  }
}
