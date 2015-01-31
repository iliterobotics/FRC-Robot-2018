package org.ilite.vision.camera.opencv.renderables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.IRenderable;
import org.ilite.vision.camera.opencv.ISelectionChangedListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.SaveDialog;
import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * This component is based off of the code, located at:
 * https://github.com/Itseez/opencv In Itseez's code, he wrote a detector on
 * Android. This code performs the same operations; select an area to calculate
 * the average color and then start tracking that color.
 * 
 */
public class ObjectDetectorRenderable implements IRenderable,
        ICameraFrameUpdateListener, ISelectionChangedListener {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private Scalar mBlobColorHsv = null;
    private BufferedImage mCurrentFrame;
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    private Object SYNC_OBJECT = new Object();
    private ImageWindow mParentWindow;

    public ObjectDetectorRenderable(ImageWindow pWindow) {
        mParentWindow = pWindow;
    }

    @Override
    public void frameAvail(BufferedImage pImage) {
        synchronized (SYNC_OBJECT) {
            mCurrentFrame = pImage;

        }

        // Do work to detect
        if (mBlobColorHsv != null) {
            process(OpenCVUtils.toMatrix(pImage));
            mParentWindow.repaint();
        }
    }

    @Override
    public void paint(Graphics pGraphics, BufferedImage pImage) {

        for (int i = 0; i < mContours.size(); i++) {
            MatOfPoint aMatOfPoint = mContours.get(i);

            GeneralPath aPath = new GeneralPath();
            org.opencv.core.Point firstPoint = null;
            for (org.opencv.core.Point aContourPoint : aMatOfPoint.toList()) {
                if (firstPoint == null) {
                    firstPoint = aContourPoint;
                    aPath.moveTo(aContourPoint.x, aContourPoint.y);
                } else {
                    aPath.lineTo(aContourPoint.x, aContourPoint.y);
                }
            }
            if (firstPoint != null) {
                aPath.lineTo(firstPoint.x, firstPoint.y);
                Graphics2D gd = (Graphics2D) pGraphics;
                gd.setColor(Color.YELLOW);
                gd.draw(aPath);
            }
        }

    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
                Core.multiply(contour, new Scalar(4, 4), contour);
                mContours.add(contour);
            }
        }
    }

    @Override
    public void selectionBoundsChanged(Rectangle pRect) {
        synchronized (SYNC_OBJECT) {
            if (mCurrentFrame != null && pRect != null) {
                Mat origMat = OpenCVUtils.toMatrix(mCurrentFrame);
                
                Rect selectedRect = new Rect(pRect.x, pRect.y, pRect.width, pRect.height);

                if(selectedRect.x + selectedRect.width > mCurrentFrame.getWidth()) {
                    selectedRect.width = Math.min(selectedRect.width - mCurrentFrame.getWidth(), selectedRect.x + selectedRect.width);
                }
  
                if(selectedRect.y  < 0) {
                    selectedRect.y = 0;
                }
                
                if(selectedRect.x < 0) {
                    selectedRect.x = 0;
                }
                
                if(selectedRect.x + selectedRect.width < mCurrentFrame.getWidth()) {
                    selectedRect.width = mCurrentFrame.getWidth() - selectedRect.x;
                }
                
                if(selectedRect.y + selectedRect.height > mCurrentFrame.getHeight()) {
                    selectedRect.height = mCurrentFrame.getHeight() - selectedRect.y;
                }
                
                Mat selectedRegionRgba = origMat.submat(selectedRect);

                Mat selectedRegionHsv = new Mat();
                
                Imgproc.cvtColor(selectedRegionRgba, selectedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

                // Calculate average color of touched region
                mBlobColorHsv = Core.sumElems(selectedRegionHsv);

                int pointCount = selectedRect.width * selectedRect.height;
                for (int i = 0; i < mBlobColorHsv.val.length; i++) {
                    mBlobColorHsv.val[i] /= pointCount;
                }
                
                setHsvColor(mBlobColorHsv);
                
                frameAvail(mCurrentFrame);
                
                openSaveDialog(OpenCVUtils.toBufferedImage(selectedRegionRgba));
            }
        }

    }

    private void openSaveDialog(BufferedImage img) {
        BlobModel model = new BlobModel();
        model.setAverageHue(mBlobColorHsv.val[0]);
        model.setAverageSaturation(mBlobColorHsv.val[1]);
        model.setAverageValue(mBlobColorHsv.val[2]);
        
        new SaveDialog(img, model);
    }
    
    private void setHsvColor(Scalar hsvColor) {
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
