package com.fauge.robotics.towertracker;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.constants.ECameraType;
import org.ilite.vision.data.Configurations;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TowerTracker1885 implements ICameraFrameUpdateListener{
	static {
		OpenCVUtils.init();
	}
	private ImageWindow mWindow = new ImageWindow(null, "Final Image");
	private ImageWindow mThreshWindow = new ImageWindow(null, "Threshold");
	private final ICameraConnection mConnection;
	private int mFrameCounter = 0;
	private Set<ITowerListener> mTowerListeners = new CopyOnWriteArraySet<>();

	public TowerTracker1885(ICameraConnection cameraConnection) {
		cameraConnection.addCameraFrameListener(this);
		mConnection = cameraConnection;
		
	}

	public static void main(String[] args) {
		//Put this in camera connection factory for the axis camera - ECameraType.ALIGNMENT_CAMERA.getCameraIP()
		ICameraConnection cameraConnection = CameraConnectionFactory.getCameraConnection(ECameraType.ALIGNMENT_CAMERA.getCameraIP());
		TowerTracker1885 aTracker = new TowerTracker1885(cameraConnection);
		aTracker.start();
		
	}

	private void start() {
		mWindow.show();
		mThreshWindow.show();
		mConnection.start();
	}

	@Override
	public void frameAvail(BufferedImage pImage) {
		Mat frame = OpenCVUtils.toMatrix(pImage);
		mFrameCounter++;

		System.out.println("FRAME: " + mFrameCounter);
		if(mFrameCounter>=Integer.MAX_VALUE) {
			mConnection.destroy();
		} else {
			processImage(frame);
		}
		
	}
	public static Mat matOriginal = new Mat();
	public static Mat matHSV = new Mat(); 
	public static Mat matThresh= new Mat();
	public static Mat clusters = new Mat(); 
	public static Mat matHeirarchy = new Mat();
//	constants for the color bgr values
	public static final Scalar 
	RED = new Scalar(0, 0, 255),
	BLUE = new Scalar(255, 0, 0),
	GREEN = new Scalar(0, 255, 0),
	BLACK = new Scalar(0,0,0),
	YELLOW = new Scalar(0, 255, 255),
	WHITE = new Scalar(255,255,255);
	
	public static final Scalar LOWER_BOUNDS;
	public static final Scalar UPPER_BOUNDS;
	static{
	    LOWER_BOUNDS = new Scalar(Configurations.getDoubleArray("LOW_COLOR"));
	    UPPER_BOUNDS = new Scalar(Configurations.getDoubleArray("HIGH_COLOR"));
	}
//	Constants for known variables
//	the height to the top of the target in first stronghold is 97 inches	
	public static final int TOP_TARGET_HEIGHT = 97;
//	the physical height of the camera lens
	public static final int TOP_CAMERA_HEIGHT = 32;
	
//	camera details, can usually be found on the datasheets of the camera
	public static final double VERTICAL_FOV  = 51;
	public static final double HORIZONTAL_FOV  = 67;
	public static final double CAMERA_ANGLE = 10;
	public static String alignment;
	public  void processImage(Mat matOriginal){
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		double x,y,targetX,targetY,distance,azimuth;
//		frame counter
			contours.clear();
//			captures from a static file for testing
//			matOriginal = Imgcodecs.imread("someFile.png");
			
			Imgproc.cvtColor(matOriginal,matHSV,Imgproc.COLOR_BGR2HSV_FULL);			
			Core.inRange(matHSV, LOWER_BOUNDS, UPPER_BOUNDS, matThresh);
			mThreshWindow.updateImage(OpenCVUtils.toBufferedImage(matThresh));
			Imgproc.findContours(matThresh, contours, matHeirarchy, Imgproc.RETR_EXTERNAL, 
					Imgproc.CHAIN_APPROX_SIMPLE);
//			make sure the contours that are detected are at least 20x20 
//			pixels with an area of 400 and an aspect ration greater then 1
			for (Iterator<MatOfPoint> iterator = contours.iterator(); iterator.hasNext();) {
				MatOfPoint matOfPoint = (MatOfPoint) iterator.next();
				Rect rec = Imgproc.boundingRect(matOfPoint);
					if(rec.height < 25 || rec.width < 25){
						iterator.remove();
					continue;
					}
					float aspect = (float)rec.width/(float)rec.height;
					if(aspect < 1.0)
						iterator.remove();
				}
				for(MatOfPoint mop : contours){
					Rect rec = Imgproc.boundingRect(mop);
					
					Core.rectangle(matOriginal, rec.br(), rec.tl(), BLACK);
			}
//			if there is only 1 target, then we have found the target we want
			if(contours.size() == 1){
				Rect rec = Imgproc.boundingRect(contours.get(0));
				
				Rectangle contourRect = new Rectangle(rec.x, rec.y, rec.width, rec.height);
				Rectangle leftHalf = new Rectangle(0, 0, matOriginal.width()/2, matOriginal.height());
				Rectangle rightHalf= new Rectangle(matOriginal.width()/2, 0, matOriginal.width()/2, matOriginal.height());
				Double leftContourArea = leftHalf.intersection(contourRect).getWidth() * leftHalf.intersection(contourRect).getHeight();
				Double rightContourArea = rightHalf.intersection(contourRect).getWidth() * rightHalf.intersection(contourRect).getHeight();
				
				if(leftContourArea.compareTo(rightContourArea) > 0){
					alignment = ECameraAlignment.LEFT.getAlignment();
				} else if(leftContourArea.compareTo(rightContourArea) < 0){
					alignment = ECameraAlignment.RIGHT.getAlignment();
				} else if(Math.abs(leftContourArea - rightContourArea) <= 10){
					alignment = ECameraAlignment.CENTER.getAlignment();
				}
				
//				"fun" math brought to you by miss daisy (team 341)!
				y = rec.br().y + rec.height / 2;
				y= -((2 * (y / matOriginal.height())) - 1);
				distance = (TOP_TARGET_HEIGHT - TOP_CAMERA_HEIGHT) / 
						Math.tan((y * VERTICAL_FOV / 2.0 + CAMERA_ANGLE) * Math.PI / 180);
//				angle to target...would not rely on this
				targetX = rec.tl().x + rec.width / 2;
				targetX = (2 * (targetX / matOriginal.width())) - 1;
				azimuth = TowerTracker.normalize360(targetX*HORIZONTAL_FOV /2.0 + 0);
//				drawing info on target
				Point center = new Point(rec.br().x-rec.width / 2 - 15,rec.br().y - rec.height / 2);
				Point centerw = new Point(rec.br().x-rec.width / 2 - 15,rec.br().y - rec.height / 2 - 20);
				Core.putText(matOriginal, ""+(int)distance, center, Core.FONT_HERSHEY_PLAIN, 1, BLACK);
				Core.putText(matOriginal, ""+(int)azimuth, centerw, Core.FONT_HERSHEY_PLAIN, 1, BLACK);
				for (ITowerListener towers2 : mTowerListeners) {
	                towers2.fire(new TowerMessage(distance,azimuth,alignment));
	            }
			}
			Core.putText(matOriginal, "Frame: " +mFrameCounter, new Point(100, 100), Core.FONT_HERSHEY_PLAIN, 1, YELLOW);
//			output an image for debugging
//			Highgui.imwrite("output-"+mFrameCounter+".png", matOriginal);
			
			mWindow.updateImage(OpenCVUtils.toBufferedImage(matOriginal));

	}

	public void addTowerListener(ITowerListener t){
	    mTowerListeners.add(t);
	}
}
