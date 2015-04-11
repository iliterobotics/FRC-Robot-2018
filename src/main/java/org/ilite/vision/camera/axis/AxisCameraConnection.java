package org.ilite.vision.camera.axis;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.ilite.vision.camera.AbstractCameraConnection;
import org.ilite.vision.constants.ECameraConfig;


/**
 * 
 * History: Base code pieced together from code found on java.sun.com forum
 * posting
 * http://forum.java.sun.com/thread.jspa?threadID=494920&start=15&tstart=0
 * 
 * Modified by Carl Gould
 * 
 * @author David E. Mireles, Ph.D.
 * @author Carl Gould
 */
public class AxisCameraConnection extends AbstractCameraConnection implements Runnable {
    private static final ScheduledExecutorService connectionService = Executors.newSingleThreadScheduledExecutor();
    private String ipAddress;
    private String mjpgURL;
    private String username = ECameraConfig.USERNAME.getStringValue();
    private String password = ECameraConfig.PASSWORD.getStringValue();
    private String base64authorization = null;
    private HttpURLConnection huc = null;
    private boolean isPaused;
    private boolean connected = false;
    private MJPEGParser parser;
    private Future<?> cameraFuture;
    private Future<?> connectionFuture;
    private int cameraDelay = (int) ECameraConfig.INITIAL_CAMERA_DELAY.getValue();
    private static final Logger sLogger = 
            Logger.getLogger(AxisCameraConnection.class);
    
    private static final ExecutorService sService = Executors
            .newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable pR) {
                    return new Thread(pR, "AxisCameraRunnable");
                }
            });
    
    private static final ExecutorService sConnectExec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        
        @Override
        public Thread newThread(Runnable pR) {
            return new Thread(pR, "ConnectExec");
        }
    });

    private static final ScheduledExecutorService sScheduler = Executors.newSingleThreadScheduledExecutor();


    /** Creates a new instance of AxisCamera */
    public AxisCameraConnection(String pIp) {
        ipAddress = pIp;
        mjpgURL = "http://" + ipAddress + "/mjpg/video.mjpg";
        
        sLogger.debug("Created Axis Camera Connection with URL= " + mjpgURL);
        
        // only use authorization if all informations are available
         if (username != null && password != null) {
             base64authorization = encodeUsernameAndPasswordInBase64(username,
                                                                     password);
         }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                disconnect();
            }
        });
    }

    /**
     * encodes username and password in Base64-encoding
     */
    private String encodeUsernameAndPasswordInBase64(String usern, String psswd) {
        String s = usern + ":" + psswd;
        String encs = (new Base64Encoder()).encode(s);
        return "Basic " + encs;
    }

    private boolean connect() {
        try {
            sLogger.debug("Starting Connect");
            URL u = new URL(mjpgURL);
            huc = (HttpURLConnection) u.openConnection();

            // if authorization is required set up the connection with the
            // encoded authorization-information
            if (base64authorization != null) {
                huc.setDoInput(true);
                huc.setRequestProperty("Authorization", base64authorization);
                huc.connect();
            }
            /*
             * This is the boundary string that my camera uses. I don't know if
             * it is a standard or not, I kind of doubt it...
             */
            String boundary = "--myboundary";
            String contentType = huc.getContentType();
            Pattern pattern = Pattern.compile("boundary=(.*)$");
            Matcher matcher = pattern.matcher(contentType);
            
            try {
                matcher.find();
                boundary = matcher.group(1);
            } catch (Exception e) {
                sLogger.error("Exception while trying to use matcher/boundary", e);
            }

            InputStream is = huc.getInputStream();
            connected = true;
            sLogger.debug("Currently connected");
            parser = new MJPEGParser(is, boundary);
            
            return true;
            
        } catch (IOException e) { // incase no connection exists wait and try
                                  // again, instead of printing the error
            sLogger.error("Caught an IOException while trying to connect, will retry",e);

            huc.disconnect();
            
            sLogger.debug("Retrying connection...");
        } catch (Exception e) {
            sLogger.error("Unexpected error in connect",e);
        }
        
        return false;
    }

    public void disconnect() {
        try {
            if (connected) {
                sLogger.debug("Disconnecting camera");
                parser.setCanceled(true);
                connected = false;
            }
        } catch (Exception e) {
            sLogger.error("Unexpected error in disconnect", e);
        }
    }

    NumberFormat decFormat = DecimalFormat.getNumberInstance();

    public void run() {
        
        try {
            sLogger.debug("Staring parser");
            parser.parse();

        } catch (IOException e) {
            sLogger.error("Exception in parser",e);

        }

    }

    public BufferedImage grabImage() {
        
        sLogger.debug("Grabbing Image");
        byte[] segment = parser.getSegment();

        if (segment == null) {
            sLogger.error("Failed to get a segment, returning null");
            return null;
        }

        if (segment.length > 0) {

            try {

                return ImageIO.read(new ByteArrayInputStream(segment));

            } catch (IOException e1) {
                sLogger.error("Failed to read the image, due to exception: ",e1);

            }
        }
        
        sLogger.error("Segment has no lengths, return null");
        return null;

    }

    @Override
    public void start() {
        sConnectExec.submit(new Runnable() {
            
            @Override
            public void run() {
                sLogger.debug("Starting");
                
                connectionFuture = connectionService.scheduleAtFixedRate(new Runnable() {
                    
                    @Override
                    public void run() {
                        if(connect()) {
                            connectionFuture.cancel(false);

                            sLogger.debug("Beging Parser");
                            sService.submit(AxisCameraConnection.this);
                            sLogger.debug("Executing camera thread");
                            executeCameraThread();
                        }
                    }
                    
                }, 1, 1, TimeUnit.SECONDS);
            }
        });
    }

    private void executeCameraThread() {
        cameraFuture = sScheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                BufferedImage aGrabImage = grabImage();
                notifyListeners(aGrabImage);

            }
        }, cameraDelay, 
           (int) ECameraConfig.CAM_RATE_MILLIS.getValue(), 
           TimeUnit.MILLISECONDS);
        
    }

    @Override
    public void destroy() {
        
    }
    
    public void pauseResume(boolean pShouldPause) {
        isPaused = pShouldPause;
        sLogger.debug("Camera pause state is now: " + pShouldPause);
        
        cameraFuture.cancel(isPaused);
        
        if(!isPaused) {
           executeCameraThread();
        }
    }
}

/**
 * This class parses an MJPEG stream, notifying ChangeListeners whenever a valid
 * segment is encountered.
 * 
 * @author Carl Gould
 */

class MJPEGParser {

    private static final byte[] JPEG_START = new byte[] { (byte) 0xFF, (byte) 0xD8 };

    private static final int INITIAL_BUFFER_SIZE = 4096;

    InputStream in;

    byte[] boundary;

    byte[] segment;

    byte[] buf;

    int cur, len;

    boolean canceled = false;

    public boolean isCanceled() {

        return canceled;

    }

    public void setCanceled(boolean canceled) {

        this.canceled = canceled;

        if (canceled) {

            try {

                // TODO make this thread-safe

                in.close();

            } catch (IOException e) {
                
                
            }

        }

    }

    /**
     * Creates a new MJPEG parser. Call parse() to begin parsing.
     * 
     * @param in
     *            An input stream to parse
     * @param boundary
     *            The boundary marker for this MJPEG stream.
     */

    public MJPEGParser(InputStream in, String boundary) {

        this.in = in;

        this.boundary = boundary.getBytes();

        buf = new byte[INITIAL_BUFFER_SIZE];

        cur = 0;

        len = INITIAL_BUFFER_SIZE;

    }

    /**
     * Reads from the MJPEG input stream, parsing it for JPEG segments. Every
     * time a JPEG segment is found, all registered change listeners will be
     * notifed. They can retrieve the latest segment via getSegment(). Note that
     * this isn't thread-safe: change listeners should retrieve the segment in
     * the same thread in which they are notified.
     * 
     */

    public void parse() throws IOException {

        int b;

        while ((b = in.read()) != -1 && !canceled) {

            append(b);

            if (checkBoundary()) {

                // We found a boundary marker. Process the segment to find the
                // JPEG image in it

                processSegment();

                // And clear out our internal buffer.

                cur = 0;

            }

        }

    }

    /**
     * Processes the current byte buffer. Ignores the last len(BOUNDARY) bytes
     * in the buffer. Searches through the buffer for the start of a JPEG. If a
     * JPEG is found, the bytes comprising the JPEG are copied into the
     * <code>segment</code> field. If no JPEG is found, nothing is done.
     * 
     */

    protected void processSegment() {

        // First, look through the new segment for the start of a JPEG

        boolean found = false;

        int i;

        for (i = 0; i < cur - JPEG_START.length; i++) {

            if (segmentsEqual(buf, i, JPEG_START, 0, JPEG_START.length)) {

                found = true;

                break;

            }

        }

        if (found) {
            int segLength = cur - boundary.length - i;

            segment = new byte[segLength];

            System.arraycopy(buf, i, segment, 0, segLength);
        }

    }

    /**
     * @return The last JPEG segment found in the MJPEG stream.
     */

    public byte[] getSegment() {
        return segment;
    }

    /**
     * Compares sections of two buffers to see if they are equal.
     * 
     * @param b1
     *            The first buffer.
     * @param b1Start
     *            The starting offset into the first buffer.
     * @param b2
     *            The second buffer.
     * @param b2Start
     *            The starting offset into the second buffer.
     * @param len
     *            The number of bytes to compare.
     * @return <code>true</code> if the <code>len</code> consecutive bytes in
     *         <code>b1</code> starting at <code>b1Start</code> are equal to the
     *         <code>len</code>consecutive bytes in <code>b2</code> starting at
     *         <code>b2Start</code>, <code>false</code> otherwise.
     */
    protected boolean segmentsEqual(byte[] b1, int b1Start, byte[] b2, int b2Start, int len) {

        if (b1Start < 0 || b2Start < 0 || b1Start + len > b1.length || b2Start + len > b2.length) {

            return false;

        } else {
            for (int i = 0; i < len; i++) {
                if (b1[b1Start + i] != b2[b2Start + i]) {

                    return false;

                }
            }

            return true;
        }

    }

    /**
     * @return true if if the end of the buffer matches the boundary
     */

    protected boolean checkBoundary() {
        return segmentsEqual(buf, cur - boundary.length, boundary, 0, boundary.length);
    }

    /**
     * @return the length of the internal image buffer in bytes
     */

    public int getBufferSize() {
        return len;
    }

    /**
     * Appends the given byte into the internal buffer. If it won't fit, the
     * buffer's size is doubled.
     * 
     * @param i
     *            the byte to append onto the internal byte buffer
     */
    protected void append(int i) {
        if (cur >= len) {

            // make buf bigger
            byte[] newBuf = new byte[len * 2];

            System.arraycopy(buf, 0, newBuf, 0, len);

            buf = newBuf;

            len = len * 2;

        }

        buf[cur++] = (byte) i;
    }
}