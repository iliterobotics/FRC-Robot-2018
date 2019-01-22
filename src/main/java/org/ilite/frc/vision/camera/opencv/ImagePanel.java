package org.ilite.frc.vision.camera.opencv;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.ilite.frc.vision.api.messages.RobotVisionMsg;
import org.ilite.frc.vision.api.system.IVisionSystem;
import org.ilite.frc.vision.api.system.VisionListener;

public class ImagePanel implements VisionListener {
    private BufferedImage currentFrame;
    private JPanel panel;
    private IVisionSystem system;
     
    
    public ImagePanel() {
        this(new NullVisionSystem());
    }
    
    public ImagePanel(IVisionSystem pSystem) {
        system = pSystem;
        
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
        panel.repaint();
    }
    
    public void updateImage(BufferedImage pImage) {
        currentFrame = pImage;
        panel.repaint();
    }
    
    public JPanel getPanel() {
		return panel;
	}
}
