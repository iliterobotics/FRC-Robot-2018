package org.ilite.frc.vision.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    
    
    public static LinkedHashMap<String, Object> read(File file) throws IOException, JSONException {
        JSONObject root;
        
        FileInputStream stream = new FileInputStream(file);
        
        byte[] buffer = new byte[(int) file.length()];
        
        DataInputStream dstream = new DataInputStream(stream);
        
        dstream.readFully(buffer);
        
        root = new JSONObject(new String(buffer));
        
        dstream.close();
        stream.close();
        
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        
        Iterator it = root.keys();
        
        while(it.hasNext()) {
            String s = (String) it.next();
            
            map.put(s, root.get(s));
        }
        
        return map;
    }
    
    public static void write(Map<String, Object> data, File file, String header) throws JSONException, IOException {
        JSONArray array = null;
        
        byte[] buffer = new byte[(int) file.length()];
        
        FileInputStream stream = new FileInputStream(file);
        DataInputStream dstream = new DataInputStream(stream);

        dstream.readFully(buffer);

        BufferedReader br = new BufferedReader(new FileReader(file));   

        if(br.readLine() != null) {
            String string = new String(buffer);
            JSONObject rootObject = new JSONObject(string);
            Object object = rootObject.get(header);
            
            if(object instanceof JSONArray) {
                array = (JSONArray)object;
            } 
            else {
                array = new JSONArray();
            }
        }
        else {
            array = new JSONArray();
        }
        
        br.close();
        
        List<JSONObject> objects = new ArrayList<JSONObject>();

        JSONObject o = new JSONObject();
        
        for(int i = 0; i < array.length(); i++) {
            objects.add(array.getJSONObject(i));
        }
        
        for(Entry<String, Object> e : data.entrySet()) {
            o.put(e.getKey(), e.getValue());
        }
        
        objects.add(o);
        
        array = new JSONArray(objects);
        
        FileWriter w = new FileWriter(file);
        
        w.write("{\"" + header + "\":" + array.toString() + "}");
        
        w.close();
    }
}
