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

public class CameraCalibration {

	public static CameraCalibrationData calibrateCamera(int pBoardWith, int pBoardHeight, List<Mat> pTrainingImage) {
		int board_w = pBoardWith;
		int board_h = pBoardHeight;
		Size board_sz = new Size(board_w, board_h);
		//Real location of the cornersin 3D
		MatOfPoint3f obj = new MatOfPoint3f();
		MatOfPoint2f corners = new MatOfPoint2f();
		Size imageSize = null;

		int board_n = board_w*board_h;
		for (int j=0; j<board_n; j++)
		{
			obj.push_back(new MatOfPoint3f(new Point3((double)j/(double)board_w, (double)j%(double)board_w, 0.0d)));

		}
		List<Mat>allObjects = new ArrayList<Mat>();

		List<Mat>imagePoints = new ArrayList<Mat>();
		allObjects.add(obj);
		for(Mat img : pTrainingImage) {
			Mat gray = new Mat();
			imageSize = img.size();


			Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);


			boolean findChessboardCorners = Calib3d.findChessboardCorners(gray, board_sz,corners,Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FILTER_QUADS);

			if(findChessboardCorners) {

				TermCriteria aCriteria = new TermCriteria(TermCriteria.EPS |
						TermCriteria.MAX_ITER, 30,0.1);
				Imgproc.cornerSubPix(gray, corners, new Size(11,11), new Size(-1,-1), aCriteria);
				Calib3d.drawChessboardCorners(gray, board_sz, corners, true);
				ImageWindow aWindow = new ImageWindow(OpenCVUtils.toBufferedImage(gray));
				aWindow.show();




				

				imagePoints.add(corners);

			} else {
				throw new IllegalStateException("Failed to find the chessboard");
			}

			return calibrateAllImages(
					allObjects, imagePoints,imageSize);
		}
		return null;
	}


	private static CameraCalibrationData calibrateAllImages(
			List<Mat> allObjects, List<Mat> imagePoints, Size pImageSize) {
		Mat cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
		Mat distCoeffs = new Mat();
		List<Mat> rvecs = new ArrayList<Mat>();
		List<Mat> tvecs = new ArrayList<Mat>();
		Calib3d.calibrateCamera(allObjects, imagePoints, pImageSize, cameraMatrix, distCoeffs, rvecs, tvecs);

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
