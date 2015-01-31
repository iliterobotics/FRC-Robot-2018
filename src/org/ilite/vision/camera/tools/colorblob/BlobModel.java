package org.ilite.vision.camera.tools.colorblob;

import java.awt.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BlobModel {
    private String name;
    private Color avgColor;
    
    public BlobModel() {
        
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String pName) {
        name = pName;
    }

    public Color getAvgColor() {
        return avgColor;
    }

    @XmlElement
    public void setAvgColor(Color pAvgColor) {
        avgColor = pAvgColor;
    }
   
}
