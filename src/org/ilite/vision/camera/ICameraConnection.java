package org.ilite.vision.camera;

public interface ICameraConnection {

    public void addCameraFrameListener(ICameraFrameUpdateListener pListener);
    public void removeCameraFrameListener(ICameraFrameUpdateListener pListener);

    public void start();

}
