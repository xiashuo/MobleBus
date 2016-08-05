package myAdapter;

import java.util.ArrayList;


import com.example.map.R;


import SQLite.DBConnection;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class myTablistview_adapter extends BaseAdapter {
	public static final String LOG_TAG="";
	private ArrayList<String> list;
	private Context context; 
	private LayoutInflater listContainer;
	public final class ListItemView{                //自定义控件集合     
		private TextView tv;
		private Button bt_rename;
		private Button bt_delete;    
	}     
	
	public  myTablistview_adapter(Context context,ArrayList<String> list) {
		this.context=context;
		this.list=list;
		listContainer = LayoutInflater.from(context); 
	}
	public void setItemList(ArrayList<String> list) {
        this.list = list;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("method", "getView"); 
		ListItemView  listItemView = null;  
		final int selectID = position;	
		if (convertView==null) {
			listItemView = new ListItemView();    
            //获取list_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.tab_listview, null);
          //获取控件对象   
            listItemView.tv=(TextView)convertView.findViewById(R.id.textView1);
        	listItemView.bt_rename=(Button)convertView.findViewById(R.id.button1);
            listItemView.bt_delete=(Button)convertView.findViewById(R.id.button2);
          //设置控件集到convertView   
            convertView.setTag(listItemView);
		}
		else {
			listItemView=(ListItemView)convertView.getTag();
		}
		
			listItemView.tv.setText(list.get(selectID));
			
			listItemView.bt_rename.setText("重命名");
			listItemView.bt_delete.setText("删除");
			listItemView.bt_rename.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Builder builder=new AlertDialog.Builder(context);
					builder.setTitle("重命名");
				    final EditText et=new EditText(context);
				    et.setText(list.get(selectID));
				    et.setBackgroundColor(Color.WHITE);
				    builder.setView(et);
				    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							tab_Operate(et.getText().toString(), list.get(selectID));
							list.set(selectID, et.getText().toString());
							notifyDataSetChanged();
						}
					});
				    builder.setNegativeButton("取消", null)
				    	   .create().show();
				}
			});
			listItemView.bt_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Builder builder=new AlertDialog.Builder(context);
					builder.setIcon(R.drawable.alert);
					builder.setTitle("提示")
					       .setMessage("你确定要删除该标签及该标签下的所有收藏吗？")
					       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DBConnection conn=new DBConnection(context);
								SQLiteDatabase db=conn.getConnection();
								db.delete("tabs", "tab=?", new String[]{list.get(selectID)});
								db.delete("collects", "tab=?", new String[]{list.get(selectID)});
								db.close();
								Toast.makeText(context, "删除标签成功", Toast.LENGTH_LONG).show();
								list.remove(selectID);
								notifyDataSetChanged();
							}
						});
					builder.setNegativeButton("取消", null)
					 	   .create().show();
					
				}
			});
				
		
		return convertView;
	}
	
	public void tab_Operate(String newtab,String oldtab) {
		DBConnection conn=new DBConnection(context);
		SQLiteDatabase db=conn.getConnection();	
		ContentValues values=new ContentValues();
		values.put("tab", newtab);
		db.update("tabs", values, "tab=?", new String[]{oldtab});
		db.close();
		Toast.makeText(context, "标签重命名成功", Toast.LENGTH_LONG).show();
		
	}
}
