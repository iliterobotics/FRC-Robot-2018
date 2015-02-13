package org.ilite.vision.examples;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.IVisionSystem;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystemAPI;
import org.ilite.vision.camera.opencv.ImagePanel;

public class ImageBlender extends JFrame implements VisionListener {

	ImagePanel iP = new ImagePanel();
	private BufferedImage myImg;
    private BufferedImage mFinalImage;
	
	public ImageBlender() throws IOException { 

		setContentPane(iP.getPanel());
		setVisible(true);   
		myImg = VisionSystemAPI.loadImage("images/NumberFour.png");  
	    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

	}

	public static void main(String[] args) throws IOException {
		ImageBlender iB = new ImageBlender();

		IVisionSystem vsi = new VisionSystemAPI().getVisionSystem();

		vsi.subscribe(iB);

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
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		graphics.drawImage(myImg, 0, 0, null);
		graphics.dispose();
		

		
		
		iP.updateImage(mFinalImage); 
		

	}

}
