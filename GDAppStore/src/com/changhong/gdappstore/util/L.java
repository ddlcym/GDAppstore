package com.changhong.gdappstore.util;

import android.util.Log;

/**
 * log util
 * 
 * just set isLog=false if you want to close all the log printed by this util
 * 
 * @author wangxiufeng
 */
public class L {

	private static final boolean isLog = true;
	private static final String tag = "com.changhong.gdappstore";

	public static void d(String msg) {
		if (isLog) {
			Log.d(tag, msg);
		}
	}

	public static void i(String msg) {
		if (isLog) {
			Log.i(tag, msg);
		}
	}

	public static void e(String msg) {
		if (isLog) {
			Log.e(tag, msg);
		}
	}

	public static void v(String msg) {
		if (isLog) {
			Log.v(tag, msg);
		}
	}

	public static void w(String msg) {
		if (isLog) {
			Log.w(tag, msg);
		}
	}
}
