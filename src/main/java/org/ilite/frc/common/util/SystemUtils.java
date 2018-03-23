package org.ilite.frc.common.util;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.util.lang.EnumUtils;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public static List<String> parseCsvRow(String pCsvRow) {
    return Arrays.asList(pCsvRow.replaceAll(" ", "").split(","));
  }

  public static Map<String, List<String>> csvToMap(File pCsvFile) {
    HashMap<String, List<String>> mCsvMap = new HashMap<>();
    ArrayList<List<String>> mSplitLines = new ArrayList<>();
    try {
      Files.readAllLines(pCsvFile.toPath()).forEach(e -> mSplitLines.add(parseCsvRow(e)));
      List<String> mHeaderLine = mSplitLines.get(0);
      mSplitLines.remove(0); // Remove the header line from our data
      for(int headerIndex = 0; headerIndex < mSplitLines.size(); headerIndex++) {
        String header = mHeaderLine.get(headerIndex);
        final int index = headerIndex;
        mCsvMap.put(header, mSplitLines.stream().map(e -> e.get(index)).collect(Collectors.toList()));
      }
    } catch (IOException e) {
      System.err.println("Error parsing CSV file: " + pCsvFile.getAbsolutePath());
      e.printStackTrace();
    }
    return mCsvMap;
  }

  /**
   * Provides a way to write every value of a codex to the smart dashboard.
   * @param pCodex
   */
  public static <V extends Number, E extends Enum<E> & CodexOf<V>> void writeCodexToSmartDashboard(Codex<V, E> pCodex, double pTime) {
    List<E> enums = EnumUtils.getSortedEnums(pCodex.meta().getEnum());
    for(E e : enums) {
      Double value = (Double) pCodex.get(e);
      if(e != null) SmartDashboard.putNumber(e.toString(), (value == null) ? 0 : value);
    }
    SmartDashboard.putNumber("TIME", pTime);
  }
  
}
