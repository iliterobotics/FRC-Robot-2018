package org.ilite.vision.data;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;


public class Configuration {
    private static final Map<String, String>mKeyMap;

    static {

        Map<String, String>tempMap = new HashMap<>();

        try {
            Map<String, Object> map = JSONManager.read(new File("properties.json"));

            for(Entry<String, Object>anEntry : map.entrySet()) {
                tempMap.put(anEntry.getKey(), (String)anEntry.getValue());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        mKeyMap = Collections.unmodifiableMap(tempMap);
    }

    public static String getValue(String pKey) {
        return mKeyMap.get(pKey);
    }
}
