package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;

import openrio.powerup.MatchData.OwnedSide;


public class SourceTestMain {
	
	public static void main(String[] args) {
	  GetAutonomous getAuto = new GetAutonomous(null, null, null, null, null, null);
	  ArrayList<ECubeAction> preferredActions = new ArrayList<>();
	  preferredActions.add(ECubeAction.SCALE);
	  preferredActions.add(ECubeAction.SWITCH);
	  
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
