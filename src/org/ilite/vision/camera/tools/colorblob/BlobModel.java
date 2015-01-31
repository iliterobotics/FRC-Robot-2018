package org.ilite.vision.camera.tools.colorblob;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private double[] hsv;
    private static final int HUE = 0;
    private static final int SATURATION = 1;
    private static final int VALUE = 2;
    
    public BlobModel() {
        hsv = new double[3];
    }
    
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlElement
    public void setAverageHue(double hue) {
        hsv[HUE] = hue;
    }
    
    @XmlElement
    public void setAverageValue(double value) {
        hsv[VALUE] = value;
    }
    
    @XmlElement
    public void setAverageSaturation(double saturation) {
        hsv[SATURATION] = saturation;
    }
    
    public double getAverageHue() {
        return hsv[HUE];
    }

    public double getAverageValue() {
        return hsv[VALUE];
    }

    public double getAverageSaturation() {
        return hsv[SATURATION];
    }
    
    public String getName() {
        return name;
    }
}
