package org.ilite.frc.robot.auto;

import java.io.File;

import org.ilite.frc.common.config.SystemSettings;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathGenerator {
	
	private Config mConfig;
	
	public PathGenerator(Config pConfig) {
		this.mConfig = pConfig;
	}
	
	public PathGenerator() {
		this(new Config(SystemSettings.MP_FIT_METHOD, SystemSettings.MP_SAMPLES, SystemSettings.MP_DELTA_TIME, 
						SystemSettings.MP_MAX_VEL, SystemSettings.MP_MAX_ACC, SystemSettings.MP_MAX_JERK));
	}
	
	public TankModifier getTrajectory(Waypoint ... pWaypoints) {
		Trajectory mTrajectory = Pathfinder.generate(pWaypoints, mConfig);
		TankModifier mTankModifier = new TankModifier(mTrajectory);
		return mTankModifier;
	}
	
	public TankModifier getTrajectory(String pFilePath, Waypoint ... pWaypoints) {
		TankModifier mTankModifier = getTrajectory(pWaypoints);
		Pathfinder.writeToCSV(new File(pFilePath), mTankModifier.getSourceTrajectory());
		return mTankModifier;
	}
	
}
