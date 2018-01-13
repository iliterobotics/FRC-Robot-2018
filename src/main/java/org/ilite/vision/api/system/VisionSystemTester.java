package org.ilite.vision.api.system;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.ilite.vision.constants.ECameraType;

public class VisionSystemTester {
    
    public static void main(String[] args) throws IOException {
        IVisionSystem aVisionSystem = VisionSystemAPI.getVisionSystem(ECameraType.ALIGNMENT_CAMERA);
        final ImageBlender aImageBlender = VisionSystemAPI.getImageBlender(aVisionSystem);
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                JFrame aFrame = new JFrame();
                aFrame.setContentPane(aImageBlender);
                aFrame.pack();
                aFrame.setVisible(true);
            }
        });
    }

}
