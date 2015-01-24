package org.ilite.vision.camera.tools.colorblob;

import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.Renderable;

//Based off of: https://github.com/Itseez/opencv/blob/master/samples/android/color-blob-detection/src/org/opencv/samples/colorblobdetect/ColorBlobDetectionActivity.java
public class ColorBlobTrainer implements ICameraFrameUpdateListener{
    
    private ImageWindow mWindow = 
	    new ImageWindow(null);
    private ICameraConnection mCamera; 
    private Renderable renderable;
    
    public ColorBlobTrainer(ICameraConnection pConnection) {
	mCamera = pConnection;
	mCamera.addCameraFrameListener(this); 

    }
    
    public void connectToCamera() {
	mCamera.start();
    }
    
    
    public void show() {
	mWindow.show();
    }
    
    
    public static void createAndShowGUI() {
	OpenCVUtils.init();

	//null IP means connect to webcam
	//Put the IP address to connect to an MPEG-J camera, otherwise null will
	//connect to a local webcam
	String ip = null;
	// ip = "192.168.137.85"

	ICameraConnection aCameraConnection = CameraConnectionFactory.getCameraConnection(ip);
	ColorBlobTrainer aTrainer = new ColorBlobTrainer(aCameraConnection);
	aTrainer.connectToCamera();
	aTrainer.show();
    }

    
    
    public static void main(String[] args) {
	
	SwingUtilities.invokeLater(new Runnable() {
	    
	    @Override
	    public void run() {
		createAndShowGUI();
	    }
	});
    }

    @Override
    public void frameAvail(BufferedImage pImage) {
	mWindow.updateImage(pImage);
	
    }

}
