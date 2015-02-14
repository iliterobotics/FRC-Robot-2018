package org.ilite.vision.camera.tools.overlayGenerator;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.renderables.ObjectDetectorRenderable;
import org.ilite.vision.camera.tools.colorblob.ColorBlobTrainer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OverlayGenerator {
    private ImageWindow mFinalWindow;
    private JPanel mView = new JPanel();
    private JComboBox<EThreshAlgorithm> mAlgorithm;
    private Mat mOrigImage;
    private JSlider mSlider;
    


    public OverlayGenerator(Mat pMatrix, ImageWindow pWindow) {
        mOrigImage = pMatrix;
        mFinalWindow = pWindow;
        mAlgorithm = new JComboBox<>(EThreshAlgorithm.values());
        mAlgorithm.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent pE) {
                updateImage();
                
            }
        });
        
        mSlider = new JSlider(1, 255);
        mSlider.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent pE) {
                if(!mSlider.getValueIsAdjusting()) {
                    updateImage();
                }
            }
        });
        mView.add(mAlgorithm);
        mView.add(mSlider);
    }
    
    protected void updateImage() {
        Mat tempMat = mOrigImage.clone();
        
        int algo = ((EThreshAlgorithm)mAlgorithm.getSelectedItem()).ordinal();
        
        Imgproc.threshold(mOrigImage, tempMat, mSlider.getValue(), 255, algo);
        
        mFinalWindow.updateImage(OpenCVUtils.toBufferedImage(tempMat));
        
    }

    public static void createAndShowGUI(Mat pMatrix, ImageWindow pFinalWindow) {
        OverlayGenerator aGenerator = new OverlayGenerator(pMatrix, pFinalWindow);
        JFrame aFrame = new JFrame();
        aFrame.setContentPane(aGenerator.getView());
        aFrame.pack();
        aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aFrame.setVisible(true);
    }

    
    public JPanel getView() {
        return mView;
    }
    
    public static void main(String[] args) throws IOException {

        File imageFile = new File("src/main/resources/images/Screenshot1.jpg");

        BufferedImage aRead = ImageIO.read(imageFile);


        Mat aMatrix = OpenCVUtils.toMatrix(aRead);
        
        Scalar averageColor = new Scalar(84.60344827586, 91.679802, 91.679802955);
        
        
        
        ObjectDetectorRenderable aRenderable  = new ObjectDetectorRenderable(null);
        aRenderable.setHsvColor(averageColor);
        aRenderable.frameAvail(aRead);
        List<MatOfPoint> aContours = aRenderable.getContours();
        
        List<GeneralPath>paths = new ArrayList<GeneralPath>();
        for(MatOfPoint aPoint : aContours) {
           
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
            paths.add(aPath);
            
        }
        System.out.println("Paths size = " + paths.size());
        
        System.out.println("Matrix width= " + aMatrix.width() + ", " +aMatrix.height());

        
        
        Mat finalImage = aMatrix.clone();
        Imgproc.cvtColor(finalImage, finalImage, Imgproc.COLOR_RGB2RGBA);
        for(int row=0;row<aMatrix.rows();row++) {
            for(int col=0;col<aMatrix.cols();col++) {
                
                boolean isBlack = true;
                for(GeneralPath aPath : paths) {
                    if(aPath.contains(col,row)) {
                        isBlack = false;
                        break;
                    }
                }
                if(isBlack) {
                    finalImage.put(row, col, new double[]{0,0,0,0.3});
                }
            }
        }
        
        ImageIO.write(OpenCVUtils.toBufferedImage(finalImage), "png", new File("src/main/resources/Overlay.png"));
        
        ImageWindow finalWindow = new ImageWindow(OpenCVUtils.toBufferedImage(finalImage));
        finalWindow.show();

        ColorBlobTrainer aTrainer = new ColorBlobTrainer(aRead);
        aTrainer.show();
//        Imgproc.cvtColor(aMatrix, aMatrix, Imgproc.COLOR_RGB2GRAY);
/*
        
        double[] aDs = aMatrix.get(50, 50);
        for(double aDouble : aDs) {
            System.out.println(aDouble +  " " );
        }
        
        Color aRed = Color.red;
        Color myRed  = new Color(255, 0, 0);
        
        System.out.println("EQUALS= " + aRead.equals(aRed));
        
        Imgproc.blur(aMatrix, aMatrix, new Size(5,5));
        
        final Mat origImage = aMatrix.clone();
        List<Mat>aMatrices = new ArrayList<Mat>();
        Core.split(aMatrix, aMatrices);
        aMatrix = aMatrices.get(1);
        

        ImageWindow aWindow = new ImageWindow(aRead);
        aWindow.addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseMoved(MouseEvent pE) {
                Point aPoint = pE.getPoint();
                
                System.out.println("Point= " + aPoint.x + ", " + aPoint.y);
                
                System.out.println("ORIG IMAGE SIZE= " + origImage.rows()+ " x "  + origImage.cols());
                
                double[] aDs2 = origImage.get(aPoint.y, aPoint.x);
                System.out.println("R= " + aDs2[0] + "G= " + aDs2[1] + "B= " + aDs2[2]);
                
            }
            
            @Override
            public void mouseDragged(MouseEvent pE) {
                // TODO Auto-generated method stub
                
            }
        });
        aWindow.show();
        
        
        
        ImageWindow grayWindpw = new ImageWindow(OpenCVUtils.toBufferedImage(aMatrix));

        createAndShowGUI(aMatrix, grayWindpw);
        */
//        grayWindpw.show();

    }
}
