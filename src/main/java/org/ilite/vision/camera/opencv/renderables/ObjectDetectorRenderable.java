package org.ilite.vision.camera.opencv.renderables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.IRenderable;
import org.ilite.vision.camera.opencv.ISelectionChangedListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.SaveDialog;
import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.ilite.vision.constants.BlobData;
import org.opencv.core.Core;
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
public class ObjectDetectorRenderable implements IRenderable, ICameraFrameUpdateListener, ISelectionChangedListener {
    private Object SYNC_OBJECT;
    private ImageWindow mParentWindow;
    private BufferedImage mCurrentFrame;
    private final Set<BlobModel> blobData;
    private List<MatOfPoint> mContours;

    public ObjectDetectorRenderable(ImageWindow pWindow, boolean readData) {
        pWindow.setListener(this);
        
        mParentWindow = pWindow;
        mContours = new ArrayList<MatOfPoint>();
        SYNC_OBJECT = new Object();

        blobData = new CopyOnWriteArraySet<BlobModel>();

        if(readData) {
            readBlobData();
        }   
        JButton clearBlobs = new JButton("clear blobs");
        clearBlobs.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent pE) {
                blobData.clear();
                process(OpenCVUtils.toMatrix(mCurrentFrame));
            }
        });
        mParentWindow.addComponentToButtonPanel(clearBlobs);
    }

    private void readBlobData() {
        try {
            
            BlobData.readBlobData();
            blobData.addAll(BlobData.getBlobData());
       
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void frameAvail(BufferedImage pImage) {
        synchronized (SYNC_OBJECT) {
            mCurrentFrame = pImage;
        }

        // Do work to detect
        if (!blobData.isEmpty()) {
            process(OpenCVUtils.toMatrix(pImage));
            
            if(mParentWindow != null) {
                mParentWindow.repaint();
            }
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
    
    public List<MatOfPoint> getContours() {
        return mContours;
    }

    public void process(Mat rgbaImage) {

        mContours.clear();
        for(BlobModel aModel : blobData) {
            Mat aPyrDownMat = aModel.getPyrDownMat();
            Mat aHsvMat = aModel.getHsvMat();
            Scalar mLowerBound = aModel.getLowerBound();
            Scalar mUpperBound = aModel.getUpperBound();
            Mat mMask = aModel.getMask();
            Mat mDilatedMask = aModel.getDilatedMask();
            Mat mHierarchy = aModel.getHierarchy();
            double mMinContourArea = aModel.getMinContourArea();
            Imgproc.pyrDown(rgbaImage, aPyrDownMat);
            Imgproc.pyrDown(aPyrDownMat, aPyrDownMat);

            Imgproc.cvtColor(aPyrDownMat, aHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

            Core.inRange(aHsvMat, mLowerBound, mUpperBound, mMask);
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
                
                if (area > maxArea) {
                    maxArea = area;
                }
            }

            // Filter contours by area and resize to fit the original image size
            each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
                    Core.multiply(contour, BlobModel.CONTOUR_SCALAR, contour);
                    mContours.add(contour);
                }
            }    
        }
    }

    @Override
    public void selectionBoundsChanged(Rectangle pRect) {
        synchronized (SYNC_OBJECT) {
            if (mCurrentFrame != null && pRect != null) {
                Mat origMat = OpenCVUtils.toMatrix(mCurrentFrame);

                Rect selectedRect = new Rect(pRect.x, pRect.y, pRect.width, pRect.height);

                if(selectedRect.x < 0) {
                    selectedRect.x = 0;
                }

                if(selectedRect.y < 0) {
                    selectedRect.y = 0;
                }

                if(selectedRect.x + selectedRect.width > mCurrentFrame.getWidth()) {
                    selectedRect.width = mCurrentFrame.getWidth() - selectedRect.x;
                }

                if(selectedRect.y + selectedRect.height > mCurrentFrame.getHeight()) {
                    selectedRect.height = mCurrentFrame.getHeight() - selectedRect.y;
                }

                Mat selectedRegionRgba = origMat.submat(selectedRect);

                Mat selectedRegionHsv = new Mat();

                Imgproc.cvtColor(selectedRegionRgba, selectedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

                BlobModel aModel = new BlobModel();

                // Calculate average color of touched region
                Scalar hsvColor = Core.sumElems(selectedRegionHsv);
                
                calculateHSV(selectedRect, hsvColor);
                aModel.setHsvColor(hsvColor);
                blobData.add(aModel);
                frameAvail(mCurrentFrame);

                openSaveDialog(OpenCVUtils.toBufferedImage(selectedRegionRgba),aModel);
            }

        }

    }

    private void constructImage(BufferedImage img, List<MatOfPoint> points) throws IOException {
        BufferedImage image = new BufferedImage(img.getWidth(), 
                                                img.getHeight(), 
                                                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        
        graphics.setColor(new Color(0, 0, 0, 0));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.GREEN);
        
        List<GeneralPath>paths = new ArrayList<GeneralPath>();
        for(MatOfPoint aPoint : points) {
           
            GeneralPath aPath = new GeneralPath();
            org.opencv.core.Point firstPoint = null;
            for (org.opencv.core.Point aContourPoint : aPoint.toList()) {
                if (firstPoint == null) {
                    firstPoint = aContourPoint;
                    aPath.moveTo(aContourPoint.x, aContourPoint.y);
                } else {
                    aPath.lineTo(aContourPoint.x, aContourPoint.y);
                }
            }
            if (firstPoint != null) {
                aPath.lineTo(firstPoint.x, firstPoint.y);
            }
            
            graphics.draw(aPath);
            
        }
        
        FileOutputStream stream = new FileOutputStream(new File("testimage.png"));
        
        ImageIO.write(image, "png", stream);
        
        graphics.dispose();
    }
    
    private void calculateHSV(Rect selectedRect, Scalar hsvColor) {
        int pointCount = selectedRect.width * selectedRect.height;
        for (int i = 0; i < hsvColor.val.length; i++) {
            hsvColor.val[i] /= pointCount;
        }
    }

    public void onGenerateOverlayClicked() {
        try {
            if(mParentWindow.isPaused()) {
                constructImage(mCurrentFrame, mContours);
            }
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
    
    public void onPauseClicked() {

    }
    
    private void openSaveDialog(BufferedImage img, BlobModel pModel) {
        new SaveDialog(img, pModel);
    }

    public void addBlobModel(BlobModel pModel) {
        blobData.add(pModel);
        
    }
}
