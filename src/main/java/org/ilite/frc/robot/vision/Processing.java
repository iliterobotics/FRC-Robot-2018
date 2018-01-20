package org.ilite.frc.robot.vision;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.common.config.SystemSettings;
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
	private List<Point> centerList;
	private ArrayList<MatOfPoint> contours;
	private Object imgLock;
	
	private long startTime;
	
	public Processing(VideoSource camera) {
		imgLock = new Object();
		this.camera = camera;
		this.cameraSink = CameraServer.getInstance().getVideo(camera);
	    this.cameraStream = CameraServer.getInstance().putVideo("Tracking", 320, 240);
	    this.currentFrame = new Mat();
	    this.centerList = new ArrayList<>();
	}
	
	@Override
	public void copyPipelineOutputs(GripPipeline pipeline) {
		startTime = System.currentTimeMillis();
		pipeline.findContoursOutput();
		
		double x, y = 0;
		contours = pipeline.filterContoursOutput;
    	Rect target = new Rect();
    	
		if(!contours.isEmpty()) {
			for(MatOfPoint contour : contours) {
				target = Imgproc.boundingRect(contour);
				x = target.x + (target.width / 2);
				y =  target.y + (target.height / 2);
				if(target.width >= SystemSettings.VISION_TWO_CUBE_WIDTH) x -= target.width / 4;
				synchronized(imgLock) {
            		centerList.add(new Point(x, y));
        		}
			}
    	}
		
		cameraSink.grabFrame(currentFrame);
		paintTarget(currentFrame);
		cameraStream.putFrame(currentFrame);
		centerList.clear();
	}
	
	public void paintTarget(Mat frameToPaint) {
		for(Point center : centerList) Imgproc.circle(frameToPaint, center, 15, new Scalar(0, 0, 255));
	}
	
	public Object getImageLock() {
		return imgLock;
	}
	
	public List<Point> getCenters() {
		return centerList;
	}

}
