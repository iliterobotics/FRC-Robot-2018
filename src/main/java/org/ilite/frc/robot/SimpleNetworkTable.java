package org.ilite.frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

public class SimpleNetworkTable  {
    private NetworkTable netTable;
    
    public SimpleNetworkTable(String name) {
        netTable = NetworkTableInstance.getDefault().getTable(name);
    }
    public synchronized void initKeys() {
    	    netTable.getEntry("Cross").setDefaultNumber(-1);
		netTable.getEntry("Starting Position").setDefaultNumber(-1);
		Number[] defaultArray = {0};
		netTable.getEntry("Cube Action").setDefaultNumberArray(defaultArray);
    }
    public synchronized NetworkTableEntry getEntry(String key) {
    		return netTable.getEntry(key);
    }
    
    public synchronized Number getNumber(String key, Integer defaultValue) {
        return netTable.getEntry(key).getNumber(defaultValue);
    }

    public synchronized void putNumber(String key, Integer value) {
        netTable.getEntry(key).forceSetNumber(value);
    }
    
    public synchronized void putNumberArray(String key, Integer[] values) {
    		netTable.getEntry(key).setNumberArray(values);
    }
    
}