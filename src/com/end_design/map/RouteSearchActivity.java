package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.a.a.a.b;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.map.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RouteSearchActivity extends Activity implements OnGetPoiSearchResultListener{
	private EditText et;
	private ListView lv;
	private Button clear;
	private PoiSearch mPoiSearch = null;
	private String city;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> list;
	private List<String> buslineIDlist=null;
	private String bus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route_search);
		et=(EditText)findViewById(R.id.editText1);
		lv=(ListView)findViewById(R.id.listView1);
		clear=(Button)findViewById(R.id.clear);
		clear.setVisibility(View.GONE);
		et.addTextChangedListener(watcher);
		mPoiSearch = PoiSearch.newInstance();
 		mPoiSearch.setOnGetPoiSearchResultListener(this);
 		
 		Intent it=getIntent();
 		Bundle bundle=it.getExtras();
 		city=bundle.getString("city");
 		lv.setOnItemClickListener(listener);
	}
	private OnItemClickListener listener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=(HashMap<String, Object>)parent.getItemAtPosition(position);
			String adr=(String) map.get("address");
			String bus1=(String) map.get("bus");
			if(adr.endsWith("路")){
				Intent it=new Intent(RouteSearchActivity.this, BusRuteActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("busNum",bus);
				bundle.putString("city", city);
				bundle.putString("nearist", "");
				it.putExtras(bundle);
				startActivity(it);
			}
			else{
				Intent it=new Intent(RouteSearchActivity.this, StationActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("address", adr);
				bundle.putString("bus", bus1);
				bundle.putString("city", city);
				it.putExtras(bundle);
				startActivity(it);
			}
		}
	};
	private TextWatcher watcher=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() <= 0) {
				list=new ArrayList<Map<String,Object>>();
				clear.setVisibility(View.GONE);
				Map<String, Object> info=new HashMap<String, Object>();
				list.add(info);	    
				adapter=new SimpleAdapter(RouteSearchActivity.this, list, R.layout.routesearch_listview,
						new String []{"image","address","bus"},
						new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
				lv.setAdapter(adapter);
				return;
			} 
			else{
				clear.setVisibility(View.VISIBLE);
				mPoiSearch.searchInCity((new PoiCitySearchOption())
						.city(city)
						.keyword(s.toString())
						);
			}
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	public void clear(View v) {
		et.setText("");
		clear.setVisibility(View.GONE);
	}
	public void back(View v) {
		finish();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RouteSearchActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		
		list=new ArrayList<Map<String,Object>>();
		for (PoiInfo poi : result.getAllPoi()) {
			
			if(poi.type==PoiInfo.POITYPE.BUS_STATION){
				Map<String, Object> info=new HashMap<String, Object>();
				info.put("image", R.drawable.timg2);
				info.put("address", poi.name);
				info.put("bus", poi.address);
				list.add(info);	    	
			}
			if(poi.type==PoiInfo.POITYPE.BUS_LINE){
				Map<String, Object> info=new HashMap<String, Object>();
				info.put("image", R.drawable.timg2);
				bus=name(poi.name)[0];
				int size=name(poi.name).length;
				String destination=name(poi.name)[size-1];
				info.put("address", bus);
				info.put("bus", "开往  "+destination);
				list.add(info);	  
			}
		}
		if(list.isEmpty()){
			Map<String, Object> info=new HashMap<String, Object>();
			info.put("address", "抱歉，未查到任何信息！");
			list.add(info);	    
			adapter=new SimpleAdapter(RouteSearchActivity.this, list, R.layout.routesearch_listview,
					new String []{"image","address","bus"},
					new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
			lv.setAdapter(adapter);
			
		}
		else{
			adapter=new SimpleAdapter(this, list, R.layout.routesearch_listview,
					new String []{"image","address","bus"},
					new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
			lv.setAdapter(adapter);
		}
		
		
		
	}
	public String[] name(String name) {
		name=name.replace("(", ",");
		name=name.replace("-","," );
		name=name.replace(")", ",");
		String[] buses=name.split(",");
		return buses;
	}

	
}
