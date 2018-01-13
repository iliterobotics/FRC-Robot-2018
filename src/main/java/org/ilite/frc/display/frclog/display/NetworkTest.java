package org.ilite.frc.display.frclog.display;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkTest {

  public static void main(String[] pArgs) throws Exception{
    Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while(ifaces.hasMoreElements()) {
      NetworkInterface ni = ifaces.nextElement();
      Enumeration<InetAddress> addrs = ni.getInetAddresses();
      while(addrs.hasMoreElements()) {
        System.out.println(addrs.nextElement());
      }
    }
  }
}
