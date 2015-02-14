package org.ilite.vision.constants;

public enum Constants {
    IMAGES_FOLDER_PATH("src/main/resources/images/"),
    PROPERTIES_FILE_PATH("properties.json"),
    BLOB_CONFIG_FILE_PATH("BlobConfig.json");

    private String value;
    
    private Constants(String val) {
        value = val;
    }
    
    public String getValue() {
        return value;
    }
}
