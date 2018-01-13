package org.ilite.vision.camera.tools.overlayGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonExample {
    
    public static void main(String[] args) throws JSONException, IOException {
        
        JSONObject anObject = new JSONObject();

        JSONObject tote = new JSONObject();
        tote.put("Name", "Tote");
        tote.put("AVERAGE_HUE", 4);
        tote.put("AVERAGE_SAT", 4);
        tote.put("AVERAGE_VALUE", 4);
        List<JSONObject>totes = new ArrayList<JSONObject>();
        totes.add(tote);
        
        JSONObject trashCan = new JSONObject();
        trashCan.put("Name", "TrashCan");
        trashCan.put("Name", "Tote");
        trashCan.put("AVERAGE_HUE", 45);
        trashCan.put("AVERAGE_SAT", 45);
        trashCan.put("AVERAGE_VALUE", 45);
        
        totes.add(trashCan);
        JSONArray objects = new JSONArray(totes);
       
        anObject.append("Objects", objects);
        
        FileWriter fw = new FileWriter(new File("src/main/resources/example.json"));
        fw.write(anObject.toString());
        fw.close();
    }

}
