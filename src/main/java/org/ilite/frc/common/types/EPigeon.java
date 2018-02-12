package org.ilite.frc.common.types;

import org.ilite.frc.common.sensors.Pigeon;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum EPigeon implements CodexOf<Double>{
  YAW,
  FUSED_HEADING,
  ROLL,
  PITCH,
  fACCEL_X,
  fACCEL_Y,
  JERK_X,
  JERK_Y,
  COLLISION;
  
  public static void map(Codex<Double, EPigeon> pCodex, Pigeon pPigeon, double pTimestampNow) {
    pPigeon.update(pTimestampNow);
    pCodex.set(YAW, pPigeon.getYaw());
    pCodex.set(FUSED_HEADING, pPigeon.getHeading());
    pCodex.set(ROLL, pPigeon.getRoll());
    pCodex.set(PITCH, pPigeon.getPitch());
    pCodex.set(fACCEL_X, pPigeon.getFilteredAccelX());
    pCodex.set(fACCEL_Y, pPigeon.getFilteredAccelY());
    pCodex.set(JERK_X, pPigeon.getJerkX());
    pCodex.set(JERK_Y, pPigeon.getJerkY());
    pCodex.set(COLLISION, pPigeon.detectCollision() ? 1d : 0d);
  }
}
