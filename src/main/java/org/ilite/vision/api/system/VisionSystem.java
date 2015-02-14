package org.ilite.vision.api.system;

import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.OpenCVUtils;

final class VisionSystem implements ICameraFrameUpdateListener, IVisionSystem {
    private LinkedHashSet<VisionListener> listeners;
    private ICameraConnection connection;
    
    protected VisionSystem(String pIP) {
        
        OpenCVUtils.init();
        listeners = new LinkedHashSet<VisionListener>();
        
        connection = CameraConnectionFactory.getCameraConnection(pIP);
        
        connection.addCameraFrameListener(this);
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                connection.start();
            }
        }).start();
    }
    
    @Override
    public void frameAvail(BufferedImage pImage) {
        RobotVisionMsg message = new RobotVisionMsg(pImage);
        
        for(VisionListener listener : listeners) {
            listener.onVisionDataRecieved(message);
        }
    }
    
    /* (non-Javadoc)
     * @see org.ilite.vision.api.system.IVisionSystem#subscribe(org.ilite.vision.api.system.VisionListener)
     */
    @Override
    public void subscribe(VisionListener listener) {
        listeners.add(listener);
    }
    
    /* (non-Javadoc)
     * @see org.ilite.vision.api.system.IVisionSystem#unsubscribe(org.ilite.vision.api.system.VisionListener)
     */
    @Override
    public void unsubscribe(VisionListener listener) {
        listeners.remove(listener);
    }

}
