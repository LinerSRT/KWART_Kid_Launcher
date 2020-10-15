package com.sgtc.launcher.ClockSkin.Util;

import android.os.SystemClock;

public class FPSCounter {
    private int mFPS = 0;
    private int mFPSCounter = 0;
    private long mFPSTime = 0;

    public FPSCounter() {
    }

    public int getFPS(){
        if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
            mFPSTime = SystemClock.uptimeMillis();
            mFPS = mFPSCounter;
            mFPSCounter = 0;
        } else {
            mFPSCounter++;
        }
        return mFPS;
    }

}
