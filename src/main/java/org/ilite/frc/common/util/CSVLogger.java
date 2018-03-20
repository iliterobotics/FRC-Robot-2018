package org.ilite.frc.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECubeTarget;
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

  private BufferedWriter writer;
  private Map<String, List<String>> codexKeys = new HashMap<>(); // Contains a mapping of codex names to codex keys. Used to retrieve codex data dumped by the robot from NetworkTables
  private boolean mHasWrittenHeaders = false;
  
  public CSVLogger() {
    putInMatrix("operator", ELogitech310.class);
    putInMatrix("driver", ELogitech310.class);
    putInMatrix(EPigeon.class);
    putInMatrix(EPowerDistPanel.class);
    putInMatrix(EDriveTrain.class);
    putInMatrix(EElevator.class);
  }
  
  public <E extends Enum<E>> List<String> getKeys(Class<E> pEnum) {
    List<String> keys = new ArrayList<>();
    EnumUtils.getEnums(pEnum).forEach(e -> keys.add(e.toString()));
    return keys;
  }
  
  public <E extends Enum<E>> void putInMatrix(String pLogName, Class<E> pEnum) {
    codexKeys.put(pLogName, getKeys(pEnum));
  }
  
  public <E extends Enum<E>> void putInMatrix(Class<E> pEnum) {
    codexKeys.put(pEnum.getSimpleName(), getKeys(pEnum));
  }
  
  public <E extends Enum<E>> void putAllInMatrix(Class<E> ... pEnumerations) {
    for(Class<E> enumeration : pEnumerations) putInMatrix(enumeration);
  }
  
  private void writeHeaderToCsv(Map<String, List<String>> dataMap) {
    
    if(mHasWrittenHeaders) return;
    mHasWrittenHeaders = true;

    try {
      for(String key : dataMap.keySet()) {
        File file = new File(String.format("./logs/%s-log.csv", key));
        handleCreation(file);
        
        dataMap.get(key).add("TIME");
        dataMap.get(key).add("TIME RECEIVED");
        
        writer = new BufferedWriter(new FileWriter(file, true));
        writer.append(SystemUtils.toCsvRow(dataMap.get(key)) + "\n");
        writer.flush();
      }
      writer.close();
    } catch (Exception e) {
        System.err.println("Error writing log headers.");
    }
    
  }
  
  private void writeMapEntry(Entry<String, List<String>> entry) throws IOException {
    File file = new File(String.format("./logs/%s-log.csv", entry.getKey()));
    
    handleCreation(file);
    
    writer = new BufferedWriter(new FileWriter(file, true));
    
    List<String> rowList = entry.getValue().stream()
            .map(networkTablesKey -> SystemSettings.SMART_DASHBOARD.getEntry(entry.getKey() + "-" + networkTablesKey).getNumber(-1).toString())
            .collect(Collectors.toList());
    rowList.add(SystemSettings.SMART_DASHBOARD.getEntry(entry.getKey() + "-" + SystemSettings.LOGGING_TIMESTAMP_KEY).getNumber(-1).toString());
    rowList.add(Long.toString(System.currentTimeMillis() / 1000));
    
    writer.append(SystemUtils.toCsvRow(rowList) + "\n");
    writer.flush();
  }
  
  public void writeRowsToCsv() {
    Logger.setLevel(ELevel.DEBUG);
    codexKeys.entrySet().forEach(entry -> {
      try { 
        System.out.printf("Writing log: %s\n", entry.getKey());
        writeMapEntry(entry);
      } catch (Exception e) {
        System.err.printf("Error writing log file: %s\n", entry.getKey());
      }   
    });
  }
  
  private void handleCreation(File file) throws IOException {
    if(file.getParentFile().exists()) file.getParentFile().mkdir();
    if(!file.exists()) file.createNewFile();
  }
  
  @Override
  public void run() {
    writeHeaderToCsv(codexKeys);
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
