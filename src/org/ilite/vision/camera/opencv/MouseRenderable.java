package org.ilite.vision.camera.opencv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class MouseRenderable implements IRenderable, MouseListener, MouseMotionListener{
    private final JPanel mView;
    private Point mMousePoint;

    public MouseRenderable(JPanel pPanel) {
	mView = pPanel;
    }

    @Override
    public void mouseDragged(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void mouseMoved(MouseEvent pE) {
	mMousePoint = pE.getPoint();
	mView.repaint();
	
    }

    @Override
    public void mouseClicked(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void mousePressed(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void mouseReleased(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void mouseEntered(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void mouseExited(MouseEvent pE) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void paint(Graphics pGraphics) {
	
	if(mMousePoint != null) {
	    Color oldColor = pGraphics.getColor();
	    pGraphics.setColor(Color.MAGENTA);
	    pGraphics.drawLine(0, mMousePoint.y, mView.getWidth(), mMousePoint.y);
	    pGraphics.drawLine(mMousePoint.x, 0, mMousePoint.x, mView.getHeight());
	    pGraphics.setColor(oldColor);
	}
	
    }

}
