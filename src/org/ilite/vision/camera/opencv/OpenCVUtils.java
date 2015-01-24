package org.ilite.vision.camera.opencv;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Class containing utility methods to go back and forth between OPENCV and 
 * Java
 * @author Christopher
 *
 */
public class OpenCVUtils {

    /**
     * Load the opencv library
     */
    static {

	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    public static void init() {
	System.out.println("Init method");

    }

    /**
     * Helper method to convert an OPENCV {@link Mat} to an {@link Image}
     * If the passed in image is a gray scale, the returned image will be gray. 
     * If the passed in image is multi-channel, the return image is RGB
     * @param pMatrix
     * 	The matrix to convert
     * @return
     * 	The Image
     */
    public static BufferedImage toBufferedImage(Mat pMatrix){

	int type = BufferedImage.TYPE_BYTE_GRAY;
	if ( pMatrix.channels() > 1 ) {
	    Mat m2 = new Mat();
	    Imgproc.cvtColor(pMatrix,m2,Imgproc.COLOR_BGR2RGB);
	    type = BufferedImage.TYPE_3BYTE_BGR;
	    pMatrix = m2;
	}
	byte [] b = new byte[pMatrix.channels()*pMatrix.cols()*pMatrix.rows()];
	pMatrix.get(0,0,b); // get all the pixels
	BufferedImage image = new BufferedImage(pMatrix.cols(),pMatrix.rows(), type);
	image.getRaster().setDataElements(0, 0, pMatrix.cols(),pMatrix.rows(), b);
	return image;
    }

    /**
     * Method to convert an image 
     * @param pImage
     * 	The image to convert
     * @return
     * 	A {@link Mat} implemetation of the image. The type will be 8-bit, unsigned, 
     * 3 channels (RGB) 
     */
    public static Mat toMatrix(BufferedImage pImage)
    {
	byte[] pixels = ((DataBufferByte) pImage.getRaster().getDataBuffer()).getData();

	int cvType = CvType.CV_8UC3; 
	if(pImage.getType() != BufferedImage.TYPE_3BYTE_BGR) {
	    cvType = CvType.CV_8UC1;
	}
	Mat tmp = new Mat(pImage.getHeight(), pImage.getWidth(), cvType);
	tmp.put(0, 0, pixels);
	return tmp;
    }

    public static ImageWindow showImage(BufferedImage pImage) {
	return new ImageWindow(pImage);
    }
   
    /**
     * Method to convert a scalar's HSV color value to the RGBA Colro Value
     * @param hsvColor
     * @return
     */
    public static Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

}
