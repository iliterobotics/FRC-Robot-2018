package org.ilite.frc.robot.math;

import java.util.ArrayList;
import java.util.List;

public class RunningAverage {
  public interface RollerListener {
    public void rollover();
  }
  
  protected int mCurrentIndex = 0;
  protected boolean mFullyPopulated = false;
  private List<RollerListener> mListeners = new ArrayList<>();
  
  public void addRolloverListener(RollerListener pListener) {
    mListeners.add(pListener);
  }
  
  protected void testRollover(int pSize) {
    mCurrentIndex++;
    if(mCurrentIndex >= pSize) {
      mFullyPopulated = true;
      mCurrentIndex = 0;
      for(RollerListener r : mListeners) {
        r.rollover();
      }
    }
  }
}
