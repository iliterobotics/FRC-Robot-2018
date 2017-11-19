package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.codex.Codex;

public class Data {
  final Codex<Double, ELogitech310> driver = Codex.of.thisEnum(ELogitech310.class);
  final Codex<Double, ELogitech310> operator = Codex.of.thisEnum(ELogitech310.class);
  public final Codex<Double, EPowerDistPanel> pdp = Codex.of.thisEnum(EPowerDistPanel.class);
  public final Codex<Double, ENavX> navx = Codex.of.thisEnum(ENavX.class);
  public final List<Codex<Double, ETalonSRX>> talons = new ArrayList<>();
  
  Data() {
  }
}