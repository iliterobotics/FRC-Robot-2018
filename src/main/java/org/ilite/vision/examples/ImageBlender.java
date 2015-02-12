package org.ilite.vision.examples;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.IVisionSystem;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystemAPI;

public class ImageBlender implements VisionListener{   
	
	
	
	public static void main(String [] args){   
		IVisionSystem vsi = new VisionSystemAPI().getVisionSystem();   
		vsi.subscribe(new ImageBlender()); 
		
		
		
	}

	@Override
	public void onVisionDataRecieved(RobotVisionMsg message) {
		 System.out.println("Hello");
		
	}
	
	
	

}
