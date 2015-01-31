package org.ilite.vision.camera.tools.colorblob;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private float r, g, b;
    
    public BlobModel() {
        
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String pName) {
        name = pName;
    }

    @XmlElement
    public void setRed(float r) {
        this.r = r;
    }
    
    @XmlElement
    public void setGreen(float g) {
        this.g = g;
    }
    
    @XmlElement
    public void setBlue(float b) {
        this.b = b;
    }
    
    public float getRed() {
        return r;
    }
    
    public float getGreen() {
        return g;
    }
    
    public float getBlue() {
        return b;
    }
}
