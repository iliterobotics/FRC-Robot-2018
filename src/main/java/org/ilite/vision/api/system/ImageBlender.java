package org.ilite.vision.api.system;

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
import org.ilite.vision.camera.opencv.ImagePanel;
import org.ilite.vision.camera.opencv.OverlaySlider;
import org.ilite.vision.camera.opencv.OverlaySlider.OverlaySliderListener;
import org.ilite.vision.constants.ECameraType;
import org.ilite.vision.constants.EStateKeys;


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
public class ImageBlender extends JPanel implements VisionListener {
    
    /**
     * The panel that will render the mFinalImage onto this frame. 
     */
	private final ImagePanel mImagePanel = new ImagePanel();
	/**
	 * The image that is to be overlayed on top of the frame.
	 */
	private final BufferedImage mOverlayImage;
	/**
	 * The main content of  this frame
	 */
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
	    super(new BorderLayout());
	    //TODO: Need to save the path of the image to the property file (JSON)
        mOverlayImage = VisionSystemAPI.loadImage((String) EStateKeys.OVERLAY_IMAGE_PATH.getValue()); 
        
        mAlphaValueSlider.subscribe(new OverlaySliderListener() {

            @Override
            public void onSliderChanged(float value) {
                //redraw the image panel so the new alpha can be applied
                mImagePanel.getPanel().repaint();
            }
            
        });
        
        mImagePanel.getPanel().setPreferredSize(new Dimension(800, 600));
        
        add(mImagePanel.getPanel(), BorderLayout.CENTER);
        add(mAlphaValueSlider.getSlider(), BorderLayout.NORTH);
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
	        setPreferredSize(new Dimension(mFinalImage.getWidth(), mFinalImage.getHeight()));
	       
	    } 
	    int screenWidth = frameImage.getWidth(); 
	    int screenHeight = frameImage.getHeight();
	    
	    Graphics2D graphics = mFinalImage.createGraphics();   
	    graphics.fillRect(0, 0, mFinalImage.getWidth(), mFinalImage.getHeight());
	    graphics.drawImage(frameImage, 0, 0,frameImage.getWidth(), frameImage.getHeight(), null);
	    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,mAlphaValueSlider.getValue()));  
	    
	    graphics.drawImage(mOverlayImage, 0, 0,screenWidth,screenHeight, null);
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
    	JFrame frame = new JFrame();  
    	ImageBlender blender = new ImageBlender();  
    
        VisionSystemAPI.getVisionSystem(ECameraType.LOCAL_CAMERA).subscribe(blender);    
        frame.setContentPane(blender);
         
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setVisible(true);  
        frame.pack();
        
    }
}
