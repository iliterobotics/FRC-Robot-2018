package org.ilite.vision.data;

public enum EStateKeys {
    
    OVERLAY_IMAGE_PATH("OverlayImagePath");
    
    private EStateKeys(String pKeyName) {
        mKeyValue = pKeyName;
    }
    
    private final String mKeyValue;
    
    public String getValue() {
        return Configuration.getValue(mKeyValue);
    }

}
