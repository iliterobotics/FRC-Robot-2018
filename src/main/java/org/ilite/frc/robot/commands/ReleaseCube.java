package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Carriage.CarriageState;

public class ReleaseCube implements ICommand {

  public static final CarriageState kDEFAULT_STATE = CarriageState.RESET;
  
  private double mStartTime, mDuration;
  private CarriageState mCarriageState;
  
  private Carriage mCarriage;
  
  public ReleaseCube(Carriage pCarriage, CarriageState pCarriageState, double pDuration) {
    this.mCarriageState = pCarriageState;
    this.mCarriage = pCarriage;
    this.mDuration = pDuration;
  }
  
  @Override
  public void initialize(double pNow) {
    if(mCarriageState != CarriageState.RESET || mCarriageState != CarriageState.KICKING) mCarriageState = kDEFAULT_STATE;
    mStartTime = pNow;
  }

  @Override
  public boolean update(double pNow) {
    mCarriage.setDesiredState(mCarriageState);
    if(pNow - mStartTime >= mDuration) {
      mCarriage.setDesiredState(CarriageState.RESET);
      return true;
    }
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
