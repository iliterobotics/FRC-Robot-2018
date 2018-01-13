package org.ilite.vision.camera.tools.overlayGenerator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ilite.vision.api.system.ImageBlender;

public class EdgeDetectWindow {
    
    public static void main(String[] args) throws IOException {
        File imageFile = new File("src/main/resources/images/Screenshot1.jpg");

        BufferedImage aRead;
        try {
            aRead = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
