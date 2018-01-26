package org.ilite.frc.vision.camera.tools.overlayGenerator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ilite.frc.vision.camera.opencv.ImageWindow;
import org.ilite.frc.vision.camera.opencv.OpenCVUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SobelEdgeFun {
    
    public static void main(String[] args) throws IOException {
        File imageFile = new File("src/main/resources/images/Screenshot1.jpg");

        BufferedImage aRead = ImageIO.read(imageFile);
        ImageWindow origImage =new ImageWindow(aRead, "ORIGINAL");
        origImage.show();
        Mat aMatrix = OpenCVUtils.toMatrix(aRead);
        
        Imgproc.GaussianBlur(aMatrix, aMatrix, new Size(3,3), 0,0);
        
        Imgproc.cvtColor(aMatrix, aMatrix, Imgproc.COLOR_RGB2GRAY);

        
        Mat gradx = new Mat();
        Mat grady = new Mat();
        Mat gradabsx = new Mat();
        Mat gradabsy = new Mat();
        
        Imgproc.Sobel(aMatrix, gradx, CvType.CV_16U,1,0,3,1,0);
        Core.convertScaleAbs(gradx, gradabsx);
        
        Imgproc.Sobel(aMatrix, grady, CvType.CV_16U,0,1,3,1,0);
        Core.convertScaleAbs(grady, gradabsy);
        
        Mat grad = new Mat();
        Core.addWeighted(gradabsx, .5, gradabsy, .5, 0, grad);
        
        ImageWindow finalWindow  = new ImageWindow(OpenCVUtils.toBufferedImage(grad),"SOBEL");
        
        
        
        finalWindow.show();

    }

}
