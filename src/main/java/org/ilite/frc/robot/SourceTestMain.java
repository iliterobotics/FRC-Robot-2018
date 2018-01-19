package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.common.types.*;
import openrio.powerup.MatchData.OwnedSide;


public class SourceTestMain {
	
	public static void main(String[] args) {
		GetAutonomous getAutonomous = new GetAutonomous(null);
		List<ECubeAction> cList = new ArrayList<ECubeAction>();
		cList.add(ECubeAction.EXCHANGE);
		cList.add(ECubeAction.SWITCH);
		cList.add(ECubeAction.SCALE);
		getAutonomous.testReceiveData(cList, null, EStartingPosition.RIGHT, OwnedSide.RIGHT, OwnedSide.RIGHT);
		getAutonomous.getAutonomous(null);
	
	}

}
