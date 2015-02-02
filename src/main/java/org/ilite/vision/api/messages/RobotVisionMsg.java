package org.ilite.vision.api.messages;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.ilite.vision.camera.opencv.OpenCVUtils;

public final class RobotVisionMsg {
    private final BufferedImage rawImage;
    private final List<VisibleObject> visibleObjects;
    
    protected RobotVisionMsg(BufferedImage rawImage) {
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
