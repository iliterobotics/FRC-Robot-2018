package org.ilite.vision.examples;

import java.io.IOException;

import javax.swing.JFrame;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.IVisionSystem;
import org.ilite.vision.api.system.VisionListener;
import org.ilite.vision.api.system.VisionSystemAPI;
import org.ilite.vision.camera.opencv.ImagePanel;

public class ImageBlender extends JFrame implements VisionListener {

	ImagePanel iP = new ImagePanel();

	public ImageBlender() {
		setContentPane(iP.getPanel());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

	}

	public static void main(String[] args) throws IOException {
		ImageBlender iB = new ImageBlender();

		IVisionSystem vsi = new VisionSystemAPI().getVisionSystem();

		vsi.subscribe(iB);

	}

	@Override
	public void onVisionDataRecieved(RobotVisionMsg message) {
		System.out.println("Hello");
		iP.updateImage(message.getRawImage());

	}

}
