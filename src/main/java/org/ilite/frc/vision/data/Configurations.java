package org.ilite.frc.vision.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Configurations {
    private static final Map<String, Object> mKeyMap;
//    private static final Logger sLogger = Logger.getLogger(Configurations.class);
    static {

        Map<String, Object>tempMap = new HashMap<>();

//        try {
//            Map<String, Object> map = JSONManager.read(new File("properties.json"));
//
//            for(Entry<String, Object>anEntry : map.entrySet()) {
//                tempMap.put(anEntry.getKey(), anEntry.getValue());
//            }
//            
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
        
        mKeyMap = Collections.unmodifiableMap(tempMap);
    }

    public static float getFloatValue(String key) {
        return ((Double) mKeyMap.get(key)).floatValue();
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
    
    public static double []getDoubleArray(String pKey) {
//        double [] returnVal = null;
//        Object object = mKeyMap.get(pKey);
//        System.out.println(object.getClass() + "");
//        JSONArray val = (JSONArray)object;
//        returnVal = new double[val.length()];
//        try
//        {
//            for(int i = 0; i < returnVal.length; i++)
//            {
//                returnVal[i] = val.getDouble(i);
//            }
//
//            
//        } catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//        
//        if(sLogger.isDebugEnabled()) {
//            for(int i = 0; i < returnVal.length; i++)
//            {
//                sLogger.debug(returnVal[i]);
//            }
//        }
//        
//        
//        //TODO: Get the value of the map and cast accordingly
//        return returnVal;
      return null;
    }
    
    
    public static void main(String[] args)
    {
//        for(Entry<String, Object>anObject : mKeyMap.entrySet()) {
//            System.out.println(anObject.getKey() + " " + anObject.getValue());
//        }
//        
//        double[] doubleArray = getDoubleArray("HIGH_COLOR");
        getDoubleArray("HIGH_COLOR");

        
    }
}
