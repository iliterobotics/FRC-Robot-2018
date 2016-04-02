package com.fauge.robotics.towertracker;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	private static final int BOUNDING_RECT_SIZE = 18;
	static {
		OpenCVUtils.init();
	}
	private ImageWindow mWindow = new ImageWindow(null, "Final Image");
	private ImageWindow mThreshWindow = new ImageWindow(null, "Threshold");
	private final ICameraConnection mConnection;
	private int mFrameCounter = 0;
	private Set<ITowerListener> mTowerListeners = new CopyOnWriteArraySet<>();
	private static JToggleButton swapButton = new JToggleButton("SWAP");
	private static boolean shouldSwap = false;
	public TowerTracker1885(ICameraConnection cameraConnection) {
		cameraConnection.addCameraFrameListener(this);
		mConnection = cameraConnection;
		test = new ValueWindow();
		addTowerListener(test);
		
		
	}

	public static void main(String[] args) {
		//Put this in camera connection factory for the axis camera - ECameraType.ALIGNMENT_CAMERA.getCameraIP()
		ICameraConnection cameraConnection = CameraConnectionFactory.getCameraConnection(ECameraType.FIELD_CAMERA.getCameraIP());
		TowerTracker1885 aTracker = new TowerTracker1885(cameraConnection);
		aTracker.start();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			

            @Override
			public void run() {
				JFrame aFrame = new JFrame("FOV");
				final JSlider aSLider = new JSlider(0, 360,(int)VERTICAL_FOV);
				final JLabel valueLabel = new JLabel("Value= " + VERTICAL_FOV);
				aSLider.addChangeListener(new  ChangeListener() {
					
					@Override
					public void stateChanged(ChangeEvent e) {
						VERTICAL_FOV = aSLider.getValue();
						valueLabel.setText("Value= " + VERTICAL_FOV);
					}
				});
				JPanel contentPanel = new JPanel();
				contentPanel.add(aSLider);
				contentPanel.add(valueLabel);
				

				contentPanel.add(swapButton);
				aFrame.setContentPane(contentPanel);
				aFrame.pack();
				aFrame.setVisible(true);
				aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			}
		});
		
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
    /**the height to the top of the target in first stronghold is 97 inches**/
	public static double TOP_TARGET_HEIGHT_IN_INCHES = 97;
	/**the physical height of the camera lens**/
	public static final double TOP_CAMERA_HEIGHT_IN_INCHES = 12.75;
	/**
	 * Horizontal field of view, in degrees. This was found from the axis camera
	 * data sheet
	 */
	public static final double HORIZONTAL_FOV  = 67;
	/**https://wpilib.screenstepslive.com/s/3120/m/8731/l/90361-identifying-and-processing-the-targets**/
	public static double VERTICAL_FOV  = 49;
	public static final double CAMERA_ANGLE = 30;
	public static String alignmentX;
	public static int multiplierX;
	public static String alignmentY;
    public static int multiplierY;
	
	public static double pixelPerInch;
	/**
	 * The width of the goal in inches.
	 */
	public static final double GOAL_WIDTH = 20.0;
	public static double offSet;
    private ValueWindow test;
	
	
	
	public  void processImage(Mat matOriginal){
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		double x,y,targetX,targetY,distance,azimuth;
//		frame counter
			contours.clear();
//			captures from a static file for testing
//			matOriginal = Imgcodecs.imread("someFile.png");
			
			if(swapButton.isSelected()) {
			    Core.flip(matOriginal, matOriginal, 0);
			}
			
			Imgproc.cvtColor(matOriginal,matHSV,Imgproc.COLOR_BGR2HSV_FULL);			
			Core.inRange(matHSV, LOWER_BOUNDS, UPPER_BOUNDS, matThresh);
			Core.rectangle(matThresh, new Point(0,0),new Point(matThresh.width(), matThresh.height()/4), BLACK);
			
			mThreshWindow.updateImage(OpenCVUtils.toBufferedImage(matThresh));
			Imgproc.findContours(matThresh, contours, matHeirarchy, Imgproc.RETR_EXTERNAL, 
					Imgproc.CHAIN_APPROX_SIMPLE);
//			make sure the contours that are detected are at least 20x20 
//			pixels with an area of 400 and an aspect ration greater then 1
			System.out.println("Found contours: " + contours.size());
			List<Rect>widthToSmall = new ArrayList<>();
			List<Rect>heightToSmall  = new ArrayList<>();
			List<Rect>bothToSmall = new ArrayList<>();
			List<Rect>aspectRatioToSmall = new ArrayList<>();
			for (Iterator<MatOfPoint> iterator = contours.iterator(); iterator.hasNext();) {
				MatOfPoint matOfPoint = (MatOfPoint) iterator.next();
				Rect rec = Imgproc.boundingRect(matOfPoint);
					if(rec.height < BOUNDING_RECT_SIZE || rec.width < BOUNDING_RECT_SIZE){
						
						if(rec.width<BOUNDING_RECT_SIZE) {
							
							if(rec.height < BOUNDING_RECT_SIZE) {
								bothToSmall.add(rec);
							} else {
								widthToSmall.add(rec);
							}
						} else {
							heightToSmall.add(rec);
						}
						
						
						iterator.remove();
					continue;
					}
					float aspect = (float)rec.width/(float)rec.height;
					if(aspect < 1.0) {
						aspectRatioToSmall.add(rec);
						iterator.remove();
					}
				}
				for(MatOfPoint mop : contours){
					Rect rec = Imgproc.boundingRect(mop);
					
					Core.rectangle(matOriginal, rec.br(), rec.tl(), BLACK);
			}
			drawErrors(matOriginal, widthToSmall, heightToSmall, bothToSmall, aspectRatioToSmall);
//			if there is only 1 target, then we have found the target we want
				System.out.println("Found contours: " + contours.size());
				if(contours.size() == 1){
				Rect rec = Imgproc.boundingRect(contours.get(0));
				
				Rectangle contourRect = new Rectangle(rec.x, rec.y, rec.width, rec.height);
				Rectangle leftHalf = new Rectangle(0, 0, matOriginal.width()/2, matOriginal.height());
				Rectangle rightHalf= new Rectangle(matOriginal.width()/2, 0, matOriginal.width()/2, matOriginal.height());
				Rectangle topHalf = new Rectangle(0, 0, matOriginal.width(), matOriginal.height()/2);
				Rectangle bottomHalf = new Rectangle(0, matOriginal.height()/2, matOriginal.width(), matOriginal.height()/2);
				Double leftContourArea = leftHalf.intersection(contourRect).getWidth() * leftHalf.intersection(contourRect).getHeight();
				Double rightContourArea = rightHalf.intersection(contourRect).getWidth() * rightHalf.intersection(contourRect).getHeight();
				Double topContourArea = topHalf.intersection(contourRect).getHeight() * bottomHalf.intersection(contourRect).getWidth();
				Double bottomContourArea = bottomHalf.intersection(contourRect).getHeight() * topHalf.intersection(contourRect).getWidth();
				
				pixelPerInch = rec.width / GOAL_WIDTH;
				offSet = (Configurations.getIntValue("CAMERA_X_OFFSET_INCHES") * pixelPerInch);
				
				this.updateValueWindow(pixelPerInch, offSet, "(" + rec.x + ", " +rec.y + ")", rec.width, rec.height);
				
				
				//Left and Right Rectangles
				if(leftContourArea.compareTo(rightContourArea) > 0){
					alignmentX = ECameraAlignment.LEFT.getAlignment();
					multiplierX = ECameraAlignment.LEFT.getMultiplier();
				} else if(leftContourArea.compareTo(rightContourArea) < 0){
					alignmentX = ECameraAlignment.RIGHT.getAlignment();
					multiplierX = ECameraAlignment.RIGHT.getMultiplier();
				} else if(Math.abs(leftContourArea - rightContourArea) <= 10){
					alignmentX = ECameraAlignment.CENTER.getAlignment();
					multiplierX = ECameraAlignment.CENTER.getMultiplier();
				}

				
				//Top and Bottom Rectangles
				if(topContourArea.compareTo(bottomContourArea) > 0){
				    alignmentY = ECameraAlignment.TOP.getAlignment();
				    multiplierY = ECameraAlignment.TOP.getMultiplier();
				} else if (topContourArea.compareTo(bottomContourArea) <0){
				    alignmentY = ECameraAlignment.BOTTOM.getAlignment();
				    multiplierY = ECameraAlignment.BOTTOM.getMultiplier();
				} else if (Math.abs(topContourArea - bottomContourArea) <=10){
				    alignmentY = ECameraAlignment.CENTER.getAlignment();
				    multiplierY = ECameraAlignment.CENTER.getMultiplier();
				}
				
//				"fun" math brought to you by miss daisy (team 341)!
				y = rec.br().y + rec.height / 2;
				y= -((2 * (y / matOriginal.height())) - 1);
				
				
				System.out.println("CHRIS: y= " + " tan= " + y*VERTICAL_FOV/2.0 + CAMERA_ANGLE);
				distance = (TOP_TARGET_HEIGHT_IN_INCHES - TOP_CAMERA_HEIGHT_IN_INCHES) / 
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
	                towers2.fire(new TowerMessage(distance,azimuth,alignmentX,alignmentY,OpenCVUtils.toBufferedImage(matOriginal), Configurations.getIntValue("CAMERA_X_OFFSET_INCHES")));
	            }
			}
				else{
					for (ITowerListener towers2 : mTowerListeners) {
		                towers2.fire(new TowerMessage(0,0,alignmentX,alignmentY,OpenCVUtils.toBufferedImage(matOriginal), Configurations.getIntValue("CAMERA_X_OFFSET_INCHES")));
		            }

				}
			Core.putText(matOriginal, "Frame: " +mFrameCounter, new Point(100, 100), Core.FONT_HERSHEY_PLAIN, 1, YELLOW);
//			output an image for debugging
//			Highgui.imwrite("output-"+mFrameCounter+".png", matOriginal);
			Point topCenter = new Point(matOriginal.width() / 2, 0);
			Point bottomCenter = new Point(matOriginal.width() / 2, matOriginal.height());
			Core.line(matOriginal, topCenter, bottomCenter, BLACK);
			mWindow.updateImage(OpenCVUtils.toBufferedImage(matOriginal));
	}
	
	public void updateValueWindow(double pixelPerInch, double OffSet, String rectTopLeft, int rectWidth, int rectHeight){
	    test.updateValue(pixelPerInch, OffSet, rectTopLeft, rectWidth, rectHeight);
	    
	}

	private void drawErrors(Mat matOriginal2, List<Rect> widthToSmall, List<Rect> heightToSmall, List<Rect> bothToSmall,
			List<Rect> aspectRatioToSmall) {
		
		for(Rect toSmall : widthToSmall) {
			Core.rectangle(matOriginal2, toSmall.br(), toSmall.tl(), RED);
		}
		
		for(Rect toHigh : heightToSmall) {
			Core.rectangle(matOriginal2, toHigh.br(), toHigh.tl(), YELLOW);
			Core.putText(matOriginal2, "Height: " +toHigh.height, toHigh.tl(), Core.FONT_HERSHEY_PLAIN, 1, YELLOW);
		}
		for(Rect both : bothToSmall) {
			Core.rectangle(matOriginal2, both.br(), both.tl(), WHITE);
		}
		for(Rect aspect : aspectRatioToSmall) {
			Core.rectangle(matOriginal2, aspect.br(), aspect.tl(),BLUE);
		}
		
		
	}
	
	public void addTowerListener(ITowerListener t){
	    mTowerListeners.add(t);
	}
}
