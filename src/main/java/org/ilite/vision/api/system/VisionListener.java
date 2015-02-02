package org.ilite.vision.api.system;

import org.ilite.vision.api.messages.RobotVisionMsg;

public interface VisionListener {
    public void onVisionDataRecieved(RobotVisionMsg message);
}
