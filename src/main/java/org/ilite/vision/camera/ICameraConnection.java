package org.ilite.vision.camera;

public interface ICameraConnection {

    public void addCameraFrameListener(ICameraFrameUpdateListener pListener);

    public void removeCameraFrameListener(ICameraFrameUpdateListener pListener);
    
    /**
     * Method to start and stop the camera feed. 
     * @param pShouldPause
     *  true if the camera should pause, false if it should resume
     */
    public void pauseResume(boolean pShouldPause);

    public void destroy();
    public void start();

}
