package org.ilite.vision.constants;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;

import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlobData {
    private static LinkedHashSet<BlobModel> models;
    
    public static void readBlobData() throws JSONException, IOException {
        
        File file = new File(Paths.BLOB_CONFIG_PATH.getValue());
        
        byte[] buffer = new byte[(int) file.length()];
        
        FileInputStream stream = new FileInputStream(file);
        DataInputStream dstream = new DataInputStream(stream);

        dstream.readFully(buffer);
        
        dstream.close();
        stream.close();
        
        BufferedReader br = new BufferedReader(new FileReader(file));   
        JSONArray array = new JSONArray();
        
        if(br.readLine() != null) {
            String string = new String(buffer);
            JSONObject rootObject = new JSONObject(string);
            Object object = rootObject.get("Blob Data");
            
            if(object instanceof JSONArray) {
                array = (JSONArray)object;
            } 
        }
        
        br.close();
        
        models = new LinkedHashSet<BlobModel>();
        
        for(int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            
            BlobModel m = new BlobModel();
            m.setName(o.getString("NAME"));
            m.setAverageHue(o.getDouble("AVERAGE_HUE"));
            m.setAverageSaturation(o.getDouble("AVERAGE_SATURATION"));
            m.setAverageValue(o.getDouble("AVERAGE_VALUE"));
            
            models.add(m);
        }
    }
    
    public static LinkedHashSet<BlobModel> getBlobData() throws Exception {
        
        if(models == null) {
            throw new Exception("No blob models loaded from file. Call readBlobData().");
        }
        
        return models;
    }
}
