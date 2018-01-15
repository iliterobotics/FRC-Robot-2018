package org.ilite.frc.robot.vision;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.VisionRunner;

public class Processing implements VisionRunner.Listener<GripPipeline> {

	private VideoSource camera;
	private CvSink cameraSink;
	private CvSource cameraStream;
	
	private Mat currentFrame;
	private Point center;
	private ArrayList<MatOfPoint> contours;
	private Object imgLock;
	
	private long startTime;
	
	public Processing(VideoSource camera) {
		imgLock = new Object();
		this.camera = camera;
		this.cameraSink = CameraServer.getInstance().getVideo(camera);
	    this.cameraStream = CameraServer.getInstance().putVideo("Tracking", 320, 240);
	    this.currentFrame = new Mat();
	}
	
	@Override
	public void copyPipelineOutputs(GripPipeline pipeline) {
		startTime = System.currentTimeMillis();
		contours = pipeline.findContoursOutput();
		
		if (!contours.isEmpty()) {
        	double[] pointArray = new double[2];
    		if(!contours.isEmpty()) {
    			Rect target = Imgproc.boundingRect(contours.get(0));
        			pointArray[0] = target.x + (target.width / 2);
        			pointArray[1] = target.y + (target.height / 2);  
        		}
    		synchronized(imgLock) {
        		center = new Point(pointArray);
    		}
        }
		System.out.printf("X: %s Y: %s\n", center.x, center.y);
		cameraSink.grabFrame(currentFrame);
		paintTarget(currentFrame);
		cameraStream.putFrame(currentFrame);
	}
	
	public void paintTarget(Mat frameToPaint) {
		Imgproc.circle(frameToPaint, center, 10, new Scalar(0, 255, 0));
	}
	
	public Object getImageLock() {
		return imgLock;
	}
	
	public Point getCenter() {
		return center;
	}

}
