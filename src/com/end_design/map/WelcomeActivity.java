package com.end_design.map;

import com.example.map.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;



public class WelcomeActivity extends Activity
{
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcom_layout);
		mContext = this;
		handler.sendEmptyMessageDelayed(1,1500);

	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			startActivity(new Intent(mContext,MainActivity.class));
			finish();
		};
	};

}
