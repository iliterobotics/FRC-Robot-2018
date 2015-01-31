package org.ilite.vision.camera.opencv.renderables;

import org.opencv.core.Scalar;

public enum EHSVValues {
    HUE("Hue Value",25,0,100),
    SAT("Sat Value",50,0,100),
    VALUE("Value Value: ",50,0,100);
    
    private String mLabelText;
    private double mDefault;
    private double mMin;
    private double mMax;

    private  EHSVValues(String pLabelText, double pDefault, double pMin, double pMax) {
        mLabelText = pLabelText;
        mDefault = pDefault;
        mMin = pMin;
        mMax = pMax;
    }
    
    public String getLabelText() {
        return mLabelText;
    }
    
    public static double getValue(Scalar pScaler, EHSVValues pValue) {
        return pScaler.val[pValue.ordinal()];
    }
    
    public double getDefault() {
        return mDefault;
    }
    
    public double getMax() {
        return mMax;
    }
    public double getMin() {
        return mMin;
    }

}
