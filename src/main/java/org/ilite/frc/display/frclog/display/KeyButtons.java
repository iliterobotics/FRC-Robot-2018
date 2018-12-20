package org.ilite.frc.display.frclog.display;

import javafx.scene.image.Image;

public class KeyButtons {

    private Image img;
    private String value;

    public KeyButtons( Image img, String value ) {
        this.img = img;
        this.value = value;
    }
    public KeyButtons( Image img ) {
        this.img = img;
        this.value = "";
    }

    public Image getImage() {
        return img;
    }

    public String getValue() {
        return value;
    }


}