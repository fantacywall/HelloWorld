package com.example.helloworld.camera;

import android.hardware.Camera.CameraInfo;

public class CameraConstants {

    /**
     * 
     */
    private CameraConstants() {//empty
    }

    // For samsung galaxy tab. CAMERA_PRIMARY = 1,  CAMERA_FRONT_FACING = 2
    // From T1 change it CAMERA_PRIMARY = 0,  CAMERA_FRONT_FACING = 1
    public static final int CAMERA_PRIMARY = CameraInfo.CAMERA_FACING_BACK;
    public static final int CAMERA_FRONT_FACING = CameraInfo.CAMERA_FACING_FRONT;
    public static final String FRONT_CAMERA = "Front_camera";
    public static final String PRIMARY_CAMERA = "Primary_camera";
    public static final String FRONT_CAMERA_MUTE = "Front_camera_Mute";
    public static final String PRIMARY_CAMERA_MUTE = "Primary_camera_Mute";
}
