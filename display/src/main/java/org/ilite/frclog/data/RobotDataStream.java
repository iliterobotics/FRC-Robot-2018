package org.ilite.frclog.data;

import java.util.HashMap;
import java.util.Map;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ESupportedTypes;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.cache.CodexElementInstance;
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
  
  private <V, E extends Enum<E> & CodexOf<V>> void registerEnum( Class<E> pEnum, IReceiveProtocol pReceiver) {
    int hash = EnumUtils.hashOf(pEnum);
    CodexReceiver<V, E> r = new CodexReceiver<>(pEnum, pReceiver);
    mReceivers.put(hash, r);
    RobotDataElementCache.inst().registerEnum(pEnum);
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
