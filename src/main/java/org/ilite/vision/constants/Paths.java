package org.ilite.vision.constants;

import org.ilite.vision.data.Configurations;

public enum Paths {
    IMAGES_FOLDER_PATH("IMAGES_FOLDER_PATH"),
    BLOB_CONFIG_FILE_PATH("BLOB_CONFIG_PATH"),
    OVERLAY_IMAGE_PATH("OVERLAY_IMAGE_PATH");

    private String value;
    
    private Paths(String val) {
        value = Configurations.getStringValue(val);
    }
    
    public String getValue() {
        return value;
    }
}
