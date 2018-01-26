package org.ilite.frc.vision.constants;

import org.ilite.frc.vision.data.Configurations;

public enum ECameraType {
    ALIGNMENT_CAMERA("ALIGNED_CAMERA_IP"),
    FIELD_CAMERA("FIELD_CAMERA_IP"),
    LOCAL_CAMERA("LOCAL_CAMERA");
    
    private ECameraType(String pPropertyName) {
        mCameraIP = (String) Configurations.getValue(pPropertyName);
    }
    
    public String getCameraIP() {
        return mCameraIP;
    }
    private final String mCameraIP;

}
