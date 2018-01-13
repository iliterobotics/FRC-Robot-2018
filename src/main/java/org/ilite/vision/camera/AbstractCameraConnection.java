package org.ilite.vision.camera;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractCameraConnection implements ICameraConnection {

    private final Set<ICameraFrameUpdateListener> mListeners = new CopyOnWriteArraySet<ICameraFrameUpdateListener>();

    @Override
    public void addCameraFrameListener(ICameraFrameUpdateListener pListener) {
        mListeners.add(pListener);
    }

    public void removeCameraFrameListener(ICameraFrameUpdateListener pListener) {
        mListeners.remove(pListener);
    }

    protected void notifyListeners(BufferedImage pImage) {

        for (ICameraFrameUpdateListener aListener : mListeners) {
            try {
                aListener.frameAvail(pImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
