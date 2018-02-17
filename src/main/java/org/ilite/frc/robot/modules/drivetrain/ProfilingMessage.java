package org.ilite.frc.robot.modules.drivetrain;

import jaci.pathfinder.followers.EncoderFollower;

public class ProfilingMessage {
  
  public final EncoderFollower leftFollower, rightFollower;
  public double angle;
  public final boolean isBackwards;
  
  public ProfilingMessage(EncoderFollower leftFollower, EncoderFollower rightFollower, boolean isBackwards) {
    super();
    this.leftFollower = leftFollower;
    this.rightFollower = rightFollower;
    this.angle = angle;
    this.isBackwards = isBackwards;
  }
 
}
