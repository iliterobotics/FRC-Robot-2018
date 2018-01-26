package org.ilite.frc.vision.camera.opencv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ilite.frc.vision.camera.CameraConnectionFactory;
import org.ilite.frc.vision.camera.ICameraConnection;
import org.ilite.frc.vision.camera.opencv.renderables.ObjectDetectorRenderable;
import org.ilite.frc.vision.constants.Paths;
import org.opencv.core.Mat;

public class ImageWindow {

    private Set<IRenderable> mRenderables = new CopyOnWriteArraySet<IRenderable>();
    private JButton pauseButton;
    private JButton generateOverlay;
    private ObjectDetectorRenderable listener;
    private JButton saveImageButton;
    
    private JPanel mImagePanel = new JPanel() {
        
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            if (mCurrentFrame != null) {
                g.drawImage(mCurrentFrame, 0, 0, mCurrentFrame.getWidth(), mCurrentFrame.getHeight(), null);
            }

            for (IRenderable renderables2 : mRenderables) {
                renderables2.paint(g, mCurrentFrame);
            }
        }
    };

    public boolean isPaused() {
        return pauseButton.getText().equals("resume");
    }
    
    public void addRenderable(IRenderable pRenderable) {
        mRenderables.add(pRenderable);

        mImagePanel.repaint();
    }
    
    public ImageWindow(BufferedImage pImage) {
        this(pImage,"");
    }
    
    public ImageWindow(BufferedImage pImage, String pTitle) {
        this(pImage, pTitle, false,false);
    }

    public ImageWindow(BufferedImage pImage, boolean pShowPause) {
        this(pImage, "", pShowPause,false);
    }

    public ImageWindow(BufferedImage pImage, String pWindowTitle, boolean pShowPause, boolean pShowGenOverlay) {

        mFrame = new JFrame(pWindowTitle);
        mCurrentFrame = pImage;

        if (mCurrentFrame != null) {
            mImagePanel.setPreferredSize(new Dimension(
                    pImage.getWidth(), pImage.getHeight()));
        }
        generateOverlay = new JButton("generate overlay");
        generateOverlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onGenerateOverlayClicked();
            }
            
        });
        generateOverlay.setEnabled(false);
        generateOverlay.setVisible(pShowGenOverlay);
        
        saveImageButton = new JButton("save current frame");
        saveImageButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File dir = new File(Paths.IMAGES_FOLDER_PATH.getValue());
                JFileChooser aChooser = new JFileChooser(dir);
                int showOpenDialog = aChooser.showOpenDialog(saveImageButton);
                if (showOpenDialog == aChooser.APPROVE_OPTION ){
                	File file = aChooser.getSelectedFile();
                	try {
                        ImageIO.write(mCurrentFrame, "png", file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                
                
            }
            
        });
        
        pauseButton = new JButton("pause");
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                boolean isPaused = false;
                
                listener.onPauseClicked();
                
                if (pauseButton.getText().equals("pause")) {
                    pauseButton.setText("resume");
                    isPaused = true;
                    
                } else if (pauseButton.getText().equals("resume")) {
                    isPaused = false;
                    pauseButton.setText("pause");
                }
                
                if(mCameraConnection != null) {
                    mCameraConnection.pauseResume(isPaused);
                }
                
                generateOverlay.setEnabled(isPaused);
            }
        });

        mButtonPanel = new JPanel();
        mButtonPanel.setLayout(new FlowLayout());
        mButtonPanel.add(pauseButton);
        mButtonPanel.add(saveImageButton);
        mButtonPanel.add(generateOverlay);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mButtonPanel, BorderLayout.NORTH);
        wrapper.add(mImagePanel, BorderLayout.CENTER);
        
        mFrame.setContentPane(wrapper);
        mFrame.pack();
        mFrame.setResizable(false);
        mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(WindowEvent arg0) {
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                CameraConnectionFactory.destroy();
            }

            @Override
            public void windowClosing(WindowEvent arg0) {
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
            }

            @Override
            public void windowOpened(WindowEvent arg0) {
            }
            
        });
        
        mMouseRenderable = new MouseRenderable(mImagePanel);
        addRenderable(mMouseRenderable);
        mImagePanel.addMouseListener(mMouseRenderable);
        mImagePanel.addMouseMotionListener(mMouseRenderable);
    }

    private BufferedImage mCurrentFrame = null;
    private JFrame mFrame;

    private MouseRenderable mMouseRenderable;
    private JPanel mButtonPanel;
    private ICameraConnection mCameraConnection;

    public void updateImage(BufferedImage pImage) {

        mCurrentFrame = pImage;
        if (mCurrentFrame != null) {
            mImagePanel.setPreferredSize(new Dimension(mCurrentFrame
                    .getWidth(), mCurrentFrame.getHeight()));
        }

        mImagePanel.repaint();
        mFrame.pack();

    }
    public void updateImage(Mat mImage) {

        BufferedImage bImage = OpenCVUtils.toBufferedImage(mImage);
        updateImage(bImage);

    }
    
    public void show() {
        mFrame.setVisible(true);
    }

    public void addMouseListener(MouseListener pListener) {
        mImagePanel.addMouseListener(pListener);
    }

    public void removeMouseListener(MouseListener pListener) {
        mImagePanel.removeMouseListener(pListener);
    }

    public void addMouseMotionListener(MouseMotionListener pListener) {
        mImagePanel.addMouseMotionListener(pListener);
    }

    public void removeMouseMotionListener(MouseMotionListener pListener) {
        mImagePanel.removeMouseMotionListener(pListener);
    }

    public MouseRenderable getMouseRenderable() {
        return mMouseRenderable;
    }
    
    public void setListener(ObjectDetectorRenderable listener) {
        this.listener = listener;
    }
    
    public ObjectDetectorRenderable getListener() {
        return listener;
    }

    public void repaint() {
        mImagePanel.repaint();
    }
    
    public void addComponentToButtonPanel(final JComponent pComponent) {
        if(SwingUtilities.isEventDispatchThread()) {
            mButtonPanel.add(pComponent);
            mButtonPanel.revalidate();
            mFrame.revalidate();
          
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                
                @Override
                public void run() {
                    addComponentToButtonPanel(pComponent);
                }
            });
        }
    }

    public void setCameraConnection(ICameraConnection pCamera) {
        mCameraConnection =pCamera;
        
    }

}
