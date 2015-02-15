package org.ilite.vision.constants;

public enum Paths {
    IMAGES_FOLDER_PATH("src/main/resources/images/"),
    PROPERTIES_FILE_PATH("properties.json"),
    BLOB_CONFIG_FILE_PATH("BlobConfig.json"),
    OVERLAY_HOOK_IMAGE_PATH("src/main/resources/images/OverlayHook.png"), 
    OVERLAY_TOTE_IMAGE_PATH("src/main/resources/images/OverlayTote.png");

    private String value;
    
    private Paths(String val) {
        value = val;
    }
    
    public String getValue() {
        return value;
    }
}
