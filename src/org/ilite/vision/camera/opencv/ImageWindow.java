package org.ilite.vision.camera.opencv;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageWindow {

    private final ImageIcon mIcon = new ImageIcon();
    private JFrame mFrame;
    public ImageWindow(BufferedImage pImage) {
	mIcon.setImage(pImage);
	mFrame = new JFrame();
	mFrame.setContentPane(new JLabel(mIcon));
	mFrame.pack();
	mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
    }
    
    public void updateImage(BufferedImage pImage) {
	mIcon.setImage(pImage);
	mFrame.revalidate();
	mFrame.pack();
    }
    
    public void show() {
	mFrame.setVisible(true);
    }
    
}
