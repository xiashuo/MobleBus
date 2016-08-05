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
	Button mBtnPre = null; // ��һ���ڵ�
    Button mBtnNext = null; // ��һ���ڵ�
    int nodeIndex = 0; // �ڵ�����,������ڵ�ʱʹ��
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    private TextView popupText = null; // ����view
    // ��ͼ��أ�ʹ�ü̳�MapView��MyRouteMapViewĿ������дtouch�¼�ʵ�����ݴ���
    // ���������touch�¼���������̳У�ֱ��ʹ��MapView����
    MapView mMapView = null;    // ��ͼView
    BaiduMap mBaidumap = null;
    // �������
    RoutePlanSearch mSearch = null;  // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
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
        
        // ������λͼ��
        mBaidumap.setMyLocationEnabled(true);
        locationService=new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.start();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        // ��ͼ����¼�����
        mBaidumap.setOnMapClickListener(this);
        // ��ʼ������ģ�飬ע���¼�����
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
     
	}
	public void location(View v) {
		 MapStatus mMapStatus = new MapStatus.Builder()
                 .target(mPoint)
                 .build();
         MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
         //�ı��ͼ״̬
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
        // ���ýڵ�����
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
     // ��ȡ�ڽ����Ϣ
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
        // �ƶ��ڵ�������
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
            Toast.makeText(RutePlanActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
            Toast.makeText(RutePlanActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(mPoint)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //�ı��ͼ״̬
            mBaidumap.setMapStatus(mMapStatusUpdate);
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
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
            Toast.makeText(RutePlanActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(mPoint)
                    .zoom(18)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //�ı��ͼ״̬
            mBaidumap.setMapStatus(mMapStatusUpdate);
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
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
	         // �������յ���Ϣ������tranist search ��˵��������������
	            mPoint=new LatLng(location.getLatitude(), location.getLongitude());
	            
	            PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
	            PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
	            if(info.equals("walk")){
	            	if (start.equals("�ҵ�λ��")) {
	            		stNode = PlanNode.withLocation(mPoint);
					}
	    			title.setText("����·��");
	    			mSearch.walkingSearch((new WalkingRoutePlanOption())
	                        .from(stNode).to(enNode));
	            }
	    		if(info.equals("bus")){
	    			if (start.equals("�ҵ�λ��")) {
	            		stNode = PlanNode.withLocation(mPoint);
					}
	    			if (end.equals("�ҵ�λ��")) {
	            		enNode = PlanNode.withLocation(mPoint);
					}
	    			title.setText("����·��");
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
