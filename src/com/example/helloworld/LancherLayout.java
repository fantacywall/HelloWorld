package com.example.helloworld;

import com.example.helloworld.R;

import com.example.helloworld.HelloActivity.OnPreviewListener;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LancherLayout extends LinearLayout implements OnPreviewListener{
    private Context context;

	
	   
    public LancherLayout(Context paramContext) {
        super(paramContext);
        this.context = paramContext;
        
       // addView(LayoutInflater.from(this.context).inflate(R.layout.activity_hello, null));
        
        
       
    }
    
	public LancherLayout(Context paramContext, int layoutId) {
		super(paramContext);
		this.context = paramContext;
        
		//addView(LayoutInflater.from(this.context).inflate(layoutId, null));
		
		
		//initViews();
	}

    

	private void initViews() {
        
    }

	public void onClick(View paramView) {
		if (onClickListener != null) {
			onClickListener.onClick(paramView);
		}
	}

	private OnFocusChangeListener onFocusChangeListener;

	/**
	 * init FocusChangeListener
	 * */
	public void setFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
		this.onFocusChangeListener = onFocusChangeListener;
	}
	
	private OnClickListener onClickListener;
    /**
     * init OnClickListener
     * */
    public void setClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    
	

    @Override
    public void onPreviewCreated() {
    }

    
	
}