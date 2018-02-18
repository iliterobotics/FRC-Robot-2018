package org.ilite.frc.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.util.lang.EnumUtils;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
  
  public static String toCsvRow(List<String> l) {
    return l.stream()
            .map(value -> value.toString())
            .map(value -> value.replaceAll("\"", "\"\""))
            .map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
            .collect(Collectors.joining(","));
  }
  
  /**
   * Provides a way to write every value of a codex to the smart dashboard.
   * @param pCodex
   */
  public static <V extends Number, E extends Enum<E> & CodexOf<V>> void writeCodexToSmartDashboard(Codex<V, E> pCodex) {
    List<E> enums = EnumUtils.getSortedEnums(pCodex.meta().getEnum());
    for(E e : enums) {
      Double value = (Double) pCodex.get(e);
      if(e != null) SmartDashboard.putNumber(e.toString(), (value == null) ? 0 : value);
    }
  }
}
