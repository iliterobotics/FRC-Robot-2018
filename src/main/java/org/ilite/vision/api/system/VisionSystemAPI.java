package org.ilite.vision.api.system;

public class VisionSystemAPI {
    private static final String CAMERA_IP=System.getProperty("CAMERA_IP");
    private static class INSTANCE_HOLDER {
        private static final IVisionSystem sSystem = new VisionSystem(CAMERA_IP);
    }
    
    public static IVisionSystem getVisionSystem() {
        System.out.println("IP:" + CAMERA_IP);
        return INSTANCE_HOLDER.sSystem;
    }

}
