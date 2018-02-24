package org.ilite.frc.robot;

import org.ilite.frc.robot.modules.Carriage;


public class SourceTestMain {
	
  private static Data data;
  
	public static void main(String[] args) {
		testCarriage(data);
	}
	
	public static void testCarriage(Data data) {
	  TestDigitalInput beamBreak = new TestDigitalInput(false);
	  TestSolenoid grabber = new TestSolenoid(false);
	  TestSolenoid kicker = new TestSolenoid(true);
	  
	  beamBreak.onChange(e -> System.out.println("Has cube: " + beamBreak));
	  grabber.onChange(e -> System.out.println("Grabber is: " + grabber));
	  kicker.onChange(e -> System.out.println("Kicker is: " + kicker));
	  
	  Carriage carriage = new Carriage(data, beamBreak, grabber, kicker);
	  
	  carriage.setHaveCube();
	  System.out.println();
	  carriage.setNoCube();
	  
	}

}
