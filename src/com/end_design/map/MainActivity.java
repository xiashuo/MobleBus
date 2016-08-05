package com.end_design.map;

import com.baidu.mapapi.SDKInitializer;
import com.example.map.R;
import com.test.view.ImageBtn;

import SQLite.DBConnection;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends ActivityGroup {
	 private LinearLayout container = null;
	 

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // 隐藏标题栏
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        // 设置视图
	        setContentView(R.layout.main);
	        container = (LinearLayout) findViewById(R.id.containerBody);
	        container.removeAllViews();
            container.addView(getLocalActivityManager().startActivity(
                    "Module1",
                    new Intent(MainActivity.this,SearchActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .getDecorView());

	        // 模块1
	        final ImageBtn btnModule1 = (ImageBtn) findViewById(R.id.btnModule1);
	        final ImageBtn btnModule2 = (ImageBtn) findViewById(R.id.btnModule2);
	        final ImageBtn btnModule3 = (ImageBtn) findViewById(R.id.btnModule3);
	        btnModule1.setTextViewText("搜索");
	        btnModule1.setImageResource(R.drawable.search2);
	        btnModule1.setmTextColor(51, 181, 229);
	        btnModule1.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	btnModule1.setImageResource(R.drawable.search2);
	    	        btnModule1.setmTextColor(51, 181, 229);
	    	        btnModule2.setImageResource(R.drawable.huancheng);
	    	        btnModule2.setmTextColor(0, 0, 0);
	    	        btnModule3.setImageResource(R.drawable.mine1);
	    	        btnModule3.setmTextColor(0, 0, 0);
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module1",
	                        new Intent(MainActivity.this,SearchActivity.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	        });

	        // 模块2
	        
	        btnModule2.setImageResource(R.drawable.huancheng);
	        btnModule2.setTextViewText("换乘");
	        btnModule2.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	btnModule1.setImageResource(R.drawable.search1);
	    	        btnModule1.setmTextColor(0, 0, 0);
	    	        btnModule2.setImageResource(R.drawable.huancheng1);
	    	        btnModule2.setmTextColor(51, 181, 229);
	    	        btnModule3.setImageResource(R.drawable.mine1);
	    	        btnModule3.setmTextColor(0, 0, 0);
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module2",
	                        new Intent(MainActivity.this, TransferActivity.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	        });

	        // 模块3
	        
	        btnModule3.setImageResource(R.drawable.mine1);
	        btnModule3.setTextViewText("我的");
	        btnModule3.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	btnModule1.setImageResource(R.drawable.search1);
	    	        btnModule1.setmTextColor(0, 0, 0);
	    	        btnModule2.setImageResource(R.drawable.huancheng);
	    	        btnModule2.setmTextColor(0, 0, 0);
	    	        btnModule3.setImageResource(R.drawable.mine2);
	    	        btnModule3.setmTextColor(51, 181, 229);
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module3",
	                        new Intent(MainActivity.this, MyActivity.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	        });
	    }
	   
	   
	    

}
