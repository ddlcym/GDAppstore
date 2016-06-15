package com.changhong.gdappstore.base;

import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.AppDetail;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.service.NetChangeReceiver;
import com.changhong.gdappstore.service.SilentInstallService;
import com.changhong.gdappstore.service.UpdateService;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.view.MyProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 基类activity
 * 
 * @author wangxiufeng
 * 
 */
public class BaseActivity extends AppCompatActivity {

	protected Context context;
	/** 频幕宽高 */
	protected int screenWidth, screenHeight;
	/** View.VISIBLE */
	protected static final int VISIBLE = View.VISIBLE;
	/** View.INVISIBLE */
	protected static final int INVISIBLE = View.INVISIBLE;
	/** View.GONE */
	protected static final int GONE = View.GONE;

	/** 静默安装Server是否已经启动 */
	private static boolean isSilentInstallServiceStart = false;

	/** 更新对话框 */
	private Dialog updateDialog = null;

	/** 更新提示对话框是否已经显示 */
	private static boolean isShowedUpdateDialog = false;

	/** 下载进度条 */
	private MyProgressDialog progressDialog;

	protected ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		init();
	}

	private void init() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;// 获取分辨率宽度
		screenHeight = dm.heightPixels;
		progressDialog = new MyProgressDialog(context);
		progressDialog.setUpdateFileSizeName(true);
		progressDialog.dismiss();
	}

	/**
	 * instead of findViewById(id)，减少每次都要去强制转换的麻烦
	 * 
	 * @param id
	 * @return
	 */
	protected <T> T findView(int id) {
		return (T) findViewById(id);
	}

	protected void showLoadingDialog() {
		if (loadingDialog == null) {
			loadingDialog = DialogUtil.showCirculProDialog(context, context.getString(R.string.tishi),
					context.getString(R.string.dataloading), false);
		}
		loadingDialog.show();
	}

	protected void dismissLoadingDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	/**
	 * 静默安装
	 */
	protected void startSilentInstallService() {
		// 启动静默安装
		if (!isSilentInstallServiceStart) {
			startService(new Intent(BaseActivity.this, SilentInstallService.class));
			isSilentInstallServiceStart = true;
		}
	}

	/**
	 * 检测更新
	 */
	protected void checkUpdate() {
		if (!MyApplication.UPDATE_ENABLE) {
			return;
		}
		try {
			int nativeVersion = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
			L.d("mainactivity readUpdate navVersion=" + nativeVersion + " serverVer " + MyApplication.SERVER_VERSION);
			if (NetworkUtils.isConnectInternet(context) && nativeVersion < MyApplication.SERVER_VERSION
					&& !TextUtils.isEmpty(MyApplication.UPDATE_APKURL) && !isShowedUpdateDialog) {
				if (updateDialog == null || (updateDialog != null && !updateDialog.isShowing())) {
					isShowedUpdateDialog = true;
					updateDialog = DialogUtil.showMyAlertDialog(context, "",
							context.getString(R.string.checked_newversion), context.getString(R.string.update_now),
							context.getString(R.string.update_nexttime), new DialogUtil.DialogBtnOnClickListener() {

								@Override
								public void onSubmit(DialogUtil.DialogMessage dialogMessage) {

									UpdateService updateService = new UpdateService(context, null, progressDialog);
									AppDetail appDetail = new AppDetail();
									appDetail.setApkFilePath(MyApplication.UPDATE_APKURL);
									updateService.update(appDetail, false);
									if (dialogMessage != null && dialogMessage.dialogInterface != null) {
										dialogMessage.dialogInterface.dismiss();
									}
								}

								@Override
								public void onCancel(DialogUtil.DialogMessage dialogMessage) {
									if (dialogMessage != null && dialogMessage.dialogInterface != null) {
										dialogMessage.dialogInterface.dismiss();
									}
								}
							});
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
