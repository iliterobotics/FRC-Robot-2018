package org.ilite.vision.camera.tools.colorblob;

import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class BlobModel {
    /**
     * Log4j logger
     */
    private static final Logger sLog = Logger.getLogger(BlobModel.class);
    /**
     * The scale factor of the contours, used to scale up the
     */
    public static final Scalar CONTOUR_SCALAR = new Scalar(4, 4);
    /**
     * Indexes for array representations of the colors
     */
    private static final int HUE_IDX = 0;
    private static final int SATURATION_IDX = 1;
    private static final int VALUE_IDX = 2;
    
    /**
     * The name of the object
     */
    private String mNameOfObject;
    /**
     * The average color, as HSV. The first element is hue, the second sat and
     * the last is value
     */
    private double[] mHSVAverageColor;
    /**
     * {@link Scalar} representation of the lower color and the upper color
     */
    private Scalar mLowerBound, mUpperBound;
    /**
     * Minimum contour area in percent for contours filtering
     */
    private double mMinContourArea;
    /**
     * Color radius for range checking in HSV color space
     */
    private Scalar mColorRadius;
    /**
     * The average color of the blob
     */
    private Scalar mBlobColorHsv;
    /**
     * The masks
     */
    private Mat mPyrDownMat, mHsvMat, mMask, mDilatedMask, mHierarchy, mSpectrum;

    
    public double[] getHsv() {
        return mHSVAverageColor;
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
        mHSVAverageColor = new double[3];
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
        this.mNameOfObject = name;
    }
    
    public void setAverageHue(double hue) {
        mHSVAverageColor[HUE_IDX] = hue;
    }
    
    public void setAverageValue(double value) {
        mHSVAverageColor[VALUE_IDX] = value;
    }
    
    public void setAverageSaturation(double saturation) {
        mHSVAverageColor[SATURATION_IDX] = saturation;
    }
    
    public double getAverageHue() {
        return mHSVAverageColor[HUE_IDX];
    }

    public double getAverageValue() {
        return mHSVAverageColor[VALUE_IDX];
    }

    public double getAverageSaturation() {
        return mHSVAverageColor[SATURATION_IDX];
    }
    
    public String getName() {
        return mNameOfObject;
    }
    
    @Override
    public String toString() {
        return "Name: " + mNameOfObject + "\nHSV: [" + mHSVAverageColor[0] + ", " + mHSVAverageColor[1] + ", " + mHSVAverageColor[2] + "]\n";
    }
    
    public void setHsvColor(Scalar hsvColor) {
        
        if(sLog.isDebugEnabled()) {
            StringBuilder debugString = new StringBuilder();
            debugString.append("Setting the HSV color= {");
            debugString.append(hsvColor.val[HUE_IDX]);
            debugString.append(", ").append(hsvColor.val[SATURATION_IDX]);
            debugString.append(", ").append(hsvColor.val[VALUE_IDX]);
            sLog.debug(debugString);;
        }
        setAverageHue(hsvColor.val[0]);
        setAverageSaturation(hsvColor.val[1]);
        setAverageValue(hsvColor.val[2]);
        
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
        
        if(sLog.isDebugEnabled()) {
            StringBuilder debugString = new StringBuilder();
            debugString.append("hue color range= {").append(mLowerBound.val[HUE_IDX]).append(", ").append(mUpperBound.val[HUE_IDX]).append("}");
            debugString.append("sat color range= {").append(mLowerBound.val[SATURATION_IDX]).append(", ").append(mUpperBound.val[SATURATION_IDX]).append("}");
            debugString.append("value color range= {").append(mLowerBound.val[VALUE_IDX]).append(", ").append(mUpperBound.val[VALUE_IDX]).append("}");
            sLog.debug(debugString);
        }

        Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH - minH; j++) {
            byte[] tmp = { (byte) (minH + j), (byte) 255, (byte) 255 };
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }
    
}
