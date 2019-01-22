package org.ilite.frc.vision.camera;

import java.awt.image.BufferedImage;

public interface ICameraFrameUpdateListener {

    public void frameAvail(BufferedImage pImage);
}
