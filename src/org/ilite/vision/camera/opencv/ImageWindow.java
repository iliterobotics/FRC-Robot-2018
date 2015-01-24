package org.ilite.vision.camera.opencv;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageWindow {

    private Set<IRenderable>mRenderables = new CopyOnWriteArraySet<IRenderable>();

    private JPanel mImagePanel = new JPanel() {

	protected void paintComponent(java.awt.Graphics g) {
	    super.paintComponent(g);

	    if(mCurrentFrame != null) {
		g.drawImage(mCurrentFrame, 0, 0, getWidth(), getHeight(), null);
	    }

	    for(IRenderable renderables2 : mRenderables ){
		renderables2.paint(g);
	    }
	}
    };
    public void addRenderable(IRenderable pRenderable) {
	mRenderables.add(pRenderable);

	mImagePanel.repaint();
    }

    public ImageWindow(BufferedImage pImage) {
	this(pImage, "");
    }
    public ImageWindow(BufferedImage pImage, String pWindowTitle) {

	mFrame = new JFrame(pWindowTitle);
	mCurrentFrame = pImage;
	if (mCurrentFrame != null) {
	    mImagePanel.setPreferredSize(new Dimension(
		    mCurrentFrame.getWidth(), mCurrentFrame.getHeight()));
	}
	mFrame.setContentPane(mImagePanel);
	mFrame.pack();
	mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


	MouseRenderable aMouseRenderable = new MouseRenderable(mImagePanel);
	addRenderable(aMouseRenderable);
	mImagePanel.addMouseListener(aMouseRenderable);
	mImagePanel.addMouseMotionListener(aMouseRenderable);
    }

    private BufferedImage mCurrentFrame = null;
    private JFrame mFrame;


    public void updateImage(BufferedImage pImage) {



	mCurrentFrame = pImage;
	if (mCurrentFrame != null) {
	    mImagePanel.setPreferredSize(new Dimension(
		    mCurrentFrame.getWidth(), mCurrentFrame.getHeight()));
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


}
