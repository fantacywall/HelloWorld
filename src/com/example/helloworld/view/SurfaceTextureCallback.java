package com.example.helloworld.view;

import android.view.SurfaceHolder.Callback;
import android.view.TextureView.SurfaceTextureListener;

public interface SurfaceTextureCallback extends SurfaceTextureListener, Callback {

    void release();
}
