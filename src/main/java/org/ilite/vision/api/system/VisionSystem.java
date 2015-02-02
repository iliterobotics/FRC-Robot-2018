package org.ilite.vision.api.system;

import java.util.LinkedHashSet;

public final class VisionSystem {
    private static LinkedHashSet<VisionListener> listeners;
    
    public static void init() {
        listeners = new LinkedHashSet<VisionListener>();
    }
    
    public static void subscribe(VisionListener listener) {
        listeners.add(listener);
    }
    
    public static void unsubscribe(VisionListener listener) {
        listeners.remove(listener);
    }
}
