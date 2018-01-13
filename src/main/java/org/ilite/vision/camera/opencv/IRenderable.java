package org.ilite.vision.camera.opencv;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Interface for a component that can draw. This is used mainly for overlays in
 * the {@link ImageWindow}
 */
public interface IRenderable {

    public void paint(Graphics pGraphics, BufferedImage pImage);
}
