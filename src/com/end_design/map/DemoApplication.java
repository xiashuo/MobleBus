package com.end_design.map;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;


import com.baidu.mapapi.SDKInitializer;
import com.location.service.LocationService;

public class DemoApplication extends Application {
	public LocationService locationService;
    public Vibrator mVibrator;
	@Override
	public void onCreate() {
		super.onCreate();
		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		SDKInitializer.initialize(this);
	}

}