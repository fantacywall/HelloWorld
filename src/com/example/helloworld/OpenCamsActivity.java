package com.example.helloworld;



import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.widget.Button;

public class OpenCamsActivity extends Activity  {
	
	
	private TextureView m_localPreview;
	private CameraPreview2 m_cameraVideo;
	
	private SurfaceView m_1sur;
	private SurfaceView m_2sur;
	
	private SurfaceView m_ffPreview;
	private CameraPreview2 m_cameraVideo2;
	
	
	private TextureView m_otherPreview;
	
	private LancherLayout m_launcher;
	private Button mSwitchCamera;
//	private Camera mCamera;
//    private TextureView mTextureView;

	
	private static int W_PREVIEW  =  800;
	private static int H_PREVIEW  =  600;
	
	
	private static final String TAG = "OpenCamsActivity";
	
	
	private SurfaceHolder mSurfaceHolder1;
	private SurfaceHolder mSurfaceHolder2;
	private Camera mCamera1;	
	private Camera mCamera2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setContentView(R.layout.activity_hello);
		//m_launcher = (LancherLayout) this.findViewById(R.id.main_lay);
		
		
//		setContentView(R.layout.activity_hello_test);
//		initViews();
//		setEventHandler();
		
//		setContentView(R.layout.activity_hello);
//		initViews2();
//		setEventHandler2();
		
		
		open2Camera();
		
	}
	
	private void open2Camera() {
	// TODO Auto-generated method stub
//		setContentView(R.layout.activity_hello_test);
		setContentView(R.layout.activity_2_cam);
		initViews2();
//		setEventHandler2();
}

	private void initViews2() {
		// TODO Auto-generated method stub
			m_1sur = (SurfaceView) findViewById(R.id.pre_a);
			
			m_2sur = (SurfaceView) findViewById(R.id.pre_b);
//			m_2sur.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					Log.d(TAG,"onClick to open cam 2");
//					m_cameraVideo2 = new CameraPreview2(CameraInfo.CAMERA_FACING_BACK, W_PREVIEW, H_PREVIEW,null );
//					m_2sur.getHolder().addCallback(m_cameraVideo2);
//				}
//	             
//	         });
			mSurfaceHolder1 = m_1sur.getHolder();
//			mSurfaceHolder2 = m_2sur.getHolder();
			mSurfaceHolder1.addCallback(mSurfaceCallback1);
//			mSurfaceHolder2.addCallback(mSurfaceCallback2);
			
	}
	
	
	
	
	
	
	
	
	private void setEventHandler2() {
        
        
      
        
        m_cameraVideo = new CameraPreview2(CameraInfo.CAMERA_FACING_FRONT, W_PREVIEW, H_PREVIEW,null );
        m_cameraVideo2 = new CameraPreview2(CameraInfo.CAMERA_FACING_BACK, W_PREVIEW, H_PREVIEW,null );
        m_1sur.getHolder().addCallback(m_cameraVideo);
//        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
        
//        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
        
        
       
        m_2sur.getHolder().addCallback(m_cameraVideo2);
        
        
//        m_cameraVideo2 = new CameraPreview(CameraInfo.CAMERA_FACING_BACK, width, height,null );
        //m_cameraVideo.setOutSurface(m_ffPreview.getHolder().getSurface());
//        m_otherPreview.setSurfaceTextureListener(m_cameraVideo2);
       
        
        
        
    }
	
	
//	private void initViews() {
//	// TODO Auto-generated method stub
//		m_localPreview = (TextureView) findViewById(R.id.pre_vv);
//		m_localPreview.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(m_cameraVideo != null)
//				{
//					m_cameraVideo.switchCamera();
//				}
//			}
//             
//         });
//		
//		mSwitchCamera = (Button) findViewById(R.id.btn_switch);
//		mSwitchCamera.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(m_cameraVideo != null)
//				{
//					m_cameraVideo.switchCamera();
//				}
//			}
//             
//         });
//		
//		m_ffPreview = (SurfaceView) findViewById(R.id.after_vv);
//		}
//	
//	
//	
	
//	private void setEventHandler() {
//        int width = 400;
//        int height = 300;
//        
//      
//        
//        m_cameraVideo = new CameraPreview(CameraInfo.CAMERA_FACING_FRONT, width, height,null );
//        //m_cameraVideo.setOutSurface(m_ffPreview.getHolder().getSurface());
//        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
//        m_cameraVideo.setHolder2(m_ffPreview.getHolder());
//        m_ffPreview.getHolder().addCallback(m_cameraVideo);
//        
//        
//        
////        m_cameraVideo2 = new CameraPreview(CameraInfo.CAMERA_FACING_BACK, width, height,null );
////        m_ffPreview.setSurfaceTextureListener(m_cameraVideo2);
//        
//        
//    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello, menu);
		return true;
	}
	
	public interface OnPreviewListener
    {
        void onPreviewCreated();
    }
	
	
	@SuppressLint("NewApi")
	/* SurfaceHodler回调,处理打开相机、关闭相机以及照片尺寸的改变 */
	SurfaceHolder.Callback mSurfaceCallback1 = new Callback() {		
	
		@SuppressWarnings("deprecation")
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
		{				
			// 若未正确连接生成camera，返回
	        if (mCamera1 == null)  return;
	        
//			// 获取相机参数
//			Camera.Parameters mParameters1 = mCamera1.getParameters();
//			// 获取系统支持的照片尺寸
//			List<Camera.Size> mList1 = mParameters1.getSupportedPictureSizes();
//			// 设置照片尺寸
//			Camera.Size mCameraSize1 = mList1.get(0);
//			mParameters1.setPictureSize(mCameraSize1.width, mCameraSize1.height);
//			// 设置照片格式
//			mParameters1.setPictureFormat(PixelFormat.JPEG);
//			// 设置相机参数
//			mCamera1.setParameters(mParameters1);
			// 开始预览
			mCamera1.startPreview();
		}

		public void surfaceCreated(SurfaceHolder holder) 
		{
			//打开后置camera
			mCamera1 = Camera.open(0);
//			mCamera1.setDisplayOrientation(90);
						
			try {
				mCamera1.setPreviewDisplay(holder);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera1 != null) {
			mCamera1.stopPreview();
			mCamera1.release();
			mCamera1 = null;
			}
		}
	};

	SurfaceHolder.Callback mSurfaceCallback2 = new Callback() 
	{		
	
		@SuppressWarnings("deprecation")
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
		{	
		
			// 若未正确连接生成camera，返回
	        if (mCamera2 == null)  return;
			// 开始预览
			mCamera2.startPreview();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			
			//打开前置camera
			mCamera2 = Camera.open(1);
			
			// 设置预览
			try {
				// 注意这里一定要是holder，而不是mSurfaceHolder
				mCamera2.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			
			if (mCamera2 != null) {
			mCamera2.stopPreview();
			mCamera2.release();
			mCamera2 = null;
			}
		}
	};
 



}
