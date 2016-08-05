package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.map.R;
import com.location.service.LocationService;
import com.test.view.RefreshableView;
import com.test.view.RefreshableView.PullToRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class NearbyActivity extends Activity implements OnGetPoiSearchResultListener{
	private String city;
//	private String mlocation;
	private PoiSearch mPoiSearch = null;
	private LocationService locationService;
	private SimpleAdapter Adapter;
	private ArrayList<HashMap<String, Object>> nearby_list;
	LatLng localPoint;
	RefreshableView refreshableView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);
		
 		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
 		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void> () {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                            
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        return null;
                    }
                    
                    protected void onPostExecute(Void result) {
                    	onStart();
                    	Toast.makeText(NearbyActivity.this, "刷新成功！", Toast.LENGTH_LONG).show();
                    	refreshableView.finishRefreshing();
                    };
                    
                }.execute();
			}
		}, 0);
 		ListView lv=(ListView)findViewById(R.id.listView1);
 		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map=(HashMap<String, Object>)parent.getItemAtPosition(position);
				String address=(String) map.get("address");
				String bus=(String) map.get("bus");
				Intent it=new Intent(NearbyActivity.this, StationActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("address", address);
				bundle.putString("bus", bus);
				bundle.putString("city", city);
//				bundle.putString("mlocation",mlocation );
				it.putExtras(bundle);
				startActivity(it);
			}
		});
 		
	}
	
	@Override
	protected void onStart() {
		locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
     // 初始化搜索模块，注册搜索事件监听
 		mPoiSearch = PoiSearch.newInstance();
 		mPoiSearch.setOnGetPoiSearchResultListener(this);
		super.onStart();
	}
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
       
       
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
       
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        
    }  

	

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(NearbyActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//			Toast.makeText(getApplicationContext(), result.getAddress(), Toast.LENGTH_LONG).show();
			return;
		}
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(NearbyActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}
		else {
			Toast.makeText(  
                    NearbyActivity.this,  
                    "总共查到" + result.getTotalPoiNum() + "站点", Toast.LENGTH_SHORT).show();  
			nearby_list=new ArrayList<HashMap<String,Object>>();
			for (PoiInfo i : result.getAllPoi()) {
				HashMap<String, Object> info=new HashMap<String, Object>();
				info.put("image", R.drawable.timg2);
				info.put("address", i.name);
				info.put("bus", i.address);
				nearby_list.add(info);	    	
				LatLng pll=new LatLng(i.location.latitude, i.location.longitude);
				int distance=(int)DistanceUtil.getDistance(localPoint, pll);
				info.put("distance", distance+"米");
//				Toast.makeText(getApplicationContext(), list.get((int) i++), Toast.LENGTH_LONG).show();
			}
			city=result.getAllPoi().get(1).city;
		    Adapter=new SimpleAdapter(this, nearby_list, R.layout.nearby_listview,
		    		new String[]{"image","address","bus","distance"}, new int[]{R.id.imageView1,R.id.textView1,R.id.textView2,R.id.textView3});
		    
		    ListView lv=(ListView)findViewById(R.id.listView1);
		    lv.setAdapter(Adapter);
		    setListViewHeightBasedOnChildren(lv);
		}
			
		
	}
	public void setListViewHeightBasedOnChildren(ListView listView) {   
        // 获取ListView对应的Adapter   
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);   
            // 计算子项View 的宽高   
            listItem.measure(0, 0);    
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);   
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
	       
			}
			localPoint=new LatLng(location.getLatitude(), location.getLongitude());
//			mlocation=location.getAddrStr();
//			Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_LONG).show();
		}
	
	};
	
}
