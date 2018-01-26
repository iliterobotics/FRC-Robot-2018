package org.ilite.frc.vision.camera.calibration;

import java.awt.image.BufferedImage;

import org.ilite.frc.vision.camera.CameraConnectionFactory;
import org.ilite.frc.vision.camera.ICameraConnection;
import org.ilite.frc.vision.camera.ICameraFrameUpdateListener;
import org.ilite.frc.vision.camera.opencv.ImageWindow;
import org.ilite.frc.vision.camera.opencv.OpenCVUtils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Hello {
	
	public static void main(String[] args) {
		OpenCVUtils.init();
		
		ICameraConnection cameraConnection = CameraConnectionFactory.getCameraConnection(null);
				
		
		final ImageWindow aWindow = new ImageWindow(null);
		cameraConnection.addCameraFrameListener(new ICameraFrameUpdateListener() {
			
			@Override
			public void frameAvail(BufferedImage pImage) {
				
				Mat orig = OpenCVUtils.toMatrix(pImage);
				Mat gray = new Mat();
				
				Imgproc.cvtColor(orig, gray, Imgproc.COLOR_RGB2GRAY);
				aWindow.updateImage(OpenCVUtils.toBufferedImage(gray));
			}
		});
		cameraConnection.start();
		aWindow.show();
	}

}
