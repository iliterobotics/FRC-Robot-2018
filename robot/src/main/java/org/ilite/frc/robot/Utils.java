package org.ilite.frc.robot;

import java.text.DecimalFormat;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public class Utils {

  
  static final DecimalFormat df = new DecimalFormat("0.00");
  static <E extends Enum<E> & CodexOf<Double>> void print(Codex<Double,E> pCodex) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < pCodex.length(); i++) {
      sb.append(i).append('=').append(df.format(pCodex.get(i))).append('\t');
    }
    System.out.println(sb);
  }
}
