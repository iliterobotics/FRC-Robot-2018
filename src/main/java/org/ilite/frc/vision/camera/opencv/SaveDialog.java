package org.ilite.frc.vision.camera.opencv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ilite.frc.vision.camera.tools.colorblob.BlobModel;
import org.ilite.frc.vision.constants.Paths;
import org.ilite.frc.vision.data.JSONManager;
import org.json.JSONException;

public class SaveDialog extends JFrame {
    private BufferedImage image;
    private BlobModel model;
    private JTextField nameTextField;
    private static final DecimalFormat sDecimalFormat = new DecimalFormat("000.00");
    
    public SaveDialog(BufferedImage image, final BlobModel pModel) {
        this.image = image;
        this.model = pModel;
             
        JButton saveButton = new JButton("Save");
        
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                

                model.setName(nameTextField.getText());
                    
                    Map<String, Object> objects = new HashMap<String, Object>();

                    objects.put("NAME", model.getName());
                    objects.put("AVERAGE_HUE", model.getAverageHue());
                    objects.put("AVERAGE_SATURATION", model.getAverageSaturation());
                    objects.put("AVERAGE_VALUE", model.getAverageValue());

                    try {
                        JSONManager.write(objects, new File(Paths.BLOB_CONFIG_PATH.getValue()), "Blob Data");
                    } catch (JSONException | IOException e1) {                   
                        e1.printStackTrace();
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
//        buttonPanel.setPreferredSize(new Dimension(image.getWidth(), saveButton.getPreferredSize().height + 5));
        
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        
        nameTextField = new JTextField("Name");
//        nameTextField.setPreferredSize(new Dimension(image.getWidth(), nameTextField.getPreferredSize().height));
        
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(imageLabel);
        box.add(buildOutputLabel("Average Hue: ",model.getAverageHue()));
        box.add(buildOutputLabel("Average Saturation: ", model.getAverageSaturation()));
        box.add(buildOutputLabel("Average Value: ", model.getAverageSaturation()));
        
        final JButton overlayColor = new JButton("Overlay Color");
        overlayColor.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent pE) {
                Color aShowDialog = JColorChooser.showDialog(overlayColor, "Overlay Color", model.getOverlayColor());
                model.setOverlayColor(aShowDialog);
                overlayColor.setForeground(aShowDialog);
            }
        });
        overlayColor.setForeground(model.getOverlayColor());
        buttonPanel.add(overlayColor);
        
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(nameTextField, BorderLayout.NORTH);
        contentPanel.add(box, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    private static JLabel buildOutputLabel(String pPreText, double pVal) {
        return new JLabel(pPreText + sDecimalFormat.format(pVal));
    }
}
