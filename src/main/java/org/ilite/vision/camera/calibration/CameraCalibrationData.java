package org.ilite.vision.camera.calibration;

import org.opencv.core.Mat;

/**
 * Structure to hold onto all of the camera calibration data. 
 * This is the data that is calculated by the {@link CameraCalibration}
 */
public class CameraCalibrationData {
	/**
	 * The matrix that holds the camera parameters, in the format:
	 * [ [fx 0 cx]
	 *   [0 fy cy]
	 *   [0 0 1]
	 *   where fx = focal length in x direction, in pixels
	 *   where fy = focal length in y direction, in pixels 
	 *   where cx = principal point in x, typically the center of the image
	 *   where cy = principal point in y, typically the center of the image
	 */
	private final Mat mCameraMatrix;
	/**
	 * The matrix that holds distance coefficients. This will be in this format:  (k_1, k_2, p_1, p_2[, k_3[, k_4, k_5, k_6]]) of 4, 5, or 8 elements.
	 * where k_3 through k_6 are optional. i.e. this will hold either 4,5 or 8 elements
	 * 
	 * k_1, k_2, k_3, k_4, k_5, and k_6 are radial distortion coefficients. This is used
	 * since each lens tends to have some slight distortion
	 */
	private final Mat mDistCoeffs;
	/**
	 * Constructs the {@link CameraCalibrationData} with the camera matrix 
	 * and the distance coefficients
	 * @param pCameraMatrix
	 * 	The camera matrix
	 * @param pDistCoeffs
	 * 	The distance coefficients
	 */
	public CameraCalibrationData(Mat pCameraMatrix, Mat pDistCoeffs) {
		super();
		this.mCameraMatrix = pCameraMatrix;
		this.mDistCoeffs = pDistCoeffs;
	}
	/**
	 * @return
	 * 	The {@link Mat} that contains the camera parameters
	 */
	public Mat getCameraMatrix() {
		return mCameraMatrix;
	}
	/**
	 * @return
	 * 	The {@link Mat} that contains the distance coefficients
	 */
	public Mat getDistCoeffs() {
		return mDistCoeffs;
	}
	
	@Override
	public String toString() {
		return "CameraCalibrationData [cameraMatrix=" + mCameraMatrix.dump()
				+ ", distCoeffs=" + mDistCoeffs.dump() + "]";
	}
	
}
