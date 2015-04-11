package org.ilite.vision.camera.tools.colorblob;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.ICameraFrameUpdateListener;
import org.ilite.vision.camera.opencv.ImageWindow;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.ilite.vision.camera.opencv.Renderable;
import org.ilite.vision.camera.opencv.renderables.ObjectDetectorRenderable;
import org.ilite.vision.camera.tools.colorblob.histogram.HistoGramImageWindow;
import org.ilite.vision.constants.ECameraType;


//Based off of: https://github.com/Itseez/opencv/blob/master/samples/android/color-blob-detection/src/org/opencv/samples/colorblobdetect/ColorBlobDetectionActivity.java
/**
 * @author Daniel
 *
 */
public class ColorBlobTrainer implements ICameraFrameUpdateListener {
    
    private static final Logger sLog = Logger.getLogger(ColorBlobTrainer.class);

    private ImageWindow mWindow = new ImageWindow(null, "Raw Image",true,true);
    private HistoGramImageWindow mHisto = new HistoGramImageWindow(null);

    private ICameraConnection mCamera;
    private Renderable renderable;
    private ObjectDetectorRenderable mObjectDetectorRenderable;
    
    public ColorBlobTrainer(final BufferedImage pImage) {
        renderable = new Renderable();
        mWindow.addRenderable(renderable);
        mObjectDetectorRenderable = new ObjectDetectorRenderable(mWindow, false);
        mWindow.addRenderable(mObjectDetectorRenderable);
        mObjectDetectorRenderable.frameAvail(pImage);
        mWindow.getMouseRenderable().addSelectionListener(
                mObjectDetectorRenderable);
        frameAvail(pImage);
        
    }

    public ColorBlobTrainer(ICameraConnection pConnection) {
        mCamera = pConnection;
        mCamera.addCameraFrameListener(this);

        renderable = new Renderable();
        mWindow.addRenderable(renderable);

        mObjectDetectorRenderable = new ObjectDetectorRenderable(mWindow, true);
        mWindow.addRenderable(mObjectDetectorRenderable);
        mCamera.addCameraFrameListener(mObjectDetectorRenderable);
        mWindow.getMouseRenderable().addSelectionListener(
                mObjectDetectorRenderable);
        mWindow.setCameraConnection(mCamera);

    }

    public void connectToCamera() {
        mCamera.start();
    }

    public void show() {
        mWindow.show();
        mHisto.show();
    }

    public static void createAndShowGUI() throws UnknownHostException, IOException {
        OpenCVUtils.init();

        // null IP means connect to webcam
        // Put the IP address to connect to an MPEG-J camera, otherwise null
        // will
        // connect to a local webcam
        //        String ip = null;
        String ip = ECameraType.ALIGNMENT_CAMERA.getCameraIP();
//
//        if(!OpenCVUtils.isIpReachable(ip)) {
//            ip = null;
//        }

        if(sLog.isDebugEnabled()) {
            StringBuilder debugString = new StringBuilder();
            debugString.append("Connecting to camera IP= ").append(ip);
            sLog.debug(debugString);
        }
        ICameraConnection aCameraConnection = CameraConnectionFactory.getCameraConnection(ip);

        ColorBlobTrainer aTrainer = new ColorBlobTrainer(aCameraConnection);
        aTrainer.connectToCamera();
        aTrainer.show();

    }

    public static void main(String[] args) {

        sLog.debug("Starting the Color Blob Trainer");
       
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    sLog.error("Invalid IP Address", e);
                }
            }
        });
    }

    @Override
    public void frameAvail(BufferedImage pImage) {
        mWindow.updateImage(pImage);
        mHisto.updateImage(pImage);

    }

}
