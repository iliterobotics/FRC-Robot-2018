package org.ilite.frc.robot;

import java.text.DecimalFormat;

import org.ilite.frc.common.config.SystemSettings;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.team254.lib.util.MovingAverage;

import edu.wpi.first.wpilibj.Timer;

public class Utils {

  
  static final DecimalFormat df = new DecimalFormat("0.00");
  static <E extends Enum<E> & CodexOf<Double>> void print(Codex<Double,E> pCodex) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < pCodex.length(); i++) {
      sb.append(i).append('=').append(df.format(pCodex.get(i))).append('\t');
    }
    System.out.println(sb);
  }
  
  
  private static final MovingAverage ma = new MovingAverage(50);
  private static int count = 0;
  static void time(Runnable r, String label) {
    double start = Timer.getFPGATimestamp();
    r.run();
    double end = Timer.getFPGATimestamp();
    ma.addNumber((end-start)*1e6);
    count++;
    if(count > 100) {
      System.out.println("Over 100 cycles, " + label + " took an average of " + ma.getAverage() + " to execute.");
      count = 0;
    }
  }
  
  public static double absoluteAverage(double a, double b) {
	  return (Math.abs(a) + Math.abs(b)) / 2;
  }
  
  public static double ticksToRotations(double ticks) {
    return ticks / SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
  }
  
  public static double ticksToInches(double ticks) {
    return ticksToRotations(ticks) * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE;
  }
  
  public static double ticksToRPM(double ticks) {
    return ticksToRotations(ticks) * 60000;
  }
  
  public static double ticksToFPS(double ticks) {
    return ticksToRotations(ticks) * SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE * (1.0 / 12.0) * 10.0;
  }
  
  public static double fpsToTicks(double fps) {
    return fps * 12 * (1 / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE) * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN * (1 / 1000) * (1 / 10);
  }
  
  public static double rotationsToTicks(double rotations) {
    return rotations * SystemSettings.DRIVETRAIN_ENC_TICKS_PER_TURN;
  }
  
  public static double inchesToTicks(double inches) {
    return rotationsToTicks(inches / SystemSettings.DRIVETRAIN_WHEEL_CIRCUMFERENCE);
  }
  
  public static double clamp(double pValue, double pMaxMagnitude) {
    double val = Math.abs(pValue);
    val = Math.min(val, pMaxMagnitude);
    return val * (pValue > 0d ? 1d : -1d);
  }
}
