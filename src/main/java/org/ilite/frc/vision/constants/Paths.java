package org.ilite.frc.vision.constants;

import org.ilite.frc.vision.data.Configurations;

public enum Paths {
    IMAGES_FOLDER_PATH("IMAGES_FOLDER_PATH"),
    IMAGES_NUMBER4_PATH("IMAGES_NUMBER4_PATH"),
    SCREEN_SHOT1_PATH("SCREEN_SHOT1_PATH"),
    BLOB_CONFIG_PATH("BLOB_CONFIG_PATH"),
    OVERLAY_HOOK_PATH("OVERLAY_HOOK_PATH"),
    OVERLAY_TOTE_PATH("OVERLAY_TOTE_PATH");

    private String value;
    
    private Paths(String val) {
        value = Configurations.getStringValue(val);
    }
    
    public String getValue() {
        return value;
    }
}
