package com.example.helloworld.camera;

import android.hardware.Camera;

public interface CameraListener {

	public void onCameraOpen(Camera camera);
	
	public void onCameraSwitch(Camera camera);
	
	public void onCameraNoSwitch();
	
	public void onCameraError();
}
