package org.ilite.frc.robot.auto;

import java.io.File;

import org.ilite.frc.common.config.SystemSettings;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathGenerator {
	
  private FieldAdapter mField;
	private Config mConfig;
	
	public PathGenerator(FieldAdapter pField, Config pConfig) {
	  this.mField = pField;
		this.mConfig = pConfig;
	}
	
	public PathGenerator() {
		this(new FieldAdapter(new DefaultField()), 
		     new Config(SystemSettings.MP_FIT_METHOD, SystemSettings.MP_SAMPLES, SystemSettings.MP_DELTA_TIME, 
						        SystemSettings.MP_MAX_VEL, SystemSettings.MP_MAX_ACC, SystemSettings.MP_MAX_JERK));
	}
	
	public TankModifier getTankModifier(Waypoint ... pWaypoints) {
		TankModifier mTankModifier = new TankModifier(getTrajectory(pWaypoints));
		return mTankModifier;
	}
	
	public Trajectory getTrajectory(Waypoint ...pWaypoints) {
	  Trajectory mTrajectory = Pathfinder.generate(pWaypoints, mConfig);
	  return mTrajectory;
	}
	
	public TankModifier getTrajectory(String pFilePath, Waypoint ... pWaypoints) {
		TankModifier mTankModifier = getTankModifier(pWaypoints);
		Pathfinder.writeToCSV(new File(pFilePath), mTankModifier.getSourceTrajectory());
		return mTankModifier;
	}
	
	public Waypoint makeWaypointLeft(double x, double y, double pHeadingDegrees) {
	  return new Waypoint(x - mField.getLeftStartingPosX(), y - mField.getLeftStartingPosY(), Pathfinder.d2r(pHeadingDegrees));
	}
	
	public Waypoint makeWaypointMiddle(double x, double y, double pHeadingDegrees) {
    return new Waypoint(x - mField.getMiddleStartingPosX(), y - mField.getMiddleStartingPosY(), Pathfinder.d2r(pHeadingDegrees));
  }
	
	public Waypoint makeWaypointRight(double x, double y, double pHeadingDegrees) {
    return new Waypoint(x - mField.getRightStartingPosX(), y - mField.getRightStartingPosY(), Pathfinder.d2r(pHeadingDegrees));
  }
	
	
	public Waypoint[] getMiddleToLeftSwitch() {
	  return new Waypoint[] {
	      makeWaypointMiddle(mField.getMiddleStartingPosX(), mField.getMiddleStartingPosY(), 0),
	      makeWaypointMiddle(mField.getLeftFrontSwitchX(), mField.getLeftFrontSwitchY(), 0)
	  };
	}
	
	public Waypoint[] getMiddleToRightSwitch() {
    return new Waypoint[] {
        makeWaypointMiddle(mField.getMiddleStartingPosX(), mField.getMiddleStartingPosY(), 0),
        makeWaypointMiddle(mField.getRightFrontSwitchX(), mField.getRightFrontSwitchY(), 0)
    };
	}
	
	
	public Waypoint[] getLeftToLeftSwitch() {
	  return new Waypoint[] {
	    makeWaypointLeft(mField.getLeftStartingPosX(), mField.getLeftStartingPosY(), 0),
	    makeWaypointLeft(9, mField.getLeftStartingPosY(), 0),
	    makeWaypointLeft(mField.getLeftSideSwitchX(), mField.getLeftSideSwitchY(), 90)
	  };
	}
	
	public Waypoint[] getLeftToRightSwitch() {
	  return new Waypoint[] {};
	}
	
	public Waypoint[] getLeftToRightScale() {
	  return new Waypoint[] {
	    makeWaypointLeft(mField.getLeftStartingPosX(), mField.getLeftStartingPosY(), 0),
	    makeWaypointLeft(14, mField.getLeftStartingPosY(), 0),
	    makeWaypointLeft(20, 16, 90),
	    makeWaypointLeft(20, 8, 90),
	    makeWaypointLeft(mField.getRightScaleX(), mField.getRightScaleY(), 0)
	  };
	}
	
	public Waypoint[] getLeftToLeftScale() {
	  return new Waypoint[] {
	      makeWaypointLeft(mField.getLeftStartingPosX(), mField.getLeftStartingPosY(), 0),
	      makeWaypointLeft(16, mField.getLeftStartingPosY(), 0),
	      makeWaypointLeft(mField.getLeftScaleX(), mField.getLeftScaleY(), 0)
	  };
	}
	
  
  public Waypoint[] getRightToLeftSwitch() {
    return new Waypoint[] {};
  }
  
  public Waypoint[] getRightToRightSwitch() {
    return new Waypoint[] {
        makeWaypointRight(mField.getRightStartingPosX(), mField.getRightStartingPosY(), 0),
        makeWaypointRight(9, mField.getLeftStartingPosY(), 0),
        makeWaypointRight(mField.getLeftSideSwitchX(), mField.getLeftSideSwitchY(), -90)
    };
  }
  
  public Waypoint[] getRightToLeftScale() {
    return new Waypoint[] {
        makeWaypointRight(mField.getRightStartingPosX(), mField.getRightStartingPosY(), 0),
        makeWaypointRight(14, mField.getRightStartingPosY(), 0),
        makeWaypointRight(20, 11, -90),
        makeWaypointRight(20, 19, -90),
        makeWaypointRight(mField.getLeftScaleX(), mField.getLeftScaleY(), 0)
    };
  }
  
  public Waypoint[] getRightToRightScale() {
    return new Waypoint[] {
      makeWaypointRight(mField.getRightStartingPosX(), mField.getRightStartingPosY(), 0),
      makeWaypointRight(16, mField.getRightStartingPosY(), 0),
      makeWaypointRight(mField.getRightScaleX(), mField.getRightScaleY(), 0)
    };
  }
  
  public Waypoint[] getCrossAutoline() {
    return new Waypoint[] {
        makeWaypointRight(mField.getRightStartingPosX(), mField.getRightStartingPosY(), 0),
        makeWaypointRight(11, mField.getRightStartingPosY(), 0)
    };
  }
	
	
}
