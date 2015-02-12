/*
 * Copyright(C) 2011 Polycom Inc.
 * All Rights Reserved.
 */

package com.example.helloworld.view;

import android.view.SurfaceHolder;

import java.util.logging.Logger;


/**
 * @author ytwang
 */
public class SoftEncodingLocalCallback extends SoftPreviewLocalCallback {

    private static final Logger LOGGER = Logger
            .getLogger(SoftEncodingLocalCallback.class.getName());

    private boolean hasInitLocalPreview = false;
    
    private static final int CAMERA_SIZE_WIDTH_ = 640;
    private static final int CAMERA_SIZE_HEIGHT = 480;
    private static final int CAMERA_FRAME_RATE = 15;
    
    @Override
    public void muteVideo() {
        mCameraHolder.mute();
    }
    
    @Override
    public void unmuteVideo() {
        mCameraHolder.unmute();
    }

    /*
     * (non-Javadoc)
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
     * )
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        if(hasInitLocalPreview == false) {
            startRecording(null, CAMERA_SIZE_WIDTH_, CAMERA_SIZE_HEIGHT, CAMERA_FRAME_RATE);
            hasInitLocalPreview = true;
        } else {
            mCameraHolder.fillBuffer();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.polycom.cmad.mobile.android.SoftPreviewLocalCallback#beforePreviewStart
     * ()
     */
    @Override
    protected void beforePreviewStart() {
//        mCameraHolder.fillBuffer();
    }

    /*
     * (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
     * SurfaceHolder)
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    public void startRecording(String sourceId, int width, int height, int frameRate){
        LOGGER.info("startFillBuffer, sourceId:"+sourceId);
        mCameraHolder.setSourceId(sourceId);
        mCameraHolder.setCameraSize(width, height);
        mCameraHolder.setCameraFrameRate(frameRate);
        
        mCameraHolder.fillBuffer();
    }
    
    public void stopRecording(){
        mCameraHolder.unfillBuffer();
    }
    
}
