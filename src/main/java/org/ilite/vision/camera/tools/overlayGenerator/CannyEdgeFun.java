package org.ilite.vision.camera.tools.overlayGenerator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CannyEdgeFun {
    
    private final JSlider mSlider = new JSlider(1, 100);
    
    private final  BufferedImage mImage;

    private ImageWindow mCanny;

    public CannyEdgeFun(BufferedImage pImage) {
        mImage = pImage;
        
        ImageWindow aWindow = new ImageWindow(mImage,"OrigImage",false);
        aWindow.show();
        
        mCanny = new ImageWindow(mImage, "Canny", false);
        mCanny.show();
        mSlider.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent pE) {
                applyCanny();
                
            }
        });
        
    }
    
    protected void applyCanny() {
        int sliderValue = mSlider.getValue();
        Mat src = OpenCVUtils.toMatrix(mImage);
        Mat srcGray = new Mat();
        Mat detectedEdges = new Mat();
        Mat dst = src.clone();
        
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(srcGray, detectedEdges,new Size(3,3),1);
        Imgproc.Canny(detectedEdges, detectedEdges, sliderValue,sliderValue*3);
        
        dst = Mat.zeros(dst.size(), dst.type());
        src.copyTo(dst, detectedEdges);
        
        mCanny.updateImage(OpenCVUtils.toBufferedImage(dst));
        
        
    }

    public static void createAndShowGUI() throws IOException {
        JFrame aFrame = new JFrame();
        File imageFile = new File("src/main/resources/images/Screenshot1.jpg");
        aFrame.setContentPane(new CannyEdgeFun(ImageIO.read(imageFile)).mSlider);
        aFrame.pack();
        aFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
    }

}
