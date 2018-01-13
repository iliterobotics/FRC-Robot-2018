package org.ilite.frc.robot;

import java.text.DecimalFormat;

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
}
