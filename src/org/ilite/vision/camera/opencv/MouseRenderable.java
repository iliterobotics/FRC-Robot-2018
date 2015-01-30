package org.ilite.vision.camera.opencv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JPanel;

public class MouseRenderable implements IRenderable, MouseListener,
        MouseMotionListener {
    private final JPanel mView;
    private Point mMousePoint;
    private Point mDragStart = null;
    private Point mDragEnd = null;

    private Set<ISelectionChangedListener> mSelectionListeners = new CopyOnWriteArraySet<ISelectionChangedListener>();

    public MouseRenderable(JPanel pPanel) {
        mView = pPanel;
    }

    @Override
    public void mouseDragged(MouseEvent pE) {
        if (mDragStart != null) {
            mDragEnd = pE.getPoint();
        }
        mView.repaint();

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
        mDragStart = pE.getPoint();
        mDragEnd = mDragStart;
        mView.repaint();

    }

    @Override
    public void mouseReleased(MouseEvent pE) {
        Rectangle selectionRect = getSelectionRectangle();
        mDragStart = null;
        mDragEnd = null;
        mView.repaint();
        notifyListener(selectionRect);

    }

    @Override
    public void mouseEntered(MouseEvent pE) {

    }

    @Override
    public void mouseExited(MouseEvent pE) {

    }

    @Override
    public void paint(Graphics pGraphics, BufferedImage pImage) {

        if (mMousePoint != null) {
            Color oldColor = pGraphics.getColor();
            pGraphics.setColor(Color.MAGENTA);
            pGraphics.drawLine(0, mMousePoint.y, mView.getWidth(),
                    mMousePoint.y);
            pGraphics.drawLine(mMousePoint.x, 0, mMousePoint.x,
                    mView.getHeight());
            pGraphics.setColor(oldColor);
        }

        if (mDragStart != null && mDragEnd != null) {
            pGraphics.setColor(Color.RED);

            Rectangle selectRect = getSelectionRectangle();

            pGraphics.drawRect(selectRect.x, selectRect.y, selectRect.width,
                    selectRect.height);
        }

    }

    private Rectangle getSelectionRectangle() {
        int startX = Math.min(mDragStart.x, mDragEnd.x);
        int startY = Math.min(mDragStart.y, mDragEnd.y);

        int endX = Math.max(mDragStart.x, mDragEnd.x);
        int endY = Math.max(mDragStart.y, mDragEnd.y);

        int width = Math.abs(startX - endX);
        int height = Math.abs(startY - endY);

        return new Rectangle(startX, startY, width, height);

    }

    public void addSelectionListener(ISelectionChangedListener pListener) {
        mSelectionListeners.add(pListener);
    }

    public void removeSelectionListener(ISelectionChangedListener pListener) {
        mSelectionListeners.remove(pListener);
    }

    private void notifyListener(Rectangle pNewRect) {
        for (ISelectionChangedListener aListener : mSelectionListeners) {
            aListener.selectionBoundsChanged(pNewRect);
        }
    }

}
