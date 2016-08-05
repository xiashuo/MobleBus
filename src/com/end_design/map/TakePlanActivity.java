package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidumap.overlay.TransitRouteOverlay;
import com.example.map.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class TakePlanActivity extends Activity implements OnGetRoutePlanResultListener{
	private TransitRouteLine route = null;
	private RoutePlanSearch mSearch = null;  // 搜索模块，也可去掉地图模块独立使用
	private LatLng mPoint;
	private ListView lv;
	private SimpleAdapter Adapter;
	private Map<String, String> map;
	private List<Map<String,String>> list;
	String city;
	String start;
	String end;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_take_plan);
		Button back=(Button)findViewById(R.id.button_back);
		lv=(ListView)findViewById(R.id.listView1);
		lv.setOnItemClickListener(mlistener);
		mSearch = RoutePlanSearch.newInstance();
	    mSearch.setOnGetRoutePlanResultListener(this);
		Intent it=getIntent();
		Bundle bundle=it.getExtras();
		city=bundle.getString("city");
		start=bundle.getString("start");
		end=bundle.getString("end");
		mPoint=new LatLng(bundle.getDouble("Latitude"), bundle.getDouble("Longitude"));
		PlanNode stNode=PlanNode.withCityNameAndPlaceName(city, start);
		PlanNode enNode=PlanNode.withCityNameAndPlaceName(city, end);
		if(start.equals("我的位置")){
			stNode=PlanNode.withLocation(mPoint);
		}
		if(end.equals("我的位置")){
			enNode=PlanNode.withLocation(mPoint);
		}
		mSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode).city(city).to(enNode));
	}
	private OnItemClickListener mlistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent it=new Intent(TakePlanActivity.this, TakeInfoActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("city", city);
			bundle.putString("start", start);
			bundle.putString("end",end);
			bundle.putDouble("Latitude", mPoint.latitude);
			bundle.putDouble("Longitude", mPoint.longitude);
			bundle.putInt("index", position);
			Map<String, String> map=(HashMap<String, String>)parent.getItemAtPosition(position);
			bundle.putString("id", map.get("id"));
			bundle.putString("takeinfo", map.get("takeinfo"));
			bundle.putString("distance", map.get("distance"));
			bundle.putString("time", map.get("time"));
			it.putExtras(bundle);
			startActivity(it);
		}
	};

	public void back(View v) {
		finish();
	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(TakePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
        	list=new ArrayList<Map<String,String>>();
        	char id='A';
            for (TransitRouteLine rute : result.getRouteLines()) {
				String distance=Distance((rute.getDistance()));
				String time=Time(rute.getDuration());
				String takeinfo="";
				for (TransitStep step : rute.getAllStep()) {
					if(step.getVehicleInfo()!=null){
						takeinfo+=step.getVehicleInfo().getTitle();
						if(rute.getAllStep().indexOf(step)<(rute.getAllStep().size()-2))
							takeinfo+="→";
					}
				}
				map=new HashMap<String, String>();
				map.put("takeinfo", takeinfo);
				map.put("distance", distance);
				map.put("time", time);
				map.put("id", String.valueOf(id));
				id++;
				list.add(map);
			}
            Adapter=new SimpleAdapter(this, list, R.layout.takeplan_listview,
            		new String[]{"id","takeinfo","time","distance"}, 
            		new int[]{R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4} );
            lv.setAdapter(Adapter);
           
        } 
        
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	public String Time(int time)
	{
		String hour = null,min;
		if(time>3600){
			hour=String.valueOf(time/3600);
			time=time%3600;
		}
		min=String.valueOf(time/60);
		if (hour==null) {
			return min+"分钟";
		}
		else
			return hour+"小时"+min+"分钟";
		
	}
	public String Distance(int distance)
	{
		String dis=String.valueOf((double)distance/1000)+"km";
		return dis;
		
	}
	@Override
    protected void onDestroy() {
        mSearch.destroy();
        super.onDestroy();
    }
	
	
}
