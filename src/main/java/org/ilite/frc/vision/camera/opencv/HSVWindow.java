package org.ilite.frc.vision.camera.opencv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HSVWindow {
    private JFrame frame;
    private JLabel imageLabel;
    
    public HSVWindow() {
        frame = new JFrame();
        
        final Box box = Box.createVerticalBox();
        
        imageLabel = new JLabel();
        
        JButton button = new JButton("Open Image");
        
        //button.setPreferredSize(new Dimension(imageLabel.getPreferredSize().width, imageLabel.getPreferredSize().height));
       
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                
                int result = chooser.showOpenDialog(frame);
                
                if(result == JFileChooser.APPROVE_OPTION) {
                    
                    try {
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                        
                        Mat rgba = OpenCVUtils.toMatrix(img);
                        Mat hsv = new Mat();
                        
                        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV_FULL);
                        
                        img = OpenCVUtils.toBufferedImage(hsv);
                        
                        imageLabel = new JLabel(new ImageIcon(img));
                  
                        box.add(imageLabel);
                        frame.pack();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    
                }
            }
            
        });
        
        box.add(imageLabel);
        box.add(button);
        
        frame.add(box);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new HSVWindow();
    }
}
