package org.ilite.frclog.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.EDriveTrain;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ESupportedTypes;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.cache.CodexElementHistory;
import com.flybotix.hfr.cache.CodexElementInstance;
import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.codex.CodexReceiver;
import com.flybotix.hfr.io.MessageProtocols;
import com.flybotix.hfr.io.receiver.IReceiveProtocol;
import com.flybotix.hfr.util.lang.EnumUtils;
import com.flybotix.hfr.util.lang.IConverter;
import com.flybotix.hfr.util.lang.IUpdate;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 */
public class RobotDataStream {
  private NetworkTable mTable;
  protected static final String NO_DATA = "NO_DATA";
  private Map<Integer, BufferedWriter> mFilePaths = new HashMap<>();
  private Map<Integer, List<Codex<?,?>>> mCSVCache = new HashMap<>();
  private final Timer t = new Timer("File writer");
  private String mCurrentFolder = "";
  
  private static final DateTimeFormatter DATE_FORMAT = 
    DateTimeFormatter.ofPattern( "uuuuMMMdd-hhmma" )
      .withLocale( Locale.US )
      .withZone( ZoneId.systemDefault() );
  
  private final Map<Integer, CodexReceiver<?,?>> mReceivers = new HashMap<>();
  
  private final Map<Integer, String> mCodexClasses = new HashMap<>();
  
  public void sendDataToRobot(String pData, ESupportedTypes pType, String pValue) {
    if(pValue == null) { 
      return;
    }
    NetworkTableEntry entry = mTable.getEntry(pData);
    switch(pType) {
    case BOOLEAN:
      entry.setBoolean(Boolean.parseBoolean(pValue));
      break;
    case DOUBLE:
    case INTEGER:
    case LONG:
      entry.setNumber(Double.parseDouble(pValue));
      break;
    case STRING:
    case UNSUPPORTED:
    default:
      entry.setString(pValue);
      break;
    }
  }
  
  private RobotDataStream() {
    mTable = NetworkTableInstance.getDefault().getTable("Generic Config Data");
    createNewFolder();
    IReceiveProtocol receiver = MessageProtocols.createReceiver(SystemSettings.CODEX_DATA_PROTOCOL, SystemSettings.DRIVER_STATION_CODEX_DATA_RECEIVER_PORT, "");
    registerEnum(EPowerDistPanel.class, receiver);
    registerEnum(ELogitech310.class, receiver);
    registerEnum(ETalonSRX.class, receiver);
    registerEnum(ENavX.class, receiver);
    registerEnum(EDriveTrain.class, receiver);
  }
  
  /**
   * Loads a codex history from a CSV file.
   * @param pEnum the enumeration that backs the codex
   * @param pFile location of the CSV
   * @param pConverter A converter that converts from a string to a codex element
   */
  public <V, E extends Enum<E> & CodexOf<V>> void loadCodexHistoryFromFile(Class<E> pEnum, Path pFile, final IConverter<String, V> pConverter) {
    if(!mCodexClasses.containsKey(pEnum)) return;
    
    RobotDataElementCache.inst().clearHistoryFor(pEnum);
    RobotDataElementCache.inst().registerEnum(pEnum);
    
    try (Stream<String> stream = Files.lines(pFile)) {
      stream
        .map(line -> new Codex<V,E>(pEnum).fillFromCSV(line, pConverter))
        .forEach(codex -> {
          Map<E, CodexElementHistory<V,E>> map = RobotDataElementCache.inst().getHistoryOf(pEnum);
          for(E e : EnumSet.allOf(pEnum)) {
            map.get(e).add(codex.meta().timestamp(), codex.get(e));
          }
        });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Loads a codex of doubles from file
   * @param pEnum enumeration that backs the codex
   * @param pFile location of the csv file
   */
  public <E extends Enum<E> & CodexOf<Double>> void loadCodexHistoryFromFile(Class<E> pEnum, Path pFile) {
    loadCodexHistoryFromFile(pEnum, pFile, str -> str == null ? null : Double.parseDouble(str));
  }


  /**
   * Stops output to the current logs, creates new logs at a new location, and starts output to the new logs.
   */
  public void resetLogs() {
    System.out.println("Resetting logs.");
    createNewFolder();
    synchronized(mFilePaths) {
      mFilePaths.values().stream().forEach(writer -> {
        try {
          writer.close();
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      });
      addFileWriter(EPowerDistPanel.class);
      addFileWriter(ELogitech310.class);
      addFileWriter(ETalonSRX.class);
      addFileWriter(ENavX.class);
      addFileWriter(EDriveTrain.class);
    }
  }
  
  public List<Class<Enum<?>>> getRegisteredCodexes() {
    List<Class<Enum<?>>> result = new ArrayList<>();
    for(String clazzname : mCodexClasses.values()) {
      try {
        Class<Enum<?>> clazz = (Class<Enum<?>>) RobotDataStream.class.getClassLoader().loadClass(clazzname);
        result.add(clazz);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
  
  /**
   * Creates a new folder for the logs.
   */
  private final void createNewFolder() {
    mCurrentFolder = DATE_FORMAT.format(Instant.now());
    new File(mCurrentFolder).mkdirs();
  }
  
  public <V, E extends Enum<E> & CodexOf<V>> void registerEnum( Class<E> pEnum, IReceiveProtocol pReceiver) {
    final int hash = EnumUtils.hashOf(pEnum);
    mCodexClasses.put(hash, pEnum.getCanonicalName());
    CodexReceiver<V, E> r = new CodexReceiver<>(pEnum, pReceiver);
    mReceivers.put(hash, r);
    RobotDataElementCache.inst().registerEnum(pEnum);
    
    mCSVCache.put(hash, new ArrayList<>());
    r.addListener(codex -> {
      synchronized(mCSVCache) {
        mCSVCache.get(hash).add(codex);
      }
    });
    
    addFileWriter(pEnum);
    
    t.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        List<Codex<?,?>> cache = new ArrayList<>();
        synchronized(mCSVCache) {
          cache.addAll(mCSVCache.get(hash));
          mCSVCache.get(hash).clear();
        }
        if(cache.isEmpty()) {
        } else {
          // Synch so we can pause writing when a new log file location is chosen
          synchronized(mFilePaths) {
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
      }
    }, 1000, 1000);
  }
  
  private <V, E extends Enum<E> & CodexOf<V>> void addFileWriter(Class<E> pEnum) {
    final int hash = EnumUtils.hashOf(pEnum);
    try {
      File f = new File(mCurrentFolder, pEnum.getSimpleName() + ".csv");
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
