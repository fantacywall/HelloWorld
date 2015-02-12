
package com.example.helloworld.view;

import com.example.helloworld.view.VideoCell.OnCellEventListener;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class HwVideoGroupView extends VideoGroupView implements OnCellEventListener {
    private boolean hasContent;

    private Runnable renderContentTask = new Runnable() {
        @Override
        public void run() {
            if (contentCell != null)
                contentCell.requestRender();
            if (getVisibility() == VISIBLE && hasContent) {
                handler.postDelayed(renderContentTask, 1000 / 15);
            }
        }

    };

    public HwVideoGroupView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HwVideoGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public HwVideoGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

//        contentCell = new GLVideoCell(context, this, VideoCell.CONTENT_VIDEO, "Content_DUMP");
//
//        peopleCell = new HwVideoCell(context, this, VideoCell.PEOPLE_VIDEO, false);
//        peopleCell.setVideoCallback(new HwRenderCallback(VideoCell.PEOPLE_VIDEO));
//
//        localCell = new HwVideoCell(context, this, VideoCell.LOCAL_VIDEO, true);

    }

    @Override
    public boolean onDoubleTap(MotionEvent e, VideoCell cell) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell) {
        return false;
    }

    @Override
    public boolean onDragMove(float movedX, float movedY, VideoCell cell) {
        return false;
    }

    

    @Override
    protected void onVisibilityChanged(android.view.View changedView, int visibility) {
        handler.removeCallbacksAndMessages(null);
        if (visibility == VISIBLE && hasContent) {
            handler.post(renderContentTask);
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed)
            return;

        refreshLayout();
    }

    @Override
    protected void refreshLayout() {
        int width = getWidth(), height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        if (contentCell != null) {
            contentCell.layout(0, 0, width, height);
            
        }

        if (peopleCell != null) {
            peopleCell.layout(0, 0, width, height);
        }

        if (localCell != null) {
            localCell.layout(0, 0, width, height);
        }
    }
    
    @Override
    protected void requestAnimation() {
       
      
    }

    private void childAnimate(RectF layout, View v) {
        float x, y, w, h = 0;
        x = layout.left * getWidth() * 0.5f;
        y = layout.top * getHeight() * 0.5f;
        w = layout.right - layout.left;
        h = layout.bottom -layout.top;
        v.animate()
                .scaleX(w).scaleY(h)
                .x(x).y(y)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    public void addContent(String sourceId, int width, int height, int ssrc) {
        super.addContent(sourceId, width, height, ssrc);

        requestAnimation();

        contentCell.setSsrc(ssrc);
        contentCell.setSourceId(sourceId);

        hasContent = true;
        handler.post(renderContentTask);
//        refreshLayout();
    }

    @Override
    public void removeContent(String sourceId) {
        super.removeContent(sourceId);
        requestAnimation();

        contentCell.setSsrc(VideoCell.CONTENT_VIDEO);
        contentCell.setSourceId("Content_DUMP");

        hasContent = false;
//        refreshLayout();
    }
}
