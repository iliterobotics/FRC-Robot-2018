package org.ilite.vision.camera.opencv;

import java.awt.Color;
import java.awt.Graphics;



public class Renderable implements IRenderable{

	@Override
	public void paint(Graphics pG) {
	 
		pG.setColor(Color.MAGENTA);
		pG.drawLine(0,0, 1000, 1000);
		
		
	} 
	
	

}
