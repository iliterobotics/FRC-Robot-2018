package org.ilite.frclog.data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flybotix.hfr.cache.CodexElementHistory;
import com.flybotix.hfr.codex.CodexOf;
import com.flybotix.hfr.codex.CodexReceiver;
import com.flybotix.hfr.util.lang.EnumUtils;

/**
 * This keeps track of history for each element of each codex from the robot.
 * 
 * The data from an individual codex is split into 'columns' - i.e. there is
 * an ArrayList for each element of a codex.
 */
public class RobotDataElementCache {
  
  // 200hz * 3 minutes of data?
  // That equates to 200hz * 180s * 8bytes * ~200 elements = ~55MB of memory
  private int mMaxHistoryPoints = 200 * 180;

  private final Map<Integer, CodexReceiver<?,?>> mReceivers = new HashMap<>();
  private final Map<Integer, CodexElementHistory<?, ?>> mCache = new HashMap<>();
  
  public <V, E extends Enum<E> & CodexOf<V>> void registerEnum(Class<E> pEnum) {
    for(E e : EnumSet.allOf(pEnum)) {
      CodexElementHistory<V,E> history = new CodexElementHistory<>(e, mMaxHistoryPoints);
      mCache.put(hashOf(e), history);
    }
  }
  
  public <V, E extends Enum<E> & CodexOf<V>> void clearHistoryFor(Class<E> pEnum) {
    for(E e : EnumSet.allOf(pEnum)) {
      mCache.get(hashOf(e)).clear();
    }
  }
  
  public <V, E extends Enum<E> & CodexOf<V>> Map<E, CodexElementHistory<V,E>> getHistoryOf(Class<E> pEnum) {
    Map<E, CodexElementHistory<V,E>> result = new HashMap<>();
    for(E e : EnumSet.allOf(pEnum)) {
      result.put(e, getHistoryOf(e));
    }
    return result;
  }
  
  public <V, E extends Enum<E> & CodexOf<V>> CodexElementHistory<V,E> getHistoryOf(E pElement) {
    if(!mCache.containsKey(hashOf(pElement))) return null;
    return (CodexElementHistory<V,E>)mCache.get(hashOf(pElement));
  }
  
  private static <V, E extends Enum<E> & CodexOf<V>> int hashOf(E pEnum) {
    return EnumUtils.hashOf(pEnum) + 31*pEnum.name().hashCode();
  }
  
  /*
   * Singleton junk
   */
  private static RobotDataElementCache inst;
  public static RobotDataElementCache inst() {
    if(inst == null) {
      inst = new RobotDataElementCache();
    }
    return inst;
  }
}
