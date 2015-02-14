package org.ilite.vision.constants;

import org.ilite.vision.data.Configurations;

public enum ECameraConfig {
    CAM_RATE_MILLIS(Configurations.getIntValue("CAM_RATE_MILLIS")),
    START_SLEEP(Configurations.getLongValue("START_SLEEP")),
    DEVICE(Configurations.getLongValue("DEVICE")),
    USERNAME(Configurations.getStringValue("USERNAME")),
    PASSWORD(Configurations.getStringValue("PASSWORD"));
    
    private long val;
    private String val2;
    
    private ECameraConfig(long v) {
        val = v;
    }
    
    private ECameraConfig(String val) {
        val2 = val;
    }

    public String getStringValue() {
        if(val2 == null) {
            return Long.toString(val);
        }
        
        return val2;
    }
    
    public long getValue() {
        return val;
    }
}
