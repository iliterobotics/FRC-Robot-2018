package org.ilite.frc.common.util;

import java.util.ArrayList;


public class FilteredAverage {
  private final ArrayList<Double> mNumbers = new ArrayList<Double>();
  private final double[] mGains;

  public FilteredAverage(double...pGains) {
    if(pGains == null || pGains.length == 0) {
      throw new IllegalArgumentException("Cannot create a filtered average without filter gains!");
    }
    mGains = pGains;
  }

  public void addNumber(double newNumber) {
      mNumbers.add(newNumber);
      if (!isUnderMaxSize()) {
          mNumbers.remove(0);
      }
  }

  public double getAverage() {
      double result = 0;
      
      int g = 0;
      for (int i = mNumbers.size()-1; i >= 0; i--) {
        result += mGains[g] * mNumbers.get(i);
        g++;
      }

      return result;
  }

  public int getSize() {
      return mNumbers.size();
  }

  public boolean isUnderMaxSize() {
      return getSize() <= mGains.length;
  }

  public void clear() {
      mNumbers.clear();
  }
}
