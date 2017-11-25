package org.ilite.frclog.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.management.openmbean.OpenMBeanOperationInfoSupport;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ESupportedTypes;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.cache.CodexElementInstance;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.codex.CodexReceiver;
import com.flybotix.hfr.io.MessageProtocols;
import com.flybotix.hfr.io.receiver.IReceiveProtocol;
import com.flybotix.hfr.util.lang.EnumUtils;
import com.flybotix.hfr.util.lang.IUpdate;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 */
public class RobotDataStream {
  private NetworkTable mTable;
  protected static final String NO_DATA = "NO_DATA";
  
  private final Map<Integer, CodexReceiver<?,?>> mReceivers = new HashMap<>();
  
  public void sendDataToRobot(String pData, ESupportedTypes pType, String pValue) {
    if(pValue == null) { 
      return;
    }
    switch(pType) {
    case BOOLEAN:
      mTable.putBoolean(pData, Boolean.parseBoolean(pValue));
      break;
    case DOUBLE:
    case INTEGER:
    case LONG:
      mTable.putNumber(pData, Double.parseDouble(pValue));
      break;
    case STRING:
    case UNSUPPORTED:
    default:
      mTable.putString(pData, pValue);
      System.out.println("Putting string " + pData + " " + pValue);
      break;
    }
  }
  
  
  private RobotDataStream() {
    mTable = NetworkTable.getTable("Generic Config Data");
    IReceiveProtocol receiver = MessageProtocols.createReceiver(SystemSettings.CODEX_DATA_PROTOCOL, SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, "");
    registerEnum(EPowerDistPanel.class, receiver);
    registerEnum(ELogitech310.class, receiver);
    registerEnum(ETalonSRX.class, receiver);
    registerEnum(ENavX.class, receiver);
  }
  
  private Map<Integer, BufferedWriter> mFilePaths = new HashMap<>();
  private Map<Integer, List<Codex<?,?>>> mCSVCache = new HashMap<>();
  Timer t = new Timer("File writer");
  
  private <V, E extends Enum<E> & CodexOf<V>> void registerEnum( Class<E> pEnum, IReceiveProtocol pReceiver) {
    final int hash = EnumUtils.hashOf(pEnum);
    CodexReceiver<V, E> r = new CodexReceiver<>(pEnum, pReceiver);
    mReceivers.put(hash, r);
    RobotDataElementCache.inst().registerEnum(pEnum);
    t.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        List<Codex<?,?>> cache = new ArrayList<>();
        synchronized(mCSVCache) {
          cache.addAll(mCSVCache.get(hash));
          mCSVCache.get(hash).clear();
        }
        if(cache.isEmpty()) {
        } else {
          System.out.println("file write");
          BufferedWriter writer = mFilePaths.get(hash);
          cache.stream().map(codex->codex.toCSV()).forEach(csv -> {
            try {
              writer.write(csv);
              writer.newLine();
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
          try {
            writer.flush();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }, 1000, 1000);
    
    mCSVCache.put(hash, new ArrayList<>());
    r.addListener(codex -> {
      synchronized(mCSVCache) {
        mCSVCache.get(hash).add(codex);
      }
    });
    
    try {
      File f = new File(pEnum.getSimpleName() + ".csv");
      String header = null;
      if(!f.exists()) {
        f.createNewFile();
        header = new Codex<>(pEnum).getCSVHeader();
      }
      BufferedWriter writer = Files.newBufferedWriter(f.toPath(), StandardOpenOption.APPEND);
      if(header != null) {
        writer.write(header);
        writer.newLine();
        writer.flush();
      }
      mFilePaths.put(hash,writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Adds a basic listener to a value
   * @return current value of the data
   */
  public <V, E extends Enum<E> & CodexOf<V>> CodexElementInstance<V,E> addListenerToData(E pData, IUpdate<CodexElementInstance<V,E>> pListener) {
    int hash = EnumUtils.hashOf(pData);
    @SuppressWarnings("unchecked")
    CodexReceiver<V, E> receiver = (CodexReceiver<V,E>)mReceivers.get(hash);
    return receiver.addElementListener(pData, pListener);
  }
  
  /*
   * Singleton junk
   */
  private static RobotDataStream inst;
  public static RobotDataStream inst() {
    if(inst == null) {
      inst = new RobotDataStream();
    }
    return inst;
  }
}
