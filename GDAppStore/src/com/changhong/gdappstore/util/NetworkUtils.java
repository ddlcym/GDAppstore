package com.changhong.gdappstore.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 网络工具类
 * 
 * @author wangxiufeng
 * 
 */
public class NetworkUtils {
	/** 网络是否链接 */
	public static boolean ISNET_CONNECT = true;

	public static boolean isConnectInternet(final Context pContext) {
		final ConnectivityManager conManager = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			ISNET_CONNECT = networkInfo.isAvailable();
			return ISNET_CONNECT;
		}
		ISNET_CONNECT = false;
		return ISNET_CONNECT;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	public static String getIpAddress(Context context) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress.getHostAddress().toString() + "";
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("getIpAddress IpAddress", ex.toString());
		}
		return "";
	}

}
