package org.ilite.frc.vision.camera.tools.colorblob.histogram;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.ilite.frc.vision.camera.opencv.ImageWindow;
import org.ilite.frc.vision.camera.opencv.OpenCVUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class HistoGramImageWindow extends ImageWindow {

    public HistoGramImageWindow(BufferedImage pImage) {
        super(null, "Histogram");
    }

    @Override
    public void updateImage(BufferedImage pImage) {
        // 1. Convert the image to a Matrix
        Mat image = OpenCVUtils.toMatrix(pImage);
        // 2. Create a list of Mat, this will be the individual color channels,
        // RGB
        List<Mat> rgb = new ArrayList<Mat>();
        // 3. Split the image into it's three channels, use the list created in
        // step 2
        Core.split(image, rgb);
        // 4. Create a MatofInt with initial size of 256 this will be used to
        // define
        // the range, i.e. 0-255

        MatOfInt histSize = new MatOfInt(255);
        Mat histImage = Mat.zeros(100, (int) histSize.get(0, 0)[0],
                CvType.CV_8UC3);
        histImage = calcHistogramSingleChannel(Arrays.asList(rgb.get(0)),
                histImage, histSize, new Scalar(255, 0, 0));
        histImage = calcHistogramSingleChannel(Arrays.asList(rgb.get(1)),
                histImage, histSize, new Scalar(0, 255, 0));
        histImage = calcHistogramSingleChannel(Arrays.asList(rgb.get(2)),
                histImage, histSize, new Scalar(0, 0, 255));

        super.updateImage(OpenCVUtils.toBufferedImage(histImage));
    }

    private Mat calcHistogramSingleChannel(List<Mat> rgb, Mat pHistImage,
            MatOfInt pHistSize, Scalar pHistoColor) {
        // Calculate histogram
        java.util.List<Mat> matList = new LinkedList<Mat>();
        matList.add(rgb.get(0));
        Mat histogram = new Mat();
        MatOfFloat ranges = new MatOfFloat(0, 256);
        Imgproc.calcHist(matList, new MatOfInt(0), new Mat(), histogram,
                pHistSize, ranges);

        // Create space for histogram image
        // Normalize histogram
        Core.normalize(histogram, histogram, 1, pHistImage.rows(),
                Core.NORM_MINMAX, -1, new Mat());

        // Draw lines for histogram points
        for (int i = 0; i < (int) pHistSize.get(0, 0)[0]; i++) {
        	Imgproc.line(
                    pHistImage,
                    new org.opencv.core.Point(i, pHistImage.rows()),
                    new org.opencv.core.Point(i, pHistImage.rows()
                            - Math.round(histogram.get(i, 0)[0])), pHistoColor,
                    1, 8, 0);
        }
        return pHistImage;
    }

}
