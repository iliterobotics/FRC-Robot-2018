package org.ilite.vision.camera.opencv;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ilite.vision.camera.tools.colorblob.BlobModel;
import org.ilite.vision.constants.Paths;
import org.ilite.vision.data.JSONManager;
import org.json.JSONException;

public class SaveDialog extends JFrame {
    private BufferedImage image;
    private Set<BlobModel> model;
    private JTextField nameTextField;
    
    public SaveDialog(BufferedImage image, final Set<BlobModel> pBlobData) {
        this.image = image;
        this.model = pBlobData;
             
        JButton saveButton = new JButton("Save");
        
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                for(BlobModel aModel : model) {

                    aModel.setName(nameTextField.getText());
                    
                    Map<String, Object> objects = new HashMap<String, Object>();

                    objects.put("NAME", aModel.getName());
                    objects.put("AVERAGE_HUE", aModel.getAverageHue());
                    objects.put("AVERAGE_SATURATION", aModel.getAverageSaturation());
                    objects.put("AVERAGE_VALUE", aModel.getAverageValue());

                    try {
                        JSONManager.write(objects, new File(Paths.BLOB_CONFIG_PATH.getValue()), "Blob Data");
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
        
        nameTextField = new JTextField("Name");
        nameTextField.setPreferredSize(new Dimension(image.getWidth(), nameTextField.getPreferredSize().height));
        
        Box box = Box.createVerticalBox();
        box.add(imageLabel);
        
        for(BlobModel aModel : pBlobData) {
            box.add(new JLabel("Average Hue: " + aModel.getAverageHue()));
            box.add(new JLabel("Average Saturation: " + aModel.getAverageSaturation()));
            box.add(new JLabel("Average Value: " + aModel.getAverageSaturation()));
        }

        box.add(nameTextField);
        box.add(buttonPanel);
        
        add(box);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
