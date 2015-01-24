package org.ilite.vision.camera.tools.colorblob.histogram;

import java.awt.image.BufferedImage;

import org.ilite.vision.camera.opencv.ImageWindow;

public class HistoGramImageWindow extends ImageWindow {

    public HistoGramImageWindow(BufferedImage pImage) {
	super(null, "Histogram");
    }
    
    @Override
    public void updateImage(BufferedImage pImage) {
	//1. Convert the image to a Matrix
	
	//2. Create a list of Mat, this will be the individual color channels, RGB

	//3. Split the image into it's three channels, use the list created in step 2
	
	//4. Create a MatofInt with initial size of 256 this will be used to define 
	//the range, i.e. 0-255
	
	//5. Calculate the histogram in all three channels. THis will be three lines
	
	
    }

}
