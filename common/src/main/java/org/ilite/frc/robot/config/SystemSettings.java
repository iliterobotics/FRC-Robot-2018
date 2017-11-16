package org.ilite.frc.robot.config;

import com.flybotix.hfr.io.Protocols.EProtocol;

public class SystemSettings {
  public static final EProtocol CODEX_DATA_PROTOCOL = EProtocol.UDP;
  public static final int DRIVER_STATION_CODEX_DATA_RECEIVER_PORT = 7777;
  public static final String DRIVER_STATION_CODEX_DATA_RECEIVER_HOST = "172.22.11.1";
  public static final int ROBOT_CODEX_DATA_SENDER_PORT = 7778;
}
