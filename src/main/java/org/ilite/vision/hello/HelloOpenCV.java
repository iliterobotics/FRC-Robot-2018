package org.ilite.vision.hello;

import java.awt.image.BufferedImage;

import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.constants.ECameraType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class HelloOpenCV {
	
	public static void main(String[] args) {
		//The opencv library must be added to the path. The options are to add 
		//a VM parameter to the run configuration: -Djava.library.path=libraries/opencv/build/java/x64
		
		//This must be called before using opencv
		OpenCVUtils.init();
		
		//Get a reference to the camera. This takes an IP address to the camera. If
		//null, the this will just connect to the local camera, i.e. the webcamera 
		//of the computer
		ICameraConnection cameraConnection = 
				CameraConnectionFactory.getCameraConnection(ECameraType.LOCAL_CAMERA.getCameraIP());
		
		//A window to display the camera field
		final ImageWindow aWindow = new ImageWindow(null, "Camera Image");
		
		//A window to display the final output image after processing. 
		final ImageWindow finalImageWindow  = new ImageWindow(null, "Final Image");
		//Register a listener for frame updates. This will be notified everytime 
		//there's a new image available for the camera
		cameraConnection.addCameraFrameListener(new ICameraFrameUpdateListener() {
			
			@Override
			public void frameAvail(BufferedImage pImage) {
				aWindow.updateImage(pImage);
				
				//TODO: Pefrom pre-processing. Such as bluring. Note that all
				//opencv methods use images stored in Mat, so you will need to 
				//convert: 
				Mat imageAsMat = OpenCVUtils.toMatrix(pImage);
				
				//It's often a good idea to create a copy of the output. This way.
				//we can hold onto the original image 
				Mat grayImage = new Mat();
				//Most algorithms will require the image to be gray scale: 
				Imgproc.cvtColor(imageAsMat, grayImage, Imgproc.COLOR_BGR2GRAY);
				
				//Perform some pre-processing. This will use convolution with 
				//a 3x3 kernal matrix. To learn more about convolution, see:
				//http://setosa.io/ev/image-kernels/
				Imgproc.GaussianBlur(grayImage, grayImage, new Size(3,3),0);
				//The image window uses a Buffered image, so convert:
				BufferedImage finalImage = OpenCVUtils.toBufferedImage(grayImage);
				finalImageWindow.updateImage(finalImage);
				
			}
		});
		
		//Show the windows
		aWindow.show();
		finalImageWindow.show();
		//Start the camera:
		cameraConnection.start();
		
	}

}
