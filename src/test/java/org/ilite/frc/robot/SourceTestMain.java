package org.ilite.frc.robot;

import java.util.ArrayList;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.util.SystemUtils;

import com.flybotix.hfr.codex.Codex;

import openrio.powerup.MatchData.OwnedSide;


public class SourceTestMain {
	
	public static void main(String[] args) {
	  
	  testLoggingLocally();
//	  testOnLocalServer();
	  
	}
	
	private static void testLoggingLocally() {
	  Data mData = new Data();
	  while(true) {
	    EDriveTrain.testMap(mData.drivetrain);
	    SystemUtils.writeCodexToSmartDashboard(EDriveTrain.class, mData.drivetrain, 0);
	  }
	}
	
	/**
	 * Tests GetAutonomous using a locally hosted NT server. Remember that this will NOT work if GetAutonomous tries to access Jaci's MatchData.
	 */
	private static void testOnLocalServer() {
	  GetAutonomous getAuto = new GetAutonomous(SystemSettings.AUTON_TABLE, null, null, null, null, null, null);

    getAuto.testReceiveSideData(OwnedSide.LEFT, OwnedSide.RIGHT);
    
    while(true) {
      getAuto.getAutonomousCommands();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
	}
	
	/**
	 * Tests every possible combination of parameters for GetAutonomous given a list of preferred actions. 
	 * Remember that this will NOT work if GetAutonomous tries to access Jaci's MatchData.
	 */
	private static void testAllAutos() {
	GetAutonomous getAuto = new GetAutonomous(null, null, null, null, null, null, null);
	
  ArrayList<ECubeAction> preferredActions = new ArrayList<>();
  preferredActions.add(ECubeAction.SWITCH);
  preferredActions.add(ECubeAction.SCALE);
  getAuto.testReceiveData(preferredActions, 
    ECross.NONE, 
    EStartingPosition.RIGHT, 
    OwnedSide.LEFT, 
    OwnedSide.LEFT);
	  
  System.out.println("Resulting command queue: " + getAuto.getAutonomousCommands());
  for(ECross cross : ECross.values()) {
    for(EStartingPosition startingPosition : EStartingPosition.values()) {
      for(OwnedSide switchSide : OwnedSide.values()) {
        for(OwnedSide scaleSide : OwnedSide.values()) {
          System.out.printf("STARTING LOOP ===== Sending: Starting Position: %s Switch Side: %s Scale Side: %s\n", startingPosition, switchSide, scaleSide);
          getAuto.testReceiveData(preferredActions, 
              cross, 
              startingPosition, 
              switchSide, 
              scaleSide);
          System.out.println("Resulting command queue: " + getAuto.getAutonomousCommands());
          System.out.println("ENDING LOOP");
          System.out.println();
        }
      }
    }
  }
	}

}
