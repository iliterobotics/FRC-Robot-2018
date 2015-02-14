package org.ilite.vision.api.messages;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ilite.vision.camera.opencv.OpenCVUtils;

/**
 * Contains data coming from the camera
 * @author Daniel Christopher
 * @version 2/1/15
 *
 */
public final class RobotVisionMsg {
    private final BufferedImage rawImage;
    private final List<VisibleObject> visibleObjects;
    
    public RobotVisionMsg(BufferedImage rawImage) {
        this.rawImage = rawImage;
        
        visibleObjects = new LinkedList<VisibleObject>();
    }
    
    public BufferedImage getRawImage() {
        return OpenCVUtils.deepCopy(rawImage);
    }
    
    public List<VisibleObject> getVisibleObjects() {
        return Collections.unmodifiableList(visibleObjects);
    }
}
