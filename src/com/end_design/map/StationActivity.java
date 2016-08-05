package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidumap.overlay.BusLineOverlay;
import com.example.map.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class StationActivity extends Activity implements OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener{
	private String city;
//	private String mlocation;
	private TextView tv1;
	private TextView tv2;
	private TextView tv_wait;
	private ProgressBar pb;
	private AlertDialog alertDialog;
	private Button bt;
	private String address;
	private String bus;
	private ListView lv;
	private static ArrayList<Map<String, Object>> list;
	private SimpleAdapter adapter;
	private BusLineResult route = null;// 保存驾车/步行路线数据的变量，供浏览节点时使用
	private String busLineID;
    String[] buses;
    int index;
	// 搜索相关
	private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BusLineSearch mBusLineSearch = null;
	BusLineOverlay overlay;//公交路线绘制对象
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_station);
		Button bt_back=(Button)findViewById(R.id.button_back);
		bt=(Button)findViewById(R.id.button1);
		lv=(ListView)findViewById(R.id.listView1);
		pb=(ProgressBar)findViewById(R.id.progress_bar);
		tv_wait=(TextView)findViewById(R.id.wait_tv);
		Intent it=getIntent();
		Bundle bundle=it.getExtras();
		address=bundle.getString("address");
		bus=bundle.getString("bus");
		city=bundle.getString("city");
//		mlocation=bundle.getString("mlocation");
		tv1=(TextView)findViewById(R.id.textView1);
		tv2=(TextView)findViewById(R.id.textView2);
		tv1.setText(address);
		tv2.setText("途径"+num_Bus(bus)+"条公交线路");
		View view=getLayoutInflater().inflate(R.layout.wait_popupwindow, null);
		alertDialog=new AlertDialog.Builder(this).create();
		alertDialog.setView(view);
		alertDialog.show();
		Window window=alertDialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		lp.alpha=0.2f;
		lp.width=700;
		lp.height=500;
		window.setAttributes(lp);
		
		mSearch = PoiSearch.newInstance();
		mSearch.setOnGetPoiSearchResultListener(this);
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
		buses=Buses(bus);
		index=0;
		list=new ArrayList<Map<String,Object>>();
		search();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent it=new Intent(StationActivity.this, BusRuteActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("busNum", buses[position]);
				bundle.putString("city", city);
				bundle.putString("nearist", address);
				it.putExtras(bundle);
				startActivity(it);
			}
		});
	}
	public void search() {
		mSearch.searchInCity((new PoiCitySearchOption()).city(
				city).keyword(
				buses[index]));
	}
	public int num_Bus(String str) {
		String [] ary =str.split(";");
		return ary.length;
	}
	public String[] Buses(String str) {
		String [] ary =str.split(";");
		return ary;
	}
	public void walkLine(View v) {
		Intent it=new Intent(getApplicationContext(), RutePlanActivity.class);
		Bundle bundle=new Bundle();
//		bundle.putString("city", city);
		bundle.putString("start", "我的位置");
		bundle.putString("end", address);
		bundle.putString("info", "walk");
		it.putExtras(bundle);
		startActivity(it);
	}
	public void back(View v) {
		finish();
	}

	
	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(StationActivity.this, "抱歉，未找到结果",
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
	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(StationActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		index++;
		route = result;
		Map<String, Object> map=new  HashMap<String,Object>();
		map.put("image",R.drawable.timg2);
		map.put("bus", route.getBusLineName());
		list.add(map);
//		Toast.makeText(getApplicationContext(),String.valueOf(route.getStartTime().getHours()), Toast.LENGTH_LONG).show();
		adapter=new SimpleAdapter(this,list, R.layout.bus_listview,
	    		new String[]{"image","bus"}, new int[]{R.id.imageView1,R.id.textView1});
		
		lv.setAdapter(adapter);
		if(index<buses.length)
			search();
		else {
			alertDialog.dismiss();
		}
	}
}
