package org.ilite.vision.constants;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlobConfig {
    private static Set<BlobModel> models;
    
    public static void initialize() throws JSONException, IOException {
        
        File file = new File("BlobConfig.json");
        
        FileInputStream stream = new FileInputStream(file);
        
        byte[] buffer = new byte[(int) file.length()];
        
        DataInputStream dstream = new DataInputStream(stream);
        
        dstream.readFully(buffer);
        
        JSONArray array = new JSONArray(new String(buffer));
        
        dstream.close();
        stream.close();
        
        models = new LinkedHashSet<BlobModel>();
        
        for(int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            
            BlobModel m = new BlobModel();
            m.setAverageHue(o.getDouble("AVERAGE_HUE"));
            m.setAverageSaturation(o.getDouble("AVERAGE_SATURATION"));
            m.setAverageValue(o.getDouble("AVERAGE_VALUE"));
        }
    }
    
    public static void main(String[] args) throws JSONException, IOException {
        Map<String, Integer> m = new LinkedHashMap();
        
        m.put("AVERAGE_HUE", 1);
        m.put("AVERAGE_VALUE", 2);
        m.put("AVERAGE_SATURATION", 3);
        m.put("AVERAGE_HUE", 4);
        m.put("AVERAGE_VALUE", 5);
        m.put("AVERAGE_SATURATION", 6);
        
        //JSONManager.writeArray(m, new File("BlobConfig.json"));
        
        initialize();
        
        System.out.println(models);
    }
}
