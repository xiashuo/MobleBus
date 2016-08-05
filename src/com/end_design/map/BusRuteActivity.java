package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineResult.BusStation;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidumap.overlay.BusLineOverlay;
import com.example.map.R;
import com.location.service.LocationService;

import SQLite.DBConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;import android.content.res.ColorStateList;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class BusRuteActivity extends Activity implements OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener{
	private String bus;
	private String city;
	private String nearist;
	private TextView start_tv;
	private TextView end_tv;
	private TextView stime_tv;
	private TextView etime_tv;
	private BusLineResult route = null;
	private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BusLineSearch mBusLineSearch = null;
	private String busLineID;
	private ListView lv;
	private LinearLayout layout1;
	private LinearLayout layout2;
	private Button map;
	private Button collect_bt;
	private BaiduMap mBaiduMap = null;
	private MapView mMapView;
	private BusLineOverlay overlay;//公交路线绘制对象
	private String end_station;
	private LocationService locationService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bus_rute);
		locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
		layout1=(LinearLayout)findViewById(R.id.linearLayout1);
		layout2=(LinearLayout)findViewById(R.id.linearLayout2);
		layout1.setVisibility(View.VISIBLE);
		layout2.setVisibility(View.GONE);
		Button back=(Button)findViewById(R.id.button_back);
		map=(Button)findViewById(R.id.button1);
		collect_bt=(Button)findViewById(R.id.button4);
		Intent it=getIntent();
		Bundle bundle=it.getExtras();
		bus=bundle.getString("busNum");
		city=bundle.getString("city");
		nearist=bundle.getString("nearist");
		TextView tittle=(TextView)findViewById(R.id.tittle);
		start_tv=(TextView)findViewById(R.id.textView2);
		end_tv=(TextView)findViewById(R.id.textView3);
		stime_tv=(TextView)findViewById(R.id.textView4);
		etime_tv=(TextView)findViewById(R.id.textView6);
		tittle.setText(bus);
		DBConnection conn=new DBConnection(getApplicationContext());
		SQLiteDatabase db=conn.getConnection();
		Cursor cursor=db.query("collects", null, "bus=? and start=?", new String[]{bus,nearist}, null, null, null);
		if(cursor.getCount()>0){
			collect_bt.setText("已收藏");
		}
		db.close();
		mSearch = PoiSearch.newInstance();
		mSearch.setOnGetPoiSearchResultListener(this);
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
		mSearch.searchInCity((new PoiCitySearchOption())
				.city(city)
				.keyword(bus));
//		Toast.makeText(getApplicationContext(), bundle.getString("busNum"), Toast.LENGTH_LONG).show();
		lv=(ListView)findViewById(R.id.listview1);
		mMapView=(MapView)findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        overlay = new myBusLineOverlay(mBaiduMap);
		mBaiduMap.setOnMarkerClickListener(overlay);
		
	}
	int i=0;
	public void collect(View v) {
		DBConnection conn=new DBConnection(getApplicationContext());
		final SQLiteDatabase db=conn.getConnection();
		if (collect_bt.getText().toString().equals("收藏")) {
			Cursor cursor=db.query("tabs", null, null, null, null, null, null);
			final String[] items=new String[cursor.getCount()];
			int index=0;
			while(cursor.moveToNext()){
				items[index++]=cursor.getString(cursor.getColumnIndex("tab"));
			}
			
			Builder builder=new AlertDialog.Builder(BusRuteActivity.this);
			builder.setTitle("       选择标签");
			builder.setSingleChoiceItems(items, 0, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					i=which;
					
				}
			});
			final EditText et=new EditText(this);
			et.setText("自定义");
			et.setBackgroundColor(Color.WHITE);
			et.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
						et.setText("");
					
				}
			});
			builder.setView(et);
			builder.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContentValues values=new ContentValues();
						values.put("start", nearist);
						values.put("end", end_station);
						values.put("city", city);
						values.put("bus", bus);
						String tab=et.getText().toString();
						if (tab.equals("自定义")) {
							values.put("tab", items[i]);
						}
						else{
							values.put("tab", tab);
							ContentValues value=new ContentValues();
							value.put("tab", tab);
							db.insert("tabs", null, value);
						}
						db.insert("collects", null, values);
						db.close();
						collect_bt.setText("已收藏");
				
						Toast.makeText(getApplicationContext(), "收藏成功！", Toast.LENGTH_LONG).show();
					}
				                     });
			builder.create().show();
		}
		else if(collect_bt.getText().toString().equals("已收藏")){
			Builder builder=new AlertDialog.Builder(BusRuteActivity.this);
			builder.setTitle("提示")
			       .setMessage("你确定要取消收藏吗？")
			       .setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.delete("collects", "bus=? and start=?", new String[]{bus,nearist});
						db.close();
						Toast.makeText(getApplicationContext(), "已取消收藏！", Toast.LENGTH_LONG).show();
						collect_bt.setText("收藏");
					}
				})
			       .setNegativeButton("取消", null)
			       .create().show();
		}
		                          
		
		
	}
	public void showMap(View v) {
		if(map.getText().toString().equals("地图")){
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			map.setText("列表");
		}
		else{
			layout2.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			map.setText("地图");
		}
	    
		
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(BusRuteActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		route=result;
		overlay.removeFromMap();
		overlay.setData(result);
		overlay.addToMap();
		overlay.zoomToSpan();
		List<BusStation> list_station=new ArrayList<BusStation>();
		list_station=route.getStations();
		String start_station=list_station.get(0).getTitle();
		end_station=list_station.get(list_station.size()-1).getTitle();
		String stime=time(route.getStartTime().getHours())
				    +":"+time(route.getStartTime().getMinutes());
		stime_tv.setText(stime);		
//		Toast.makeText(BusRuteActivity.this,String.valueOf(stime),
//				Toast.LENGTH_LONG).show();
		start_tv.setText(start_station);
		end_tv.setText(end_station);
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		int i=1;
		for (BusStation station: route.getStations()) {
			Map<String, Object> map=new HashMap<String, Object>();
			
			if(station.getTitle().equals(nearist)){
				map.put("image", R.drawable.station_num2);
				map.put("num", "上");
				i++;
			}
			else{
				map.put("image", R.drawable.station_num);
				map.put("num", i++);
			}	
			map.put("station", station.getTitle());
			list.add(map);
		}
		SimpleAdapter adapter=new SimpleAdapter(this, list,R.layout.listview_station, 
				new String[]{"image","num","station"}, new int[]{R.id.button1,R.id.button1,R.id.textview1});
		lv.setAdapter(adapter);
	}
	public String time(int time) {
		if(time<10)
			return "0"+String.valueOf(time);
		else
			return String.valueOf(time);
	}
	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(BusRuteActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// 遍历所有poi，找到类型为公交线路的poi
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_LINE
					|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
				busLineID=poi.uid;
			}
		}
		mBusLineSearch.searchBusLine((new BusLineSearchOption()
				.city(city).uid(busLineID)));
		
	}
	private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError){
				city=location.getCity();
	       
			}
			
		}
	
	};
	private class myBusLineOverlay extends BusLineOverlay{

		public myBusLineOverlay(BaiduMap baiduMap) {
			super(baiduMap);
			// TODO Auto-generated constructor stub
		}
		@Override
		public boolean onBusStationClick(int index) {
			TextView popupText = new TextView(getApplicationContext());
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setTextColor(0xff000000);
//			Toast.makeText(getApplicationContext(), String.valueOf(index), Toast.LENGTH_LONG).show();
			popupText.setText(route.getStations().get(index).getTitle());
		
			InfoWindow infoWindow=new InfoWindow(popupText, route.getStations().get(index).getLocation(), 0);
				
			mBaiduMap.showInfoWindow(infoWindow);
			return super.onBusStationClick(index);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mBusLineSearch.destroy();
		super.onDestroy();
	}
	
}
