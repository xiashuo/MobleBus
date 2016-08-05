 package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.e.p;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.map.R;
import com.example.map.R.menu;
import com.location.service.LocationService;

import SQLite.DBConnection;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnFocusChangeListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class TransferActivity extends Activity implements OnGetRoutePlanResultListener,OnGetSuggestionResultListener{
	private Button change;
	private LinearLayout linearlayout1;
	private LinearLayout linearlayout2;
	private EditText et1;
	private EditText et2;
	private ListView lv1;
	private ListView lv2;
	private Button clear1;
	private Button clear2;
	private SimpleAdapter adapter2;
	private Map<String, Object> map2;
	private List<Map<String, Object>> list2;
	private SuggestionSearch mSuggestionSearch = null;
	private RoutePlanSearch mSearch = null;
	private RouteLine route = null;
	private LocationService locationService;
	private String city;
	private double x;
	private double y;
	private SimpleAdapter adapter1;
	private Map<String, Object> map1;
	private List<Map<String, Object>> list1;
	private String myLocation;
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST+1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);
		change=(Button)findViewById(R.id.change);
		linearlayout1=(LinearLayout)findViewById(R.id.linearLayout1);
		linearlayout2=(LinearLayout)findViewById(R.id.linearLayout2);
		lv1=(ListView)findViewById(R.id.listview1);
		lv2=(ListView)findViewById(R.id.listview2);
		clear1=(Button)findViewById(R.id.clear1);
		clear2=(Button)findViewById(R.id.clear2);
		clear1.setVisibility(View.GONE);
		clear2.setVisibility(View.GONE);
		clear1.setOnClickListener(listener1);
		clear2.setOnClickListener(listener2);
		EditText et=(EditText)findViewById(R.id.none);
		et.requestFocus();
		et1=(EditText)findViewById(R.id.editText1);
		et2=(EditText)findViewById(R.id.editText2);
		linearlayout2.setVisibility(View.GONE);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		map2=new HashMap<String, Object>();
		list2=new ArrayList<Map<String,Object>>();
		map2.put("image", R.drawable.mylocation);
		map2.put("address", "我的位置");
		list2.add(map2);
		adapter2=new SimpleAdapter(this, list2, R.layout.search_listview, new String[]{"image","address"},
				new int[]{R.id.imageView1,R.id.textView1});
		lv2.setAdapter(adapter2);
		lv2.setOnItemClickListener(lvListener2);
		locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
		et1.setOnFocusChangeListener(mlistener);
		et2.setOnFocusChangeListener(mlistener);
		et1.addTextChangedListener(watcher1);
		et2.addTextChangedListener(watcher2);
		DBConnection conn=new DBConnection(getApplicationContext());
		SQLiteDatabase db=conn.getConnection();
		Cursor cursor=db.query("routes", null, null, null, null, null, null);
		list1=new ArrayList<Map<String,Object>>();
		for(int i=cursor.getCount()-1;i>=0;i--)
		{
		    cursor.moveToPosition(i);		
			map1=new HashMap<String, Object>();
			map1.put("image", R.drawable.st_en);
			String start=cursor.getString(cursor.getColumnIndex("start"));
			map1.put("start",start);
			String end=cursor.getString(cursor.getColumnIndex("end"));
			map1.put("end", end);
			list1.add(map1);
		}
		if(list1.isEmpty()){
			Button clear=(Button)findViewById(R.id.clearhistory);
			clear.setVisibility(View.GONE);
		}	
		adapter1=new SimpleAdapter(this, list1, R.layout.historyroute_listview
				, new String[]{"image","start","end"}, new int[]{R.id.imageView1,R.id.textView1
						,R.id.textView2});
		lv1.setAdapter(adapter1);
		lv1.setOnItemClickListener(lvListener1);
		registerForContextMenu(lv1);
		
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("操作");
		menu.add(0,ITEM1, 0, "删除该条搜索记录");
		menu.add(0,ITEM2, 0, "取消");
	
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ITEM1:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			DBConnection conn=new DBConnection(getApplicationContext());
			SQLiteDatabase db=conn.getConnection();
			Map<String, Object> map=(Map<String, Object>)adapter1.getItem(info.position);
			String start=(String)map.get("start");
			String end=(String)map.get("end");
			db.delete("routes", "start=? and end=?", new String[]{start,end});
			db.close();
			onCreate(null);
		
			Toast.makeText(getApplicationContext(),"成功删除1条记录！", Toast.LENGTH_LONG).show();
			break;
		case ITEM2:
			break;
		}
		return true;
	}
	private OnItemClickListener lvListener1=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=(HashMap<String, Object>)parent.getItemAtPosition(position);
			Intent it=new Intent(TransferActivity.this, TakePlanActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("start", (String)map.get("start"));
			bundle.putString("end", (String)map.get("end"));
			bundle.putString("city", city);
			bundle.putDouble("Latitude", x);
			bundle.putDouble("Longitude", y);
			it.putExtras(bundle);
			startActivity(it);
			
		}
	};
	public void clearHistory(View v) {
		DBConnection conn=new DBConnection(getApplicationContext());
		SQLiteDatabase db=conn.getConnection();
		db.delete("routes", null, null);
		db.close();
		onCreate(null);
		
	}
	private OnItemClickListener lvListener2=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			HashMap<String, Object> map=(HashMap<String, Object>)parent.getItemAtPosition(position);
			String adr=(String)map.get("address");
			if(et1.isFocused()){
				if (et2.getText().toString().equals("")) {
					et1.setText(adr);
				}
				else{
					et1.setText(adr);
					DBConnection conn=new DBConnection(getApplicationContext());
					SQLiteDatabase db=conn.getConnection();
					ContentValues values=new ContentValues();
					values.put("start", st_end(et1.getText().toString()));
					values.put("end", st_end(et2.getText().toString()));
					db.insert("routes", null, values);
					db.close();
					Intent it=new Intent(TransferActivity.this, TakePlanActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("start", et1.getText().toString());
					bundle.putString("end", et2.getText().toString());
					bundle.putString("city", city);
					bundle.putDouble("Latitude", x);
					bundle.putDouble("Longitude", y);
					it.putExtras(bundle);
					startActivity(it);
				}
				
				
			}
			if(et2.isFocused()){
				et2.setText(adr);
				DBConnection conn=new DBConnection(getApplicationContext());
				SQLiteDatabase db=conn.getConnection();
				ContentValues values=new ContentValues();
				values.put("start", st_end(et1.getText().toString()));
				values.put("end", st_end(et2.getText().toString()));
				db.insert("routes", null, values);
				db.close();
				System.out.println("插入成功");
				Intent it=new Intent(TransferActivity.this, TakePlanActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("start", et1.getText().toString());
				bundle.putString("end", et2.getText().toString());
				bundle.putString("city", city);
				bundle.putDouble("Latitude", x);
				bundle.putDouble("Longitude", y);
				it.putExtras(bundle);
				startActivity(it);
			}
				
		}
	};
	public String st_end(String s) {
		if (s.equals("我的位置")) {
			s=myLocation;
		}
		return s;
	}
	private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError){
//				Toast.makeText(getApplicationContext(), location.getCity(), Toast.LENGTH_LONG).show();
				city=location.getCity();
				x=location.getLatitude();
	            y=location.getLongitude();
		        String adr=location.getLocationDescribe();
		        myLocation=adr.substring(1, adr.length()-2);
//		        Toast.makeText(getApplicationContext(), adr, Toast.LENGTH_LONG).show();
				}
			
		}
	};
	private View.OnClickListener listener1=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			et1.setText("");
			clear1.setVisibility(View.GONE); 
		}
	};
	private View.OnClickListener listener2=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 et2.setText("");
			 clear2.setVisibility(View.GONE); 
		}
	};
	private TextWatcher watcher1=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() <= 0) {
				clear1.setVisibility(View.GONE);
				list2.clear();
				map2=new HashMap<String, Object>();
				map2.put("image", R.drawable.mylocation);
				map2.put("address", "我的位置");
				list2.add(map2);
				adapter2=new SimpleAdapter(TransferActivity.this, list2, R.layout.search_listview, new String[]{"image","address"},
						new int[]{R.id.imageView1,R.id.textView1});
				lv2.setAdapter(adapter2);
				return;
			}
			
			/**
			 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
			 */
			else{
				clear1.setVisibility(View.VISIBLE);
				mSuggestionSearch
				.requestSuggestion((new SuggestionSearchOption())
						.keyword(s.toString())
						.city(city));
//		Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_LONG).show();
			}
	
		}
	
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	
		}
		
		@Override
		public void afterTextChanged(Editable s) {
	
		}
	};
private TextWatcher watcher2=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() <= 0) {
				clear2.setVisibility(View.GONE);
				list2.clear();
				map2=new HashMap<String, Object>();
				map2.put("image", R.drawable.mylocation);
				map2.put("address", "我的位置");
				list2.add(map2);
				adapter2=new SimpleAdapter(TransferActivity.this, list2, R.layout.search_listview, new String[]{"image","address"},
						new int[]{R.id.imageView1,R.id.textView1});
				lv2.setAdapter(adapter2);
				return;
			}
			
			/**
			 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
			 */
			else{
				clear2.setVisibility(View.VISIBLE);
				mSuggestionSearch
				.requestSuggestion((new SuggestionSearchOption())
						.keyword(s.toString())
						.city(city));
//		Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_LONG).show();
			}
			
			
		}
		
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
			
		}
	};
	
	public void change(View v) {
		String str=et1.getText().toString();
		et1.setText(et2.getText().toString());
		et2.setText(str);
	}
	private OnFocusChangeListener mlistener=new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				if(et1.equals(v)&&et1.getText().toString().equals("我的位置"))
					et1.setText("");
				if(et1.equals(v)&&!et1.getText().toString().equals(""))
					clear1.setVisibility(View.VISIBLE);
				if(et2.equals(v)&&et2.getText().toString().equals("我的位置"))
					et2.setText("");
				if(et2.equals(v)&&!et2.getText().toString().equals(""))
					clear2.setVisibility(View.VISIBLE);
				linearlayout2.setVisibility(View.VISIBLE);
				linearlayout1.setVisibility(View.GONE);
			}
			if(!hasFocus){
				if(et1.equals(v)){
					clear1.setVisibility(View.GONE);
					if(et1.getText().toString().equals("")){
						et1.setText("我的位置");
						clear1.setVisibility(View.GONE);
					}
				}
				if(et2.equals(v))
					clear2.setVisibility(View.GONE);
				linearlayout1.setVisibility(View.VISIBLE);
				linearlayout2.setVisibility(View.GONE);
			}
		}
			
		
	};
	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
//		Toast.makeText(getApplicationContext(), "测试", Toast.LENGTH_LONG).show();
		list2.clear();
		map2.put("image", R.drawable.mylocation);
		map2.put("address", "我的位置");
		list2.add(map2);
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			
			if (info.key != null){
				map2=new HashMap<String, Object>();
				map2.put("image", R.drawable.slocation);
				map2.put("address", info.key);
				list2.add(map2);
				
			}
				
		}
		
		
		adapter2=new SimpleAdapter(this, list2, R.layout.search_listview, new String[]{"image","address"},
			new int[]{R.id.imageView1,R.id.textView1});
		lv2.setAdapter(adapter2);
			
		
	}

	
	
}

