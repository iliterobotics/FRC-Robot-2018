package org.ilite.frc.robot.math;
public class AverageDouble extends RunningAverage{
  private final double[] mNumbers;
  
  public AverageDouble(int pSize) {
    mNumbers = new double[pSize];
  }
  
  public void add(double pNumber) {
    mNumbers[mCurrentIndex] = pNumber;
    testRollover(mNumbers.length);
  }
  
  public double getAverage() {
    double result = 0;
    double length = (mFullyPopulated ? mNumbers.length : mCurrentIndex);
    for(int i = 0; i < length; i++) {
      result += mNumbers[i];
    }
    return result/length;
  }
}