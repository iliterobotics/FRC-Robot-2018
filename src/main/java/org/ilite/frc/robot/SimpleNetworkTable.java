package org.ilite.frc.robot;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

public class SimpleNetworkTable  {
    private NetworkTable netTable;
    
    public SimpleNetworkTable(String name) {
        netTable = NetworkTableInstance.getDefault().getTable(name);
        netTable.getInstance().startClient("1885");//Init 
        netTable.getInstance().setServerTeam(1885);
        netTable.getInstance().startClientTeam(1885);
    }
    public synchronized void initKeys() {
    	    netTable.getEntry(ECross.class.getSimpleName()).setDefaultNumber(-1);
      		netTable.getEntry(EStartingPosition.class.getSimpleName()).setDefaultNumber(-1);
      		Number[] defaultArray = {0};
      		netTable.getEntry(ECubeAction.class.getSimpleName()).setDefaultNumberArray(defaultArray);
      		netTable.getEntry("Chosen Autonomous").setDefaultString("");
    }
    public synchronized NetworkTableEntry getEntry(String key) {
    		return netTable.getEntry(key);
    }
  
    public synchronized void putNumber(String key, Integer value) {
        netTable.getEntry(key).forceSetNumber(value);
    }
    
    public synchronized void putNumberArray(String key, Integer[] values) {
    		netTable.getEntry(key).setNumberArray(values);
    }
    
    public synchronized void putString(String key, String value) {
    		netTable.getEntry(key).forceSetString(value);
    }
    
    public synchronized NetworkTableInstance getInstance() {
    		return netTable.getInstance();
    }
    
}