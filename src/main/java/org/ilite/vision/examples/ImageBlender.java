package org.ilite.vision.examples;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystemAPI;
import org.ilite.vision.camera.opencv.ImagePanel;
import org.ilite.vision.camera.opencv.OverlaySlider;
import org.ilite.vision.camera.opencv.OverlaySlider.OverlaySliderListener;

/**
 * A proof of concept to overlay one image on top of each other. This will register
 * with the live camera, using the {@link VisionSystemAPI} to get a reference. 
 * When a new frame comes in, this image will blend the mOverlayImage on top of 
 * the frame and then render it to the display. 
 * 
 * There {@link OverlaySlider} is used to adjust the alpha value. Valid alpha 
 * values are from 0.0 to 1.0. Essentially the alpha values are percentages, with 
 * 1.0 representing full opaque and 0.0 being completely invisible. 
 * 
 * This class will put the {@link OverlaySlider} on the top of the frame and 
 * the image panel in the center
 */
public class ImageBlender extends JFrame implements VisionListener {
    
    /**
     * The panel that will render the mFinalImage onto this frame. 
     */
	private final ImagePanel mImagePanel = new ImagePanel();
	/**
	 * The image that is to be overlayed on top of the frame.
	 */
	private final BufferedImage mOverlayImage;
	/**
	 * The main content of this frame
	 */
	private final JPanel mContentPanel = new JPanel(new BorderLayout());
	/**
	 * Container of a {@link JSlider} to adjust the alpha value for the blending
	 */
	private OverlaySlider mAlphaValueSlider= new OverlaySlider();
	/**
	 * The final image that is to be rendered. This will be created once in the 
	 * onVisonDataReceived, once this starts to receive frames from the camera
	 */
    private BufferedImage mFinalImage;
	
    /**
     * Creates and shows this frame
     * @throws IOException
     *  Thrown if there was an issue loading the overlay Image
     */
	public ImageBlender() throws IOException { 
	    
	    //TODO: Need to save the path of the image to the property file (JSON)
        mOverlayImage = VisionSystemAPI.loadImage("images/NumberFour.png"); 
        
        mAlphaValueSlider.subscribe(new OverlaySliderListener() {

            @Override
            public void onSliderChanged(float value) {
                //redraw the image panel so the new alpha can be applied
                mImagePanel.getPanel().repaint();
            }
            
        });
        
        mContentPanel.add(mImagePanel.getPanel(), BorderLayout.CENTER);
        mContentPanel.add(mAlphaValueSlider.getSlider(), BorderLayout.NORTH);
		setContentPane(mContentPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
        setVisible(true);   
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onVisionDataRecieved(RobotVisionMsg message) {
	    BufferedImage frameImage = message.getRawImage();
	    if(mFinalImage == null) {
	        mFinalImage = new BufferedImage(frameImage.getWidth(), frameImage.getHeight(), BufferedImage.TYPE_INT_RGB); 
	        mContentPanel.setPreferredSize(new Dimension(mFinalImage.getWidth(), mFinalImage.getHeight()));
	        pack();
	    }
	    Graphics2D graphics = mFinalImage.createGraphics();   
	    graphics.fillRect(0, 0, mFinalImage.getWidth(), mFinalImage.getHeight());
	    graphics.drawImage(frameImage, 0, 0,frameImage.getWidth(), frameImage.getHeight(), null);
	    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,mAlphaValueSlider.getValue()));
	    graphics.drawImage(mOverlayImage, 0, 0, null);
	    graphics.dispose();

	    mImagePanel.updateImage(mFinalImage); 
	}

	/**
	 * Starts the Image Blender and registers it with the {@link VisionSystemAPI}
	 * @param args
	 *     Not used
	 * @throws IOException
	 *     thrown if the overlay image could not be loaded
	 */
    public static void main(String[] args) throws IOException {
        VisionSystemAPI.getVisionSystem().subscribe(new ImageBlender());
    }
}
