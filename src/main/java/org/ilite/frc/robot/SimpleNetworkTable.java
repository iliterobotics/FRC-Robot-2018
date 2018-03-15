package org.ilite.frc.robot;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

public class SimpleNetworkTable  {
    private NetworkTable netTable;
    
    public SimpleNetworkTable(String name) {
        NetworkTableInstance.getDefault().setUpdateRate(SystemSettings.NETWORK_TABLE_UPDATE_RATE);
        netTable = NetworkTableInstance.getDefault().getTable(name);

        netTable.getInstance().setUpdateRate(SystemSettings.NETWORK_TABLE_UPDATE_RATE);
        netTable.getInstance().setServerTeam(1885);
        netTable.getInstance().startClientTeam(1885);
    }
    
    public synchronized NetworkTableEntry getEntry(String key) {
    		return netTable.getEntry(key);
    }
  
    public synchronized void putDouble(String key, double value) {
    	netTable.getEntry(key).forceSetDouble(value);
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
    
    public synchronized NetworkTable getInstance() {
    		return netTable;
    }
    
}