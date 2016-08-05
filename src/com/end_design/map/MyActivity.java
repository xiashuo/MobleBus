package com.end_design.map;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.example.map.R;
import com.location.service.LocationService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity implements MKOfflineMapListener{
	private LocationService locationService;
	private String city;
	private MKOfflineMap mOffline = null;
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private TextView stateView;
	private Button state_bt;
	private int flag=0;
	private int cityid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my);
		stateView=(TextView)findViewById(R.id.textView4);
		state_bt=(Button)findViewById(R.id.button1);
		locationService=new LocationService(getApplicationContext());
		locationService.registerListener(listener);
		locationService.start();
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		
	}
	
	public void start(View v) {
	    switch (flag) {
		case 0:
			mOffline.start(cityid);
			state_bt.setBackgroundResource(R.drawable.ing);
			flag=-1;
			break;
		case 2:
			remove();
		default:
			break;
		}
		

	        
	}
	public void remove() {
		Builder builder=new AlertDialog.Builder(MyActivity.this);
		builder.setIcon(R.drawable.alert)
		       .setTitle("提示")
		       .setMessage("你确定要删除已下载的离线地图吗？")
		       .setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mOffline.remove(cityid);
					state_bt.setBackgroundResource(R.drawable.download);
					flag=0;
				}
			})
		       .setNegativeButton("取消", null)
		       .create().show();
	}
	public void setTab(View v) {
		Intent it=new Intent(MyActivity.this, TabActivity.class);
		startActivity(it);
	}
	private BDLocationListener listener=new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			city=location.getCity(); 
			TextView tv1=(TextView)findViewById(R.id.textView3);
			tv1.setText("离线地图("+city+")");
			ArrayList<MKOLSearchRecord> records = mOffline.searchCity(city);
	        if (records == null || records.size() != 1) {
	            return;
	        }
	        cityid =records.get(0).cityID;
	        MKOLUpdateElement element=null;
	        localMapList=mOffline.getAllUpdateInfo();
	        for (MKOLUpdateElement e : localMapList) {
				if (e.cityName.equals(city)) {
					element=e;
					
					break;
				}
			}
	        if (element==null) {
				state_bt.setBackgroundResource(R.drawable.download);
				flag=0;
//				Toast.makeText(getApplicationContext(),"fdf", Toast.LENGTH_LONG)
//				.show();
			}
	        else {
				if (element.status==MKOLUpdateElement.FINISHED) {
					state_bt.setBackgroundResource(R.drawable.delete);
					flag=2;
				} 
				else {
					flag=0;
				}	
				
			}
		}
	};
	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				stateView.setText(update.ratio+"%");	
			}
			if (update.ratio==100) {
				state_bt.setBackgroundResource(R.drawable.delete);
				stateView.setText("");
				flag=2;
			}
			
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }
		
	}
	
	@Override
	protected void onPause() {
		MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
		if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
			mOffline.pause(cityid);
		}
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		mOffline.destroy();
		super.onDestroy();
	}
}
