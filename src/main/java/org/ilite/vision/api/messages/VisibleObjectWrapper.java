package org.ilite.vision.api.messages;

import java.awt.Point;
import java.util.List;

/**
 * A wrapper class / controller class for a visible object
 * @author Daniel Christopher
 * @version 2/1/15
 *
 */
public class VisibleObjectWrapper {
    private final VisibleObject visibleObject;
    
    public VisibleObjectWrapper(VisibleObject visibleObject) {
        this.visibleObject = visibleObject;
    }
    
    public String getName() {
        return visibleObject.getName();
    }
    
    public List<Point> getPoints() {
        return visibleObject.getPoints();
    }
    
    public Point getCenterPoint() {
        return visibleObject.getCenterPoint();
    }
}
