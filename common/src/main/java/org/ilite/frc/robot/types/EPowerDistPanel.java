package org.ilite.frc.robot.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public enum EPowerDistPanel implements CodexOf<Double> {
  CURRENT0,
  CURRENT1,
  CURRENT2,
  CURRENT3,
  CURRENT4,
  CURRENT5,
  CURRENT6,
  CURRENT7,
  CURRENT8,
  CURRENT9,
  CURRENT10,
  CURRENT11,
  CURRENT12,
  CURRENT13,
  CURRENT14,
  CURRENT15,
  VOLTAGE,
  TEMPERATURE;
  
  
  private EPowerDistPanel() {
  }
  
//  private final static Executor executor = Executors.newFixedThreadPool(18);
//  private interface IMap {
//    public EPowerDistPanel run();
//  }
  
  /**
   * WARNING -
   * This code takes about 5ms to execute.  If I try to split it to concurrently read the PDP,
   * it takes about 7.5ms.
   * 
   * Longer-term, we should cut out the PDP current readings we don't need.
   * @param pCodex PDP codex
   * @param pPDP PDP hardware
   */
  public static void map(Codex<Double, EPowerDistPanel> pCodex, PowerDistributionPanel pPDP) {
//    List<IMap> tasks = new ArrayList<>();
//    Arrays.stream(EPowerDistPanel.values())
//      .forEach(epdp -> {
//        switch(epdp) {
//        case VOLTAGE:
//          tasks.add(() -> {pCodex.set(epdp, pPDP.getVoltage()); return epdp;});
//          break;
//        case TEMPERATURE:
//          tasks.add(() -> {pCodex.set(epdp, pPDP.getTemperature()); return epdp;});
//          break;
//        default:
//          tasks.add(() -> {pCodex.set(epdp.ordinal(), pPDP.getCurrent(epdp.ordinal())); return epdp;});
//          break;
//        }
//      });
//    
//    List<CompletableFuture<EPowerDistPanel>> futures = tasks
//       .stream()
//       .map(t -> CompletableFuture.supplyAsync(() -> t.run(), executor))
//       .collect(Collectors.toList());
//   
//    futures.stream()
//           .map(CompletableFuture::join)
//           .collect(Collectors.toList());
    
    
    for(int i = 0; i < 16; i++) {
      pCodex.set(i, pPDP.getCurrent(i));
    }
    pCodex.set(VOLTAGE, pPDP.getVoltage());
    pCodex.set(TEMPERATURE, pPDP.getTemperature());
  }
  
}
