package org.ilite.vision.camera.opencv;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystem;

public class ImagePanel implements VisionListener {
    private BufferedImage currentFrame;
    private VisionSystem system;
    private JPanel panel;
    
    public ImagePanel() {
        system = new VisionSystem();
        
        system.subscribe(this);
        
        panel = new JPanel() {
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                if (currentFrame != null) {
                    g.drawImage(currentFrame, 0, 0, getWidth(), getHeight(), null);
                }
                
            }
        };
    }

    @Override
    public void onVisionDataRecieved(RobotVisionMsg message) {
        currentFrame = message.getRawImage();
    }
}
