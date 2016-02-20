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
				CameraConnectionFactory.getCameraConnection(ECameraType.ALIGNMENT_CAMERA.getCameraIP());
		
		//A window to display the camera field
		final ImageWindow aWindow = new ImageWindow(null, "Camera Image");
		
		//A window to display the grayscaled output image after processing. 
		final ImageWindow grayImageWindow  = new ImageWindow(null, "Grayscale Image");
		
		//A window to display the blurred output image after processing.
		final ImageWindow blurredImageWindow = new ImageWindow(null, "Blurred Image");
		
		//A window to display an output image that highlights edges after processing.
		final ImageWindow edgesImageWindow = new ImageWindow(null, "Edges Image");
		
		//Register a listener for frame updates. This will be notified every time 
		//there's a new image available for the camera
		final ImageWindow cWindow = new ImageWindow(null);
		cameraConnection.addCameraFrameListener(new ICameraFrameUpdateListener() {
			
			@Override
			public void frameAvail(BufferedImage pImage) {
				aWindow.updateImage(pImage);
				
				//TODO: Perform pre-processing. Such as blurring. Note that all
				//opencv methods use images stored in Mat, so you will need to 
				//convert: 
				Mat imageAsMat = OpenCVUtils.toMatrix(pImage);
				
				//It's often a good idea to create copies of the outputs. This way.
				//we can hold onto the original image 
				Mat grayMat = new Mat();
				Mat blurredMat = new Mat();
				Mat edgesMat = new Mat();
				Mat cMat = new Mat();
				//Most algorithms will require the image to be gray scale: 
				Imgproc.cvtColor(imageAsMat, grayMat, Imgproc.COLOR_BGR2GRAY);
				
				//Other algorithms will require the image to be blurred: 
				Imgproc.GaussianBlur(imageAsMat, blurredMat, new Size(), 20.0);
				
				//Finally, certain algorithms need to identify edges:
				Imgproc.GaussianBlur(imageAsMat, edgesMat, new Size(), 5.0);
				Imgproc.Canny(edgesMat, edgesMat, 5.0, 30.0);
				
				//Perform some pre-processing. This will use convolution with 
				//a 3x3 kernal matrix. To learn more about convolution, see:
				//http://setosa.io/ev/image-kernels/
				Imgproc.GaussianBlur(grayMat, grayMat, new Size(3,3),0);
				
				//The image window uses a Buffered image, so convert:
				BufferedImage grayImage = OpenCVUtils.toBufferedImage(grayMat);
				grayImageWindow.updateImage(grayImage);
				
				BufferedImage blurredImage = OpenCVUtils.toBufferedImage(blurredMat);
				blurredImageWindow.updateImage(blurredImage);
				
				BufferedImage edgesImage = OpenCVUtils.toBufferedImage(edgesMat);
				edgesImageWindow.updateImage(edgesImage);
				
				Imgproc.cvtColor(imageAsMat, cMat, Imgproc.COLOR_BGR2Luv);
				BufferedImage cImage = OpenCVUtils.toBufferedImage(cMat);
				cWindow.updateImage(cImage);
			}
		});
		
		//Show the windows
		aWindow.show();
		grayImageWindow.show();
		blurredImageWindow.show();
		edgesImageWindow.show();
		cWindow.show();
		//Start the camera:
		cameraConnection.start();
		
	}
	
	
	
	

}
