package org.ilite.vision.api.system;

import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;

public final class VisionSystem implements ICameraFrameUpdateListener {
    private LinkedHashSet<VisionListener> listeners;
    private ICameraConnection connection;
    
    public VisionSystem() {
        listeners = new LinkedHashSet<VisionListener>();
        
        connection = CameraConnectionFactory.getCameraConnection(null);
        
        connection.addCameraFrameListener(this);
    }
    
    @Override
    public void frameAvail(BufferedImage pImage) {
        RobotVisionMsg message = new RobotVisionMsg(pImage);
        
        for(VisionListener listener : listeners) {
            listener.onVisionDataRecieved(message);
        }
    }
    
    public void subscribe(VisionListener listener) {
        listeners.add(listener);
    }
    
    public void unsubscribe(VisionListener listener) {
        listeners.remove(listener);
    }

}
