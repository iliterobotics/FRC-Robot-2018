package org.ilite.frc.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ilite.frc.common.config.SystemSettings;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.util.lang.EnumUtils;

import org.ilite.frc.common.types.EGameMode;

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
   * Convenience method, so we don't have to specify game mode.
   * @param pEnumeration
   * @param pCodex
   * @param pTime
   * @param <V>
   * @param <E>
   */
  public static <V extends Number, E extends Enum<E> & CodexOf<V>> void writeCodexToSmartDashboard(Class<E> pEnumeration, Codex<V, E> pCodex, double pTime) {
    writeCodexToSmartDashboard(pEnumeration.getSimpleName(), pCodex, EGameMode.AUTONOMOUS_PERIODIC, pTime);
  }

  /**
   * Provides a way to write every value of a codex to NetworkTables. Provided for convenience - automatically names codexes.
   * @param pCodex The codex you want to dump to NetworkTables.
   */
  public static <V extends Number, E extends Enum<E> & CodexOf<V>> void writeCodexToSmartDashboard(Class<E> pEnumeration, Codex<V, E> pCodex, EGameMode pGameMode, double pTime) {
    writeCodexToSmartDashboard(pEnumeration.getSimpleName(), pCodex, pGameMode, pTime);
  }
  
  /**
   * Provides a way to write every value of a codex to NetworkTables.
   * @param name Allows you to define a name for the codex so two of the same type can be written at once.
   * @param pCodex The codex you want to dump to NetworkTables.
   * @param pTime The current time.
   */
  public static <V extends Number, E extends Enum<E> & CodexOf<V>> void writeCodexToSmartDashboard(String name, Codex<V, E> pCodex, EGameMode pGameMode, double pTime) {
    List<E> enums = EnumUtils.getSortedEnums(pCodex.meta().getEnum());
    for(E e : enums) {
      Double value = (Double) pCodex.get(e);
      if(e != null) logNumber(name, e, value);
    }
    logTime(name, pTime);
    logGameMode(pGameMode);

  }
  
  public static <E extends Enum<E>> void logNumber(String pName, E pEnumeration, Number pNumber) {
    logNumber(pName, pEnumeration.toString(), pNumber);
  }
  
  public static <E extends Enum<E>> void logNumber(String pName, String key, Number pNumber) {
    SystemSettings.LOGGING_TABLE.putDouble(pName + "-" + key, (pNumber == null) ? 0 : pNumber.doubleValue());
  }

  public static void logGameMode(EGameMode pGameMode) {
    logNumber(SystemSettings.LOGGING_GLOBAL_KEY_PREFIX, SystemSettings.GAME_MODE_KEY, pGameMode.ordinal());
  }

  public static void logTime(String name, double pTime) {
     logNumber(name, SystemSettings.LOGGING_TIMESTAMP_KEY, pTime);
  }
  
}
