package org.ilite.vision.main;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.ilite.vision.camera.AxisCameraConnection;
import org.ilite.vision.camera.opencv.OpenCVUtils;

/**
 * Class to test the {@link AxisCameraConnection}. 
 * @author Christopher O'Connell
 *
 */
public class CameraConnectionMain {

    public static void main(String[] args) throws InterruptedException {
	
	OpenCVUtils.init();
	
	String cameraIP = "169.254.36.239";
	AxisCameraConnection aConnection = new AxisCameraConnection(cameraIP);
	aConnection.connect();
	
	Executors.newSingleThreadExecutor().submit(aConnection);

	System.out.println("SLEEP!!!");
	Thread.sleep(5000);
	
	final BufferedImage aGrabImage = aConnection.grabImage();
	System.out.println("HAS IMAGE= " + aGrabImage);
	SwingUtilities.invokeLater(new Runnable() {
	    
	    @Override
	    public void run() {
		OpenCVUtils.showImage(aGrabImage).show();
		
	    }
	});
    }
}
