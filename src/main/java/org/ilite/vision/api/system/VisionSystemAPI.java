package org.ilite.vision.api.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.constants.ECameraConfig;
import org.ilite.vision.constants.ECameraType;

public class VisionSystemAPI {
    
    /**
     * Log4j logger
     */
    private static final Logger sLogger = Logger.getLogger(VisionSystemAPI.class);

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

	public static BufferedImage loadImage(String path)throws IOException {
		BufferedImage myImage = ImageIO.read(new File(path));
 
		return myImage;
		
	}
	
	public static BufferedImage loadImageAsResource(String pResourceName) throws IOException  {
	    return ImageIO.read(ClassLoader.getSystemResource(pResourceName));
	}

	public static ImageBlender getImageBlender(IVisionSystem system) throws IOException {
	    ImageBlender b = new ImageBlender();
	    system.subscribe(b);
	    
	    return b;
	}
	
	public static IVisionSystem getVisionSystem(ECameraType pCameraType) {
	    ECameraType aType = pCameraType;
	    
	    if(ECameraConfig.USE_LOCAL_IF_NOT_AVAILABLE.getBooleanValue() && !OpenCVUtils.isAvailable(aType.getCameraIP())) {
	        aType = ECameraType.LOCAL_CAMERA;
	        
	        if(sLogger.isEnabledFor(Level.WARN)) {
	            StringBuilder warnString = new StringBuilder();
	            warnString.append("Unable to connect to camera type: ");
	            warnString.append(pCameraType);
	            warnString.append(", iP= ").append(pCameraType.getCameraIP());
	            sLogger.warn(warnString);
	        }
	    }
	    
	    if(sLogger.isDebugEnabled()) {
	        StringBuilder debugString = new StringBuilder();
	        debugString.append("Loading vision system for type= ").append(aType);
	        debugString.append(", IP= ").append(aType.getCameraIP());
	        sLogger.debug(debugString);
	    }
	    
		IVisionSystem iVisionSystem = INSTANCE_HOLDER.mCameraTypes.get(aType);
		iVisionSystem.start();
		return iVisionSystem;
	}

}
