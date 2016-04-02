package com.fauge.robotics.towertracker;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ValueWindow implements ITowerListener
{
    enum FIELDS{
        DISTANCE,
        AOE,
        ALIGNMENT,
        X_OFF_SET,
        PIXEL_PER_INCH,
        ROBOT_OFFSET,
        RECT_TOP_LEFT,
        RECT_WIDTH,
        RECT_HEIGHT;
    }
    
    private final Map<FIELDS, JLabel> mValueLabels = new HashMap<>();
    
    private JFrame valueW;

    public ValueWindow(){
        valueW = new JFrame();
        JPanel contentPanel = new JPanel(new GridLayout(FIELDS.values().length,2));
        for(FIELDS aField : FIELDS.values()){
            JLabel header = new JLabel(aField.toString());
            contentPanel.add(header);
            JLabel dataLabel = new JLabel("          ");
            contentPanel.add(dataLabel);
            mValueLabels.put(aField, dataLabel);
        }
        valueW.setContentPane(contentPanel);
        valueW.pack();
        valueW.setVisible(true);
        
        
    }
    
    

    public static void main(String[] args)
    {
        new ValueWindow();

    }

    public void fire(TowerMessage message)
    {
        mValueLabels.get(FIELDS.DISTANCE).setText(Double.toString(message.distance));
        mValueLabels.get(FIELDS.AOE).setText(Double.toString(message.AoE));
        mValueLabels.get(FIELDS.ALIGNMENT).setText(message.alignmentX);
        mValueLabels.get(FIELDS.X_OFF_SET).setText(Integer.toString(message.xOffSet));
        
    }



    public void updateValue(double pixelPerInch, double offSet, String rectTopLeft, int rectWidth, int rectHeight)
    {
        mValueLabels.get(FIELDS.PIXEL_PER_INCH).setText(Double.toString(pixelPerInch));
        mValueLabels.get(FIELDS.ROBOT_OFFSET).setText(Double.toString(offSet));
        mValueLabels.get(FIELDS.RECT_TOP_LEFT).setText(rectTopLeft);
        mValueLabels.get(FIELDS.RECT_WIDTH).setText(Integer.toString(rectWidth));
        mValueLabels.get(FIELDS.RECT_HEIGHT).setText(Integer.toString(rectHeight));
    }

}
