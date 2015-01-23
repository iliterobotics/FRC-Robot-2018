package org.ilite.vision.camera.tools.colorblob;

import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.axis.AxisCameraConnection;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;

//Based off of: https://github.com/Itseez/opencv/blob/master/samples/android/color-blob-detection/src/org/opencv/samples/colorblobdetect/ColorBlobDetectionActivity.java
public class ColorBlobTrainer implements ICameraFrameUpdateListener{
    
    private ImageWindow mWindow = 
	    new ImageWindow(null);
    private ICameraConnection mCamera;
    
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
	ColorBlobTrainer aTrainer = new ColorBlobTrainer(new AxisCameraConnection("192.168.137.85"));
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
