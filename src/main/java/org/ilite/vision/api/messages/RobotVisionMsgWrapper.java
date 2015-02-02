package org.ilite.vision.api.messages;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A wrapper and controller around the RobotVisionMessage
 * @author Daniel Christopher
 * @version 2/1/15
 */
public class RobotVisionMsgWrapper {
    private final RobotVisionMsg message;
    
    public RobotVisionMsgWrapper(RobotVisionMsg message) {
        this.message = message;
    }
    
    public BufferedImage getRawImage() {
        return message.getRawImage();
    }
    
    public List<VisibleObject> getVisibleObjects() {
        return message.getVisibleObjects();
    }
}
