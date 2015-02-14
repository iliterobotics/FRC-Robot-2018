package org.ilite.vision.api.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.ilite.vision.api.ECameraType;
import org.opencv.core.Mat;

public class VisionSystemAPI {

	private static class INSTANCE_HOLDER {
	    
	    private static Map<ECameraType, IVisionSystem>mCameraTypes;  
	    
	    static {
	        Map<ECameraType, IVisionSystem>tempMap = new EnumMap<>(ECameraType.class);
	        for(ECameraType aType : ECameraType.values()) {
	            tempMap.put(aType, new VisionSystem(aType.getCameraIP()));
	        }
	        mCameraTypes = Collections.unmodifiableMap(tempMap);
	    }
	}

	public static BufferedImage loadImage(String FILEPATH)throws IOException {
		BufferedImage myImage = ImageIO.read(new File(FILEPATH));
 
		return myImage;
		
	}

	public static IVisionSystem getVisionSystem(ECameraType pCameraType) {
		System.out.println("IP:" + pCameraType.getCameraIP());
		return INSTANCE_HOLDER.mCameraTypes.get(pCameraType);
	}

}
