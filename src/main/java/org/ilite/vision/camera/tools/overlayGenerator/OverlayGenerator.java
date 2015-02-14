package org.ilite.vision.camera.tools.overlayGenerator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.opencv.core.Mat;

public class OverlayGenerator {

    
    public static void main(String[] args) throws IOException {

        File imageFile = new File("src/main/resources/images/Screenshot1.jpg");

        BufferedImage aRead = ImageIO.read(imageFile);

        Mat aMatrix = OpenCVUtils.toMatrix(aRead);

        ImageWindow aWindow = new ImageWindow(aRead);
        aWindow.show();

    }
}
