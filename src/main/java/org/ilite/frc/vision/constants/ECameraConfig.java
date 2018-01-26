package org.ilite.frc.vision.constants;

import org.ilite.frc.vision.data.Configurations;

public enum ECameraConfig {
    CAM_RATE_MILLIS(Configurations.getIntValue("CAM_RATE_MILLIS")),
    CAMERA_PERIOD(Configurations.getIntValue("CAMERA_PERIOD")),
    INITIAL_CAMERA_DELAY(Configurations.getIntValue("INITIAL_CAMERA_DELAY")),
    DEVICE(Configurations.getLongValue("DEVICE")),
    USERNAME(Configurations.getStringValue("USERNAME")),
    PASSWORD(Configurations.getStringValue("PASSWORD")),
    USE_LOCAL_IF_NOT_AVAILABLE(Configurations.getBooleanValue("USE_LOCAL_IF_NOT_AVAILABLE"));
    
    private long val;
    private String val2;
    private boolean val3;
    
    private ECameraConfig(boolean b) {
        this.val3 = b;
    }
    
    private ECameraConfig(long v) {
        val = v;
    }
    
    private ECameraConfig(String val) {
        val2 = val;
    }

    public boolean getBooleanValue() {
        return val3;
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
