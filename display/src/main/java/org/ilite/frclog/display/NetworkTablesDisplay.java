package org.ilite.frclog.display;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.ilite.frc.robot.types.ELogitech310;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;

public class NetworkTablesDisplay {
  private static final double UPDATE_RATE_HZ = 0.5;
  private static final long UPDATE_PERIOD_MS = (long)(1000d/UPDATE_RATE_HZ);
  private  NetworkTable nt;
  
  private NetworkTablesDisplay() {
    String name = ELogitech310.class.getSimpleName().toUpperCase();
    NetworkTable.setClientMode();
    NetworkTable.setIPAddress("roboRIO-1885-FRC.local");
    nt = NetworkTable.getTable(name);
    nt.addConnectionListener(new ConnectionListener(), true);
    
    new Timer().scheduleAtFixedRate(new TimerTask() {
      public void run() {
        if(isConnected) {
          StringBuilder sb = new StringBuilder();
          for(ELogitech310 e : ELogitech310.values()) {
            sb.append(e.name()).append('=').append(df.format(nt.getNumber(e.name(), 0))).append("  ");
          }
          System.out.println(sb);
        }
      }
    }, UPDATE_PERIOD_MS, UPDATE_PERIOD_MS);
  }

  public static void main(String[] pArgs) {
    new NetworkTablesDisplay();
  }
  
  private final static DecimalFormat df = new DecimalFormat("0.0");
  
  private boolean isConnected = false;
  
  private class ConnectionListener implements IRemoteConnectionListener {
    @Override
    public void connected(IRemote remote) {
      System.out.println("Network tables Connected");
      isConnected = true;
    }

    @Override
    public void disconnected(IRemote remote) {
      System.out.println("Network tables Disconnected");
      isConnected = false;
    }
  }
  
}
