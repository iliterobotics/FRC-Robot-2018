package org.ilite.frc.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EElevator;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.common.types.EPigeon;
import org.ilite.frc.common.types.EPowerDistPanel;

import com.flybotix.hfr.util.lang.EnumUtils;
import com.flybotix.hfr.util.log.ELevel;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.networktables.NetworkTableInstance;

public class CSVLogger extends Thread{

  private static final String LOG_PATH_FORMAT = "./logs/%s-log.csv";
  
  private Map<String, Writer> mCodexWriters = new HashMap<>();
  private Map<String, List<String>> mCodexKeys = new HashMap<>(); // Contains a mapping of codex names to codex keys. Used to retrieve codex data dumped by the robot from NetworkTables
  
  public CSVLogger() {
    putInMatrix("operator", ELogitech310.class);
    putInMatrix("driver", ELogitech310.class);
    putInMatrix(EPigeon.class);
    putInMatrix(EPowerDistPanel.class);
    putInMatrix(EDriveTrain.class);
    putInMatrix(EElevator.class);
  }
  
  public <E extends Enum<E>> void putInMatrix(String pLogName, Class<E> pEnum) {
    File log = new File(String.format(LOG_PATH_FORMAT, pLogName));
    mCodexKeys.put(pLogName, getKeys(pEnum));
    try {
      handleCreation(log);
      mCodexWriters.put(pLogName, new BufferedWriter(new FileWriter(log)));
    } catch (IOException e) {
      System.err.printf("Failed creating log file for enum %s with name %s.\n", pEnum.getSimpleName(), pLogName);
      e.printStackTrace();
    }
  }
  
  public <E extends Enum<E>> void putInMatrix(Class<E> pEnum) {
    putInMatrix(pEnum.getSimpleName(), pEnum);
  }
  
  public <E extends Enum<E>> void putAllInMatrix(Class<E> ... pEnumerations) {
    for(Class<E> enumeration : pEnumerations) putInMatrix(enumeration);
  }
  
  private void writeHeaderToCsv(Map<String, List<String>> pCodexKeys, Map<String, Writer> pCodexWriters) {
    
    try {
      for(String key : pCodexKeys.keySet()) {       
        pCodexKeys.get(key).add("TIME");
        pCodexKeys.get(key).add("TIME RECEIVED");
        
        pCodexWriters.get(key).append(SystemUtils.toCsvRow(pCodexKeys.get(key)) + "\n");
        pCodexWriters.get(key).flush();
      }
    } catch (Exception e) {
        System.err.println("Error writing log headers.");
        e.printStackTrace();
    }
    
  }
  
  private void writeMapEntry(Entry<String, List<String>> pEntry, Map<String, Writer> pCodexWriters) throws IOException {
    List<String> rowList = pEntry.getValue().stream()
            .map(networkTablesKey -> retrieveStringValue(pEntry.getKey(), networkTablesKey))
            .collect(Collectors.toList());
    rowList.set(rowList.size() - 1, Long.toString(System.currentTimeMillis() / 1000));
    
    double time = Double.parseDouble(retrieveStringValue(pEntry.getKey(), SystemSettings.LOGGING_TIMESTAMP_KEY));
    
    if(isAuto(time)) {
      Writer writer = pCodexWriters.get(pEntry.getKey());
      writer.append(SystemUtils.toCsvRow(rowList) + "\n");
      writer.flush();
    }
  }
  
  public void writeRowsToCsv() {
    System.out.println("Writing rows");
    Logger.setLevel(ELevel.DEBUG);
    mCodexKeys.entrySet().forEach(entry -> {
      try { 
//        System.out.printf("Writing log: %s\n", entry.getKey());
        writeMapEntry(entry, mCodexWriters);
      } catch (Exception e) {
        System.err.printf("Error writing log file: %s\n", entry.getKey());
      }   
    });
  }
  
  private void handleCreation(File pFile) throws IOException {
    if(pFile.getParentFile().exists()) pFile.getParentFile().mkdir();
    if(!pFile.exists()) pFile.createNewFile();
  }
  
  public <E extends Enum<E>> List<String> getKeys(Class<E> pEnum) {
    List<String> keys = new ArrayList<>();
    EnumUtils.getEnums(pEnum).forEach(e -> keys.add(e.toString()));
    return keys;
  }
  
  private String retrieveStringValue(String pLogName, String pKey) {
    return SystemSettings.LOGGING_TABLE.getEntry(pLogName + "-" + pKey).getNumber(-1).toString();
  }
  
  private boolean isAuto(double time) {
    return time <= 15.0;
  }
  
  private boolean isTeleop(double time) {
    return time > 15 && time <= 135;
  }
  
  @Override
  public void run() {
    writeHeaderToCsv(mCodexKeys, mCodexWriters);
    while(!Thread.interrupted()) {
      if(NetworkTableInstance.getDefault().isConnected()) writeRowsToCsv();
      try {
        sleep(10);
      } catch (InterruptedException e) {
        System.err.println("Thread sleep interrupted.");
      }
    }
  }
}
