package org.ilite.frc.robot.types;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import com.kauailabs.navx.frc.AHRS;

public enum ENavX implements CodexOf<Double> {
  YAW,
  X_DISPLACEMENT,
  Y_DISPLACEMENT,
  Z_DISPLACEMENT,
  CURRENT_ANGLE,
  COMPASS_HEADING,
  FUSED_HEADING,
  X_ACCEL,
  Y_ACCEL,
  Z_ACCEL;
  
  public static void map(Codex<Double, ENavX> pCodex, AHRS pNavX, long pTimestampNanos) {
    pCodex.set(YAW, (double)pNavX.getYaw());
    pCodex.set(X_DISPLACEMENT, (double)pNavX.getDisplacementX());
    pCodex.set(Y_DISPLACEMENT, (double)pNavX.getDisplacementY());
    pCodex.set(Z_DISPLACEMENT, (double)pNavX.getDisplacementZ());
    pCodex.set(CURRENT_ANGLE, pNavX.getAngle());
    pCodex.set(COMPASS_HEADING, (double)pNavX.getCompassHeading());
    pCodex.set(FUSED_HEADING, (double)pNavX.getFusedHeading());
    pCodex.set(X_ACCEL, (double)pNavX.getWorldLinearAccelX());
    pCodex.set(Y_ACCEL, (double)pNavX.getWorldLinearAccelY());
    pCodex.set(Z_ACCEL, (double)pNavX.getWorldLinearAccelZ());
    pCodex.meta().setTimeNanos(pTimestampNanos);
  }
  
}
