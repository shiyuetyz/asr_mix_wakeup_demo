package cn.yunzhisheng.demo.ui;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ModelDB extends SQLiteOpenHelper{

	public static final String TAG = "ModelOpenHelper";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "modeldb";

	public static final String MODEL_TABLE_NAME = "_model";
	public static final String MODEL_ID = "_id";
	public static final String MODEL_CODE = "_code";
	public static final String MODEL_PATH = "_path";

	private static final String MODEL_TABLE_CREATE = "CREATE TABLE "
			+ MODEL_TABLE_NAME + " (" 
			+ MODEL_ID + " INTEGER PRIMARY KEY, " 
			+ MODEL_CODE + " TEXT, "
			+ MODEL_PATH + " TEXT);";
	
	public ModelDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MODEL_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
