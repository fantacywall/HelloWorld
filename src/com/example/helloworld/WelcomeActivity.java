package com.example.helloworld;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	
	private static final String TAG = "WelcomeActivity";
	
	
	private Button mBtn1,mBtn2,mBtn3 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_welcome);
		initViews();
		
	}


	private void initViews() {
		// TODO Auto-generated method stub
		mBtn1 = (Button) findViewById(R.id.btn_function1);
		mBtn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openFunctionPage(1);
			}

			
             
         });
		
		mBtn2 = (Button) findViewById(R.id.btn_function2);
		mBtn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openFunctionPage(2);
			}

			
             
         });
		
	}

	private void openFunctionPage(int pageIndex) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(pageIndex)
		{
		case 1:
			 intent = new Intent(this,OpenCamsActivity.class); 
			 startActivity(intent);
			break;
		case 2:
			intent = new Intent(this,MultiCamActivity.class); 
			startActivity(intent);
			break;	
		default:
			break;
		}
		
		
	}
	
	

}
