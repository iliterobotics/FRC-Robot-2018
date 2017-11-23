package org.ilite.frclog.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flybotix.hfr.codex.CodexOf;

public class RobotDataElementHistory <V, E extends Enum<E> & CodexOf<V>> {

  private final ArrayList<V> mList;
  private final Class<E> mEnum;
  private final int mMaxPoints;
  
  RobotDataElementHistory(Class<E> pEnum, int pNumPointsToKeep) {
    mList = new ArrayList<>(pNumPointsToKeep);
    mEnum = pEnum;
    mMaxPoints = pNumPointsToKeep;
  }
  
  public void add(V pDataPoint) {
    mList.add(pDataPoint);
    if(mList.size() > mMaxPoints) {
      mList.remove(0);
    }
  }
  
  public List<V> getData() {
    return new ArrayList<>(mList);
  }
  
  
}
