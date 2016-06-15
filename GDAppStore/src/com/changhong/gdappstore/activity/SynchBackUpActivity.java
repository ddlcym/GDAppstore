package com.changhong.gdappstore.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.changhong.gdappstore.database.DBManager;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.NativeApp;
import com.changhong.gdappstore.model.SynchApp;
import com.changhong.gdappstore.model.SynchApp.Type;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.util.DateUtils;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.SharedPreferencesUtil;
import com.changhong.gdappstore.util.Util;

public class SynchBackUpActivity extends BaseActivity implements OnClickListener, OnKeyListener {

	private String DOBATCH;
	private String CONFIRM_BACKUP;
	private GridView gridView;
	private SynchGridAdapter adapter;
	private Button bt_batch;
	private ImageView iv_shandow_item1, iv_shandow_item2, iv_shandow_item3, iv_batch_icon;
	private TextView tv_batch_suggest, tv_num_checked, tv_ge;
	private TextView tv_pagename;
	/** 批量操作时候选择的item个数 */
	private int curCheckedItem = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synch_backup);
		showLoadingDialog();
		initView();
		initData();
	}

	private void initView() {
		DOBATCH = context.getString(R.string.batch_backup);
		CONFIRM_BACKUP = context.getString(R.string.confirm_backup);
		gridView = findView(R.id.gridview);
		iv_shandow_item1 = findView(R.id.iv_shandow_item1);
		iv_shandow_item2 = findView(R.id.iv_shandow_item2);
		iv_shandow_item3 = findView(R.id.iv_shandow_item3);

		adapter = new SynchGridAdapter(context, null);
		gridView.setOnItemSelectedListener(itemSelectedListener);
		gridView.setOnItemClickListener(onItemClickListener);
		gridView.setOnKeyListener(this);
		gridView.setAdapter(adapter);

		tv_batch_suggest = findView(R.id.tv_batch_suggest);
		tv_batch_suggest.setText(context.getString(R.string.dobatchbyclickmenu));

		tv_pagename = findView(R.id.tv_pagename);
		tv_pagename.setText(context.getString(R.string.backup));

		bt_batch = findView(R.id.bt_batch);
		bt_batch.setOnKeyListener(this);
		bt_batch.setOnClickListener(this);
		bt_batch.requestFocus();
		bt_batch.setText(DOBATCH);

		tv_batch_suggest = findView(R.id.tv_batch_suggest);
		tv_batch_suggest.setText(context.getString(R.string.dobatchbyclickmenu));

		tv_num_checked = findView(R.id.tv_num_checked);
		iv_batch_icon = findView(R.id.iv_batch_icon);
		tv_ge = findView(R.id.tv_ge);

	}

	private void initData() {
		adapter.setBatch(false);
		List<String> packages = new ArrayList<String>();
		// 获取本地已安装应用的报名
		final List<NativeApp> nativeApps = Util.getApp(context);
		for (int i = 0; i < nativeApps.size(); i++) {
			if (nativeApps.get(i) != null && !TextUtils.isEmpty(((NativeApp) nativeApps.get(i)).getAppPackage())) {
				packages.add(((NativeApp) nativeApps.get(i)).getAppPackage());
			}
		}
		/****************** 每天只请求所有应用一次，其它时候都剔除缓存中非我们市场应用 *************************/
		final String thisDay = DateUtils.getDayByyyyyMMdd();
		final String lastRequestDay = SharedPreferencesUtil.getAppSynch(context, SharedPreferencesUtil.KEY_REQUESTDAY);
		L.d("checkotherapp thisday==" + thisDay + "  lastday==" + lastRequestDay + " compare=="
				+ thisDay.compareTo(lastRequestDay));
		if (thisDay.compareTo(lastRequestDay) <= 0) {
			// 当天已经请求过了。
			List<String> otherApps = DBManager.getInstance(context).queryOtherApps();
			if (otherApps != null && otherApps.size() > 0 && packages != null && packages.size() > 0) {
				for (int i = 0; i < otherApps.size(); i++) {
					String otherAppPackage = otherApps.get(i);
					boolean isInstalled = false;
					for (int j = 0; j < packages.size(); j++) {
						if (packages.get(j).equals(otherAppPackage)) {
							isInstalled = true;
							L.d("checkotherapp removed befor request package==" + otherAppPackage);
							packages.remove(j);
							break;
						}
					}
					if (!isInstalled) {
						// 删除已经卸载应用在数据库中记录
						L.d("checkotherapp delete uninstalled app package==" + otherAppPackage);
						DBManager.getInstance(context).deleteOtherApp(otherAppPackage);
					}
				}
			}
			SharedPreferencesUtil.putAppSynch(context, SharedPreferencesUtil.KEY_REQUESTDAY, thisDay);
		}
		/*****************************************************************/

		DataCenter.getInstance().checkBackUpApp(packages, context, new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				List<SynchApp> items = (List<SynchApp>) object;
				List<SynchApp> itemsBySort = new ArrayList<SynchApp>();
				List<String> otherAppsInDb = DBManager.getInstance(context).queryOtherApps();
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
					for (int i = 0; i < nativeApps.size(); i++) {
						NativeApp nativeApp = (NativeApp) nativeApps.get(i);
						boolean isotherApp = true;
						for (int j = 0; j < items.size(); j++) {
							SynchApp synchApp = items.get(j);
							if (synchApp.getPackageName().equals(nativeApp.appPackage)) {
								isotherApp = false;
								if (TextUtils.isEmpty(synchApp.getAppname())) {
									synchApp.setAppname(nativeApp.appname);
								}
								synchApp.setAppIcon(nativeApp.appIcon);
								synchApp.setVersionInt(nativeApp.nativeVersionInt);
								if (synchApp.getSynchType() == Type.BACKUPED) {
									itemsBySort.add(synchApp);
								} else {
									itemsBySort.add(0, synchApp);// 排序
								}
								break;
							}
						}
						if (isotherApp && !containsString(otherAppsInDb, nativeApp.getAppPackage())) {
							// 非我们市场的应用保存到数据库缓存中
							L.d("checkotherapp +insert " + nativeApp.getAppPackage());
							DBManager.getInstance(context).insertOtherApp(nativeApp.getAppPackage());
						}
					}

					// 非我们应用市场应用缓存操作，只有请求所有本地应用时候才用得着，所以要判断是否是同一天
					if (thisDay.compareTo(lastRequestDay) > 0) {
						// 删除本地安装属于我们市场的应用。
						for (int i = 0; i < items.size(); i++) {
							for (int j = 0; j < otherAppsInDb.size(); j++) {
								if (otherAppsInDb.get(j).equals(items.get(i).getPackageName())) {
									DBManager.getInstance(context).deleteOtherApp(otherAppsInDb.get(j));
									L.d("checkotherapp delete otherapp in db " + otherAppsInDb.get(j));
									otherAppsInDb.remove(j);
									break;
								}
							}
						}
					}
				}
				adapter.updateList(itemsBySort);
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * String列表是否包含字符串name
	 * 
	 * @param list
	 * @param name
	 * @return
	 */
	private boolean containsString(List<String> list, String name) {
		if (list == null || list.size() <= 0 || name == null) {
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(name)) {
				return true;
			}
		}
		return false;
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
		if (bt_batch.getText().toString().equals(CONFIRM_BACKUP)) {
			// 已经是批量操作了，执行批量提交
			String ids = "";
			for (int i = 0; i < adapter.getCount(); i++) {
				SynchApp app = (SynchApp) adapter.getItem(i);
				if (app.isChecked()) {
					if (ids.equals("")) {
						ids = ids + app.getAppid();
					} else {
						ids = ids + "," + app.getAppid();
					}
					app.setChecked(false);
				}
			}
			postBackUp(ids);
		} else {
			// 从正常操作转向批量操作
			bt_batch.setText(CONFIRM_BACKUP);
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
	 * 请求备份
	 * 
	 * @param ids
	 *            需要备份应用的id列表，中间用逗号隔开
	 * @param isbatch
	 *            是否是批量操作
	 */
	private void postBackUp(String ids) {
		showLoadingDialog();
		DataCenter.getInstance().postBackup(ids, context, new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				List<Integer> successIds = (List<Integer>) object;
				List<SynchApp> items = adapter.getItems();
				if (successIds != null && items != null) {
					for (int i = 0; i < items.size(); i++) {
						for (int j = 0; j < successIds.size(); j++) {
							if (items.get(i).getAppid() == successIds.get(j).intValue()) {
								items.get(i).setSynchType(Type.BACKUPED);
								items.get(i).setChecked(false);
							}
						}
					}
				}
				if (adapter.isBatch()) {
					// 批量提交
					bt_batch.setText(DOBATCH);
					tv_batch_suggest.setText(context.getString(R.string.dobatchbyclickmenu));
					iv_batch_icon.setVisibility(VISIBLE);
					tv_num_checked.setVisibility(INVISIBLE);
					tv_ge.setVisibility(INVISIBLE);
					adapter.updateList(items, false);
				} else {
					// 普通提交
					adapter.updateList(items);
				}
				dismissLoadingDialog();
			}
		});
	}

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
			if (adapter.isBatch() && app.getSynchType() != Type.BACKUPED) {
				// 批量操作

				if (app != null) {
					curCheckedItem = app.isChecked() ? curCheckedItem - 1 : curCheckedItem + 1;
					refreshCheckedItemText();
					app.setChecked(!app.isChecked());
					adapter.notifyDataSetChanged();
				}
			} else if (app.getSynchType() != Type.BACKUPED) {
				// 普通操作
				postBackUp(app.getAppid() + "");
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
}
