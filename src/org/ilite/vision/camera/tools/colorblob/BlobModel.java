package org.ilite.vision.camera.tools.colorblob;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private float average;
    
    public BlobModel() {
        
    }

    public String getName() {
        return name;
    }

    public float getAverage() {
        return average;
    }
    
    @XmlElement
    public void setName(String pName) {
        name = pName;
    }

    @XmlElement
    public void setAverage(float average) {
        this.average = average;
    }
}
