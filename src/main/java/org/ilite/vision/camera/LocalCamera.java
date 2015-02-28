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

public class LocalCamera extends AbstractCameraConnection {
    

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
            int DEVICE = (int) ECameraConfig.DEVICE.getValue();
            mCamera = new VideoCapture(DEVICE);
            try {
                Thread.sleep(ECameraConfig.INITIAL_CAMERA_DELAY.getValue());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            mCamera.open(DEVICE);

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
            mCamera.read(currentFrame);
            mLastFrame = OpenCVUtils.toBufferedImage(currentFrame);
            notifyListeners(mLastFrame);
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

            mScheduleAtFixedRate = CAMERA_EXEC.scheduleAtFixedRate(mCameraRunnable, CAM_RATE_MILLIS,
                    CAM_RATE_MILLIS, TimeUnit.MILLISECONDS);
        }

    };
}
