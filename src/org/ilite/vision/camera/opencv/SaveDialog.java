package org.ilite.vision.camera.opencv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.ilite.vision.camera.tools.colorblob.BlobModel;

public class SaveDialog extends JFrame {
    private BufferedImage image;
    private BlobModel model;
    
    public SaveDialog(BufferedImage image, BlobModel model) {
        this.image = image;
        this.model = model;
             
   
        Box box = Box.createVerticalBox();
        box.add(new JLabel(new ImageIcon(image)));
        box.add(new JLabel("Average Color: " + model.averageColor()));
        box.add(new JTextField("Name"));
        
        add(box);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Daniel\\Desktop\\Images\\ground.png"));
        BlobModel model = new BlobModel();
        
        model.setName("Test");
        model.setBlue(100);
        model.setGreen(50);
        model.setRed(75);
       
        new SaveDialog(image, model);
    }
}
