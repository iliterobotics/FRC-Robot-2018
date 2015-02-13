package org.ilite.vision.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;


public class Configuration {
    private static final String CAMERA_IP;
    
    static {
        String temp = "";
        
        try {
            HashMap<String, Object> map = JSONManager.read(new File("properties.json"));
            
            temp = (String) map.get("CameraIP");       
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        
        CAMERA_IP = temp;
    }
    
    public static String getCameraIP() {
        return CAMERA_IP;
    }
}
