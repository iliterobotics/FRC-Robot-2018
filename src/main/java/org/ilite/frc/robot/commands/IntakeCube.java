package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Intake;

public class IntakeCube implements ICommand {

  private Intake mIntake;
  private Carriage mCarriage;
  private double mIntakePower;
  private double mStartTime, mTimeout;
  private boolean mRetractWhenHasCube;
  
  public IntakeCube(Intake pIntake, Carriage pCarriage, double pIntakePower, double pTimeout, boolean pRetractWhenHasCube) {
    mIntake = pIntake;
    mCarriage = pCarriage;
    mIntakePower = pIntakePower;
    mTimeout = pTimeout;
    mRetractWhenHasCube = pRetractWhenHasCube;
  }
  
  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
    mIntake.setIntakeRetracted(false);
  }

  @Override
  public boolean update(double pNow) {
    mIntake.intakeIn(mIntakePower);
    if(mCarriage.getBeamBreak() && mRetractWhenHasCube) mIntake.setIntakeRetracted(true);
    if(pNow - mStartTime >= mTimeout) return true;
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
