package org.ilite.vision.camera.opencv;

import org.ilite.vision.api.system.IVisionSystem;
import org.ilite.vision.api.system.VisionListener;

/**
 * A null implementation of the {@link IVisionSystem}, used when there is no
 * vision system. This is done to prevent {@link NullPointerException}. This implementation
 * will do nothing 
 *
 */
class NullVisionSystem implements IVisionSystem {
	
	@Override
	public void start() {
		System.err.println("Attempting to start null camera!");
		
	}

    @Override
    public void subscribe(VisionListener pListener) {
        
    }

    @Override
    public void unsubscribe(VisionListener pListener) {
        
    }

}
