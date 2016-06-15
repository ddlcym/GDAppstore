package com.changhong.gdappstore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.service.AppBroadcastReceiver.AppChangeListener;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.SharedPreferencesUtil;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.util.DialogUtil.DialogBtnOnClickListener;
import com.changhong.gdappstore.util.DialogUtil.DialogMessage;

/**
 * 网络变化监听器
 * 
 * @author wangxiufeng
 * 
 */
public class NetChangeReceiver extends BroadcastReceiver {
	/** 网络情况改变回调监听器 key为context.getClass().getName()以防重复添加 */
	public static Map<String, NetChangeListener> listeners = new HashMap<String, NetChangeReceiver.NetChangeListener>();

	private static final int WHAT_SHOWNOACCESS = 11;

	private Context context;

	/**
	 * 网络变化监听器
	 * 
	 * @author wangxiufeng
	 * 
	 */
	public interface NetChangeListener {
		void onNetChange(boolean isconnect);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		boolean isconnect = NetworkUtils.isConnectInternet(context);
		String curClassName = Util.getTopActivity(context);
		L.d("网络情况发生改变，isconnect= " + isconnect + " 当前class " + curClassName + " ipaddress is "
				+ NetworkUtils.getIpAddress(context));

//		Toast.makeText(context,
//				(isconnect ? context.getString(R.string.net_connected) : context.getString(R.string.net_disconnect)),
//				Toast.LENGTH_LONG).show();

		// 监听器回调
		if (listeners != null && listeners.size() > 0) {
			for (NetChangeListener listener : listeners.values()) {
				listener.onNetChange(isconnect);
			} 
		} 

		// 重新获取能否进入应用请求认证
		requestAccessable(isconnect);
	}

	private void requestAccessable(boolean isconnect) {
//		boolean hasAccess = SharedPreferencesUtil.getAccessCache(context, false);
		if (!isconnect || MyApplication.ACCESSUSER_INITED) {
			return;
		}
		DataCenter.getInstance().loadBootADData(context, null);
	}

}
