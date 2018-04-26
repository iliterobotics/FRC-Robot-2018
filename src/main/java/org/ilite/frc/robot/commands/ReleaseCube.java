package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Carriage.CarriageState;

public class ReleaseCube implements ICommand {

  public static final CarriageState kDEFAULT_STATE = CarriageState.RESET;
  
  private double mStartTime, mDuration, mRelaseTime;
  private CarriageState mCarriageState;
  
  private Carriage mCarriage;
  
  public ReleaseCube(Carriage pCarriage, CarriageState pCarriageState, double pDuration) {
    this.mCarriageState = pCarriageState;
    this.mCarriage = pCarriage;
    this.mDuration = pDuration;
  }
  
  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
    mRelaseTime = pNow + mDuration;
    mCarriage.setFinishedKicking(false);
  }

  @Override
  public boolean update(double pNow) {
    if(mCarriage.isFinishedKicking()) {
      mCarriage.setDesiredState(CarriageState.RESET);
    } else {
      mCarriage.setDesiredState(CarriageState.KICKING);
    }
//    if(pNow >= mRelaseTime) {
//      mCarriage.setDesiredState(CarriageState.KICKING);
//      return true;
//    }
    return mCarriage.isFinishedKicking();
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
