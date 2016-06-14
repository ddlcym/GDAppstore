package com.changhong.gdappstore.service;

import android.content.Context;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.util.SharedPreferencesUtil;

public class CacheManager {
	public static final String KEYJSON_CATEGORIES = "keyjsoncategories";
	public static final String KEYJSON_PAGEAPPS = "keyjsonpageapps";
	public static final String KEYJSON_CATEGORYAPPS = "keyappbycategoryid";
	public static final String KEYJSON_TOPICAPPS = "keyappbytopicid";
	public static final String KEYJSON_RANKLIST = "keyjsonranklist";
	public static final String KEYJSON_RECOMMENDAPPS = "keyrecommendapps";
	public static final String KEYJSON_BACKUPEDAPPS = "keybackupedapps";
	public static final String KEYJSON_HOMEPAGEPOSTER = "keyhomepageposter";
	/**是否采用缓存的已备份应用数据。如果用户没有操作过备份应用和删除备份应用，则获取备份应用信息时候采用缓存*/
	public static boolean useCacheBackupedApps=false;

	/**
	 * 存放json文件缓存
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static final void putJsonFileCache(Context context, String key, String value) {
		if (Config.ISCACHEABLE) {
			SharedPreferencesUtil.putJsonCache(context, key, value);
		}
	}

	/**
	 * 获取json文件缓存
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static final String getJsonFileCache(Context context, String key) {
		if (Config.ISCACHEABLE) {
			return SharedPreferencesUtil.getJsonCache(context, key);
		}
		return "";
	}
}
