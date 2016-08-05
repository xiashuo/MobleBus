package com.test.view;


import com.example.map.R;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageBtn extends LinearLayout{
	private ImageView imageView;
	private TextView textView;
	public ImageBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.imagebtn, this);
		imageView=(ImageView) findViewById(R.id.imageView1);
		textView=(TextView)findViewById(R.id.textView1);
	}

	public ImageBtn(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public void setImageResource(int resId) {
		imageView.setImageResource(resId);
	}
	public void setTextViewText(String text) {
		textView.setText(text);
	}
	public void setmTextColor(int red,int green,int blue) {
		textView.setTextColor(Color.rgb(red, green, blue));
	}
	

}
