package org.ilite.vision.api;

import org.ilite.vision.data.Configuration;

public enum ECameraType {
    ALIGNMENT_CAMERA("ALIGNED_CAMERA_IP"),
    FIELD_CAMERA("FIELD_CAMERA_IP"),
    LOCAL_CAMERA("LOCAL_CAMERA");
    
    private ECameraType(String pPropertyName) {
        mCameraIP = Configuration.getValue(pPropertyName);
    }
    
    public String getCameraIP() {
        return mCameraIP;
    }
    private final String mCameraIP;

}
