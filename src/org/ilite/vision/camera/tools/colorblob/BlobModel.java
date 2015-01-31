package org.ilite.vision.camera.tools.colorblob;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private float[] hsv;
    private static final int HUE = 0;
    private static final int SATURATION = 1;
    private static final int VALUE = 2;
    
    public BlobModel() {
        hsv = new float[3];
    }
    
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement
    public void setAverageHue(float hue) {
        hsv[HUE] = hue;
    }
    
    @XmlElement
    public void setAverageValue(float value) {
        hsv[VALUE] = value;
    }
    
    @XmlElement
    public void setAverageSaturation(float saturation) {
        hsv[SATURATION] = saturation;
    }
    
    public float getAverageHue() {
        return hsv[HUE];
    }

    public float getAverageValue() {
        return hsv[VALUE];
    }

    public float getAverageSaturation() {
        return hsv[SATURATION];
    }
    
    public String getName() {
        return name;
    }
}
