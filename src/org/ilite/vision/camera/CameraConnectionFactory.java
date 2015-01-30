package org.ilite.vision.camera;

import org.ilite.vision.camera.axis.AxisCameraConnection;

public class CameraConnectionFactory {

    private static ICameraConnection mConnection;

    public static synchronized ICameraConnection getCameraConnection(String pIP) {

        if (mConnection != null) {
        } else if (pIP != null) {
            mConnection = new AxisCameraConnection(pIP);
        } else {
            mConnection = new LocalCamera();
        }

        return mConnection;

    }
    
    public static void destroy() {
        mConnection.destroy();
    }
}
