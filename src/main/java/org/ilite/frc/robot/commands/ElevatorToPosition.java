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
  
  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
    mElevator.setElevControlMode(ElevatorControlMode.POSITION);
  }

  @Override
  public boolean update(double pNow) {
    mElevator.setPosition(mPosition);
    if(mElevator.getCurrentTachLevel() == mPosition.tapeMark) return true;
    if(pNow - mStartTime >= mTimeout) return true;
    return false;
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }

}
