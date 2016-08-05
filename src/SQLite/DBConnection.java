package SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "test.db";  
    private static final int DATABASE_VERSION = 1;  
    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table routes("
				+ "id integer primary key autoincrement,"
				+ "start text,"
				+ "end text)"
				   ); 
		db.execSQL("create table collects("
				+ "id integer primary key autoincrement,"
				+ "start text,"
				+ "end text,"
				+ "city text,"
				+ "bus text,"
				+ "tab text)"
				   ); 
		db.execSQL("create table tabs("
				+ "id integer primary key autoincrement,"
				+ "tab text)"
				   ); 
		db.execSQL("insert into tabs values(null,?)",new String[]{"上班"});
		db.execSQL("insert into tabs values(null,?)",new String[]{"回家"});
		System.out.println("数据表创建成功！");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	

	public SQLiteDatabase getConnection(){
	    SQLiteDatabase db=getWritableDatabase();
	    return db;
	}
	public void close(SQLiteDatabase db){
	    db.close();
	}

}
