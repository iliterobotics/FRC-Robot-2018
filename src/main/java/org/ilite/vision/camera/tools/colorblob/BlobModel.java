package org.ilite.vision.camera.tools.colorblob;


public class BlobModel {
    private String name;
    private double[] hsv;
    private static final int HUE = 0;
    private static final int SATURATION = 1;
    private static final int VALUE = 2;
    
    public BlobModel() {
        hsv = new double[3];
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setAverageHue(double hue) {
        hsv[HUE] = hue;
    }
    
    public void setAverageValue(double value) {
        hsv[VALUE] = value;
    }
    
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
    
    @Override
    public String toString() {
        return "Name: " + name + "\nHSV: [" + hsv[0] + ", " + hsv[1] + ", " + hsv[2] + "]\n";
    }
    
}
