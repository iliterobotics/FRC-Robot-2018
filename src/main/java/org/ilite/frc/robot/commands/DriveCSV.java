package org.ilite.frc.robot.commands;

import org.ilite.frc.robot.modules.DriveTrain;

import java.io.File;

public class DriveCSV implements ICommand {

  private double mStartTime;

  private File mCsvFile;
  private DriveTrain mDrivetrain;

  public DriveCSV(File pFile, DriveTrain pDrivetrain) {
    this.mCsvFile = pFile;
    this.mDrivetrain = pDrivetrain;
  }

  @Override
  public void initialize(double pNow) {
    mStartTime = pNow;
  }

  @Override
  public boolean update(double pNow) {
    return false;
  }

  @Override
  public void shutdown(double pNow) {

  }
}
