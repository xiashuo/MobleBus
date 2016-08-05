package com.end_design.map;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidumap.overlay.PoiOverlay;
import com.example.map.R;
import com.example.map.R.color;
import com.location.service.LocationService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MapActivity extends Activity implements OnGetPoiSearchResultListener{
	private PoiSearch mPoiSearch = null;
	private PoiResult poiresult;
	private LocationService locationService;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationMode mCurrentMode=LocationMode.NORMAL;
    private BitmapDescriptor mCurrentMarker=BitmapDescriptorFactory.fromResource(R.drawable.lmark);
    private LatLng mPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		Button bt_back=(Button)findViewById(R.id.button_back);
        mMapView=(MapView)findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
     // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
     // 初始化搜索模块，注册搜索事件监听
  		mPoiSearch = PoiSearch.newInstance();
  		mPoiSearch.setOnGetPoiSearchResultListener(this);
  		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();
				
			}
		});
  		
	}
	
	public void back(View v) {
		finish();
	}
	private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError){
//			Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_LONG).show();
				mPoiSearch.searchNearby(new PoiNearbySearchOption()
						.location(new LatLng(location.getLatitude(), location.getLongitude()))
						.keyword("公交车站")
						.radius(700));	
	            mPoint=new LatLng(location.getLatitude(), location.getLongitude());
	            MyLocationData locData = new MyLocationData.Builder()
	                    .accuracy(location.getRadius())
	                            // 此处设置开发者获取到的方向信息，顺时针0-360
	                    .direction(100).latitude(location.getLatitude())
	                    .longitude(location.getLongitude()).build();
	            mBaiduMap.setMyLocationData(locData);
			
			}
			
		}
	};
	protected void onStart() {
		super.onStart();
	};
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
     // 退出时销毁定位
        locationService.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
    }  
   

	
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MapActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getApplicationContext(), result.getAddress(), Toast.LENGTH_LONG).show();
			return;
		}
		
	}
	@Override
	public void onGetPoiResult(PoiResult result) {	
		if (result == null|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MapActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}
		else {
			poiresult=result;
			
			Toast.makeText(  
                    MapActivity.this,  
                    "总共查到" + result.getTotalPoiNum() + "站点", Toast.LENGTH_SHORT).show(); 
			
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			
			return;
		
		}
	}
	public void location(View v) {
		 MapStatus mMapStatus = new MapStatus.Builder()
                .target(mPoint)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
	}
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			final int i=index;
			TextView popupText = new TextView(MapActivity.this);
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText(poiresult.getAllPoi().get(index).name+">");
			popupText.setTextColor(Color.rgb(51, 181, 229));
			popupText.setTextSize(20);
			popupText.setGravity(Gravity.CENTER_VERTICAL);
			popupText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent it=new Intent(MapActivity.this, StationActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("address", poiresult.getAllPoi().get(i).name);
					bundle.putString("bus", poiresult.getAllPoi().get(i).address);
					bundle.putString("city", poiresult.getAllPoi().get(i).city);
					it.putExtras(bundle);
					startActivity(it);
				}
			});
			
			mBaiduMap.showInfoWindow(new InfoWindow(popupText, poiresult.getAllPoi().get(index).location,
					-30));
			super.onPoiClick(index);
			return true;
		}
		
	}
}
