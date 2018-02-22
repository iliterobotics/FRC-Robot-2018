package org.ilite.frc.common.types;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.vision.Processing;
import org.ilite.frc.robot.vision.Target;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum ECubeTarget implements CodexOf<Double>{
  
  CENTER_X,
  CENTER_Y,
  DELTA_X,
  DELTA_Y,
  DELTA_ANGLE,
  DELTA_DISTANCE;
  
  public static void map(Codex<Double, ECubeTarget> pCodex, Processing pProcessing, int targetIndex) {
    pCodex.reset();
    if(pProcessing.getTargets().size() <= 0) return;
    try {
      Target cubeTarget = pProcessing.getTargets().get(targetIndex);
      pCodex.set(ECubeTarget.CENTER_X, cubeTarget.centerX);
      pCodex.set(ECubeTarget.CENTER_Y, cubeTarget.centerY);
      pCodex.set(ECubeTarget.DELTA_X, cubeTarget.deltaX);
      pCodex.set(ECubeTarget.DELTA_Y, cubeTarget.deltaY);
      pCodex.set(ECubeTarget.DELTA_ANGLE, cubeTarget.deltaAngle);
      pCodex.set(ECubeTarget.DELTA_DISTANCE, cubeTarget.deltaDistance);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public static void map(Codex<Double, ECubeTarget> pCodex, Processing pProcessing) {
    map(pCodex, pProcessing, 0);
  }
  
}
