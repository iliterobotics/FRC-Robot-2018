package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.EDriveTrain;
import org.ilite.frc.robot.types.ELogitech310;
import org.ilite.frc.robot.types.ENavX;
import org.ilite.frc.robot.types.EPowerDistPanel;
import org.ilite.frc.robot.types.ETalonSRX;

import com.flybotix.hfr.codex.Codex;

public class Data {
  //TODO - make driver[] an array, where driver[0] = current and driver[1] = last
  public final Codex<Double, ELogitech310> driverinput = new Codex<>(ELogitech310.class);
  public final Codex<Double, ELogitech310> operator = Codex.of.thisEnum(ELogitech310.class);
  public final Codex<Double, EPowerDistPanel> pdp = Codex.of.thisEnum(EPowerDistPanel.class);
  public final Codex<Double, ENavX> navx = Codex.of.thisEnum(ENavX.class);
  public final Codex<Double, EDriveTrain> drivetrain = new Codex<>(EDriveTrain.class);
  public final List<Codex<Double, ETalonSRX>> talons = new ArrayList<>();
  
  private final Codex<?,?>[] all = new Codex[]{
    driverinput,operator,pdp,navx,drivetrain
  };
  
  Data() {
    driverinput.meta().setCompositeKey(SystemSettings.JOYSTICK_PORT_DRIVER);
    driverinput.meta().setCompositeKey(SystemSettings.JOYSTICK_PORT_OPERATOR);
  }

  public void resetAll(double pTimestamp) {
    for(Codex<?,?> c : all) {
      c.reset();
      c.meta().setTimestamp(pTimestamp);
    }
    
  }

}