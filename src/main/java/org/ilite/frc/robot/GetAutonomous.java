package org.ilite.frc.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
//Java8
import java.util.stream.Collectors;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.robot.commands.ICommand;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.OwnedSide;


public class GetAutonomous {
	//Network Table instance variables.
	private NetworkTable nAutonTable;
	private NetworkTableEntry nPosEntry;
	private NetworkTableEntry nCrossEntry;
	private NetworkTableEntry nCubeActionPrefsEntry;
	
	//Decision variables to be set by networktable entries.
	private List<ECubeAction> mCubeActionPrefs;
	private EStartingPosition mStartingPos;
	private ECross mCrossType;
	private boolean doComplexAutonomous;
	
	//Game Data - Jaci's API
	private OwnedSide mScaleSide;
	private OwnedSide mSwitchSide;
	
	//Used for turning.  Starting on left side = 1, starting on right side = -1;  Unknown or middle = 0
	private int mTurnScalar = 0;
	
	/**
	 * 
	 * @param pAutonTable - Autonomous network table to be passed in from Robot.java
	 */
	public GetAutonomous(NetworkTable pAutonTable) {
		this.nAutonTable = pAutonTable;
		doComplexAutonomous = true;
    try {
      nPosEntry = nAutonTable.getEntry("position");
      nCrossEntry = nAutonTable.getEntry("cross");
      nCubeActionPrefsEntry = nAutonTable.getEntry("cubeActionList");
    } catch (Exception e) {
    
    }
    mScaleSide = getScaleOwnedSide();
    mSwitchSide = getSwitchOwnedSide();
	}
	
	/**
	 * "Main" method that returns a Command Queue to robot Java.
	 * @return Command Queue of autonomous commands.
	 */
	public Queue<ICommand> getAutonomousCommands() {
    parseEntries();
    Queue<ICommand> commands = new LinkedList<ICommand>();
		
		mCubeActionPrefs = getCubeActionsOnMySide();
		
		if (doComplexAutonomous) {

			if (!mCubeActionPrefs.isEmpty()) {
				ECubeAction prefAction = mCubeActionPrefs.get(0);//Does most preferred driver selection.
				System.out.println("Autonomous chose: " + prefAction.toString());
				switch (prefAction) {
				case SCALE:
				  commands.addAll(doScale());
					break;
				case SWITCH:
				  commands.addAll(doSwitch());
					break;
				case EXCHANGE:
				  commands.addAll(doExchange());
					break;
				case NONE:
				  commands.addAll(crossAutoLine());
					break;
				}
			}
			else {
			  commands.addAll(crossAutoLine());//Default
			}
			
		} else {
			// Drive forward > minimum necessary autonomous for ranking point.
		}

		return commands;

	}
	/**
	 * Do scale autonomous; switch based on starting position.
	 */
	public Queue<ICommand> doScale() {
	  //TODO replace with turning scalar
		switch (mStartingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
		
		return new LinkedList<ICommand>();
	}
	
	/**
	 * Do switch autonomous; switch based on starting position.
	 */
	public Queue<ICommand> doSwitch() {
    //TODO replace with turning scalar
		switch (mStartingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
    return new LinkedList<ICommand>();
	}
	
	/**
	 * Place cube in exchange autonomous; switch based on starting position.
	 */
	@SuppressWarnings("all")
	public Queue<ICommand> doExchange() {
    //TODO replace with turning scalar
		switch (mStartingPos) {	
		case LEFT:
			break;
		case MIDDLE:
			break;
		}
    return new LinkedList<ICommand>();
	}
  
  /**
   * Crosses autonomous line based on starting position.
   */
  public Queue<ICommand> crossAutoLine() {
    //TODO replace with turning scalar
    switch(mStartingPos) {
    case LEFT:
      break;
    case MIDDLE:
      break;
    case RIGHT:
      break;
    }
    return new LinkedList<ICommand>();
  }
	
	/**
	 * Determines whether or not our starting position corresponds to the pre-configured owned side.
	 * @param side - Corresponding side received from match data.
	 * @return - Whether or not the starting position matches the owned side from match data.
	 */
	public boolean isOnMySide(OwnedSide side) {
    switch(side) {
    case LEFT:
      return mStartingPos == EStartingPosition.LEFT || mStartingPos == EStartingPosition.MIDDLE;
    case RIGHT:
      return mStartingPos == EStartingPosition.RIGHT || mStartingPos == EStartingPosition.MIDDLE;
    case UNKNOWN:
    default:
      return false;
    }
  }
	/**
	 * Determines whether or not our starting position corresponds to the exchange.
	 * @return - Whether or not our starting position is viable to place a cube into the exchange.
	 */
	public boolean isExchangeOnMySide() {
		return mStartingPos == EStartingPosition.MIDDLE || mStartingPos == EStartingPosition.LEFT;
	}
	
	/**
	 * Parses the network table entries and stores the values into the decision variables.
	 * Converts the number representation of the network table entry into an enum.
	 */
	private void parseEntries() {
		int posNum = nPosEntry.getNumber(EStartingPosition.UNKNOWN.ordinal()).intValue();
		int crossNum = nCrossEntry.getNumber(ECross.NONE.ordinal()).intValue();
		Integer[] defaultArray = { ECubeAction.NONE.ordinal() };
		Number[] cubeArray = nCubeActionPrefsEntry.getNumberArray(defaultArray);

		mStartingPos = EStartingPosition.intToEnum(posNum);
		mCrossType = ECross.intToEnum(crossNum);
		for (Number n : cubeArray) {
			mCubeActionPrefs.add(ECubeAction.intToEnum(n.intValue()));
		}

		switch(mStartingPos) {
		case LEFT: mTurnScalar = 1; break;
		
		case RIGHT: mTurnScalar = -1; break;
		
    case MIDDLE: 
		case UNKNOWN:
	  default: mTurnScalar = 0; break;
		}
	}
	
	/**
	 * Game data method. Returns the owned side of the switch at beginning of match.
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getSwitchOwnedSide() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}
	
	/**
	 * Game data method. Returns the owned side of the scale at beginning of match.
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getScaleOwnedSide() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}
	
	/**
	 * Utilizes the previous to mySide methods.
	 * @param c - Type of cube action, SCALE, SWITCH, EXCHANGE.
	 * @return - Whether or not our starting position is viable to perform the given cube action.
	 */
	public boolean isCubeActionOnMySide(ECubeAction c) {
	  switch(c) {
	  case EXCHANGE:
	    return isExchangeOnMySide();
	  case SCALE:
	    return isOnMySide(mScaleSide);
	  case SWITCH:
	    return isOnMySide(mSwitchSide);
    case NONE:
    default:
      return true;
	  }
	}
  
  /**
   * Utilizes the previous to mySide methods.
   * @param c - Type of cube action, SCALE, SWITCH, EXCHANGE.
   * @return - Whether or not our starting position is viable to perform the given cube action.
   */
  public boolean isCubeActionOtherSide(ECubeAction c) {
    switch(c) {
    case EXCHANGE:
      return false;
    case SCALE:
      return !isOnMySide(mScaleSide);
    case SWITCH:
      return !isOnMySide(mSwitchSide);
    case NONE:
    default:
      return true;
    }
  }
	
	/*package*/ List<ECubeAction> getCubeActionsOnMySide() {
	  return mCubeActionPrefs
        .stream()
        .filter(cA -> isCubeActionOnMySide(cA))
        .collect(Collectors.toList());
	}
  
  /*package*/ List<ECubeAction> getCubeActionsOnOtherSide() {
    return mCubeActionPrefs
        .stream()
        .filter(cA -> isCubeActionOtherSide(cA))
        .collect(Collectors.toList());
  }
	
	/**
	 * Testing method.
	 * @param pActions
	 * @param pCross
	 * @param pPos
	 * @param pSwitchSide
	 * @param pScaleSide
	 */
	public void testReceiveData(List<ECubeAction> pActions, ECross pCross, EStartingPosition pPos,
		OwnedSide pSwitchSide, OwnedSide pScaleSide) {
		mCubeActionPrefs = pActions;
		mCrossType = pCross;
		mStartingPos = pPos;
		mSwitchSide = pSwitchSide;
		mScaleSide = pScaleSide;
	}

}
