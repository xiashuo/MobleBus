package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.example.map.R;
import com.test.view.ImageBtn;

import SQLite.DBConnection;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;


public class CollectActivity extends Activity {
	private ExpandableListView eplv;
	List<String> groups;
	SimpleExpandableListAdapter mAdapter;
	String city;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		eplv=(ExpandableListView)findViewById(R.id.expandableListView1);
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        DBConnection conn=new DBConnection(getApplicationContext());
        SQLiteDatabase db=conn.getConnection();
        Cursor cursor1=db.query("tabs", null, null, null, null, null, null);
        while(cursor1.moveToNext()){
            Map<String, String> curGroupMap = new HashMap<String, String>();
            String tab=cursor1.getString(cursor1.getColumnIndex("tab"));
            curGroupMap.put("tab",tab);
            groupData.add(curGroupMap);  
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            Cursor cursor2=db.query("collects", null, "tab=?", new String[]{tab}, null, null, null);
            while(cursor2.moveToNext()){
                Map<String, String> curChildMap = new HashMap<String, String>();
                curChildMap.put("bus",cursor2.getString(cursor2.getColumnIndex("bus")));
                curChildMap.put("start",cursor2.getString(cursor2.getColumnIndex("start")));
                curChildMap.put("end","开往  "+cursor2.getString(cursor2.getColumnIndex("end")));
                children.add(curChildMap);
                city=cursor2.getString(cursor2.getColumnIndex("city"));
            }
            
            ;
            childData.add(children);
        }
        db.close();
        // Set up our adapter
        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                R.layout.group_elist,
                new String[] { "tab"},
                new int[] {R.id.textView1},
                childData,
                R.layout.collect_listview,
                new String[] {"bus","start", "end" },
                new int[] { R.id.textView1,R.id.textView2,R.id.textView3 }
                );
        eplv.setAdapter(mAdapter);
        eplv.setOnChildClickListener(ChildClickListener);
        registerForContextMenu(eplv);
	} 
	private OnChildClickListener ChildClickListener=new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			Map<String, String> map=new HashMap<String, String>();
			map=(HashMap<String, String>)mAdapter.getChild(groupPosition, childPosition);
			String bus=map.get("bus");
			String start=map.get("start");
			Intent it=new Intent(CollectActivity.this, BusRuteActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("busNum", bus);
			bundle.putString("city", city);
			bundle.putString("nearist", start);
			it.putExtras(bundle);
			startActivity(it);
			
			return true;
		}
	};
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition); 
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
        if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD ){
        	menu.setHeaderTitle("操作");
    		getMenuInflater().inflate(R.menu.collect, menu);; 
        }
		
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu1:
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo)item.getMenuInfo();
			int child_index=ExpandableListView.getPackedPositionChild(info.packedPosition);
			int group_index=ExpandableListView.getPackedPositionGroup(info.packedPosition);
			DBConnection conn=new DBConnection(getApplicationContext());
			SQLiteDatabase db=conn.getConnection();
			Cursor cursor=db.query("tabs", null, null, null, null, null, null);
			cursor.moveToPosition(group_index);
			String tab=cursor.getString(1);
			cursor=db.query("collects", null, "tab=?", new String[]{tab}, null, null, null);
			cursor.moveToPosition(child_index);
			String bus=cursor.getString(4);
			String start=cursor.getString(1);
			db.delete("collects", "tab=? and bus=? and start=?", new String[]{tab,bus,start});
			Log.e("test", "瞎搞");
			db.close();
			Toast.makeText(getApplicationContext(), "已取消收藏该路线", Toast.LENGTH_LONG).show();
			onCreate(null);
//			
			return true;
		case R.id.menu2:
		
			return true;
		default:
			return false;
		}
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		onCreate(null);
	}
	
}
