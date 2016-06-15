package com.changhong.gdappstore.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.model.NativeApp;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.util.InstallUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 静默安装和静默卸载service
 * 
 * @author wangxiufeng
 * 
 */
public class SilentInstallService extends Service {
	private final String TAG = "SilentInstallService";

	int silentInstallPos = -1;
	HttpUtils http = new HttpUtils(Config.CONNECTION_TIMEOUT);
	/** 静默安装和卸载列表 */
	List<List<Object>> lists = new ArrayList<List<Object>>();
	/** 静默安装列表 */
	List<Object> installApps = new ArrayList<Object>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		L.d(TAG + " oncreate");
		new Thread(new Runnable() {

			@Override
			public void run() {
				loadSilentInstallAppList();
			}
		}).start();

	}

	/** 下载回调函数 */
	private RequestCallBack<File> requestCallBack = new RequestCallBack<File>() {

		@Override
		public void onStart() {
			L.d(TAG + " onstart load");
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			L.d(TAG + " onloading " + current + "  " + total);
		}

		@Override
		public void onSuccess(final ResponseInfo<File> responseInfo) {
			L.d(TAG + "---responseinfo  " + responseInfo.result.getPath());
			Util.chrome0777File(Config.baseXutilDownPath);
			Util.chrome0777File(responseInfo.result.getPath());
			App app = (App) installApps.get(silentInstallPos);
			DataCenter.getInstance().submitAppDownloadOK(app.getAppid() + "", getApplicationContext());
			new Thread(new Runnable() {

				@Override
				public void run() {// 静默安装
					boolean success = InstallUtil.installAppByCommond(responseInfo.result.getPath());
					Util.deleteFile(responseInfo.result.getPath());
					L.d(TAG + " installsuccess is  " + success);
				}
			}).start();
			downloadApp();// 继续下一个
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			L.d(TAG + "---load onFailure  " + msg);
			downloadApp();// 失败一个也要继续下载后面的
		}
	};

	/**
	 * 请求静默安装和静默卸载数据
	 */
	private void loadSilentInstallAppList() {
		Util.deleteFileChildrens(Config.baseXutilDownPath);
		silentInstallPos = -1;
		DataCenter.getInstance().loadSilentInstallData(getApplicationContext(), new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				lists = (List<List<Object>>) object;
				doUninstall(lists.get(1));// 执行静默卸载
				installApps = lists.get(0);// 静默安装列表
				Util.clearListNullItem(installApps);// 去除list里面空值
				checkNativeApps();// 比对本地安装应用
				if (installApps.size() > 0) {
					downloadApp();// 下载第0个开始
				} else {
					L.d(TAG + " installApps size = 0");
					stopSelf();
					return;
				}
			}
		});

	}

	/**
	 * 静默卸载操作
	 * 
	 * @param uninstallApps
	 */
	private void doUninstall(List<Object> uninstallApps) {
		for (int j = 0; j < uninstallApps.size(); j++) {
			if (uninstallApps.get(j) != null) {// 卸载
				InstallUtil.uninstallAppByCommond(((App) uninstallApps.get(j)).getPackageName());
			}
		}
	}

	/**
	 * 对比本地应用和静默安装应用，如果已安装并且是最新版本则不安装
	 */
	private void checkNativeApps() {
		List<NativeApp> nativeapps = Util.getApp(getApplicationContext());
		Map<String, Integer> natPackageVersion=new HashMap<String, Integer>();
		if (nativeapps == null || nativeapps.size() <= 0 || installApps.size() <= 0) {
			return;
		}
		
		//封装本地APP应用的包名和版本号
		for(int n=0;n<nativeapps.size();n++){
			NativeApp nApp=nativeapps.get(n);
			natPackageVersion.put(nApp.appPackage, nApp.nativeVersionInt);
		}
		
		for (int j = 0; j < installApps.size(); j++) {
			App installApp = (App) installApps.get(j);
			
			if(installApp.getAppMustType().equals("INSTALL")){
				//强制安装流程
				for (int i = 0; i < nativeapps.size(); i++) {
					NativeApp nativeApp = (NativeApp) nativeapps.get(i);
					if (nativeApp.appPackage.equals(installApp.getPackageName())) {
						// 本地安装有该应用并且本地版本号不低于服务端版本号
						L.d(TAG + "installed " + nativeApp.appname + " package:" + nativeApp.appPackage + " nativeversion="
								+ nativeApp.nativeVersionInt + " serverVersion=" + installApp.getVersionInt());
						
							if (nativeApp.nativeVersionInt >= installApp.getVersionInt()) {
								installApps.remove(j);
								j--;
								break;
							}
					}
				}
			}else if(installApp.getAppMustType().equals("UPDATE")){
				//强制更新流程
				if(!natPackageVersion.containsKey(installApp.getPackageName())){
					installApps.remove(j);
					j--;
				}else if(natPackageVersion.get(installApp.getPackageName()) >= installApp.getVersionInt()){
					installApps.remove(j);
					j--;
				}
			}
		}
	}

	/**
	 * 下载静默安装app
	 */
	private void downloadApp() {
		silentInstallPos++;
		if (silentInstallPos >= installApps.size()) {
			stopSelf();
			return;
		}
		App app = (App) installApps.get(silentInstallPos);
		String apkLoadUrl = app.getPosterFilePath();// TODO
													// 临时使用海报图片路径变量代替apk下载路径变量
		String apkname = apkLoadUrl.substring(apkLoadUrl.lastIndexOf("/") + 1, apkLoadUrl.length()).trim();
		L.d(TAG + " downloadapp pos=" + silentInstallPos + " " + app.toString());
		DownLoadManager.putFileDownLoad(apkLoadUrl, app.getPackageName(), Config.baseXutilDownPath + "/" + apkname, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				requestCallBack);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		L.d(TAG + " service on destroy");
		super.onDestroy();
	}

}
