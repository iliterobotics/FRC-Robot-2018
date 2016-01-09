package org.ilitie.vision.stereo.calibration;

import org.opencv.core.Mat;

public class CameraCalibrationData {
	private final Mat cameraMatrix;
	private final Mat distCoeffs;
	public CameraCalibrationData(Mat cameraMatrix, Mat distCoeffs) {
		super();
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
	}
	public Mat getCameraMatrix() {
		return cameraMatrix;
	}
	public Mat getDistCoeffs() {
		return distCoeffs;
	}
}
