package com.example.helloworld;



import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

public class HelloActivity extends Activity  {
	
	
	private TextureView m_localPreview;
	private CameraPreview m_cameraVideo;
	
	private SurfaceView m_1sur;
	private SurfaceView m_2sur;
	
	private SurfaceView m_ffPreview;
	private CameraPreview m_cameraVideo2;
	
	
	private TextureView m_otherPreview;
	
	private LancherLayout m_launcher;
	private Button mSwitchCamera;
//	private Camera mCamera;
//    private TextureView mTextureView;

	
	private static final String TAG = "HelloActivity";
	
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
		setEventHandler2();
}

	private void initViews2() {
		// TODO Auto-generated method stub
		m_1sur = (SurfaceView) findViewById(R.id.pre_vv);
//			m_localPreview.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					if(m_cameraVideo != null)
//					{
//						m_cameraVideo.switchCamera();
//					}
//				}
//	             
//	         });
			
			
			
			m_2sur = (SurfaceView) findViewById(R.id.after_vv);
			
	}
	
	
	private void setEventHandler2() {
        int width = 400;
        int height = 300;
        
      
        
        m_cameraVideo = new CameraPreview(CameraInfo.CAMERA_FACING_FRONT, width, height,null );
        Log.d(TAG,"m_cameraVideo is: " + m_cameraVideo);
        Log.d(TAG,"m_1sur is: " + m_1sur);

        m_1sur.getHolder().addCallback(m_cameraVideo);
//        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
        
//        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
        
        
//        m_cameraVideo2 = new CameraPreview(CameraInfo.CAMERA_FACING_BACK, width, height,null );
        //m_cameraVideo.setOutSurface(m_ffPreview.getHolder().getSurface());
//        m_otherPreview.setSurfaceTextureListener(m_cameraVideo2);
       
        
        
        
    }
	
	
	private void initViews() {
	// TODO Auto-generated method stub
		m_localPreview = (TextureView) findViewById(R.id.pre_vv);
		m_localPreview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_cameraVideo != null)
				{
					m_cameraVideo.switchCamera();
				}
			}
             
         });
		
		mSwitchCamera = (Button) findViewById(R.id.btn_switch);
		mSwitchCamera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_cameraVideo != null)
				{
					m_cameraVideo.switchCamera();
				}
			}
             
         });
		
		m_ffPreview = (SurfaceView) findViewById(R.id.after_vv);
		}
	
	
	
	
	private void setEventHandler() {
        int width = 400;
        int height = 300;
        
      
        
        m_cameraVideo = new CameraPreview(CameraInfo.CAMERA_FACING_FRONT, width, height,null );
        //m_cameraVideo.setOutSurface(m_ffPreview.getHolder().getSurface());
        m_localPreview.setSurfaceTextureListener(m_cameraVideo);
        m_cameraVideo.setHolder2(m_ffPreview.getHolder());
        m_ffPreview.getHolder().addCallback(m_cameraVideo);
        
        
        
//        m_cameraVideo2 = new CameraPreview(CameraInfo.CAMERA_FACING_BACK, width, height,null );
//        m_ffPreview.setSurfaceTextureListener(m_cameraVideo2);
        
        
    }
	

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
	
	
	



}
