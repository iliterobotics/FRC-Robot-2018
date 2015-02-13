package org.ilite.vision.api.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.opencv.core.Mat;

public class VisionSystemAPI {

	private static final String CAMERA_IP = System.getProperty("CAMERA_IP");

	private static class INSTANCE_HOLDER {
		private static final IVisionSystem sSystem = new VisionSystem(CAMERA_IP);
	}

	public static void loadImage(String FILEPATH)throws IOException {
		BufferedImage myImage = ImageIO.read(new File(FILEPATH));

	}

	public static IVisionSystem getVisionSystem() {
		System.out.println("IP:" + CAMERA_IP);
		return INSTANCE_HOLDER.sSystem;
	}

}
