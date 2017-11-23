package org.ilite.frclog.data;

import java.util.HashMap;
import java.util.Map;

import com.flybotix.hfr.codex.CodexReceiver;

/**
 * This keeps track of history for each element of each codex from the robot.
 * 
 * The data from an individual codex is split into 'columns' - i.e. there is
 * an ArrayList for each element of a codex.
 */
public class RobotDataCache {

  private final Map<Integer, CodexReceiver<?,?>> mReceivers = new HashMap<>();
  
  /*
   * Singleton junk
   */
  private static RobotDataCache inst;
  public static RobotDataCache inst() {
    if(inst == null) {
      inst = new RobotDataCache();
    }
    return inst;
  }
}
