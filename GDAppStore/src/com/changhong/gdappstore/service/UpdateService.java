package com.changhong.gdappstore.service;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.AppDetail;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.InstallUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.view.MyProgressDialog;

public class UpdateService {

	/** 系统上下文 */
	private Context context;

	/** ACTIVITY传过来的消息处理 */
	private Handler handler;

	/** handler需要处理的MESSAGE的编号 */
	private final int MESSAGE_SERVER_FILEERROR = 11;
	private final int MESSAGE_DOWNLOADOVER = 12;
	private final int MESSAGE_DOWNEXCEPTION = 13;
	private final int MESSAGE_NETNOTCONNECT = 14;

	/** ACITIVY传过来更新进度条 */
	private MyProgressDialog progressDialog;

	public static boolean downloading = false;
	public static boolean THREAD_ONE_FINISHED = true;
	public static boolean THREAD_TWO_FINISHED = true;
	public static boolean THREAD_DOWNLOAD_EXCEPTION = false;
	/** 下载apk存放文件夹 */
	private File updateFile;
	private AppDetail appDetail;
	/** 是否是下载应用，true下载，false更新 */
	private boolean isdownload = false;

	public UpdateService(Context context, Handler handler, MyProgressDialog progressDialog) {
		this.context = context;
		this.handler = handler;
		this.progressDialog = progressDialog;
	}

	/**
	 * 更新或下载应用
	 * 
	 * @param appDetail
	 * @param isdownload
	 *            true表示下载应用，用于下载统计
	 */
	public void update(AppDetail appDetail, boolean isdownload) {
		if (appDetail == null || TextUtils.isEmpty(appDetail.getApkFilePath())) {
			return;
		}
		if (downloading) {
			Toast.makeText(context, context.getString(R.string.hasapp_isloading), Toast.LENGTH_SHORT).show();
			return;
		}
//		AppBroadcastReceiver.curAppDetail = appDetail;//TODO 取消数据库保存应用信息
		if (handler == null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case MESSAGE_SERVER_FILEERROR:
						DialogUtil.showLongToast(context, context.getString(R.string.error_netconnect_please_checknet));
						break;
					case MESSAGE_DOWNLOADOVER:
						DialogUtil.showLongToast(context, context.getString(R.string.downloadover));
						break;
					case MESSAGE_DOWNEXCEPTION:
						DialogUtil.showLongToast(context, context.getString(R.string.downloadfailed));
						break;
					case MESSAGE_NETNOTCONNECT:
						DialogUtil.showLongToast(context, context.getString(R.string.net_disconnect));
						break;

					default:
						break;
					}
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					downloading = false;
				}
			};
		}

		/**
		 * 检查上次下载是否有异常
		 */
		// UpdateLogService preferenceService = new UpdateLogService(context);
		// boolean downingException =
		// preferenceService.isDownloadingException();
		this.appDetail = appDetail;
		String apkname = appDetail.getApkFilePath().substring(appDetail.getApkFilePath().lastIndexOf("/"),
				appDetail.getApkFilePath().length()).trim();
		File baseFile = new File(Config.baseUpdatePath);
		if (baseFile == null || !baseFile.exists() || !baseFile.isDirectory()) {
			baseFile.mkdirs();
		}
		updateFile = new File(Config.baseUpdatePath + apkname);
		/**
		 * 根据下载的文件和上次的异常来判断走升级那个流程
		 */
		if (updateFile.exists()) {
			// 本地APK已存在流程 该方法不适合此环境，因为版本是采用该服务器手动配置本地数据库保存方式，无法知道该apk版本，
			// fileExistFlow();
			// installApp();//可以直接安装是因为
			// 版本不同apk名字不同，在这里apk名字是服务器的名字，所以是最新的apk，所以可以直接安装
		} else {
			
		}
		Util.deleteFileChildrens(Config.baseUpdatePath);
		/**
		 * 本地文件不存在，从服务器获得更新流程
		 */
		fileNotExistFlow();
	}

	private void fileExistFlow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 先比较本地下载APK和服务端最新的版本
					PackageManager pm = context.getPackageManager();
					PackageInfo filePMInfo = pm.getPackageArchiveInfo(updateFile.getAbsolutePath().toString(),
							PackageManager.GET_ACTIVITIES);
					final String newVersion = appDetail.getVersion();// versonname，不是versioncode
					if (!TextUtils.isEmpty(newVersion)) {
						// 先比较本地程序和服务器的版本
						if (filePMInfo != null) {
							// int installVersionCode = filePMInfo.versionCode;
							try {
								float installVersionName = Float.parseFloat(filePMInfo.versionName);
								float newVersionName = Float.parseFloat(newVersion);
								L.d("fileExistFlow--fileversion=" + filePMInfo.versionName + " " + newVersionName);
								if (newVersionName > installVersionName) {
									// 有更新 弹框提示下载更新
									updateFile.delete();
									fileNotExistFlow();
									return;
								}
							} catch (Exception e) {
								updateFile.delete();
								fileNotExistFlow();
								e.printStackTrace();
								return;
							}
						} else {
							L.d("fileExistFlow--filePMInfo is null");
							// 文件包存在，但是又得不到信息，证明下载的文件又问题，重新下载
							updateFile.delete();
							fileNotExistFlow();
							return;
						}
					}
					// 弹框提示安装
					installApp();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void fileNotExistFlow() {
		// 本地版本小于等于服务器版本,有更新 弹框提示下载更新
		handler2.sendEmptyMessage(100);
	}

	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			downloadApp();
		};
	};

	private void downloadApp() {
		//TODO 每次下载前都删除APK，每次都是新下载，去掉断点记录
		UpdateLogService preferenceService=new UpdateLogService(context);
		preferenceService.saveThreadDownloadDataSize(1, 0);
		preferenceService.saveThreadDownloadDataSize(2, 0);
		if (progressDialog != null) {
			progressDialog.setProgress(0);
			progressDialog.show();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!NetworkUtils.isConnectInternet(context)) {
					handler.sendMessage(handler.obtainMessage(MESSAGE_NETNOTCONNECT));
					downloading = false;
					return;
				}
				try {
					downloading = true;

					// 先获得文件的大小
					int fileTotalSize = 0;
					HttpURLConnection connection = null;
					try {
						URL url = new URL(appDetail.getApkFilePath());
						connection = (HttpURLConnection) url.openConnection();
						connection.setUseCaches(false);
						connection.setConnectTimeout(Config.CONNECTION_TIMEOUT);
						connection.setReadTimeout(Config.CONNECTION_TIMEOUT);
						connection.setRequestMethod("GET");
						if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							connection.connect();
							fileTotalSize = connection.getContentLength();
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.setMax(fileTotalSize);
							}
						}
					} catch (Exception e) {
						downloading = false;
						e.printStackTrace();
					} finally {
						try {
							if (connection != null) {
								connection.disconnect();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// 获得文件的大小为0，直接返回, 并通知用户
					if (fileTotalSize <= 0) {
						handler.sendMessage(handler.obtainMessage(MESSAGE_SERVER_FILEERROR));
						downloading = false;
						return;
					}

					// 计算两个线程分别要下载的文件大小
					long firstThreadStart = 0;
					long firstThreadEnd = fileTotalSize / 2;
					long secondThreadStart = fileTotalSize / 2 + 1;
					long secondThreadEnd = fileTotalSize;

					// 第一个线程下载
					THREAD_ONE_FINISHED = false;
					int DOWNLOAD_THREAD_ONE = 1;
					UpdateFileDownloadThread firstThread = new UpdateFileDownloadThread(context, DOWNLOAD_THREAD_ONE,
							firstThreadStart, firstThreadEnd, appDetail.getApkFilePath(), updateFile);
					firstThread.start();

					// 第二个线程下载
					THREAD_TWO_FINISHED = false;
					int DOWNLOAD_THREAD_TWO = 2;
					UpdateFileDownloadThread secondThread = new UpdateFileDownloadThread(context, DOWNLOAD_THREAD_TWO,
							secondThreadStart, secondThreadEnd, appDetail.getApkFilePath(), updateFile);
					secondThread.start();

					// 不停的更新下载的状态
					UpdateLogService preferenceService = new UpdateLogService(context);
					while (!THREAD_ONE_FINISHED || !THREAD_TWO_FINISHED) {
						Thread.sleep(100);

						/**
						 * 检查是否有异常，如果有异常，向外抛出异常， 用handler通知用户
						 */
						if (THREAD_DOWNLOAD_EXCEPTION) {
							THREAD_DOWNLOAD_EXCEPTION = false;
							throw new RuntimeException("thread download fail");
						}

						/**
						 * 计算现在更新的进度
						 */
						int alreadyRead = (int) preferenceService.getTotalDownlaodDataSize();
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.setProgress(alreadyRead);
						}
					}

					// 下载完成，重置下载的进度
					preferenceService.saveThreadDownloadDataSize(DOWNLOAD_THREAD_ONE, 0);
					preferenceService.saveThreadDownloadDataSize(DOWNLOAD_THREAD_TWO, 0);
					// 提交下载统计
					if (appDetail.getAppid()>0) {
						DataCenter.getInstance().submitAppDownloadOK(appDetail.getAppid() + "",context);
//						DetailActivity.detailLoadCount++;
					}
					// 下载完成 安装
					installApp();

					// 现在完成，设置DONWLOADING标志
					downloading = false;
					handler.sendMessage(handler.obtainMessage(MESSAGE_DOWNLOADOVER));
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.setProgress(0);
					}

				} catch (Exception e) {
					// 异常捕获
					L.d("download exception---");
					e.printStackTrace();
					downloading = false;
					handler.sendMessage(handler.obtainMessage(MESSAGE_DOWNEXCEPTION));
				}
			}
		}).start();
	}

	private void installApp() {
		// 安装最新的apk文件
		if (!updateFile.exists()) {
			return;
		}
		Util.chrome0777File(Config.baseUpdatePath);
		Util.chrome0777File(updateFile.getAbsolutePath().toString());
		InstallUtil.installApp(context, updateFile);
	}

}
