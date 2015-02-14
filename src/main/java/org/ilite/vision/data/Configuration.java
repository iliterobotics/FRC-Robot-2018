package org.ilite.vision.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;


public class Configuration {
    private static final String CAMERA_IP;
    private static final String OVERLAY_IMAGE_PATH;
    
    static {
        String temp = "";
        String temp1 = "";
        
        try {
            HashMap<String, Object> map = JSONManager.read(new File("properties.json"));
            
            temp = (String) map.get("CameraIP");    
            temp1 = (String) map.get("OverlayImagePath");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        
        CAMERA_IP = temp;
        OVERLAY_IMAGE_PATH = temp1;
    }
    
    public static String getOverlayImagePath() {
        return OVERLAY_IMAGE_PATH;
    }
    
    public static String getCameraIP() {
        return CAMERA_IP;
    }
}
