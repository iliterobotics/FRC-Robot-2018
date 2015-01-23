package org.ilite.vision.camera.opencv;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageWindow {

    private final ImageIcon mIcon = new ImageIcon();
    private JFrame mFrame;
    private final JLabel mIconLabel;
    public ImageWindow(BufferedImage pImage) {

	mFrame = new JFrame();
	if(pImage == null) {

	} else {
	    mIcon.setImage(pImage);

	}
	mIconLabel = new JLabel(mIcon);
	mFrame.setContentPane(mIconLabel);
	mFrame.pack();
	mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void updateImage(BufferedImage pImage) {
	mIcon.setImage(pImage);
	mIconLabel.repaint();
	
	mFrame.revalidate();
	mFrame.pack();
    }

    public void show() {
	mFrame.setVisible(true);
    }

}
