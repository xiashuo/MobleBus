package com.end_design.map;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.example.map.R;
import com.location.service.LocationService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends TabActivity {
	private TabHost tabHost;
	private TabWidget tabWidget;
	private Button bt_city;
	private Button bt_map;
	private LocationService locationService;
	private String city;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
		tabHost=(TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("tab1")
					.setIndicator("附近")
					.setContent(new Intent(this,NearbyActivity.class))
				);
		tabHost.addTab(tabHost.newTabSpec("tab2")
				.setIndicator("收藏")
				.setContent(new Intent(this,CollectActivity.class))
			);
		tabWidget=tabHost.getTabWidget();
		updateTab(tabHost);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				tabHost.setCurrentTabByTag(tabId);
				updateTab(tabHost);
				
			}
		});
		bt_city=(Button)findViewById(R.id.city);
		
		
	}
	public void search(View v) {
		Intent it=new Intent(SearchActivity.this, RouteSearchActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("city", city);
		it.putExtras(bundle);
		startActivity(it);
	}


	public void updateTab(TabHost tabhost) {
		for (int i =0; i < tabhost.getTabWidget().getChildCount(); i++) {  
            //修改Tabhost高度和宽度
//            tabWidget.getChildAt(i).getLayoutParams().height = 30;  
//            tabWidget.getChildAt(i).getLayoutParams().width = 65;
            //修改显示字体大小
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(20);  
            if (tabhost.getCurrentTab()==i) {
            	tv.setTextColor(Color.rgb(51, 181, 229));
			}
            else
            	tv.setTextColor(Color.BLACK);
           
		}
		
	}
private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError){
				city=location.getCity();
			    String city2=city.replace("市", "");
				bt_city.setText(city2);
			    
	       
			}
	
		}
	
	};
	public void getMap(View v) {
		Intent it=new Intent(SearchActivity.this, MapActivity.class);
		startActivity(it);
	}
	
	

	
}
