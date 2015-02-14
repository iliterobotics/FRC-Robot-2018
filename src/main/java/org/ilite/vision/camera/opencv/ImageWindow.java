package org.ilite.vision.camera.opencv;

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
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ilite.vision.camera.CameraConnectionFactory;

public class ImageWindow {

    private Set<IRenderable> mRenderables = new CopyOnWriteArraySet<IRenderable>();
    private JButton pauseButton;
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

    public void addRenderable(IRenderable pRenderable) {
        mRenderables.add(pRenderable);

        mImagePanel.repaint();
    }
    
    public ImageWindow(BufferedImage pImage) {
        this(pImage, false);
    }

    public ImageWindow(BufferedImage pImage, boolean pShowPause) {
        this(pImage, "", pShowPause);
    }

    public ImageWindow(BufferedImage pImage, String pWindowTitle, boolean pShowPause) {

        mFrame = new JFrame(pWindowTitle);
        mCurrentFrame = pImage;

        if (mCurrentFrame != null) {
            mImagePanel.setPreferredSize(new Dimension(
                    pImage.getWidth(), pImage.getHeight()));
        }

        saveImageButton = new JButton("save current frame");
        saveImageButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File dir = new File("src/main/resources/images/");
                File file = new File("src/main/resources/images/" + "image" + dir.listFiles().length + ".png");
                
                try {
                    ImageIO.write(mCurrentFrame, "png", file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            
        });
        
        pauseButton = new JButton("pause");
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (pauseButton.getText().equals("pause")) {
                    pauseButton.setText("resume");

                    CameraConnectionFactory.getCameraConnection(null).pauseResume(true);
                } else if (pauseButton.getText().equals("resume")) {
                    pauseButton.setText("pause");
                    CameraConnectionFactory.getCameraConnection(null).pauseResume(false);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(pauseButton);
        buttonPanel.add(saveImageButton);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(buttonPanel, BorderLayout.NORTH);
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

    public void updateImage(BufferedImage pImage) {

        mCurrentFrame = pImage;
        if (mCurrentFrame != null) {
            mImagePanel.setPreferredSize(new Dimension(mCurrentFrame
                    .getWidth(), mCurrentFrame.getHeight()));
        }

        mImagePanel.repaint();
        mFrame.pack();

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

    public void repaint() {
        mImagePanel.repaint();
    }

}
