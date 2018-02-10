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
	private List<Target> targetList;
	private ArrayList<MatOfPoint> contours;
	private Object imgLock;
	
	private long startTime;
	
	public Processing(VideoSource camera) {
		imgLock = new Object();
		this.camera = camera;
		this.cameraSink = CameraServer.getInstance().getVideo(camera);
    this.cameraStream = CameraServer.getInstance().putVideo("Tracking", 320, 240);
    this.currentFrame = new Mat();
    
    this.targetList = new ArrayList<>();
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
				if(target.width >= SystemSettings.VISION_TWO_CUBE_WIDTH) target.width /= 4;
				synchronized(imgLock) {
				  targetList.add(calculateTarget(target));
        }
			}
    }
		
		cameraSink.grabFrame(currentFrame);
		paintTarget(currentFrame);
		cameraStream.putFrame(currentFrame);
		
		targetList.clear();
	}
	
	public void paintTarget(Mat frameToPaint) {
		for(Target target : targetList) Imgproc.circle(frameToPaint, new Point(target.centerX, target.centerY), 15, new Scalar(0, 0, 255));
	}
	
	public Object getImageLock() {
		return imgLock;
	}
	
	public Target calculateTarget(Rect rect) {
	  double centerX = rect.x + (rect.width / 2);
	  double centerY = rect.y + (rect.height / 2);
	  double angle = (SystemSettings.VISION_DEGREES_PER_PIXEL_X * centerX) - SystemSettings.VISION_CAMERA_DEGREES_CENTER_X;
	  // TODO: Add distance calculation
	  return new Target(centerX, centerY, 0, 0, angle);
	}
	
	public List<Target> getTargets() {
	  return targetList;
	}

}
