package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.example.map.R;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TakeInfoActivity extends Activity implements OnGetRoutePlanResultListener{
	private TransitRouteLine route = null;
	private RoutePlanSearch mSearch = null;  // 搜索模块，也可去掉地图模块独立使用
	private LatLng mPoint;
	private ListView lv;
	private SimpleAdapter Adapter;
	private Map<String, Object> map;
	private List<Map<String, Object>> list;
	String city;
	String start;
	String end;
	int index;
	private View inflate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_take_info);
		Button back=(Button)findViewById(R.id.button_back);
		TextView tv1=(TextView)findViewById(R.id.textView1);
		TextView tv2=(TextView)findViewById(R.id.textView2);
		TextView tv3=(TextView)findViewById(R.id.textView3);
		TextView tv4=(TextView)findViewById(R.id.textView4);
		lv=(ListView)findViewById(R.id.listView1);
		Intent it=getIntent();
		Bundle bundle=it.getExtras();
		tv1.setText(bundle.getString("id"));
		tv2.setText(bundle.getString("takeinfo"));
		tv3.setText(bundle.getString("time"));
		tv4.setText(bundle.getString("distance"));
		city=bundle.getString("city");
		start=bundle.getString("start");
		end=bundle.getString("end");
		index=bundle.getInt("index");
		mPoint=new LatLng(bundle.getDouble("Latitude"), bundle.getDouble("Longitude"));
		mSearch = RoutePlanSearch.newInstance();
	    mSearch.setOnGetRoutePlanResultListener(this);
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

	
	
	public void rute(View v) {
		Intent it=new Intent(getApplicationContext(), RutePlanActivity.class);
		Bundle bundle=new Bundle();
//		bundle.putString("city", city);
		bundle.putString("start", start);
		bundle.putString("end", end);
		bundle.putString("info", "bus");
		it.putExtras(bundle);
		startActivity(it);
		
	}	
	public String deleteDaoda(String s) {
		if (s.startsWith("到达")) {
			s=s.substring(2);
		}
		return s;
	}
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
            Toast.makeText(TakeInfoActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
        	list=new ArrayList<Map<String,Object>>();
        	route=result.getRouteLines().get(index);
//        	map=new HashMap<String, String>();
//        	map.put("image", value)
        	map=new HashMap<String, Object>();
			map.put("image",R.drawable.begin);
			map.put("stepinfo", start);
			list.add(map);
            for (TransitStep step : route.getAllStep()) {
            	String intro = null;
				for (String info : stepinfo(step.getInstructions())) {
					map=new HashMap<String, Object>();
					if(info.startsWith("步行")){
						map.put("image",R.drawable.walk1);
						
					}
					
					else if (info.startsWith("乘坐")) {
						intro=info+",";
						continue;
					}
					else if (info.startsWith("经过")) {
						map.put("image", R.drawable.gongjiao);
						
						info=intro+info;
					}
					else if (info.startsWith("到达终点")) {
						map.put("image", R.drawable.zhongdian);
						
						info=end;
					}
					else
						map.put("image",R.drawable.hh);
					map.put("stepinfo", info);
					list.add(map);
					
				}
				
				
			}
            
            Adapter=new SimpleAdapter(this, list, R.layout.takeinfo_listview,
            		new String[]{"image","stepinfo","walk"}, 
            		new int[]{R.id.imageButton1,R.id.textView1,R.id.textView2});
            lv.setAdapter(Adapter);
        }
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	public String[] stepinfo(String stepinfo) {
		String[] step=stepinfo.split(",");
		return step;
		
	}
}
