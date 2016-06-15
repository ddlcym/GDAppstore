package com.changhong.gdappstore.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.changhong.gdappstore.util.L;

/**
 * 数据库管理类
 * 
 * @author wangxiufeng
 * 
 */
public class DBManager {

	private static DBManager dbManager = null;

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	private static Context context;

	public static DBManager getInstance(Context context2) {
		context = context2;
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}

		return dbManager;
	}

	private DBManager(Context context) {
		helper = DatabaseHelper.getInstance(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * 插入app版本信息数据
	 * 
	 * @param packageName
	 * @return 插入的id，-1表示异常
	 */
	public long insertOtherApp(String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			L.e("DBManager insertOtherApp insertOtherApp is null ");
			return -1;
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(helper.CLUM_PCKNAME, packageName);
		return db.insert(helper.TABLE_OTHERAPPS, "", contentValues);
		// db.beginTransaction(); // 开始事务
		// db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?)", new Object[] {
		// person.name, person.age,person.info });
		// db.setTransactionSuccessful(); // 设置事务成功完成
		// db.endTransaction(); // 结束事务
	}

	/**
	 * 插入或者更新数据，如果有数据就更新，没有就插入
	 * 
	 * @param app
	 */
	public void insertOrUpdateOtherApp(String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			L.d("insertOrUpdateOtherApp returned by packageName is null");
			return;
		}
		boolean hasthisApp = isOtherApp(packageName);
		if (hasthisApp) {
			deleteOtherApp(packageName);
			insertOtherApp(packageName);
		} else {
			insertOtherApp(packageName);
		}
	}

	/**
	 * 删除
	 * 
	 * @param packageName
	 */
	public void deleteOtherApp(String packageName) {
		db.delete(helper.TABLE_OTHERAPPS, helper.CLUM_PCKNAME + "= ?", new String[] { packageName + "" });
	}

	/**
	 * 查询某个应用是否是其它应用（每次调用这个方法回去查询一次数据库，数据量多的慎用！）
	 * 
	 * @param packagename
	 * @return
	 */
	public boolean isOtherApp(String packageName) {
		boolean isother = false;
		List<String> otherApps = queryOtherApps();
		for (int i = 0; i < otherApps.size(); i++) {
			if (otherApps.get(i).equals(packageName)) {
				isother = true;
				break;
			}
		}
		return isother;
	}

	/**
	 * 查询所有其它应用
	 * 
	 * @return
	 */
	public List<String> queryOtherApps() {
		List<String> otherPackages = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT * FROM " + helper.TABLE_OTHERAPPS, null);
		if (c == null || c.getCount() == 0) {
			return otherPackages;
		}
		while (c.moveToNext()) {
			otherPackages.add(c.getString(c.getColumnIndex(helper.CLUM_PCKNAME)));
		}
		c.close();
		return otherPackages;
	}
}
