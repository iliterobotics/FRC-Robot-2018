package org.ilite.vision.camera.opencv;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.ilite.vision.data.JSONManager;
import org.json.JSONException;

public class SaveDialog extends JFrame {
    private BufferedImage image;
    private BlobModel model;
    
    public SaveDialog(BufferedImage image, final BlobModel pModel) {
        this.image = image;
        this.model = pModel;
             
        JButton saveButton = new JButton("Save");
        
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                int result = fileChooser.showSaveDialog(SaveDialog.this);
                
                if(result == JFileChooser.APPROVE_OPTION) {
                    Map<String, Object> objects = new HashMap<String, Object>();

                    objects.put("average hue", model.getAverageHue());
                    objects.put("average saturation", model.getAverageSaturation());
                    objects.put("average value", model.getAverageValue());

                    try {
                        JSONManager.write(objects, fileChooser.getSelectedFile());
                    } catch (JSONException | IOException e1) {
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
        box.add(new JLabel("Average Hue: " + pModel.getAverageHue()));
        box.add(new JLabel("Average Saturation: " + pModel.getAverageSaturation()));
        box.add(new JLabel("Average Value: " + pModel.getAverageSaturation()));
        box.add(nameTextField);
        box.add(buttonPanel);
        
        add(box);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
