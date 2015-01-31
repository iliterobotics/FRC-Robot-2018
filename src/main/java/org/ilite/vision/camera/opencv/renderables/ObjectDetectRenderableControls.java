package org.ilite.vision.camera.opencv.renderables;

import java.awt.BorderLayout;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Scalar;

public class ObjectDetectRenderableControls implements ChangeListener {

    private final JPanel mView = new JPanel(new BorderLayout());
    
    private Map<EHSVValues, JLabel>mHSVLabels = new EnumMap<>(EHSVValues.class);
    private Map<EHSVValues, JSlider>mSliders = new EnumMap<EHSVValues, JSlider>(EHSVValues.class);
    private ObjectDetectorRenderable mRenderable;


    private ObjectDetectRenderableControls(ObjectDetectorRenderable pRenderable) {
        mRenderable = pRenderable;
        

        Box aCreateVerticalBox = javax.swing.Box.createVerticalBox();
        
        for(EHSVValues aValue : EHSVValues.values()) {
            JLabel valueLabel = new JLabel("000");
            mHSVLabels.put(aValue, valueLabel);
            
            JPanel singlePanel = new JPanel();
            singlePanel.add(new JLabel(aValue.getLabelText()));
            JSlider aSlider = new JSlider((int)aValue.getMin(), (int)aValue.getMax(), (int)aValue.getDefault());
            valueLabel.setText(Double.toString(aValue.getDefault()));
            aSlider.addChangeListener(this);
            singlePanel.add(aSlider);
            singlePanel.add(valueLabel);
            mSliders.put(aValue, aSlider);
            
            aCreateVerticalBox.add(singlePanel);
            
        }

        

        
        mView.add(aCreateVerticalBox, BorderLayout.CENTER);


    }


    public static void show(ObjectDetectorRenderable pRenderable) {
        JFrame aFrame = new JFrame("Object Detect Config");
        ObjectDetectRenderableControls aControls = new ObjectDetectRenderableControls(pRenderable);
        aFrame.setContentPane(aControls.mView);
        aFrame.pack();
        aFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        aFrame.setVisible(true);
    }


    @Override
    public void stateChanged(ChangeEvent pE) {
        
        
        Object aSource = pE.getSource();
        if(aSource instanceof JSlider) {
            if(((JSlider) aSource).getValueIsAdjusting()) {
                return;
            }
        }
        Scalar aColorRadius = mRenderable.getColorRadius();
        
        int hue = mSliders.get(EHSVValues.HUE).getValue();
        int sat = mSliders.get(EHSVValues.SAT).getValue();
        int value = mSliders.get(EHSVValues.VALUE).getValue();
        
        double minContourPercent = mSliders.get(EHSVValues.MIN_CONTOUR).getValue();
        mHSVLabels.get(EHSVValues.MIN_CONTOUR).setText(Double.toString(minContourPercent) + "%");
        minContourPercent/=100d;
        
        mHSVLabels.get(EHSVValues.HUE).setText(Integer.toString(hue));
        mHSVLabels.get(EHSVValues.SAT).setText(Integer.toString(sat));
        mHSVLabels.get(EHSVValues.VALUE).setText(Integer.toString(value));
        
        aColorRadius.val[0] = hue;
        aColorRadius.val[1] = sat;
        aColorRadius.val[2] = value;
        
        mRenderable.updateColorRadius(aColorRadius,minContourPercent);
        
    }

}
