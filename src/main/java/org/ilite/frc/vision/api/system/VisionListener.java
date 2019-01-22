package org.ilite.frc.vision.api.system;

import org.ilite.frc.vision.api.messages.RobotVisionMsg;

public interface VisionListener {
    public void onVisionDataRecieved(RobotVisionMsg message);
}
