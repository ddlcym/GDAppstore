package com.changhong.gdappstore.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 安装包文件管理类 记录正在下载url和文件目录列表。
 * 在AppBrodcastReceiver里面监听，删除安装后的apk文件。如果下载列表为空，删除所有的安装包apk文件
 * 
 * @author wangxiufeng
 * 
 */
public class DownLoadManager {
	private static final String TAG = "DownLoadManager";

	private static HttpUtils http = new HttpUtils(Config.CONNECTION_TIMEOUT);
	/**
	 * 报名-下载文件地址 键值对。用于安装完成后删除对应的文件
	 */
	public static Map<String, String> map_filepath = new HashMap<String, String>();
	/**
	 * 正在下载的文件列表，用于清理安装包时候判断是否还有文件正在下载中。
	 */
	public static List<String> list_loadingUrl = new LinkedList<String>();

	/**
	 * 
	 * @param url
	 *            下载地址
	 * @param packageName
	 *            应用包名
	 * @param target
	 *            下载文件存放路径（包含文件名字及后缀名）
	 * @param autoResume
	 *            // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
	 * @param autoRename
	 *            // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
	 * @param callback
	 *            回调函数
	 */
	public static void putFileDownLoad(final String url, final String packageName, String target, boolean autoResume,
			boolean autoRename, final RequestCallBack<File> callback) {
		if (autoResume) {
			Util.deleteFile(target);// 不断电续下载就手动删除文件
		}
		http.download(url, target, autoResume, autoRename, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				L.d(TAG + " onstart load " + getRequestUrl());
				// 添加进正在下载列表
				list_loadingUrl.add(url);
				if (callback != null) {
					callback.onStart();
				}
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				L.d(TAG + " onloading " + current + "  " + total + " ");
				if (callback != null) {
					callback.onLoading(total, current, isUploading);
				}
				super.onLoading(total, current, isUploading);
			}

			@Override
			public void onSuccess(final ResponseInfo<File> responseInfo) {
				L.d(TAG + "---responseinfo  " + responseInfo.result.getPath());
				if (!TextUtils.isEmpty(packageName)) {
					map_filepath.put(packageName, responseInfo.result.getPath());
				}
				list_loadingUrl.remove(url);

				if (callback != null) {
					callback.onSuccess(responseInfo);
				}
			}

			@Override
			public void onFailure(HttpException httpException, String msg) {
				L.d(TAG + "---load onFailure  code=" + httpException.getExceptionCode() + " "
						+ httpException.getMessage());
				L.d(TAG + "---load onFailure  msg=" + httpException);
				L.d(TAG + "---load onFailure  msg=" + msg);

				list_loadingUrl.remove(url);

				if (callback != null) {
					callback.onFailure(httpException, msg);
				}
			}
		});
	}
}
