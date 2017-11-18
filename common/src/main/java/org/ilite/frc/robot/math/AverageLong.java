package org.ilite.frc.robot.math;
public class AverageLong extends RunningAverage {
  private final long[] mNumbers;
  
  public AverageLong(int pSize) {
    mNumbers = new long[pSize];
  }
  
  public void add(long pNumber) {
    mNumbers[mCurrentIndex] = pNumber;
    testRollover(mNumbers.length);
  }
  
  public long getAverage() {
    long result = 0;
    long length = (mFullyPopulated ? mNumbers.length : mCurrentIndex);
    for(int i = 0; i < length; i++) {
      result += mNumbers[i];
    }
    return result/length;
  }
}