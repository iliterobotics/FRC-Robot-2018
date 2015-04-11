package org.ilite.vision.data;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ilite.vision.constants.Paths;
import org.json.JSONException;


public class Configurations {
    private static final Map<String, Object> mKeyMap;

    static {

        Map<String, Object>tempMap = new HashMap<>();

        try {
            Map<String, Object> map = JSONManager.read(new File("properties.json"));

            for(Entry<String, Object>anEntry : map.entrySet()) {
                tempMap.put(anEntry.getKey(), anEntry.getValue());
            }
            
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        
        mKeyMap = Collections.unmodifiableMap(tempMap);
    }

    public static float getFloatValue(String key) {
        return ((Float) mKeyMap.get(key)).floatValue();
    }
    
    public static long getLongValue(String key) {
        return ((Double) mKeyMap.get(key)).longValue();
    }
    
    public static int getIntValue(String key) {
        return ((Integer) mKeyMap.get(key)).intValue();
    }
    
    public static boolean getBooleanValue(String key) {
        return ((Boolean) mKeyMap.get(key)).booleanValue();
    }
    
    public static String getStringValue(String key) {
        return (String) mKeyMap.get(key);
    }
    
    public static Object getValue(String pKey) {
        return mKeyMap.get(pKey);
    }
}
