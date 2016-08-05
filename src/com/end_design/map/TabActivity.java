package com.end_design.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.map.R;

import SQLite.DBConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import myAdapter.myTablistview_adapter;

public class TabActivity extends Activity {
	private ListView lv;
	private ArrayList<String> list;
	private myTablistview_adapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tabset);
		lv=(ListView)findViewById(R.id.listView1);
		Button add_bt=new Button(this);
		add_bt.setText("+新建标签");
		add_bt.setBackgroundResource(R.drawable.selector_collect);
		add_bt.setOnClickListener(mListener);
		lv.addFooterView(add_bt);
		DBConnection conn=new DBConnection(getApplicationContext());
		SQLiteDatabase db=conn.getConnection();
		Cursor cursor=db.query("tabs", null, null, null, null, null, null);
		list=new ArrayList<String>();
		while(cursor.moveToNext()){
			list.add(cursor.getString(1));
		}
		
		db.close();
//		Toast.makeText(getApplicationContext(), list.get(0), Toast.LENGTH_LONG).show();
		adapter=new myTablistview_adapter(this, list);
		lv.setAdapter(adapter);
		
		
	}
	private OnClickListener mListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			Toast.makeText(getApplicationContext(), "fds", Toast.LENGTH_LONG).show();
			Builder builder=new AlertDialog.Builder(TabActivity.this);
			builder.setIcon(R.drawable.tab);
			builder.setTitle("添加标签");
		    final EditText et=new EditText(TabActivity.this);
		    et.setBackgroundColor(Color.WHITE);
		    builder.setView(et);
		    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DBConnection conn=new DBConnection(TabActivity.this);
					SQLiteDatabase db=conn.getConnection();
					ContentValues values=new ContentValues();
					if (et.getText().toString().isEmpty()) {
						Toast.makeText(TabActivity.this, "输入不能为空！", Toast.LENGTH_LONG).show();
					}
					else {
						values.put("tab", et.getText().toString());
						db.insert("tabs", null, values);
						db.close();
						Toast.makeText(TabActivity.this, "添加标签成功！", Toast.LENGTH_LONG).show();
						list.add(et.getText().toString());
						adapter.setItemList(list);
						lv.setAdapter(adapter);
						
					}
					
					
				}
			});
		    
		    builder.setNegativeButton("取消", null)
		    	   .create().show();
			
		}
	};
	
	public void back(View v) {
		finish();
	}
	
	
	
}
