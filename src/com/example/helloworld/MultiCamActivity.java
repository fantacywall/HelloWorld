package com.example.helloworld;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;

public class MultiCamActivity extends Activity {
	
	
	private SurfaceHolder mSurfaceHolder1;
	private SurfaceHolder mSurfaceHolder2;
	private SurfaceHolder mSurfaceHolder3;
	private SurfaceHolder mSurfaceHolder4;
	
	private Camera mCamera1;	
	private Camera mCamera2;
	private Camera mCamera3;	
	private Camera mCamera4;
	

	private SurfaceView m_1sur;
	private SurfaceView m_2sur;
	private SurfaceView m_3sur;
	private SurfaceView m_4sur;
	
	
	private static final String TAG = "MultiCamActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_multi_cams);
		initViews();
		
	}

	private void initViews() {
		// TODO Auto-generated method stub
		m_1sur = (SurfaceView) findViewById(R.id.sur_1);
		mSurfaceHolder1 = m_1sur.getHolder();
		mSurfaceHolder1.addCallback(mSurfaceCallback1);
		m_1sur.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Log.d(TAG,"on surface "+ arg0 + " clicked");
						openCamera(0);
					}
		 });
		
		
		m_2sur = (SurfaceView) findViewById(R.id.sur_2);
		m_2sur.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					
					}
		 });
		
		
		m_3sur = (SurfaceView) findViewById(R.id.sur_3);
		mSurfaceHolder3 = m_3sur.getHolder();
		mSurfaceHolder3.addCallback(mSurfaceCallback3);
		
		m_3sur = (SurfaceView) findViewById(R.id.sur_3);
		m_3sur.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					
					}
		 });
		
		
		m_4sur = (SurfaceView) findViewById(R.id.sur_4);
		m_4sur.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					
					}
		 });
		
		
	}
	
	
	private void openCamera(int index)
	{
		switch(index)
		{
		case 0:
			if (mCamera1 == null)  return;
		       
			mCamera1.startPreview();
			break;
		case 1:
			
			break;
		case 111:
			if (mCamera3 == null)  return;
		       
			mCamera3.startPreview();
		default:
			break;
		}
		
		

		
		
		
	}
	
	
	
	
	@SuppressLint("NewApi")
	SurfaceHolder.Callback mSurfaceCallback1 = new Callback() {		
	
		@SuppressWarnings("deprecation")
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
		{				
			Log.d(TAG,"on surfaceCreated");
	        
		}

		public void surfaceCreated(SurfaceHolder holder) 
		{
			Log.d(TAG,"on surfaceCreated");
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
	
	
	@SuppressLint("NewApi")
	SurfaceHolder.Callback mSurfaceCallback3 = new Callback() {		
	
		@SuppressWarnings("deprecation")
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
		{				
			Log.d(TAG,"on surfaceCreated");
	        
		}

		public void surfaceCreated(SurfaceHolder holder) 
		{
			Log.d(TAG,"on surfaceCreated");
			//打开后置camera
			mCamera3 = Camera.open(11);
//			mCamera1.setDisplayOrientation(90);
						
			try {
				mCamera3.setPreviewDisplay(holder);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera3 != null) {
			mCamera3.stopPreview();
			mCamera3.release();
			mCamera3 = null;
			}
		}
	};
	
}
