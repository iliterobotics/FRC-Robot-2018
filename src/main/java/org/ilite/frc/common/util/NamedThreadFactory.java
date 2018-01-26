package org.ilite.frc.common.util;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {
    
    private String mThreadName;

    public NamedThreadFactory(String pThreadName) {
        mThreadName = pThreadName;
    }

    @Override
    public Thread newThread(Runnable pR) {
        return new Thread(pR, mThreadName);
    }

}
