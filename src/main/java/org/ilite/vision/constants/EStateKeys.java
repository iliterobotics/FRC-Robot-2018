package org.ilite.vision.constants;

import org.ilite.vision.data.Configurations;

public enum EStateKeys {
    COLOR_BLOB_DATA("Blob Config"),
    OVERLAY_IMAGE_PATH("OverlayImagePath");
    
    private EStateKeys(String pKeyName) {
        mKeyValue = pKeyName;
    }
    
    private final String mKeyValue;
    
    public Object getValue() {
        return Configurations.getValue(mKeyValue);
    }

}
