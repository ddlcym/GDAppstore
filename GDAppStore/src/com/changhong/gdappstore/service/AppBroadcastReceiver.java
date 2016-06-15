package com.changhong.gdappstore.service;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.Util;

public class AppBroadcastReceiver extends BroadcastReceiver {
	// public static AppDetail curAppDetail;

	/** 网络情况改变回调监听器 key为context.getClass().getName()以防重复添加 */
	public static Map<String, AppChangeListener> listeners = new HashMap<String, AppBroadcastReceiver.AppChangeListener>();

	/**
	 * 网络变化监听器
	 * 
	 * @author wangxiufeng
	 * 
	 */
	public interface AppChangeListener {
		void onAppChange(Intent intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (listeners != null && listeners.size() > 0) {
			for (AppChangeListener listener : listeners.values()) {
				listener.onAppChange(intent);
			}
		}
		// 接收安装广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString();
			L.d("AppBroadcastReceiver--installed packageName=" + packageName + " ");

			if (packageName != null && packageName.startsWith("package:")) {
				packageName = packageName.substring(packageName.indexOf(":") + 1, packageName.length());
			}

			String installedPath = DownLoadManager.map_filepath.get(packageName);
			if (!TextUtils.isEmpty(installedPath)) {
				Util.deleteFile(installedPath);// 删除该安装apk的安装包
				DownLoadManager.map_filepath.remove(packageName);// 删除记录
//				String filename = installedPath.substring(installedPath.lastIndexOf("/"), installedPath.length());
//				DialogUtil.showShortToast(context, "删除安装包" + filename);
			}
			
			if (DownLoadManager.list_loadingUrl.size() == 0 && DownLoadManager.map_filepath.size()==0) {
				// 没有下载任务时候删除所有安装包
				Util.deleteFileChildrens(Config.baseXutilDownPath);
			}
		}
		// 接收卸载广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString();
			L.d("AppBroadcastReceiver--uninstalled app " + packageName);
		}

	}
}
