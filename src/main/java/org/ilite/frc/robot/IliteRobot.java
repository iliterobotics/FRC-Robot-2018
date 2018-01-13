package org.ilite.frc.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * We extend IterativeRobot because it gives us a notification of data receipt
 * from the driver's station. This means we have new Joystick & DS data.  So
 * there isn't any reason to try to read the Joysticks faster than that.  The
 * parent class will also handle the enable/disable logic.  DS data comes in
 * at 50hz, however with field latency and dropped packets this is
 * by no means a precise measure or even a promise.
 * <br><br>
 * This class is responsible for setting what the drivers *desire* the robot to
 * do.  The control loops (separate thread) will do the logic and motor outputs.
 * <br><br>
 * Our control loop will use the wpilib Notifier class.  That class uses the
 * FPGA clock to run a periodic loop with more repeatability.  In addition,
 * we don't need to worry about issues surrounding loop state and threading.
 * <br><br>
 * Keep in mind that there are 2 main threads in our code: 1. The loop
 * of this robot class; and 2. the Notifer that we'll use to update the control
 * loops.
 */
public class IliteRobot extends IterativeRobot {
  
  private final Data mData = new Data();
  
  public IliteRobot() {
  }
  
  public void robotInit() {
    
  }
  
  public void autonomousInit() {
    
  }

  public void autonomousPeriodic() {
    
  }
  
  public void teleopInit() {
    
  }
  
  public void teleopPeriodic() {
    
  }
  
  public void disabledInit() {
    
  }
  
  public void disabledPeriodic() {
    
  }
  
  public void testInit() {
    
  }
  
  public void testPeriodic() {
    
  }
}
