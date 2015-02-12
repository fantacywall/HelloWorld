/*
 * Copyright(C) 2011 Polycom Inc.
 * All Rights Reserved.
 */

package com.example.helloworld.view;



import android.view.SurfaceHolder;

/**
 * @author ytwang
 *
 */
public abstract class LocalCallback implements SurfaceTextureCallback {
    
	public static LocalCallback newInstance(boolean isHardware) {
	    
		return  new SoftEncodingLocalCallback();
	}

    LocalCallback() {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // ignore here
    }
    
    public void muteVideo() {};
    public void unmuteVideo() {};

    public abstract void switchCamera(CameraSwitchCallback cb);
    
    public interface CameraSwitchCallback {
        void onCameraOpen();
    }

    public void startRecording(String sourceId, int width, int height, int frameRate) {}

    public void stopRecording() {}
}
