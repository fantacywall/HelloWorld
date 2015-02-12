
package com.example.helloworld.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class VideoCell {

    public static final int PEOPLE_VIDEO = 0;

    public static final int CONTENT_VIDEO = -1;

    public static final int LOCAL_VIDEO = -2;

    public static final int CONTENT_SEND_VIDEO = -3;

    public static final String SOURCE_ID_LOCAL_PREVIEW = "LocalPreviewID";

    public static final int ACTIVE_SPEAKER_COLOR = 0xff0055ff; // orange

    public static final int NORMAL_PARTICIPANT_COLOR = 0xff959595; // gray

    public static final int SITE_NAME_COLOR = 0xffffffff; // white

    public static final int VIDEO_ROTATE_0 = 0;

    public static final int VIDEO_ROTATE_90 = 90;

    public static final int VIDEO_ROTATE_180 = 180;

    public static final int VIDEO_ROTATE_270 = 270;

    // move view to an absolute position. (2000, 2000)
    static final int FAR_AWAY = 2000;

    public class CellData {
        public int ssrc;

        public String displayName;

        public Boolean isActive = false;

        public int videoWidth = 320; // default to 180P

        public int videoHeight = 180;
    }

    private static int viewIdCounter = 0;

    public int generateViewId() {
        if (viewIdCounter > 100000)
            return 0;
        return ++viewIdCounter;
    }

    private int left;

    private int top;

    private int right;

    private int bottom;

    

    protected TextView nameView;

    protected ViewGroup parent;

    protected float fontHeightPix = 30;

    private CellData cellData = new CellData();

    protected OnCellEventListener mCellEventListener = null;
    
    protected SurfaceTextureCallback videoCallback;

    public interface OnCellEventListener {
        boolean onDoubleTap(MotionEvent e, VideoCell cell);

        boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell);

        boolean onDragMove(float movedX, float movedY, VideoCell cell);
    }

    public VideoCell(Context context, ViewGroup parent, int ssrc) {
        this.parent = parent;
        this.cellData.ssrc = ssrc;

//        initRectView(context, parent);
    }

    

    protected float getAdjustedSize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float fontHeightInch = 0.1875f; // inch
        this.fontHeightPix = fontHeightInch * metrics.ydpi;
        float font10HeightPix, font30HeightPix;

        {
            Paint paint = new Paint();
            paint.setTextSize(10);
            FontMetrics fm = paint.getFontMetrics();
            font10HeightPix = (float) (Math.ceil(fm.descent - fm.top) + 2);
        }
        {
            Paint paint = new Paint();
            paint.setTextSize(30);
            FontMetrics fm = paint.getFontMetrics();
            font30HeightPix = (float) (Math.ceil(fm.descent - fm.top) + 2);
        }

        float font = (10 + 20 * (fontHeightPix - font10HeightPix)
                / (font30HeightPix - font10HeightPix))
                / metrics.density;

        return font;
    }

    public void layout(int l, int t, int r, int b) {
        left = l;
        top = t;
        right = r;
        bottom = b;

        int borderWidth = 0;
       


        View videoView = getVideoView();
        if (videoView != null) {
            videoView.layout(l + borderWidth, t + borderWidth, r - borderWidth, b - borderWidth);
        }

        if (nameView != null) {
            nameView.layout(l + 5, b - 5 - (int) fontHeightPix, r - 5, b - 5);
        }
    }

    public void destroy(ViewGroup parent) {
        

        if (nameView != null) {
            nameView.setVisibility(View.GONE);
            parent.removeView(nameView);
        }

        View videoView = getVideoView();

        if (videoView != null) {
            videoView.setVisibility(View.GONE);
            parent.removeView(videoView);
            if (videoCallback!= null) videoCallback.release();
        }
        
        setCellEventListener(null);
    }

    public void onResume() {};

    public void onPause() {};

    public abstract void bringToFront();

    public void setRotate(int degree) {};

    public void requestRender() {};

    public abstract View getVideoView();

    public void setSourceId(String sourceId) {};

    public void showName() {
        if (nameView != null)
            nameView.setVisibility(View.VISIBLE);
    }

    public void hidName() {
        if (nameView != null)
            nameView.setVisibility(View.GONE);
    }

    public void moveOutOfScreen() {
      
        if (nameView != null)
            nameView.setVisibility(View.GONE);
    }

    public void setVisibility(int visibility) {
     
        if (nameView != null)
            nameView.setVisibility(visibility);
    }

    public void onResolutionChanged(int width, int height) {
        cellData.videoWidth = width;
        cellData.videoHeight = height;
    }

    public ViewGroup getParent() {
        return parent;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public void setRectColor(int color) {
      
           
      
        // if(nameView != null) {
        // nameView.setTextColor(color);
        // nameView.invalidate();
        // }
    }
    
    public void setVideoCallback(SurfaceTextureCallback callback) {
        videoCallback = callback;
    }

    public void setRectBorderWidth(int width) {
        
    }

    public void setCellEventListener(OnCellEventListener mCellEventListener) {
        this.mCellEventListener = mCellEventListener;
    }

    public int getSsrc() {
        return cellData.ssrc;
    }

    public void setSsrc(int ssrc) {
        cellData.ssrc = ssrc;
    }

    public String getDisplayName() {
        return cellData.displayName;
    }

    public void setDisplayName(String displayName) {
        cellData.displayName = displayName;
        if (nameView != null) {
            nameView.setText(displayName);
            nameView.invalidate();
        }
    }

    public TextView getNameView() {
        return nameView;
    }

    public void setNameColor(int color) {
        if (nameView != null) {
            nameView.setTextColor(color);
        }
    }

    public Boolean getIsActive() {
        return cellData.isActive;
    }

    public void setIsActive(Boolean isActive) {
        cellData.isActive = isActive;
    }

    public void setVideoWidth(int videoWidth) {
        cellData.videoWidth = videoWidth;
    }

    public int getVideoWidth() {
        return cellData.videoWidth;
    }

    public void setVideoHeight(int videoHeight) {
        cellData.videoHeight = videoHeight;
    }

    public int getVideoHeight() {
        return cellData.videoHeight;
    }

    public CellData getCellData() {
        return cellData;
    }
}
