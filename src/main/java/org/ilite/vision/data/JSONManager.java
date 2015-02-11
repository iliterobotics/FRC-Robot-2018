package org.ilite.vision.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    
    public static void read(File file) {
        
    }
    
    public static void write(Map<String, Object> data, File file) throws JSONException, IOException {
        JSONObject json = new JSONObject();
        
        for(Entry<String, Object> entry : data.entrySet()) {
            json.append(entry.getKey(), entry.getValue());
        }

        FileWriter w = new FileWriter(file);
       
        w.write(json.toString());
        
        w.flush();
        
        w.close();
    }
}
