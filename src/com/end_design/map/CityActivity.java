package com.end_design.map;

import java.util.ArrayList;
import java.util.List;

import com.example.map.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CityActivity extends Activity {
	private GridView gview;
	private Button bt_back;
	private ArrayAdapter<String> sim_adapter;
	private String[] cities = { "北京", "上海", "广州", "武汉", "重庆", "深圳", "杭州",
            "江苏" };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_city);
		bt_back=(Button)findViewById(R.id.button_back);
		gview = (GridView) findViewById(R.id.gridView1);
		sim_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        gview.setAdapter(sim_adapter);
        gview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
//				Toast.makeText(getApplicationContext(), city, Toast.LENGTH_LONG).show();
				Intent it=getIntent();
				Bundle bd=new Bundle();
				bd.putString("city", cities[position]);
				it.putExtras(bd);
				setResult(1, it);
				finish();
			}
		});

	}
	public void back(View v) {
		finish();
	}

	
}
