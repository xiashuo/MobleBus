package com.end_design.map;

import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidumap.overlay.OverlayManager;
import com.baidumap.overlay.TransitRouteOverlay;
import com.baidumap.overlay.WalkingRouteOverlay;
import com.example.map.R;
import com.location.service.LocationService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class RutePlanActivity extends Activity implements OnGetRoutePlanResultListener,OnMapClickListener{
	Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = 0; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    private TextView popupText = null; // 泡泡view
    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null;  // 搜索模块，也可去掉地图模块独立使用
    LatLng mPoint;
    private LocationService locationService;
    private String city;
    private String start;
    private String end;
    private String info;
    private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_walk_rute);
		Intent it=getIntent();
		Bundle bundle=it.getExtras();
		start=bundle.getString("start");
		end=bundle.getString("end");
		info=bundle.getString("info");
		title=(TextView)findViewById(R.id.title);
		Button location_bt=(Button)findViewById(R.id.location);
		Button back=(Button)findViewById(R.id.button_back);
		mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();
        
        // 开启定位图层
        mBaidumap.setMyLocationEnabled(true);
        locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
     
	}
	public void location(View v) {
		 MapStatus mMapStatus = new MapStatus.Builder()
                 .target(mPoint)
                 .build();
         MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
         //改变地图状态
         mBaidumap.setMapStatus(mMapStatusUpdate);
	}
	public void back(View v) {
		finish();
	}
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == 0 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
     // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RutePlanActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        if (nodeTitle.length()>15) {
			popupText.setWidth(600);
			nodeTitle+="\n";
		}
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
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
            Toast.makeText(RutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            Toast.makeText(RutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(mPoint)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaidumap.setMapStatus(mMapStatusUpdate);
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
           
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
		
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(mPoint)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaidumap.setMapStatus(mMapStatusUpdate);
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
//          Toast.makeText(getApplicationContext(), start, Toast.LENGTH_LONG).show();
        }
		
	}
private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError){
//			Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_LONG).show();
				city=location.getCity();         
	         // 设置起终点信息，对于tranist search 来说，城市名无意义
	            mPoint=new LatLng(location.getLatitude(), location.getLongitude());
	            
	            PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
	            PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
	            if(info.equals("walk")){
	            	if (start.equals("我的位置")) {
	            		stNode = PlanNode.withLocation(mPoint);
					}
	    			title.setText("步行路线");
	    			mSearch.walkingSearch((new WalkingRoutePlanOption())
	                        .from(stNode).to(enNode));
	            }
	    		if(info.equals("bus")){
	    			if (start.equals("我的位置")) {
	            		stNode = PlanNode.withLocation(mPoint);
					}
	    			if (end.equals("我的位置")) {
	            		enNode = PlanNode.withLocation(mPoint);
					}
	    			title.setText("换乘路线");
	    			mSearch.transitSearch((new TransitRoutePlanOption())
	                        .from(stNode).city(city).to(enNode));
	    			
	    		}
			}
			
		}
	};

	@Override
	public void onMapClick(LatLng arg0) {
		mBaidumap.hideInfoWindow();
		
	}
	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }
	
}
