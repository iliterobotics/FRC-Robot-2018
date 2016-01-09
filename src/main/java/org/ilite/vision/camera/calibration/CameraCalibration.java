package org.ilite.vision.camera.calibration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Class that contains utility methods to calibrate the camera and calculate the 
 * {@link CameraCalibrationData}
 *
 */
public class CameraCalibration {

	/**
	 * Calibrates the camera with a list of training images. These images should
	 * be of the same size and should be off the chess board, but at various angles
	 * and orientations
	 * @param pNumInnerCornersX
	 * 	The number of inner corners on the chess board, along the x direction
	 * @param pNumInnerCornersY
	 * The number of inner corners on the chess board, along the x direction
	 * @param pTrainingImages
	 * 	List containing all of the training images
	 * @return
	 * 	The {@link CameraCalibrationData} for the given training images
	 */
	public static CameraCalibrationData calibrateCamera(int pNumInnerCornersX, int pNumInnerCornersY, List<Mat> pTrainingImages) {
		int board_w = pNumInnerCornersX;
		int board_h = pNumInnerCornersY;
		Size board_sz = new Size(board_w, board_h);
		//Real location of the cornersin 3D
		MatOfPoint2f corners = new MatOfPoint2f();
		Size imageSize = null;
		List<Mat>imagePoints = new ArrayList<Mat>();

		List<Mat>objs = new ArrayList<Mat>();
		for(Mat img : pTrainingImages) {
			Mat gray = new Mat();
			imageSize = img.size();

			int board_n = board_w*board_h;

			MatOfPoint3f obj = new MatOfPoint3f();
			for (int j=0; j<board_n; j++)
			{
				obj.push_back(new MatOfPoint3f(new Point3((double)j/(double)board_w, (double)j%(double)board_w, 0.0d)));

			}
			objs.add(obj);


			Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
			boolean findChessboardCorners = Calib3d.findChessboardCorners(gray, board_sz,corners,Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FILTER_QUADS);

			if(findChessboardCorners) {
				TermCriteria aCriteria = new TermCriteria(TermCriteria.EPS |
						TermCriteria.MAX_ITER, 30,0.1);
				Imgproc.cornerSubPix(gray, corners, new Size(11,11), new Size(-1,-1), aCriteria);
				Calib3d.drawChessboardCorners(gray, board_sz, corners, true);
				imagePoints.add(corners);
			} 

		}

		return performCalibration(
				objs, imagePoints,imageSize);
	}


	/**
	 * Method to computes the {@link CameraCalibrationData} for the 
	 * @param pObj The {@link Mat} that contains the expected position of the
	 * grids.
	 * @param pImagePoints
	 * 	A list containing all of the points of the grids, for each training image
	 * @param pImageSize
	 * 	The size, in pixels, of each image
	 * @return
	 * 	The {@link CameraCalibrationData} for the given training images
	 */
	private static CameraCalibrationData performCalibration(
			List<Mat> pObj, List<Mat> pImagePoints, Size pImageSize) {
		Mat cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
		Mat distCoeffs = new Mat();
		List<Mat> rvecs = new ArrayList<Mat>();
		List<Mat> tvecs = new ArrayList<Mat>();
		Calib3d.calibrateCamera(pObj, pImagePoints, pImageSize, cameraMatrix, distCoeffs, rvecs, tvecs);

		return new CameraCalibrationData(cameraMatrix, distCoeffs);
	}


	public static void main(String[] args) {
		OpenCVUtils.init();

		Mat chessSlant = Highgui.imread("C:/Users/Christopher/gitrepos/FRC-Vision/chessboard2.png");
		Mat chessReg = Highgui.imread("C:/Users/Christopher/gitrepos/FRC-Vision/chessboard.png");
		
		List<Mat>allTraining = new ArrayList<Mat>();
		allTraining.add(chessSlant);
		allTraining.add(chessReg);
		System.out.println(calibrateCamera(9, 6,allTraining));

	}

}
