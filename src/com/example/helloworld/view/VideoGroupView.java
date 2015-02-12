
package com.example.helloworld.view;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public abstract class VideoGroupView extends ViewGroup {

    protected static final String TAG = "VideoGroupView";
    
    protected Handler handler;
    
    protected VideoCell localCell;
    protected VideoCell peopleCell;
    protected VideoCell contentCell;

    public VideoGroupView(Context context) {
        super(context);
        init(context);
    }

    public VideoGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void init(Context context) {
        handler = new Handler();
      
    }


    
    protected int toRealPx(float width, float percent) {
        return (int) (percent * width);
    }
    
    public void onCallConnected() {

        requestAnimation();
    }
    
    protected void requestAnimation() {
    }

    public void onCallDisconnected() {}

    public void setLocalCallback(SurfaceTextureCallback callback) {
        SurfaceHolder holder = null;
        View videoView = localCell.getVideoView();
        if (videoView instanceof TextureView) {
            TextureView textureView = (TextureView) videoView;
            textureView.setSurfaceTextureListener(callback);
        } else if (videoView instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) videoView;
            if ((holder = surfaceView.getHolder()) != null) {
                holder.addCallback(callback);
            }
        }
    }

    public void destroy() {
        if (localCell != null) {
            localCell.destroy(this);
            localCell = null;
        }
        if (peopleCell != null) {
            peopleCell.destroy(this);
            peopleCell = null;
        }
        if (contentCell != null) {
            contentCell.destroy(this);
            contentCell = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    public void addPeople(String sourceId, int width, int height, int ssrc) {
       
    }
    
    public void removePeople(String sourceId) {
        
    }

    public void addContent(String sourceId, int width, int height, int ssrc) {
      
    }
    
    public void removeContent(String sourceId) {
        
    }

    public void hidePip() {
       
        refreshLayout();
    }
    
    public void showPip() {
       
        refreshLayout();
    }

    protected void refreshLayout() {}
    
    public void onResume() {}
    
    public void onPause() {}

}
