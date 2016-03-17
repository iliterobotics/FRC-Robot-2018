package org.ilite.vision.camera;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.constants.ECameraConfig;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class Camera extends AbstractCameraConnection {

    private String cameraIP;
    
    /**
     * Default Constructor that sets cameraIP to null and uses local camera
     */
    
    public Camera(){
        cameraIP = null;
    }
    /**
     * Constructor that takes in a camera's IP address
     * @param p_CameraIP
     */
    
    public Camera(String p_CameraIP){
        
        cameraIP = p_CameraIP;
        
    }

    /**
     * The last buffered image
     */
    private BufferedImage mLastFrame;

    /**
     * The rate at which to pull frames from the camera, in milliseconds
     */
    private static final long CAM_RATE_MILLIS = ECameraConfig.CAM_RATE_MILLIS.getValue();

    /**
     * Executor service to start a timer to start pulling frames of the camera
     * and then notify listeners
     */
    private static final ScheduledExecutorService CAMERA_EXEC = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactory() {

                @Override
                public Thread newThread(Runnable pR) {
                    return new Thread(pR, "Local Camera Thread");
                }
            });

    private VideoCapture mCamera;

    private ScheduledFuture<?> mScheduleAtFixedRate;

    @Override
    public void start() {

        if (mCamera == null) {
            
            if(cameraIP == null){
                int DEVICE = (int) ECameraConfig.DEVICE.getValue();
                mCamera = new VideoCapture(DEVICE);
                try {
                    Thread.sleep(ECameraConfig.INITIAL_CAMERA_DELAY.getValue());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                mCamera.open(DEVICE);
            }
            else{
                mCamera = new VideoCapture();
                cameraIP = "http://"+cameraIP+"/mjpg/video.mjpg";
                try {
					Thread.sleep(ECameraConfig.INITIAL_CAMERA_DELAY.getValue());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                mCamera.open(cameraIP);
            }

            if (!mCamera.isOpened()) {
                throw new IllegalStateException("Unable to start the camera");
            }

        }
        
        pauseResume(false);

    }

    /**
     * Timer task that will grab a frame and then notify listeners
     */
    private final Runnable mCameraRunnable = new Runnable() {


        @Override
        public void run() {
            Mat currentFrame = new Mat();
            long start = 0;
            long end = 0;
            do {
             start = System.currentTimeMillis();
            mCamera.read(currentFrame);
             end = System.currentTimeMillis();
            mLastFrame = OpenCVUtils.toBufferedImage(currentFrame);
            } while(end - start < 10);
            notifyListeners(mLastFrame);
            long updateRate = (cameraIP == null)?CAM_RATE_MILLIS : 5;
            mScheduleAtFixedRate = CAMERA_EXEC.schedule(mCameraRunnable, updateRate, TimeUnit.MILLISECONDS);
        }
    };
    @Override
    public void destroy() {
        CAMERA_EXEC.shutdown();
        mCamera.release();
    }
    public void pauseResume(boolean pShouldPause) {

        if(pShouldPause) {
            if(mScheduleAtFixedRate != null) {
                mScheduleAtFixedRate.cancel(true);
                mScheduleAtFixedRate  = null;
            }
            
            notifyListeners(mLastFrame);
            

        } else if(mScheduleAtFixedRate == null){
            
            long updateRate = (cameraIP == null)?CAM_RATE_MILLIS : 5;
//            mScheduleAtFixedRate = CAMERA_EXEC.scheduleAtFixedRate(mCameraRunnable, updateRate,
//                    updateRate, TimeUnit.MILLISECONDS);
            mScheduleAtFixedRate = CAMERA_EXEC.schedule(mCameraRunnable, updateRate, TimeUnit.MILLISECONDS);
        }

    };
}
