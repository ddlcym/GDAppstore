package com.changhong.gdappstore.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库
 * 
 * @author wangxiufeng
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	/** 数据库名字 */
	private final static String DATABASE_NAME = "app.db";
	/** 数据库app版本信息表 */
	public final static String TABLE_OTHERAPPS = "otherapps";
	/** app版本信息表 app包名列 */
	public static final String CLUM_PCKNAME = "packagename";

	private static int CURRENT_VERSION = 1;

	private static DatabaseHelper databaseHelper = null;

	protected DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, CURRENT_VERSION);
	}

	public static DatabaseHelper getInstance(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context);
		}

		return databaseHelper;
	}

	/**
	 * 数据库第一次创建的时候别调用的
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_OTHERAPPS + " ( " + CLUM_PCKNAME + " VARCHAR(100) PRIMARY KEY )");

	}

	/**
	 * 数据库版本更新的时候被调用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTHERAPPS);
		onCreate(db);
	}
}
