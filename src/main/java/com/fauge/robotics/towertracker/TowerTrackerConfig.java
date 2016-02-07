package com.fauge.robotics.towertracker;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.Renderable;
import org.ilite.vision.camera.opencv.renderables.ObjectDetectorRenderable;
import org.ilite.vision.constants.ECameraType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class TowerTrackerConfig implements ICameraFrameUpdateListener{
	
	enum COLOR_TYPES {
		HUE(0,188),
		SAT(0,255),
		VALUE(0,255);
		
		private int mMin;
		private int mMax;
		
		public int getmMax() {
			return mMax;
		}
		
		public int getmMin() {
			return mMin;
		}

		private COLOR_TYPES(int pMin, int pMax) {
			mMin = pMin;
			mMax = pMax;
		}
	}
	private final Map<COLOR_TYPES, JSlider>mLowValues = new EnumMap<>(COLOR_TYPES.class);
	private final Map<COLOR_TYPES, JSlider>mHighValues = new EnumMap<>(COLOR_TYPES.class);
	private final ImageWindow mWindow = new ImageWindow(null, "Original");
	private final ImageWindow mOutput = new ImageWindow(null,"Output");
	private final JFrame mControlFrame = new JFrame("Control");
	private final ICameraConnection mCameraConnection;
	
	private final Mat matHSV = new Mat();
	private final Mat matThresh = new Mat();
	private ObjectDetectorRenderable mObjectDetectorRenderable;
	private Renderable renderable;
	
	public TowerTrackerConfig(ICameraConnection cameraConnection) {
		mCameraConnection = cameraConnection;
		mCameraConnection.addCameraFrameListener(this);
		
		JPanel configPanel = new JPanel(new GridLayout(3, 1));
		for(COLOR_TYPES aType : COLOR_TYPES.values()) {
			JPanel aPanel = new JPanel();
			aPanel.add(buildSliderPanel(aType, "LOW", mLowValues));
			aPanel.add(buildSliderPanel(aType, "HIGH", mHighValues));
			
			configPanel.add(aPanel);
			
		}
		mControlFrame.setContentPane(configPanel);
		mControlFrame.pack();
		renderable = new Renderable();
        mWindow.addRenderable(renderable);
		mObjectDetectorRenderable = new ObjectDetectorRenderable(mWindow, true);
		mWindow.addRenderable(mObjectDetectorRenderable);
		mWindow.getMouseRenderable().addSelectionListener(
                mObjectDetectorRenderable);
		
	}
	
	private static JPanel buildSliderPanel(COLOR_TYPES pType,String pLowHigh, Map<COLOR_TYPES, JSlider>pLowMap) {
		JPanel aPanel = new JPanel();
		aPanel.add(new JLabel(pType.toString() +" "+  pLowHigh));
		final JSlider aSlider = new JSlider(pType.getmMin(), pType.getmMax(), 0);
		final JLabel outputLabel = new JLabel(" 0 ");
		aSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				outputLabel.setText(Integer.toString(aSlider.getValue()));
			}
		});
		pLowMap.put(pType, aSlider);
		
		aPanel.add(aSlider);
		aPanel.add(outputLabel);
		
		return aPanel;
	}

	public static void createAndShowGUI() {
		ICameraConnection cameraConnection = CameraConnectionFactory.getCameraConnection(null);
		TowerTrackerConfig aconfig = new TowerTrackerConfig(cameraConnection);
		aconfig.start();
		
	}
	
	public void start() {
		mWindow.show();
		mOutput.show();
		mControlFrame.setVisible(true);
		mCameraConnection.start();
	}
	
	public static void main(String[] args) {
		OpenCVUtils.init();
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
		mObjectDetectorRenderable.frameAvail(pImage);
		Mat matOriginal = OpenCVUtils.toMatrix(pImage);
		
		Mat gaussianImage = new Mat();
		Imgproc.GaussianBlur(matOriginal, gaussianImage, new Size(5,5), 1);
//		Imgproc.pyrDown(matOriginal, aPyrDownMat);
//        Imgproc.pyrDown(aPyrDownMat, aPyrDownMat);
		
		Imgproc.cvtColor(matOriginal,matHSV,Imgproc.COLOR_BGR2HSV_FULL);	
		Scalar lowBounds = 
				new Scalar(mLowValues.get(COLOR_TYPES.HUE).getValue(), 
						mLowValues.get(COLOR_TYPES.SAT).getValue(), 
						mLowValues.get(COLOR_TYPES.VALUE).getValue());
		
		Scalar highBounds = 
				new Scalar(mHighValues.get(COLOR_TYPES.HUE).getValue(), 
						mHighValues.get(COLOR_TYPES.SAT).getValue(), 
						mHighValues.get(COLOR_TYPES.VALUE).getValue());
//		Scalar LOWER_BOUNDS = new Scalar(45,233,233),
//				UPPER_BOUNDS = new Scalar(93,255,240);
		Core.inRange(matHSV, lowBounds, highBounds, matThresh);
//		Core.inRange(matHSV, LOWER_BOUNDS, UPPER_BOUNDS, matThresh);
		mOutput.updateImage(OpenCVUtils.toBufferedImage(matThresh));
		
	}

}
