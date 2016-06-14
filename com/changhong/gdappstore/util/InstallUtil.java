package com.changhong.gdappstore.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class InstallUtil {

	public static void installApp(Context context, String filePath) {
		// 安装最新的apk文件
		if (TextUtils.isEmpty(filePath)) {
			return;
		}
		installApp(context, new File(filePath));
	}

	public static void installApp(Context context, File file) {
		// 安装最新的apk文件
		if (file == null || !file.exists()) {
			return;
		}
		// 安装新的APK， 下载后用户直接安装
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载应用
	 * 
	 * @param context
	 * @param packageName
	 *            应用包名
	 */
	public static void unInstallApp(Context context, String packageName) {
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}

	/**
	 * 静默安装
	 * 
	 * @param path
	 *            应用路径
	 * @return success为0,fail 为2
	 */
	public static boolean installAppByCommond(String filePath) {
		String[] args = { "pm", "install", "-r", filePath };
		return exeCommond(args)==0;
	}

	/**
	 * 静默卸载应用
	 * 
	 * @param packageName
	 *            包名
	 * @return 
	 */
	public static boolean uninstallAppByCommond(String packageName) {
		String[] args = { "pm", "uninstall", packageName };
		return exeCommond(args)==0;
	}

	private static int exeCommond(String[] args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int result;
		try {
			process = processBuilder.start();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;

			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}

			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = 2;
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		} finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}

		// TODO should add memory is not enough here
		if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
			result = 0;
		} else {
			result = 2;
		}
		Log.d("installSlient", "successMsg:" + successMsg + " result==" + result + ", ErrorMsg:" + errorMsg);
		return result;
	}
}
