package org.ilite.frc.vision.api.messages;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Daniel Christopher
 * @version 2/1/15
 *
 */
public final class VisibleObject {
    private final String name;
    private final LinkedList<Point> points;
    private final Point centerPoint;
    
    public VisibleObject(String name, LinkedList<Point> points, Point centerPoint) {
        this.name = name;
        this.points = points;
        this.centerPoint = centerPoint;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }
    
    public Point getCenterPoint() {
        return new Point(centerPoint);
    }
}
