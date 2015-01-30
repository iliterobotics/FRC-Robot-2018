package org.ilite.vision.camera.tools.colorblob;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private float avgColor;
    
    public BlobModel() {
        
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String pName) {
        name = pName;
    }

    public float getAvgColor() {
        return avgColor;
    }

    @XmlElement
    public void setAvgColor(float pAvgColor) {
        avgColor = pAvgColor;
    }
   
}
