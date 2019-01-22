package org.ilite.frc.vision.api.system;

import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ilite.frc.vision.api.messages.RobotVisionMsg;
import org.ilite.frc.vision.camera.CameraConnectionFactory;
import org.ilite.frc.vision.camera.ICameraConnection;
import org.ilite.frc.vision.camera.ICameraFrameUpdateListener;
import org.ilite.frc.vision.camera.opencv.OpenCVUtils;

final class VisionSystem implements ICameraFrameUpdateListener, IVisionSystem {
    private LinkedHashSet<VisionListener> listeners;
    private ICameraConnection connection;
    /**
     * Flag indicating whether this vision system has been initializied. 
     * This is used to ensure that multiple calls to start do nothing
     */
    private final AtomicBoolean mIsInit = new AtomicBoolean(false);
    private static final ExecutorService sService = Executors.newCachedThreadPool(new ThreadFactory() {
        
        @Override
        public Thread newThread(Runnable pR) {
            return new Thread(pR, "Vision System Connection Thread");
        }
    });
    
    protected VisionSystem(String pIP) {
        
        OpenCVUtils.init();
        listeners = new LinkedHashSet<VisionListener>();
        
        connection = CameraConnectionFactory.getCameraConnection(pIP);
        
        connection.addCameraFrameListener(this);

    }
    
    @Override
    public void frameAvail(BufferedImage pImage) {
        RobotVisionMsg message = new RobotVisionMsg(pImage);
        
        for(VisionListener listener : listeners) {
            listener.onVisionDataRecieved(message);
        }
    }
    
    @Override
    public void start() {
    	boolean isInit = mIsInit.getAndSet(true);
    	
    	if(!isInit) {
        sService.submit(new Runnable() {
            
            @Override
            public void run() {
                connection.start();     
            }
        });
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
