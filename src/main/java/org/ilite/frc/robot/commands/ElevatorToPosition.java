package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Elevator.ElevatorPosition;

public class ElevatorToPosition implements ICommand {

  private double mStartTime, mTimeout;
  private ElevatorPosition mPosition;
  
  private Elevator mElevator;
  
  public ElevatorToPosition(Elevator pElevator, ElevatorPosition pPosition, double pTimeout) {
    this.mElevator = pElevator;
    this.mPosition = pPosition;
    this.mTimeout = pTimeout;
  }
  
  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
  }

  @Override
  public boolean update(double pNow) {
    if(pNow - mStartTime >= pNow) {
      return true;
    }
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
