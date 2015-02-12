/**
 * 
 */
package com.example.helloworld.camera;


import com.example.helloworld.view.VideoCell;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author tonywang
 *
 */
public class CameraHolder implements PreviewCallback {
    
    private static final Logger LOGGER = Logger
            .getLogger(CameraHolder.class.getName());
	
	private static final int OPEN = 0;
	private static final int RELEASE = 1;
	private static final int SWITCH = 2;
	private static final int MUTE = 3;
	private static final int UNMUTE = 4;
	private static final int FILL_BUFFER = 5;
	private static final int START_PREVIEW = 6;
	private static final int STOP_PREVIEW = 7;
	private static final int UNFILL_BUFFER = 8;
	/**
	 * Sets larger resolution for main activity preview.
	 */
	private static final int SET_RESOLUTION = 9;
	
	private static final int RELEASE_DELAY = 2000;
    private static final int FPS = 15;
    private static final int BUFFER_SIZE = 1;
    
    private volatile AtomicInteger currentFPS = new AtomicInteger(FPS);

	private Handler mHandler;
	private Camera mCamera;
	private int mResolution;
	
	private Camera.Size actualFrameSize = null;
	
    private AtomicLong mFirstPostTime = new AtomicLong(0);
    private AtomicInteger mFrameCount = new AtomicInteger(0);
	
	private volatile int users;
	private volatile CameraListener mListener;
	private volatile int mCameraId = -1;
	private volatile boolean validSurface;
	private volatile Object mSurfaceHolderLock = new Object();
	private volatile int mDisplayRotationDegree = 0;
	private volatile int mVideoRotationDegree = 0;
	private volatile boolean isFESupportVideoRotation = false;
	
	private Object mLock = new Object();
	
	private static CameraHolder INSTANCE;
	
	// added to support multiple resolution
	// when the camera is opened the first time, store the camera parameters 
	// so that it doesn't need to open the camera each time when the camera parameter
	// is required.
	Camera.Parameters m_cameraParams ;
	
	protected int captureWidth;
    protected int captureHeight;
    private String sourceId;
	
	public static synchronized CameraHolder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CameraHolder();
		}
		return INSTANCE;
	}
	
	public static boolean supportMultipleCamera() {
		return Camera.getNumberOfCameras() > 1;
	}

	private CameraHolder() {
	   
		HandlerThread t = new HandlerThread("CameraHolderThread", Process.THREAD_PRIORITY_BACKGROUND);
		t.start();
		mHandler = new CameraHandler(t.getLooper());
		
		if (CameraInfo.CAMERA_FACING_FRONT < Camera.getNumberOfCameras()) {
			mCameraId = CameraInfo.CAMERA_FACING_FRONT;
		} else {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}
		
	}
	
	public void openAsync() {
		if (users != 0) {
			LOGGER.info(String.format("use is %d, ignore openAsync.", users));
			return;
		}
		
		mHandler.removeMessages(RELEASE);
		users ++;
		mHandler.sendEmptyMessage(OPEN);
		LOGGER.info("finish openAsync user: " + users);
	}
	
	public void closeAsync() {
		if (users != 1) {
			LOGGER.info(String.format("use is %d, ignore closeAsync.", users));
			return;
		}
		
		cleanQueue();
		-- users;
		LOGGER.info("camera stop preview.");
		mHandler.sendEmptyMessageDelayed(RELEASE, RELEASE_DELAY);
		LOGGER.info("finish closeAsync user: " + users);
	}

	private void cleanQueue() {
        mHandler.removeMessages(OPEN);
        mHandler.removeMessages(RELEASE);
        mHandler.removeMessages(SWITCH);
        mHandler.removeMessages(MUTE);
        mHandler.removeMessages(UNMUTE);
        mHandler.removeMessages(FILL_BUFFER);
    }

    public void mute() {
	    mHandler.sendEmptyMessage(MUTE);
	}
	
	public void unmute() {
	    mHandler.sendEmptyMessage(UNMUTE);
	}
	
	public void fillBuffer() {
	    mHandler.sendEmptyMessage(FILL_BUFFER);
	}
	
	public void unfillBuffer() {
	    mHandler.sendEmptyMessage(UNFILL_BUFFER);
	}
	
	public void startPreview() {
		mHandler.sendEmptyMessage(START_PREVIEW);
	}
	
	public void stopPreview() {
		synchronized (mLock) {
			LOGGER.info(">>> before camera stop preview.");
			if (mCamera != null) mCamera.stopPreview();
			LOGGER.info(">>> after camera stop preview.");
		}
	}
	
	public void setListener(CameraListener mListener) {
		this.mListener = mListener;
		LOGGER.info("setCameraListener." + (mListener == null ? "null" : mListener));
	}
	
	public void removeListener(CameraListener mListener) {
		if (this.mListener == mListener) {
			this.mListener = null;
			LOGGER.info("Camera listener removed.");
		} else {
			LOGGER.warning("Do not remove listener since listener is changed.");
		}
	}
	
	public void switchCamera(int cameraId) {
		if (mCameraId != cameraId) {
			mCameraId = cameraId;
			mHandler.sendEmptyMessage(SWITCH);
		} else {
			mListener.onCameraNoSwitch();
		}
	}

	public void setmCameraId(int mCameraId) {
        this.mCameraId = mCameraId;
    }
	
	public int getCameraId() {
        return this.mCameraId;
    }
	
	public void setCameraDisplayOrientation(int displayDegrees, int videoDegrees) {
	    mVideoRotationDegree = videoDegrees;
	    
		if(mDisplayRotationDegree != displayDegrees) {
			mDisplayRotationDegree = displayDegrees;
			try {
				if(android.os.Build.VERSION.SDK_INT >= 14) {
		        	if(mCamera != null) {
		        		mCamera.setDisplayOrientation(mDisplayRotationDegree);
		        	}
		        }
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// add to support multiple camera resolution
	private static final int RESOLUTION_QVGA = 1;
	private static final int RESOLUTION_VGA = RESOLUTION_QVGA<<1;
	private static final int RESOLUTION_720P = RESOLUTION_VGA<<1;
	

	public int setCameraFrameRate(int frameRate) {
		LOGGER.info("setCameraFrameRate: " + frameRate);
		if(currentFPS.get() != frameRate)
		{
			mFrameCount.set(0);
			mFirstPostTime.set(0);
		}
		currentFPS.set((frameRate > FPS) ? FPS : frameRate);
		
		return currentFPS.get();
	}
	
    public int setCameraSize(int width, int height) {
        
        LOGGER.info("Enter setCameraSize(),  width is : " + width + "height is: "+height);
        
        int nPreviousResolution = mResolution ;
        
        int nRequestCameraSize = getStandardizedResolution(width, height);
        int nAcceptableCameraSize = nRequestCameraSize+nRequestCameraSize-1; 
        int nSupportedCameraSize = getSupportedCameraSize();
        
        int nMatchResult = nAcceptableCameraSize & nSupportedCameraSize ;
       
      
        
        // if camera has opened, need to reopen it to apply the new resolution
        if( ( mCamera != null)&&(nPreviousResolution != mResolution) ) {
            mHandler.sendEmptyMessage(SWITCH);
        }
        
        return mResolution ;
    }
    
    private int getSupportedCameraSize()
    {
        int result = RESOLUTION_QVGA ;  //QVGA should be supported by all camera
        
        // if m_cameraParams has  be got
        if( m_cameraParams != null ) {
            
            List<Camera.Size> listSupportedPreviewResolution = m_cameraParams.getSupportedPreviewSizes();
            
            ListIterator<Camera.Size> it = listSupportedPreviewResolution.listIterator();
            while(it.hasNext()) {
                
                Camera.Size sizeSupported = it.next();
                
                if( (sizeSupported.width == 1280)&&(sizeSupported.height == 720) ) {              // 720p
                    result |= RESOLUTION_720P ;
                }
                else if((sizeSupported.width == 640)&&(sizeSupported.height == 480)) {         //  VGA
                    result |= RESOLUTION_VGA ;
                }
           
            }
        }
        LOGGER.info("Got Supported camera size: "+ result);
        return result;
    }
    private int getStandardizedResolution(int width, int height) {
        
        int result = RESOLUTION_QVGA ;
        
        if( (width >= 1280)&&(height >= 720)) {
            result = RESOLUTION_720P;
        }
        else if( (width >= 640)&&(height >= 360)) {
            result = RESOLUTION_VGA ;
        }
        else {
            result = RESOLUTION_QVGA ;
        }
        
        return result;
    }

    /**
     * 
     */
    private void setParameters(int width, int height) { 	
   	
        captureWidth = width;
        captureHeight = height;
        
        Parameters params = mCamera.getParameters();
        params.setPreviewSize(width, height);
        
        List<Integer> supportedFormats = params.getSupportedPreviewFormats();
        
        if (supportedFormats.contains(ImageFormat.NV21)) {
            params.setPreviewFormat(ImageFormat.NV21);
        } else {
            LOGGER.severe("preview format unspported!");
        }
        
        if(android.os.Build.VERSION.SDK_INT >= 14) {
      		mCamera.setDisplayOrientation(mDisplayRotationDegree);
        }
        
        List<String> focusModes = params.getSupportedFocusModes();
        if(focusModes != null && focusModes.size() > 0) {
        	params.setFocusMode(focusModes.get(0));
        	for(String focusMode : focusModes) {
        		if(focusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
        			params.setFocusMode(focusMode);
        			break;
        		}
        	}
        }

        try {
        	mCamera.setParameters(params);
        } catch (Exception e) {
        	LOGGER.severe("CameraHolder setParameter exception, mResolution = " + mResolution);
        	LOGGER.severe("androidruntime: " + e.getLocalizedMessage());
        	actualFrameSize = getMinMatchSize(width, height);
        	if(actualFrameSize != null) {
        		LOGGER.info("getMinMatchSize success, width = " + actualFrameSize.width + " height = " + actualFrameSize.height);
        		setParameters(actualFrameSize.width, actualFrameSize.height);
        	} else {
        		LOGGER.severe("getMinMatchSize failed");
        	}
        }
    }
    
    private Camera.Size getMinMatchSize(int width, int height) {
        if( m_cameraParams != null ) {
            
            List<Camera.Size> listSupportedPreviewResolution = m_cameraParams.getSupportedPreviewSizes();
            
            Collections.sort(listSupportedPreviewResolution, new Comparator<Camera.Size>() {
				@Override
				public int compare(Size lhs, Size rhs) {
					return lhs.width * lhs.height - rhs.width * rhs.height;
				}            	
            });    
                     
            
            ListIterator<Camera.Size> it = listSupportedPreviewResolution.listIterator();
            
           
        }
        
    	return null;
    }

    class CameraHandler extends Handler {


        public CameraHandler(Looper looper) {
			super(looper);
		}

		@Override
		public synchronized void handleMessage(Message msg) {
		    LOGGER.info(String.format("CameraHandler handleMessage %d", msg.what));
			switch (msg.what) {
			case OPEN:
				if (users == 1) {
					if (mCamera == null) {
		                if (!tryOpen()) {
		                    users --;
		                    CameraListener l = mListener;
		                    if (l != null) {
		                        l.onCameraError();
		                        return;
		                    }
		                }
						LOGGER.info("camera open.");
					} else {
					    LOGGER.info("before camera reconnect.");
					    
					    synchronized (mLock) {
					    	mCamera.release();
					    	mCamera = null;
					    	if (!tryOpen()) {
					    		CameraListener l = mListener;
					    		if (l != null) {
					    			l.onCameraError();
					    			return;
					    		}
					    	}
						}
					    LOGGER.info("after camera reconnect.");
					}
					
			        
					LOGGER.info("after camera set param.");
					
					CameraListener l = mListener;
					if (l != null) {
						l.onCameraOpen(mCamera);
					} else {
					    LOGGER.warning("camera open but camera listener is null");
					}
					
					LOGGER.info("after camera open callback.");
				}
				break;
			case RELEASE:
				if (users == 0) {
					if (mCamera != null) {
						synchronized (mLock) {
							LOGGER.info(">>> before camera release.");
							mCamera.setPreviewCallbackWithBuffer(null);
							mCamera.release();
							mCamera = null;
							LOGGER.info(">>> after camera release.");
						}
						LOGGER.info("camera released.");
					}
				}
				break;
				
			case SWITCH:
				synchronized (mLock) {
					if (mCamera != null) {
						mCamera.setPreviewCallbackWithBuffer(null);
						mCamera.stopPreview();
						mCamera.release();
						mCamera = null;
					}
	                if (!tryOpen()) {
	                    CameraListener l = mListener;
	                    if (l != null) {
	                        l.onCameraError();
	                        return;
	                    }
	                }
				}
				LOGGER.info("camera switched.");
				
		  
				
				CameraListener l = mListener;
				if (l != null) {
					l.onCameraSwitch(mCamera);
				}
				break;
			case MUTE:
			    if (mCamera != null) {
                    mCamera.setPreviewCallbackWithBuffer(null);
		        }
			    
			    mFirstPostTime.set(0);
                mFrameCount.set(0);
			    break;
			    
			case UNMUTE:
			    if (mCamera != null) {
                    fillBufferInternal();
                    mCamera.setPreviewCallbackWithBuffer(CameraHolder.this);
		        }
			    break;
			case FILL_BUFFER:
			    if (mCamera != null) {
                    fillBufferInternal();
                    mCamera.setPreviewCallbackWithBuffer(CameraHolder.this);
                }
			    break;
			case START_PREVIEW:
				if (mCamera != null) {
					synchronized (mSurfaceHolderLock) {
						if (validSurface) {
							mCamera.setDisplayOrientation(mDisplayRotationDegree);
							try {
	                            mCamera.startPreview();
							    mCamera.autoFocus(null);
							} catch(Exception e) {
							    LOGGER.severe("StartPreview fail:" + e);
							}
						}
					}
				}
				break;
			case STOP_PREVIEW:
				if (mCamera != null) {
					mCamera.stopPreview();
				}
				break;
			case SET_RESOLUTION:
				if (mCamera != null) {
					Parameters p = mCamera.getParameters();
					p.setPreviewSize(msg.arg1, msg.arg2);
					mCamera.setParameters(p);
				}
			default:
				break;
			}
			LOGGER.info(String.format("CameraHandler handleMessage %d done", msg.what));
		}
    }

    /**
     * This method should only works in camera thread to avoid thread problem.
     */
    private void fillBufferInternal() {
        Parameters param = mCamera.getParameters();
        Size size = param.getPreviewSize();
        
        if (size == null) {
//            Resolution reso = Resolution.getResolutionFromId(mResolution);
//            size = mCamera.new Size(reso.getWidth(), reso.getHeight());
        }
        
        int previewFormat = param.getPreviewFormat();
        if (previewFormat == 0) {
            previewFormat = ImageFormat.NV21;
        }

        int length = size.width * size.height * ImageFormat.getBitsPerPixel(previewFormat) / 8;
        LOGGER.info(String.format("frame format is %d, width is %d, height is %d, buf size is %d", previewFormat, size.width, size.height, length));

        for (int i = 0; i < BUFFER_SIZE; i++) {
            mCamera.addCallbackBuffer(new byte[1280 * 720 * 3 /2]);
        }
        
        mCamera.setPreviewCallbackWithBuffer(this);
    }
    
    public boolean getIsFESupportVideoRotation( ) {
    	return isFESupportVideoRotation;
	}
    
    public void setIsFESupportVideoRotation(boolean bFESupportVideoRotation) {
    	isFESupportVideoRotation = bFESupportVideoRotation;
	}
    
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        if(sourceId != null) {
//            NativeDataSourceManager.putVideoData(sourceId, data, captureWidth * captureHeight * 3 / 2, 
//                    captureWidth, captureHeight, mVideoRotationDegree/90);
//        }
//        NativeDataSourceManager.putVideoData(VideoCell.SOURCE_ID_LOCAL_PREVIEW, data, captureWidth * captureHeight * 3 / 2, 
//                captureWidth, captureHeight, mVideoRotationDegree/90);
        mCamera.addCallbackBuffer(data);
    }
    
    /**
     * @return 
     * 
     */
    private boolean tryOpen() {
        try {
            mCamera = Camera.open(mCameraId);
            // when the camera is opened for the first time, record the parameters.
            if(m_cameraParams == null ) {
                
                m_cameraParams = mCamera.getParameters();
            }
        } catch (RuntimeException e) {
            // open camera failed.
            LOGGER.log(Level.SEVERE, "Camera open error", e);
            return false;
        }
        return true;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (mCamera != null) {
        	mCamera.release();
        	mCamera = null;
        }
        super.finalize();
    }

	public void setSurfaceFlag(boolean valid) {
		synchronized (mSurfaceHolderLock) {
			validSurface = valid;
		}
	}
	
	
	public void setResolution(int width, int height) {
		mHandler.obtainMessage(SET_RESOLUTION, width, height).sendToTarget();
	}

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
	
	
}
