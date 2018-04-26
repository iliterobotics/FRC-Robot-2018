package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Elevator.ElevatorControlMode;
import org.ilite.frc.robot.modules.EElevatorPosition;

public class ElevatorToPosition implements ICommand {

  private double mStartTime, mTimeout;
  private EElevatorPosition mPosition;
  
  private Elevator mElevator;
  
  public ElevatorToPosition(Elevator pElevator, EElevatorPosition pPosition, double pTimeout) {
    this.mElevator = pElevator;
    this.mPosition = pPosition;
    this.mTimeout = pTimeout;
  }
  
  public ElevatorToPosition(Elevator pElevator, EElevatorPosition pPosition) {
    this(pElevator, pPosition, -1);
  }
  
  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
    mElevator.setElevControlMode(ElevatorControlMode.POSITION);
    mElevator.setPosition(mPosition);
  }

  @Override
  public boolean update(double pNow) {
    if(mElevator.isFinishedGoingToPosition()) {
      System.out.println("=== Finished going to position: " + mPosition);
      return true;
    }
    if(mTimeout > 0) {
      if(pNow - mStartTime >= mTimeout) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
