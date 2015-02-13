package org.ilite.vision.examples;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystemAPI;
import org.ilite.vision.camera.opencv.ImagePanel;
import org.ilite.vision.camera.opencv.OverlaySlider;
import org.ilite.vision.camera.opencv.OverlaySlider.OverlaySliderListener;

public class ImageBlender extends JFrame implements VisionListener {
	private ImagePanel ip;
	private BufferedImage myImg;
	private JPanel panel;
	private OverlaySlider slider;
	private float sliderValue;
    private BufferedImage mFinalImage;
	
	public ImageBlender() throws IOException { 
        myImg = VisionSystemAPI.loadImage("images/NumberFour.png");  
        slider = new OverlaySlider();
        ip = new ImagePanel();
        
        slider.subscribe(new OverlaySliderListener() {

            @Override
            public void onSliderChanged(float value) {
                sliderValue = value;
                ip.getPanel().repaint();
            }
            
        });
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(ip.getPanel(), BorderLayout.CENTER);
        panel.add(slider.getSlider(), BorderLayout.NORTH);
        
		setContentPane(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
        setVisible(true);   
	}

	@Override
	public void onVisionDataRecieved(RobotVisionMsg message) {
		BufferedImage frameImage = message.getRawImage();
		if(mFinalImage == null) {
		mFinalImage = new BufferedImage(frameImage.getWidth(), frameImage.getHeight(), BufferedImage.TYPE_INT_RGB); 
		}
		Graphics2D graphics = mFinalImage.createGraphics();   
		graphics.fillRect(0, 0, mFinalImage.getWidth(), mFinalImage.getHeight());
		graphics.drawImage(frameImage, 0, 0,frameImage.getWidth(), frameImage.getHeight(), null);
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sliderValue));
		graphics.drawImage(myImg, 0, 0, null);
		graphics.dispose();

		ip.updateImage(mFinalImage); 
	}

    public static void main(String[] args) throws IOException {
        VisionSystemAPI.getVisionSystem().subscribe(new ImageBlender());
    }
}
