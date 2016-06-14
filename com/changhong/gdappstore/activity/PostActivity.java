package com.changhong.gdappstore.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BaseActivity;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.net.LoadListener.LoadListListener;
import com.changhong.gdappstore.post.PostItem;
import com.changhong.gdappstore.post.PostSetting;
import com.changhong.gdappstore.post.PosterLayoutView;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.view.PostTitleView;
import com.changhong.gdappstore.view.PostTitleView.TitleItemOnClickListener;
import com.changhong.gdappstore.view.PostTitleView.TitleItemOnFocuesChangedListener;
import com.post.view.base.BasePosterLayoutView;
import com.post.view.listener.IPosteDateListener;
import com.post.view.listener.Listener.IItemOnClickListener;

/**
 * 海报墙页面
 * 
 * @author wangxiufeng
 * 
 */
public class PostActivity extends BaseActivity {
	/** 海报墙view */
	protected PosterLayoutView postView;
	/** 海报墙配置项 */
	protected PostSetting postSetting;
	/** 标题view */
	protected PostTitleView titleView;
	/** 数据处理中心 */
	protected DataCenter dataCenter;
	/** 父栏目，根据首页传过来的id确定 */
	protected Category parentCategory = null;
	/** 栏目名字 */
	protected TextView tv_name, tv_page;
	/** 搜索按钮 */
	protected ImageView iv_search;
	/** 当前应用列表 */
	private List<Object> currentApps = new ArrayList<Object>();
	// protected ImageView iv_shandow1, iv_shandow2, iv_shandow3, iv_shandow4;
	/** 当前显示类别id **/
	protected int curCategoryId = 0;

	private int itemsize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		showLoadingDialog();
		initView();
		initData();
	}

	private void initView() {
		postView = findView(R.id.postview);
		titleView = findView(R.id.posttitleview);
		tv_name = findView(R.id.tv_pagename);
		tv_page = findView(R.id.tv_page);
		iv_search = findView(R.id.iv_search);
		iv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(PostActivity.this, SearchActivity.class));
			}
		});
		iv_search.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					if (titleView != null && titleView.getCurrentSelectedView() != null) {
						titleView.getCurrentSelectedView().requestFocus();
						return true;
					}
				}
				return false;
			}
		});
		titleView.setTitleItemOnClickListener(new TitleItemOnClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				Category category = (Category) view.getTag();
				if (curCategoryId != category.getId()) {
					showLoadingDialog();
					if (category.isIstopic()) {
						//专题
						dataCenter.loadAppsByTopicId(context, category.getId(), loadAppListListener);
					}else {
						//普通栏目
						dataCenter.loadAppsByCategoryId(context, category.getId(), loadAppListListener);
					}
					curCategoryId = category.getId();
				}
			}
		});
		titleView.setTitleItemOnFocuesChangedListener(new TitleItemOnFocuesChangedListener() {

			@Override
			public void onItemFocuesChanged(View view, boolean hasFocues, int position) {

			}
		});
		titleView.setMargin(-15, -15);
		titleView.setIfFocuesWithSelected(false);
		initPostView();
	}

	private void initPostView() {

		// 海报墙设置，监听器没有可以设为空，行列设为负数则使用默认值
		postSetting = new PostSetting(3, 3, R.drawable.selector_bg_postitem, iPosteDateListener, null,
				postItemOnclickListener, null, postOnKeyListener);
		postSetting.setVerticalScroll(false);// 纵向滚动
		postSetting.setVisibleClumn(1.07f);// 显示的页数
		postSetting.setMargins(0, 0, 0, 0);// item的距离
		postSetting.setLastClumnFocusRight(false);
		postSetting.setFirstRowFocusUp(true);// 第一排是否允许焦点再往上
		postSetting.setFirstClumnFocusLeft(false);
		postSetting.setFristItemFocus(false);
		postSetting.setShowShandow(true);
		postSetting.setPosttype(PostSetting.TYPE_NORMAL);
		// 如果需要海报墙使用自己的设置，要先调用设置设置方法，在调用设置数据
		postView.init(postSetting);

	}

	protected void initData() {
		dataCenter = DataCenter.getInstance();
		Intent intent = getIntent();
		int parentCategoryId = intent.getIntExtra(Config.KEY_PARENT_CATEGORYID, -2);
		int currentCategoryId = intent.getIntExtra(Config.KEY_CURRENT_CATEGORYID, parentCategoryId);
		if (parentCategoryId < -1 && parentCategoryId != Config.ID_ZHUANTI) {
			dismissLoadingDialog();
			return;
		}
		parentCategory = dataCenter.getCategoryById(parentCategoryId);// 获取父栏目
		curCategoryId = currentCategoryId;
		L.d("parentCategory " + parentCategory.getId() + "  " + parentCategory.getName());
		if (parentCategoryId != Config.ID_ZHUANTI) {
			if (parentCategory != null) {
				tv_name.setText(parentCategory.getName());
				titleView.initData(parentCategory, parentCategory.getCategoyChildren());
				if (parentCategory.getId() == currentCategoryId) {
					titleView.setFocusItem(0);// 选中全部
					titleView.setSelectedItem(0);
				} else if (parentCategory.getCategoyChildren() != null) {
					for (int i = 0; i < parentCategory.getCategoyChildren().size(); i++) {
						if (parentCategory.getCategoyChildren().get(i).getId() == currentCategoryId) {
							titleView.setFocusItem(i + 1);// 选中当前item
							titleView.setSelectedItem(i + 1);
						}
					}
				}
			}
			dataCenter.loadAppsByCategoryId(context, currentCategoryId, loadAppListListener);
		} else {
			tv_name.setText(context.getString(R.string.zhuanti));
//			if (parentCategory != null) {
//				titleView.initData(parentCategory, parentCategory.getCategoyChildren());
//				for (int i = 0; i < parentCategory.getCategoyChildren().size(); i++) {
//					if (parentCategory.getCategoyChildren().get(i).getId() == currentCategoryId) {
//						titleView.setFocusItem(i);// 选中当前item
//						titleView.setSelectedItem(i);
//					}
//				}
//			}
			titleView.setVisibility(INVISIBLE);
			dataCenter.loadAppsByTopicId(context, currentCategoryId, loadAppListListener);
		}
	}

	private LoadListListener loadAppListListener = new LoadListListener() {

		@Override
		public void onComplete(List<Object> items) {
			currentApps = items;
			L.d("loadapp complete " + ((items == null) ? "items is null" : items.size()));
			tv_page.setText("("+0+"/"+0+")");
			if (items != null && items.size() > 0) {
				itemsize = currentApps.size();
				postView.setVisibility(VISIBLE);
				postView.refreshAllData(currentApps, postSetting, currentApps == null ? 0 : itemsize);
			} else {
				itemsize = 0;
				postView.setVisibility(INVISIBLE);
			}
			dismissLoadingDialog();
		}
	};

	/** 海报墙点击监听 **/
	private IItemOnClickListener postItemOnclickListener = new IItemOnClickListener() {

		@Override
		public void itemOnClick(BasePosterLayoutView arg0, View arg1, int arg2) {
			if (arg1 == null || arg1.getTag() == null) {
				return;
			}
			if (!NetworkUtils.ISNET_CONNECT) {
				DialogUtil.showShortToast(context, context.getString(R.string.net_disconnected_pleasecheck));
				return;
			}
			App app = (App) arg1.getTag();
			Intent intent = new Intent(PostActivity.this, DetailActivity.class);
			intent.putExtra(Config.KEY_APPID, app.getAppid());
			startActivity(intent);
		}
	};

	private IPosteDateListener iPosteDateListener = new IPosteDateListener() {

		@Override
		public void requestNextPageDate(int currentSize) {
			// 请求新数据回调
		}

		@Override
		public void changePage(Boolean isnext, int curpage, int totalpage) {
			// 翻页回调
			tv_page.setText("("+curpage+"/"+totalpage+")");
		}

		@Override
		public void lastPageOnKeyDpadDown() {
			// Toast.makeText(PostActivity.this, "已经是最后一页了！",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void firstPageOnKeyDpadup() {
			// Toast.makeText(PostActivity.this, "已经是第一页了！",
			// Toast.LENGTH_SHORT).show();
		}
	};

	private OnKeyListener postOnKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int pos = ((PostItem) v).getPosition();
			if (pos < postSetting.getPost_column() && keyCode == KeyEvent.KEYCODE_DPAD_UP
					&& event.getAction() == KeyEvent.ACTION_DOWN && titleView.getCurrentSelectedView() != null) {
				// 处理每次海报墙按上键时候选中当前标签
				titleView.getCurrentSelectedView().requestFocus();
				return true;
			}
			return false;
		}
	};
}
