/*
 * Copyright(C) 2011 Polycom Inc.
 * All Rights Reserved.
 */
package com.example.helloworld.view;

import static java.util.logging.Level.SEVERE;
import com.example.helloworld.camera.CameraConstants;
import com.example.helloworld.camera.CameraHolder;
import com.example.helloworld.camera.CameraListener;
import com.example.helloworld.camera.DirectVideo;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author ytwang
 *
 */
public class SoftPreviewLocalCallback extends LocalCallback implements CameraListener {
    
    private static final Logger LOGGER = Logger.getLogger(SoftPreviewLocalCallback.class
            .getName());
    private volatile SurfaceHolder mHolder;
    protected CameraHolder mCameraHolder = CameraHolder.getInstance();
    
    private CameraSwitchCallback mSwitchCallback;

    DirectVideo mDirectVideo;
    int texture;
    private SurfaceTexture surface;
    
    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LOGGER.info(">> SoftPreviewLocalCallback surfaceCreated");
        int currentCamera = getSessionCamera();
        mCameraHolder.setmCameraId(currentCamera);
        
        mCameraHolder.setListener(this);
        mCameraHolder.openAsync();
        mHolder = holder;

        texture = createTexture();
        mDirectVideo = new DirectVideo(texture);
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        
        surface = new SurfaceTexture(texture);
        
    	LOGGER.info("<< SoftPreviewLocalCallback surfaceCreated" + mHolder);
    }
    
    private int createTexture()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1,texture, 0);
      
        return texture[0];
        
    }

    private int getSessionCamera() {
    	if (!CameraHolder.supportMultipleCamera()) {
    		return 0;
    	}
    	
    	int currentCamera = CameraConstants.CAMERA_FRONT_FACING;
    	/*IRPService service = BeanFactory.getRPService();
        SessionData sessionData = null;
		try {
		    if(service != null) {
		        sessionData = service.getActiveSessionData();
		    }			
		} catch (RemoteException e) {
		}
        
        if (sessionData != null) {
            currentCamera = sessionData.getCurrentCamera();
        }
        return currentCamera;*/
    	return currentCamera;
    }

    public void startPreview() {
        beforePreviewStart();
        mCameraHolder.startPreview();
    }
    
    protected void beforePreviewStart() {
        // empty.
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LOGGER.info(">> SoftPreviewLocalCallback surfaceDestroyed");
        mHolder = null;
        mCameraHolder.removeListener(this);
        mCameraHolder.setSurfaceFlag(false);
        stopCamera();
        LOGGER.info("<< SoftPreviewLocalCallback surfaceDestroyed");
    }

	private void stopCamera() {
	    mCameraHolder.stopPreview();
	    mCameraHolder.closeAsync();
	}


    /* (non-Javadoc)
     * @see com.polycom.cmad.mobile.android.LocalCallback#switchCamera()
     */
    @Override
    public void switchCamera(CameraSwitchCallback cb) {
        int currentCamera = getSessionCamera();
        mSwitchCallback = cb;
        mCameraHolder.switchCamera(currentCamera);
    }


	@Override
	public void onCameraOpen(Camera camera) {
	    
        if (mHolder == null) return;
        
        try {
        	LOGGER.info("setPreviewDisplay: surface holder is: " + mHolder);
//            camera.setPreviewDisplay(mHolder);
            camera.setPreviewTexture(surface);
            mCameraHolder.setSurfaceFlag(true);
        } catch (IOException e) {
            if (LOGGER.isLoggable(SEVERE)) {
                LOGGER.severe("onCameraOpen: error when setPreviewDisplay");
            }
        }
        startPreview();
	}

	@Override
	public void onCameraSwitch(Camera camera) {
	    if (mHolder == null) return;
        
        try {
//            camera.setPreviewDisplay(mHolder);
            camera.setPreviewTexture(surface);
        } catch (IOException e) {
            if (LOGGER.isLoggable(SEVERE)) {
                LOGGER.severe("onCameraSwitch: error when setPreviewDisplay");
            }
        }
        startPreview();
        
        if (mSwitchCallback != null) {
            mSwitchCallback.onCameraOpen();
        }
	}

	@Override
	public void onCameraError() {
	    if (LOGGER.isLoggable(SEVERE)) {
            LOGGER.severe("onCameraError: error");
        }
	}

	@Override
	public void onCameraNoSwitch() {
		if (mSwitchCallback != null) {
            mSwitchCallback.onCameraOpen();
        }
	}

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        
    }

}
