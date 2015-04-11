package org.ilite.vision.camera.opencv;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.ilite.vision.constants.ECameraConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Class containing utility methods to go back and forth between OPENCV and Java
 * 
 * @author Christopher
 * 
 */
public class OpenCVUtils {
    
    private static final Logger sLogger = Logger.getLogger(OpenCVUtils.class);

    /**
     * Load the opencv library
     */
    static {

        sLogger.debug("Loading OPENCV library: " + Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    public static void init() {
        sLogger.debug("Starting opencv...");
    }

    /**
     * Method that is used to create a deep copy of a BufferedImage
     * @param image Image to make a copy of
     * @return The copied image
     */
    public static BufferedImage deepCopy(BufferedImage image) {
        return new BufferedImage(image.getColorModel(), 
                                 image.copyData(null), 
                                 image.getColorModel().isAlphaPremultiplied(), 
                                 null);    
    }
    
    /**
     * Helper method to convert an OPENCV {@link Mat} to an {@link Image} If the
     * passed in image is a gray scale, the returned image will be gray. If the
     * passed in image is multi-channel, the return image is RGB
     * 
     * @param pMatrix
     *            The matrix to convert
     * @return The Image
     */
    public static BufferedImage toBufferedImage(Mat pMatrix) {

        int type = BufferedImage.TYPE_BYTE_GRAY;
        
        if (pMatrix.channels() > 1) {
            Mat m2 = new Mat();
            Imgproc.cvtColor(pMatrix, m2, Imgproc.COLOR_BGR2RGB);
            type = BufferedImage.TYPE_3BYTE_BGR;
            pMatrix = m2;
        }
        
        byte[] b = new byte[pMatrix.channels() * pMatrix.cols() * pMatrix.rows()];
        
        pMatrix.get(0, 0, b); // get all the pixels
        
        BufferedImage image = new BufferedImage(pMatrix.cols(), pMatrix.rows(), type);
        
        image.getRaster().setDataElements(0, 0, pMatrix.cols(), pMatrix.rows(), b);
        
        return image;
    }

    /**
     * Method to convert an image
     * 
     * @param pImage
     *            The image to convert
     * @return A {@link Mat} implemetation of the image. The type will be 8-bit,
     *         unsigned, 3 channels (RGB)
     */
    public static Mat toMatrix(BufferedImage pImage) {
        BufferedImage origImage = pImage;


        int cvType = CvType.CV_8UC3;
        
        switch(pImage.getType()) {
        case BufferedImage.TYPE_4BYTE_ABGR:
            origImage = new BufferedImage(pImage.getWidth(), pImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D graphics = origImage.createGraphics();
            graphics.drawImage(pImage, 0, 0, null);
            graphics.dispose();
            break;
        case BufferedImage.TYPE_3BYTE_BGR:
            break;
            default:
                cvType = CvType.CV_8UC1;
                
        }
        byte[] pixels = ((DataBufferByte) origImage.getRaster().getDataBuffer())
                .getData();
        Mat tmp = new Mat(origImage.getHeight(), origImage.getWidth(), cvType);
        tmp.put(0, 0, pixels);
        return tmp;
    }

    public static ImageWindow showImage(BufferedImage pImage) {
        return new ImageWindow(pImage);
    }

    /**
     * Method to convert a scalar's HSV color value to the RGBA Colro Value
     * 
     * @param hsvColor
     * @return
     */
    public static Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
                4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
    

    
    /**
     * Method to see if a passed in IP (for the camera) is pingable
     * @param pTargetIP
     *  The IP address of the target
     * @return
     *  true if the IP is pingable
     */
    public static boolean isAvailable(String pTargetIP) {

        boolean result = true;
        
        try {
            InetAddress target = InetAddress.getByName(pTargetIP);
            result = target.isReachable(5000);  //timeout 5sec
        } catch (UnknownHostException ex) {
            sLogger.error("Unable to reach IP: " + pTargetIP, ex);
        } catch (IOException ex) {
            sLogger.error("IOException while trying to connect to: " + pTargetIP);
        }


        return result;        
    }
}
