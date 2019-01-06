package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.*;

import com.flybotix.hfr.codex.Codex;

public class Data {
  public final Codex<Double, ELogitech310> driverinput = new Codex<>(ELogitech310.class);
  public final Codex<Double, ELogitech310> operator = new Codex<>(ELogitech310.class);
  public final Codex<Double, ELogitech310> tester = new Codex<>(ELogitech310.class);
  public final Codex<Double, EPowerDistPanel> pdp = Codex.of.thisEnum(EPowerDistPanel.class);
  public final Codex<Double, ENavX> navx = Codex.of.thisEnum(ENavX.class);
  public final Codex<Double, EPigeon> pigeon = Codex.of.thisEnum(EPigeon.class);
  public final Codex<Double, EDriveTrain> drivetrain = new Codex<>(EDriveTrain.class);
  public final Codex<Double, ECubeTarget> vision = new Codex<>(ECubeTarget.class);
  public final Codex<Double, EElevator> elevator = new Codex<>(EElevator.class);
  public final Codex<Double, ECarriage> carriage = new Codex<>(ECarriage.class);
  public final List<Codex<Double, ETalonSRX>> talons = new ArrayList<>();
  
  private final Codex<?,?>[] all = {
    driverinput,operator,pdp,pigeon,drivetrain,vision,carriage
  };
  
  Data() {
    driverinput.meta().setCompositeKey(SystemSettings.JOYSTICK_PORT_DRIVER);
    operator.meta().setCompositeKey(SystemSettings.JOYSTICK_PORT_OPERATOR);
  }

  public void resetAll(double pTimestamp) {
    for(Codex<?,?> c : all) {
      c.reset();
      c.meta().setTimestamp(pTimestamp);
    } 
  }
  
  

}