package org.ilite.vision.camera.opencv;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.bind.JAXBException;

import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.ilite.vision.data.XMLManager;

public class SaveDialog extends JFrame {
    private BufferedImage image;
    private BlobModel model;
    
    public SaveDialog(BufferedImage image, BlobModel model) {
        this.image = image;
        this.model = model;
             
        JButton saveButton = new JButton("Save");
        
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                int result = fileChooser.showSaveDialog(SaveDialog.this);
                
                if(result == JFileChooser.APPROVE_OPTION) {
                    try {
                        XMLManager.write(fileChooser.getSelectedFile(), model);
                    } catch (JAXBException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            
        });
        
        JButton dropButton = new JButton("Drop");
        
        dropButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
            
        });
        
        JPanel buttonPanel = new JPanel();
       
        buttonPanel.add(saveButton);
        buttonPanel.add(dropButton);
        buttonPanel.setPreferredSize(new Dimension(image.getWidth(), saveButton.getPreferredSize().height + 5));
        
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        
        JTextField nameTextField = new JTextField("Name");
        nameTextField.setPreferredSize(new Dimension(image.getWidth(), nameTextField.getPreferredSize().height));
        
        Box box = Box.createVerticalBox();
        box.add(imageLabel);
        box.add(new JLabel("Average Hue: " + model.getAverageHue()));
        box.add(new JLabel("Average Saturation: " + model.getAverageSaturation()));
        box.add(new JLabel("Average Value: " + model.getAverageSaturation()));
        box.add(nameTextField);
        box.add(buttonPanel);
        
        add(box);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
