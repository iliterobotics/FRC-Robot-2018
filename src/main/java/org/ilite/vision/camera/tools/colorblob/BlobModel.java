package org.ilite.vision.camera.tools.colorblob;

import java.awt.image.BufferedImage;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class BlobModel {
    private String name;
    private double[] hsv;
    public static final Scalar CONTOUR_SCALAR = new Scalar(4, 4);
    private Scalar mLowerBound, mUpperBound; // Bounds for range checking in HSV color space
    private double mMinContourArea;          // Minimum contour area in percent for contours filtering
    private Scalar mColorRadius;             // Color radius for range checking in HSV color space
    private Scalar mBlobColorHsv;
    private BufferedImage mCurrentFrame;
    private Mat mPyrDownMat, mHsvMat, mMask, mDilatedMask, mHierarchy, mSpectrum;
    private static final int HUE = 0;
    private static final int SATURATION = 1;
    private static final int VALUE = 2;
    
    public double[] getHsv() {
        return hsv;
    }

    public Scalar getLowerBound() {
        return mLowerBound;
    }

    public Scalar getUpperBound() {
        return mUpperBound;
    }

    public double getMinContourArea() {
        return mMinContourArea;
    }

    public Scalar getColorRadius() {
        return mColorRadius;
    }

    public Scalar getBlobColorHsv() {
        return mBlobColorHsv;
    }

    public BufferedImage getCurrentFrame() {
        return mCurrentFrame;
    }

    public Mat getPyrDownMat() {
        return mPyrDownMat;
    }

    public Mat getHsvMat() {
        return mHsvMat;
    }

    public Mat getMask() {
        return mMask;
    }

    public Mat getDilatedMask() {
        return mDilatedMask;
    }

    public Mat getHierarchy() {
        return mHierarchy;
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public BlobModel() {
        hsv = new double[3];
        mLowerBound = new Scalar(0);
        mUpperBound = new Scalar(0);
        mMinContourArea = 0.1;
        mColorRadius = new Scalar(25, 50, 50, 0);
        mSpectrum = new Mat();
        mBlobColorHsv = null;
        mPyrDownMat = new Mat();
        mHsvMat = new Mat();
        mDilatedMask = new Mat();
        mMask = new Mat();
        mHierarchy = new Mat();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setAverageHue(double hue) {
        hsv[HUE] = hue;
    }
    
    public void setAverageValue(double value) {
        hsv[VALUE] = value;
    }
    
    public void setAverageSaturation(double saturation) {
        hsv[SATURATION] = saturation;
    }
    
    public double getAverageHue() {
        return hsv[HUE];
    }

    public double getAverageValue() {
        return hsv[VALUE];
    }

    public double getAverageSaturation() {
        return hsv[SATURATION];
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + "\nHSV: [" + hsv[0] + ", " + hsv[1] + ", " + hsv[2] + "]\n";
    }
    
    public void setHsvColor(Scalar hsvColor) {
        BlobModel aModel = new BlobModel();
        aModel.setAverageHue(hsvColor.val[0]);
        aModel.setAverageSaturation(hsvColor.val[1]);
        aModel.setAverageValue(hsvColor.val[2]);
        
        mBlobColorHsv = hsvColor;
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]
                - mColorRadius.val[0]
                : 0;
        double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0]
                + mColorRadius.val[0]
                : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH - minH; j++) {
            byte[] tmp = { (byte) (minH + j), (byte) 255, (byte) 255 };
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }
    
}
