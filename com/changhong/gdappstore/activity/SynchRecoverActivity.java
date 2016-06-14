package com.changhong.gdappstore.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.adapter.SynchGridAdapter;
import com.changhong.gdappstore.base.BaseActivity;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.NativeApp;
import com.changhong.gdappstore.model.SynchApp;
import com.changhong.gdappstore.model.SynchApp.Type;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.service.AppBroadcastReceiver;
import com.changhong.gdappstore.service.CacheManager;
import com.changhong.gdappstore.service.AppBroadcastReceiver.AppChangeListener;
import com.changhong.gdappstore.service.DownLoadManager;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.InstallUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.view.MyProgressDialog;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SynchRecoverActivity extends BaseActivity implements OnClickListener, OnKeyListener {

	private static final String TAG = "SynchRecoverActivity";
	private static final int UPDATE_DIALOG_TITLE = 110;
	private static final int SHOW_INSTALL_SUCCESS = 111;
	private static final int SHOW_INSTALL_FAILED = 112;
	private static final int DODOWNLOAD = 113;
	private String DOBATCH;
	private String CONFIRM_RECOVER;
	private GridView gridView;
	private SynchGridAdapter adapter;
	private Button bt_batch;
	private ImageView iv_shandow_item1, iv_shandow_item2, iv_shandow_item3, iv_batch_icon;
	private TextView tv_batch_suggest, tv_num_checked, tv_ge;
	private TextView tv_pagename;
	/** 批量操作时候选择的item个数 */
	private int curCheckedItem = 0;
	/** 下载apk进度对话框 */
	private MyProgressDialog downloadPDialog = null;
	/** apk 下载列表 */
	private List<SynchApp> downloadApps = new ArrayList<SynchApp>();
	private int curDownLoadPos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synch_backup);
		showLoadingDialog();
		initView();
		initData();
	}

	private void initView() {
		DOBATCH = context.getString(R.string.batch_recovery);
		CONFIRM_RECOVER = context.getString(R.string.confirm_recover);

		gridView = findView(R.id.gridview);
		iv_shandow_item1 = findView(R.id.iv_shandow_item1);
		iv_shandow_item2 = findView(R.id.iv_shandow_item2);
		iv_shandow_item3 = findView(R.id.iv_shandow_item3);

		adapter = new SynchGridAdapter(context, null);
		gridView.setOnItemSelectedListener(itemSelectedListener);
		gridView.setOnItemClickListener(onItemClickListener);
		gridView.setOnKeyListener(this);
		gridView.setAdapter(adapter);

		bt_batch = findView(R.id.bt_batch);
		bt_batch.setOnKeyListener(this);
		bt_batch.setOnClickListener(this);
		bt_batch.requestFocus();
		bt_batch.setText(DOBATCH);

		tv_batch_suggest = findView(R.id.tv_batch_suggest);
		tv_batch_suggest.setText(context.getString(R.string.dobatchbyclickmenu));

		tv_pagename = findView(R.id.tv_pagename);
		tv_pagename.setText(context.getString(R.string.recovery));

		tv_num_checked = findView(R.id.tv_num_checked);
		iv_batch_icon = findView(R.id.iv_batch_icon);
		tv_ge = findView(R.id.tv_ge);

		downloadPDialog = new MyProgressDialog(context);
		downloadPDialog.setUpdateFileSizeName(true);
		downloadPDialog.dismiss();

		AppBroadcastReceiver.listeners.put(context.getClass().getName(), appChangeListener);
	}

	private void initData() {
		adapter.setBatch(false);
		DataCenter.getInstance().loadBackUpApps(context, CacheManager.useCacheBackupedApps, new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				List<SynchApp> items = (List<SynchApp>) object;
				List<SynchApp> itemsBySort = new ArrayList<SynchApp>();
				if (items.size() > 6) {
					iv_shandow_item1.setVisibility(VISIBLE);
				}
				if (items.size() > 7) {
					iv_shandow_item2.setVisibility(VISIBLE);
				}
				if (items.size() > 8) {
					iv_shandow_item3.setVisibility(VISIBLE);
				}
				if (items != null) {
					List<NativeApp> nativeApps = Util.getApp(context);
					for (int i = 0; i < items.size(); i++) {
						SynchApp synchApp = items.get(i);
						for (int j = 0; j < nativeApps.size(); j++) {
							NativeApp nativeApp = (NativeApp) nativeApps.get(j);
							if (synchApp.getPackageName().equals(nativeApp.appPackage)) {
								synchApp.setAppIcon(nativeApp.appIcon);
								synchApp.setSynchType(Type.RECOVERED);
							}
						}
						if (synchApp.getSynchType() == Type.RECOVERED) {
							itemsBySort.add(synchApp);
						} else {
							itemsBySort.add(0, synchApp);
						}
					}
				}
				adapter.updateList(itemsBySort);
				dismissLoadingDialog();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_batch:
			// 批量备份
			doBatchOnClick();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU) {
			doBatchOnClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 批量操作按钮点击事件响应
	 */
	private void doBatchOnClick() {
		if (adapter.getCount() == 0) {
			DialogUtil.showShortToast(context, context.getString(R.string.noapps_cando));
			return;
		}
		if (bt_batch.getText().toString().equals(CONFIRM_RECOVER)) {
			// 已经是批量操作了，执行批量提交
			List<SynchApp> apps = new ArrayList<SynchApp>();
			for (int i = 0; i < adapter.getCount(); i++) {
				SynchApp app = (SynchApp) adapter.getItem(i);
				if (app.isChecked()) {
					if (!isInDownLoadingList(app.getAppid())) {
						apps.add(app);
					}
					app.setChecked(false);
				}
			}
			downloadApps(apps);
		} else {
			// 从正常操作转向批量操作
			bt_batch.setText(CONFIRM_RECOVER);
			tv_batch_suggest.setText(context.getString(R.string.selected));
			iv_batch_icon.setVisibility(INVISIBLE);
			refreshCheckedItemCount();
			refreshCheckedItemText();
			adapter.setBatch(true);
		}
	}

	/**
	 * 批量操作时候，刷新选中的个数
	 */
	private void refreshCheckedItemText() {
		tv_num_checked.setVisibility(VISIBLE);
		tv_ge.setVisibility(VISIBLE);
		tv_num_checked.setText(curCheckedItem + "");
	}

	/**
	 * 刷新批量操作时候的选择个数
	 */
	private void refreshCheckedItemCount() {
		curCheckedItem = 0;
		List<SynchApp> items = adapter.getItems();
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).isChecked()) {
					curCheckedItem++;
				}
			}
		}
		if (tv_num_checked.getVisibility() == VISIBLE) {
			refreshCheckedItemText();
		}
	}

	/**
	 * 下载应用
	 * 
	 * @param downloadApps
	 */
	private void downloadApps(List<SynchApp> downloadApps) {
		// if (adapter.isBatch()) {
		// // 批量提交
		bt_batch.setText(DOBATCH);
		tv_batch_suggest.setText(context.getString(R.string.dobatchbyclickmenu));
		iv_batch_icon.setVisibility(VISIBLE);
		tv_num_checked.setVisibility(INVISIBLE);
		tv_ge.setVisibility(INVISIBLE);
		adapter.setBatch(false);
		// // adapter.updateList(items, false);
		// } else {
		// // 普通提交
		// // adapter.updateList(items);
		// }
		if (downloadApps == null || downloadApps.size() <= 0) {
			return;
		}
		// this.downloadApps = downloadApps;
		this.downloadApps.addAll(downloadApps);
		doDownLoad();
	}

	private void doDownLoad() {
		if (downloadApps == null || (curDownLoadPos + 1) >= downloadApps.size() || curDownLoadPos < -1) {
			L.d("doDownLoad over " + curDownLoadPos);
			if (curDownLoadPos <= 0) {
				curDownLoadPos = -1;
			}
			if (downloadPDialog != null && downloadPDialog.isShowing()) {
				downloadPDialog.dismiss();
			}
			return;
		}
		if (!NetworkUtils.ISNET_CONNECT) {
			DialogUtil.showLongToast(context, context.getString(R.string.error_netconnect_please_checknet));
			downloadPDialog.dismiss();
			return;
		}
		curDownLoadPos++;
		final SynchApp app = downloadApps.get(curDownLoadPos);
		L.d("doDownLoad curDownLoadPos=" + curDownLoadPos + " downloadappsize " + downloadApps.size() + " app is "
				+ app.toString());
		if (curDownLoadPos == 0 && !downloadPDialog.isShowing()
				&& Util.getTopActivity(context).equals(context.getClass().getName())) {
			if (!((Activity) context).isFinishing()) {
				downloadPDialog.show();
				L.d("doDownLoad show downloadPDialog over ");
			} else {
				// 如果在这里显示对话框的话，在这个情况下回崩溃：选择一个，然后再批量选几个，返回退出页面，再进入时候。
				L.d("doDownLoad not show context isFinishing");
			}
		}
		downloadPDialog.setProgress(0);
		downloadPDialog.setMax(0);
		downloadPDialog.setMyTitle(context.getString(R.string.downloading) + "：" + app.getAppname());

		String apkLoadUrl = app.getApkFilePath();
		final String apkname = apkLoadUrl.substring(apkLoadUrl.lastIndexOf("/") + 1, apkLoadUrl.length()).trim();
		DownLoadManager.putFileDownLoad(apkLoadUrl, app.getPackageName(), Config.baseXutilDownPath + "/" + apkname,
				false, true, new RequestCallBack<File>() {
					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						downloadPDialog.setMax((int) total);
						downloadPDialog.setProgress((int) current);
						L.d("download recover app onloading total is " + total + " current " + current);
						super.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(final ResponseInfo<File> responseInfo) {
						Util.chrome0777File(Config.baseXutilDownPath);
						Util.chrome0777File(responseInfo.result.getPath());
						new Thread(new Runnable() {

							@Override
							public void run() {// 安装
								if (Config.ISNORMAL_INSTALL) {
									InstallUtil.installApp(context, responseInfo.result.getPath());
									removeDownLoadingApp(app.getAppid());
									handler.sendEmptyMessage(DODOWNLOAD);// 下载下一个
								} else {
									Message installingMsg = handler.obtainMessage(UPDATE_DIALOG_TITLE);
									installingMsg.obj = context.getString(R.string.downloadover_installing);
									handler.sendMessage(installingMsg);
									boolean success = InstallUtil.installAppByCommond(responseInfo.result.getPath());
									L.d("install success " + success);
									if (success) {
										Message msg = handler.obtainMessage(SHOW_INSTALL_SUCCESS);
										msg.obj = app.getAppname() + "";
										handler.sendMessage(msg);
									} else {
										Message msg = handler.obtainMessage(SHOW_INSTALL_FAILED);
										msg.obj = app.getAppname() + "";
										handler.sendMessage(msg);
									}
									removeDownLoadingApp(app.getAppid());
									handler.sendEmptyMessage(DODOWNLOAD);// 下载下一个
								}
							}
						}).start();
					}

					@Override
					public void onFailure(HttpException paramHttpException, String msg) {
						String downloadFailed = context.getString(R.string.downloadfailed);
						if (!NetworkUtils.ISNET_CONNECT) {
							DialogUtil.showLongToast(context, context.getString(R.string.error_net_notconnect));
						} else if (msg.contains("ConnectTimeoutException")) {
							DialogUtil.showLongToast(context, context.getString(R.string.error_netconnect_timeout));
						} else {
							DialogUtil.showLongToast(context, context.getString(R.string.error_netconnect));
						}

						removeDownLoadingApp(app.getAppid());
						handler.sendEmptyMessage(DODOWNLOAD);// 下载下一个
					}
				});
	}

	/**
	 * 从选择列表中删除该应用。
	 * 
	 * @param id
	 */
	private void removeDownLoadingApp(int appid) {
		if (downloadApps == null || downloadApps.size() == 0) {
			return;
		}
		for (int i = 0; i < downloadApps.size(); i++) {
			if (downloadApps.get(i).getAppid() == appid) {
				downloadApps.remove(i);
				curDownLoadPos--;
				i--;
			}
		}
	}

	/**
	 * 是否在下载列表中
	 * 
	 * @param appid
	 * @return
	 */
	private boolean isInDownLoadingList(int appid) {
		if (downloadApps == null || downloadApps.size() == 0) {
			return false;
		}
		for (int i = 0; i < downloadApps.size(); i++) {
			if (downloadApps.get(i).getAppid() == appid) {
				return true;
			}
		}
		return false;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DODOWNLOAD:
				doDownLoad();// 下载下一个
				break;
			case SHOW_INSTALL_SUCCESS:
				DialogUtil.showLongToast(context, (String) msg.obj + " " + context.getString(R.string.install_success));
				break;
			case SHOW_INSTALL_FAILED:
				DialogUtil.showLongToast(context, (String) msg.obj + " " + context.getString(R.string.install_failed));
				break;
			case UPDATE_DIALOG_TITLE:
				L.d("install settitle " + (String) msg.obj);
				downloadPDialog.setMyTitle((String) msg.obj);
				break;

			default:
				break;
			}
		}
	};

	AppChangeListener appChangeListener = new AppChangeListener() {

		@Override
		public void onAppChange(Intent intent) {
			//
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
				String packageName = intent.getDataString();
				if (packageName != null && packageName.startsWith("package:")) {
					packageName = packageName.substring(packageName.indexOf(":") + 1, packageName.length());
					boolean hasRecovered = false;
					for (int i = 0; i < adapter.getCount(); i++) {
						SynchApp app = (SynchApp) adapter.getItem(i);
						if (app.getPackageName().equals(packageName)) {
							app.setSynchType(Type.RECOVERED);
							hasRecovered = true;
						}
					}
					if (hasRecovered) {
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	};

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.bt_batch:
			// 批量操作按钮按又选中上次选中位置
			if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				gridView.requestFocus();// 选中上次选中的位置
				return true;
			}
			break;
		case R.id.gridview:
			if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				// 处理最后一排不能按下，不然焦点会跑到批量操作上面去
				int itemCount = adapter.getCount();
				int curSelectPos = gridView.getSelectedItemPosition();
				if (itemCount > 6) {
					int lastRowCount = itemCount % 3;
					if (curSelectPos >= (itemCount - lastRowCount)) {
						return true;
					}
				}
			}
			break;

		default:
			break;
		}
		return false;
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SynchApp app = (SynchApp) adapter.getItem(position);
			if (adapter.isBatch() && app.getSynchType() != Type.RECOVERED) {
				// 批量操作

				if (app != null) {
					curCheckedItem = app.isChecked() ? curCheckedItem - 1 : curCheckedItem + 1;
					refreshCheckedItemText();
					app.setChecked(!app.isChecked());
					adapter.notifyDataSetChanged();
				}
			} else if (app.getSynchType() != Type.RECOVERED) {
				// 普通操作
				if (isInDownLoadingList(app.getAppid())) {
					DialogUtil.showShortToast(context, context.getString(R.string.app_already_indownloadlist));
				} else {
					List<SynchApp> apps = new ArrayList<SynchApp>();
					apps.add(app);
					downloadApps(apps);
					DialogUtil.showShortToast(context, context.getString(R.string.addin_downloadlist_success));
				}
			}
		}
	};

	OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			refreshShandowVisible();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/**
	 * 刷新倒影
	 */
	private void refreshShandowVisible() {
		// 处理最后一排不满3个时候，倒影显示问题
		int firstVisiblePos = gridView.getFirstVisiblePosition();
		int itemCount = adapter.getCount();
		if (itemCount > 6) {
			// 有第三排时候才显示倒影
			int lastRowCount = itemCount % 3;
			L.d("onItemSelected firstvisible=" + firstVisiblePos + " " + (itemCount - (lastRowCount + 6)));
			if (firstVisiblePos >= (itemCount - (lastRowCount + 6))) {
				iv_shandow_item1.setVisibility(VISIBLE);
				if (lastRowCount == 1) {
					iv_shandow_item2.setVisibility(INVISIBLE);
					iv_shandow_item3.setVisibility(INVISIBLE);
				} else if (lastRowCount == 2) {
					iv_shandow_item2.setVisibility(VISIBLE);
					iv_shandow_item3.setVisibility(INVISIBLE);
				} else {
					iv_shandow_item2.setVisibility(VISIBLE);
					iv_shandow_item3.setVisibility(VISIBLE);
				}
			} else if (itemCount > 6) {
				iv_shandow_item1.setVisibility(VISIBLE);
				iv_shandow_item2.setVisibility(VISIBLE);
				iv_shandow_item3.setVisibility(VISIBLE);
			}
		} else {
			iv_shandow_item1.setVisibility(INVISIBLE);
			iv_shandow_item2.setVisibility(INVISIBLE);
			iv_shandow_item3.setVisibility(INVISIBLE);
		}

	}

	@Override
	protected void onDestroy() {
		L.d(TAG + " ondestroy ");
		AppBroadcastReceiver.listeners.remove(context.getClass().getName());
		super.onDestroy();
	}
}
