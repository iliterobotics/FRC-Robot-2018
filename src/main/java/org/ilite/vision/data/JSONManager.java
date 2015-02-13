package org.ilite.vision.data;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    
    public static HashMap<String, Object> read(File file) throws IOException, JSONException {
        JSONObject root;
        
        FileInputStream stream = new FileInputStream(file);
        
        byte[] buffer = new byte[(int) file.length()];
        
        DataInputStream dstream = new DataInputStream(stream);
        
        dstream.readFully(buffer);
        
        root = new JSONObject(new String(buffer));
        
        dstream.close();
        stream.close();
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        Iterator it = root.keys();
        
        while(it.hasNext()) {
            String s = (String) it.next();
            
            map.put(s, root.get(s));
        }
        
        return map;
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
