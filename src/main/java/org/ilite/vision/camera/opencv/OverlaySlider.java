package org.ilite.vision.camera.opencv;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OverlaySlider {
    private Set<OverlaySliderListener> listeners;
    private final JSlider slider;
    
    public static interface OverlaySliderListener {
        public void onSliderChanged(float value);
    }
    
    public OverlaySlider() {
        listeners = new LinkedHashSet<OverlaySliderListener>();
        
        slider = new JSlider();
        slider.setMaximum(100);
        slider.setMinimum(0);
        slider.setValue(50);
        
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                for(OverlaySliderListener l : listeners) {
                    l.onSliderChanged(slider.getValue() / 100f);
                }
            }
            
        });
    }
    
    public JSlider getSlider() {
        return slider;
    }
    
    public void subscribe(OverlaySliderListener l) {
        listeners.add(l);
    }
    
    public void unsubscribe(OverlaySliderListener l) {
        listeners.remove(l);
    }
    
    public static void main(String[] args) {
        new OverlaySlider();
    }
}
