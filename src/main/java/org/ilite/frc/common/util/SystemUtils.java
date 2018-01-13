package org.ilite.frc.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SystemUtils {

  public static List<InetAddress> getSystemInetAddresses() {
    List<InetAddress> addrs = new ArrayList<>();

    try {
      Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      while(ifaces.hasMoreElements()) {
        NetworkInterface ni = ifaces.nextElement();
        Enumeration<InetAddress> eaddrs = ni.getInetAddresses();
        while(eaddrs.hasMoreElements()) {
          InetAddress ina = eaddrs.nextElement();
          addrs.add(ina);
        }
      }
    } catch (SocketException se) {
      se.printStackTrace();
    }
    
    return addrs;
  }
}
