package com.example.helloworld;

import com.example.helloworld.HelloActivity.OnPreviewListener;
import com.example.helloworld.codec.AvcDecoder;
import com.example.helloworld.codec.AvcEncoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView.SurfaceTextureListener;

import java.util.Iterator;
import java.util.List;


class CameraPreview implements Camera.PreviewCallback, Camera.ErrorCallback,
        SurfaceHolder.Callback, SurfaceTextureListener{
    private static final String TAG = "CameraPreview";
	private SurfaceHolder mholder;
	private SurfaceTexture mTexture = null;
	private volatile Looper mServiceLooper = null;
	private volatile CameraHandler mHandler = null;
	HandlerThread mThread = null;
	private Object mLock = new Object();
	
	private static final int BUFFER_SIZE = 2;
	
    private static final int OPEN_WITH_HOLDER = 0;
    private static final int OPEN_WITH_TEXTURE = 1;
    private static final int RELEASE = 2;
	
    private OnPreviewListener mPreviewListener;
    
    
    
    private AvcDecoder mDecoder;
    private AvcEncoder mEncoder;
    
    private Surface mOutSurface;
    
    private static int BIT_RATE= 125000;
    private static int FRAME_RATE= 15;
    
    public CameraPreview(int cameraIndex, int width, int height, OnPreviewListener previewListener,Surface outSurface) {
        videoWidth = width;
        videoHeight = height;
        nCamIndex = cameraIndex;
        mPreviewListener = previewListener;
        
        mEncoder = new AvcEncoder(videoWidth,videoHeight,BIT_RATE,30);
        
        mDecoder = new AvcDecoder(videoWidth,videoHeight,outSurface);
        
        
    }
    
    public CameraPreview(int cameraIndex, int width, int height, OnPreviewListener previewListener) {
        videoWidth = width;
        videoHeight = height;
        nCamIndex = cameraIndex;
        mPreviewListener = previewListener;
        
        
        
        //mDecoder = new AvcDecoder(videoWidth,videoHeight,outSurface);
        
        
    }
    

	private boolean isSupportMultiCamera() {
		return Camera.getNumberOfCameras() > 1;
	}

	private boolean start(SurfaceHolder holder, int nIndex, int w, int h) {
		Log.d(TAG,"in start SurfaceHolder");
		if ( mThread != null )
			return false;
		
		mThread = new HandlerThread("LocalPreviewThread", Process.THREAD_PRIORITY_BACKGROUND);
		mThread.start();
		mServiceLooper = mThread.getLooper();
		mHandler = new CameraHandler(mServiceLooper);
		
		videoWidth = w;
		videoHeight = h;
		mholder = holder;
		nCamIndex = nIndex;

		Thread tid = Thread.currentThread();
		Log.e(TAG, "[mfwCameraAndroid] start tid = " + tid.getId());
		try {
			Message msg = new Message();
			msg.what = OPEN_WITH_HOLDER;
			if ( mHandler != null) {
				mHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, "[mfwCameraAndroid] leave start");
	    return true;
	}
	
	private boolean start(SurfaceTexture texture, int nIndex, int w, int h) {
		Log.d(TAG,"in start SurfaceTexture");
        if ( mThread != null )
            return false;
        
        mThread = new HandlerThread("LocalPreviewThread", Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();
        mServiceLooper = mThread.getLooper();
        mHandler = new CameraHandler(mServiceLooper);
        
        videoWidth = w;
        videoHeight = h;
//        mTexture = texture;
        nCamIndex = nIndex;

        Thread tid = Thread.currentThread();
//        Log.e(TAG, "[mfwCameraAndroid] start tid = " + tid.getId());
        try {
            Message msg = new Message();
            msg.what = OPEN_WITH_TEXTURE;
            if ( mHandler != null) {
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d(TAG, "[mfwCameraAndroid] leave start");
        return true;
    }
    
	private boolean startImpl(SurfaceTexture texture, int nIndex, int w, int h) {
        if (mCamera != null) {
            Log.e(TAG, "[mfwCameraAndroid] startCam reopened");
            return false;
        }

        if (!isSupportMultiCamera()) {
            nIndex = 0;
        }

        videoWidth = w;
        videoHeight = h;

        Log.d(TAG, "[mfwCameraAndroid] enter  startCam nIndex=" + nIndex);

        try {
            mCamera = Camera.open(nIndex);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "[mfwCameraAndroid] Camera open Exception ");
            e.printStackTrace();
        }
        if (mCamera == null)
            return false;

        try {
            setupCamera();
            mCamera.setPreviewTexture(texture);
            mCamera. setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            mCamera.release();
            mCamera = null;
            e.printStackTrace();
            return false;
        }


        Log.d(TAG, "[mfwCameraAndroid] leave  startCam nIndex=" + nIndex);

        
        return true;
    }
    
	private boolean startImpl(SurfaceHolder holder, int nIndex, int w, int h) {
		if (mCamera != null) {
			Log.e(TAG, "[mfwCameraAndroid] startCam reopened");
			return false;
		}

		if (!isSupportMultiCamera()) {
			nIndex = 0;
		}

		videoWidth = w;
		videoHeight = h;

		Log.d(TAG, "[mfwCameraAndroid] enter  startCam nIndex=" + nIndex);

		try {
			mCamera = Camera.open(nIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "[mfwCameraAndroid] Camera open Exception ");
			e.printStackTrace();
		}
		if (mCamera == null)
			return false;

		try {
			setupCamera();
			mCamera.setPreviewDisplay(holder);

			mCamera.startPreview();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mCamera.release();
			mCamera = null;
			e.printStackTrace();
			return false;
		}


		Log.d(TAG, "[mfwCameraAndroid] leave  startCam nIndex=" + nIndex);

		
		return true;
	}

	private void setupCamera() {
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			dumpSupportedPreviewSizes(parameters);
			int minValue;
			int maxValue;

			minValue = parameters.getMinExposureCompensation();
			maxValue = parameters.getMaxExposureCompensation();

			Log.d(TAG, "[mfwCameraAndroid] ExposureCompensation: "
					+ minValue + "<-->" + maxValue);

			// boolean l = parameters.getAutoExposureLock();

			// parameters.setAutoExposureLock(false);
			// parameters.setExposureCompensation(maxValue);
			Log.d(TAG, "[mfwCameraAndroid]  setPreviewSize,  videoWidth = "  +  videoWidth + ", videoHeight=" + videoHeight);

			parameters.setPreviewSize(videoWidth, videoHeight);
			
			// float step = parameters.getExposureCompensationStep();

			// parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			// maxValue = parameters.getMaxZoom();

			Log.d(TAG, "[VideoRecorder] Zoom: " + 0 + "<-->" + maxValue);
			// parameters.setZoom(maxValue);

//			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			parameters.set("recording-hint", "true");
//			parameters.set("mode", "video-mode");
			parameters.set("mode", "video-mode");
			parameters.setPreviewFormat(ImageFormat.NV21);
			// parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			// parameters.setAutoExposureLock(false);
			// ArrayList<Area> mFocusArea = new ArrayList<Area>();
			// mFocusArea.add(new Area(new Rect(200, 200, 400, 400), 1));

			// parameters.setFocusAreas(mFocusArea);
			// parameters.setMeteringAreas(mFocusArea);
			mCamera.setParameters(parameters);
			
//			mCamera.setDisplayOrientation(90);
		} catch (Exception e) {
			Log.e(TAG, "[mfwCameraAndroid] setupCamera setParameters  Exception ");
		}
		
		try {
		   Camera.Parameters parameters  = mCamera.getParameters();

			// set up the capture real size
			Size sz = parameters.getPreviewSize();
		
			int previewFormat = parameters.getPreviewFormat();
	        if (previewFormat == 0) {
	            previewFormat = ImageFormat.NV21;
	        }
	        
			int length = sz.width * sz.height
					* ImageFormat.getBitsPerPixel(previewFormat) / 8;
			Log.d(TAG,
					String.format(
							"frame format is %d, width is %d, height is %d, buf size is %d",
							previewFormat, sz.width, sz.height, length));
			
			for (int i = 0; i < BUFFER_SIZE; i++) {
				mCamera.addCallbackBuffer(new byte[length]);
			}
			mEncoder = new AvcEncoder(sz.width,sz.height,FRAME_RATE,BIT_RATE);
			mDecoder = new AvcDecoder(sz.width,sz.height,mHolder2.getSurface());
			mEncoder.setFrameListener(mDecoder);
		        
		} catch (Exception e) {
			Log.e(TAG, "[mfwCameraAndroid]  setParameters  Exception ");
		}
			
		mCamera.setErrorCallback(this);
	}
	
	private void stop() {
		Log.d(TAG, "[mfwCameraAndroid] enter stop");
		try {
			Message msg = new Message();
			msg.what = RELEASE;
			if ( mHandler != null) {
				mHandler.sendMessage(msg);
				mThread.join();
				mThread = null;
				mHandler = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "[mfwCameraAndroid] leave stop");
	}
	
	private void stopImpl() {
		try {

			if (mCamera != null) {
				Log.d(TAG, "[mfwCameraAndroid] enter  stopCam");
				mCamera.setErrorCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				Log.d(TAG, "[mfwCameraAndroid] leave stopCam");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	@Override
	public void onError(int errorCode, Camera camera) {
		// TODO Auto-generated method stub
		stop();
	}

	private void dumpSupportedPreviewSizes(Parameters parameters) {
		Log.d(TAG, "[mfwCameraAndroid] Start dump.");

		List<Size> supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			Iterator<Camera.Size> itor = supportedPreviewSizes.iterator();
			if (!itor.hasNext()) {
				break;
			}
			if (supportedPreviewSizes.get(i) == null)
				continue;

			Log.d(TAG, "[mfwCameraAndroid] Support size is "
					+ supportedPreviewSizes.get(i).width + "*"
					+ supportedPreviewSizes.get(i).height + " .");
		}
		Log.d(TAG, "[mfwCameraAndroid] Stop dump.");
	}

	class CameraHandler extends Handler {

		public CameraHandler(Looper looper) {
			super(looper);
		}

		@Override
		public synchronized void handleMessage(Message msg) {
			Log.d(TAG, String.format("CameraHandler handleMessage %d", msg.what));
			switch (msg.what) {
			case OPEN_WITH_HOLDER: {
				synchronized (mLock) {
					Log.d(TAG,String.format("CameraHandler startImpl index %d, width %d, height %d", nCamIndex, videoWidth, videoHeight));
					startImpl(mholder, nCamIndex, videoWidth, videoHeight);
					
/*					if (mCamera != null) {
						mCamera.setPreviewCallback(mfwCameraAndroid.this);
					}*/
				}
			}
				break;
            case OPEN_WITH_TEXTURE: {
                synchronized (mLock) {
                    Log.d(TAG,String.format("CameraHandler startImpl index %d, width %d, height %d", nCamIndex, videoWidth, videoHeight));
                    startImpl(mTexture, nCamIndex, videoWidth, videoHeight);
                }
            }
                break;
			case RELEASE: {
				synchronized (mLock) {
					Log.d(TAG, "CameraHandler stopImpl");
					stopImpl();
					mServiceLooper.quit();
				}
			}
				break;
			}
		}
	}
	
    @Override
    protected void finalize() throws Throwable {
    	stopImpl();
        super.finalize();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	
    	Log.d(TAG,"in surfaceChanged");
        holder.setFixedSize(width, height);
    }

   

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }
    
	private Camera mCamera;
	private int videoWidth;
	private int videoHeight;
	private int nCamIndex;
	
	
	private static boolean mIs2Ready = false;
	private static boolean mIs1Ready = false;
	
	 @Override
	    public void surfaceCreated(SurfaceHolder holder) {
		 Log.d(TAG,"in surfaceCreated");
//		 	mIs2Ready = true;
//		 	
//		 	if(mIs1Ready)
//		 	{
//		 		start(mTexture, nCamIndex, videoWidth, videoHeight);
//		 	}
		 start(mTexture, nCamIndex, videoWidth, videoHeight);
	        
	    }
	 
	 
	 
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    	Log.d(TAG,"in onSurfaceTextureAvailable");
        mTexture = surface;
        mIs1Ready = true;
        if(mIs2Ready)
        	start(surface, nCamIndex, videoWidth, videoHeight);
//        if (mPreviewListener != null) {
//            mPreviewListener.onPreviewCreated();
//        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mTexture = null;
        mIs1Ready = mIs2Ready = false;
        stop();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
    
    public void onResume() {
        if (mTexture == null) {
            return;
        }
        start(mTexture, nCamIndex, videoWidth, videoHeight);
    }
    
    
    public void onPause() {
        stop();
    }
    
    static boolean isUseFrontCamera = true;
    public void switchCamera()
    {
    	Log.d(TAG,"in switchCamera");
    	stop();
    	
    	
    	
    	if(nCamIndex == CameraInfo.CAMERA_FACING_FRONT)
    	{
    		nCamIndex = CameraInfo.CAMERA_FACING_BACK;
    	}else
    	{
    		nCamIndex = CameraInfo.CAMERA_FACING_FRONT;
    	}
    	
    	start(mTexture, nCamIndex, videoWidth,videoHeight ) ;
    }
    
    public void setOutSurface(Surface surface) {
		// TODO Auto-generated method stub
    	mOutSurface = surface;
    	mDecoder.setOutSurface(mOutSurface);
    	mEncoder.setFrameListener(mDecoder);
    	
    	
	}
    private SurfaceHolder mHolder2;
    public int textureBuffer[];
    private int bufferSize;
    public Bitmap gBitmap;
    private Rect gRect;
	public void setHolder2(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		mHolder2 = holder;
//		bufferSize = 800 * 600;
//		textureBuffer=new int[bufferSize];
//		gBitmap= Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
//		gRect=new Rect(0,0,1800,1024);
	}
    
    @Override
    public synchronized void onPreviewFrame(byte[] data, Camera camera)
	{
		Log.d(TAG, " onPreviewFrame "+ data.length);
		
//    	mEncoder.offerEncoder(input, output);
    	mEncoder.onFrame(data, 0, data.length, 0);
    	
//    	for(int i=0;i<textureBuffer.length;i++)
//    	textureBuffer[i]=0xff000000|data[i];
//		gBitmap.setPixels(textureBuffer, 0, 800, 0, 0, 800, 600);
//		synchronized (mHolder2)
//		{        
//		Canvas canvas = mHolder2.lockCanvas();
//		canvas.drawBitmap(gBitmap, null,gRect, null);
//		        //canvas.drawBitmap(textureBuffer, 0, screenWidth, 0, 0, screenWidth, screenHeight, false, null);
//		          mHolder2.unlockCanvasAndPost(canvas);
//		 }
    	
    	
    	
    	
		if (mCamera != null) {
			mCamera.addCallbackBuffer(data);
		}
		
	}
    
    

	
}
