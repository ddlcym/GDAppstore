package com.changhong.gdappstore.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.adapter.MainViewPagerAdapter;
import com.changhong.gdappstore.base.BaseActivity;
import com.changhong.gdappstore.base.BasePageView;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.AppDetail;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.net.LoadListener.LoadCompleteListener;
import com.changhong.gdappstore.service.NetChangeReceiver;
import com.changhong.gdappstore.service.NetChangeReceiver.NetChangeListener;
import com.changhong.gdappstore.service.SilentInstallService;
import com.changhong.gdappstore.service.UpdateService;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.DialogUtil.DialogBtnOnClickListener;
import com.changhong.gdappstore.util.DialogUtil.DialogMessage;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.view.HomePageView;
import com.changhong.gdappstore.view.MyProgressDialog;
import com.changhong.gdappstore.view.PostTitleView;
import com.changhong.gdappstore.view.PostTitleView.TitleItemOnClickListener;
import com.changhong.gdappstore.view.PostTitleView.TitleItemOnFocuesChangedListener;
import com.changhong.gdappstore.view.TopicView;

/**
 * homepage
 * 
 * @author wangxiufeng
 * 
 */
public class MainActivity extends BaseActivity {
	/** 标签按钮view **/
	private PostTitleView titleView;
	/***/
	private ViewPager viewPager;
	/** viewpager适配器 */
	private MainViewPagerAdapter viewPagerAdapter;

	/** viewpager 翻页动画 */
	private Animation anim_rightin, anim_leftin;
	/** viewpager 当前选中标签页 */
	private int currIndex = 0;
	/** 栏目分类数据 */
	private List<Category> categories = null;
	/** 设置按钮 */
	private ImageView iv_setting;

	/** 默认显示几页 **/
	private static final int PAGESIZE = 5;
	/** 当前几页，可能会变化 **/
	private static int curPageSize = PAGESIZE;
	private BasePageView[] homePages = new BasePageView[curPageSize];
	private HomePageView homePageView;


	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// viewPager.setAdapter(viewPagerAdapter);
			if (msg.what == 11) {
				viewPager.setAdapter(viewPagerAdapter);
				initOnCreateData();
			} else if (msg.what == 12) {
				initData();
			}


		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d("mainactivity on create ");
		
		setContentView(R.layout.activity_main);
		initView();
		startSilentInstallService();
	}


	private void initView() {
		showLoadingDialog();
		// init title view
		titleView = findView(R.id.titleview);
		titleView.setTitleItemOnClickListener(titleItemOnClickListener);
		titleView.setTitleItemOnFocuesChangedListener(titleItemOnFocuesChangedListener);
		homePages[0] = homePageView = new HomePageView(context);// 首页
		homePageView.setPageIndex(0);
		NetChangeReceiver.listeners.put(context.getClass().getName(), new NetChangeReceiver.NetChangeListener() {

			@Override
			public void onNetChange(boolean isconnect) {
				L.d("MainActivity---onnetchanged--" + isconnect + " " + Util.getTopActivity(context) + " ");
				DataCenter.getInstance().loadCategoryAndPageData(context, new LoadListener.LoadCompleteListener() {

					@Override
					public void onComplete() {
						if (Util.getTopActivity(context).equals(context.getClass().getName())) {// 网络从断开到链接更新数据
							initData();
						}
					}
				}, true);
			}
		});
		initPageChangeAnimtion();
		iv_setting = findView(R.id.iv_setting);
		iv_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS); // 系统设置
				startActivity(intent);
			}
		});

		// init view pager
		viewPager = findView(R.id.viewpager);
		viewPagerAdapter = new MainViewPagerAdapter(getHomePageList(homePages));

		viewPager.setCurrentItem(0);
		viewPager.setAnimationCacheEnabled(true);
		viewPager.setOffscreenPageLimit(5);
		viewPager.setOnPageChangeListener(pageChangeListener);

		handler.sendEmptyMessageDelayed(11, 10);// 解决跳转时候上个页面停留太久
	}

	/**
	 * 执行onCreate页面首次进入时候请求数据
	 */
	private void initOnCreateData() {
		L.d("initdata initOnCreateData----");
		showLoadingDialog();
		new Thread(new Runnable() {// 解决跳转时候上个页面停留太久

					@Override
					public void run() {
						DataCenter.getInstance().loadCategoryAndPageData(context, new LoadCompleteListener() {

							@Override
							public void onComplete() {
								L.d("initdata onComplete----");
								handler.sendEmptyMessage(12);
							}
						}, true);
					}
				}).start();
	}

	/**
	 * 初始化数据
	 */
	protected void initData() {
		// 栏目数据
		categories = DataCenter.getInstance().getCategories();
		// 标签距离
		titleView.setMargin(-5, -5);
		// 初始化首页默认数据（首页默认显示）
		homePageView.initNativeData();
		if (categories != null) {
			// 移除多余PAGESIZE的页面
			for (int i = 0; i < categories.size() - 1; i++) {
				if (i > PAGESIZE - 2) {// 有专题存在
					L.d("categroies will be remove " + i + "  " + categories.get(i).getName());
					categories.remove(i);
					i--;
				}
			}
			// 当前页面curPageSize和预定义PAGESIZE不相等时候，重置当前页面数据
			if (curPageSize != PAGESIZE) {
				homePages = new BasePageView[PAGESIZE];
				homePages[0] = homePageView;// 首页是必须添加项
				curPageSize = PAGESIZE;
			}
			// 如果栏目数据小于预定义页数，重新定义页数
			if (categories.size() < PAGESIZE) {
				curPageSize = categories.size();
				curPageSize = curPageSize == 0 ? 1 : curPageSize;// 如果为0需要加上首页
				homePages = new BasePageView[curPageSize];
				homePages[0] = homePageView;// 首页是必须添加项
				L.d("categoriessize<PAGESIZE categoriessize=" + categories.size() + " homePagesSize="
						+ homePages.length);
			}
			L.d("initdata categoriessize is " + categories.size() + " new homePagesSize is " + homePages.length);
			// 设置标签数据
			titleView.initData(categories);
			// 设置页面数据
			for (int i = 0; i < curPageSize; i++) {

				if (categories.size() > i) {
					Category category = categories.get(i);
					if (category.isIstopic()) {// 专题页面
						if (homePages[i] == null) {
							homePages[i] = new TopicView(context);
							homePages[i].setPageTag(BasePageView.TAG_TOPIC);
						}
						((TopicView) homePages[i]).initData(category);
						((TopicView) homePages[i]).setNextFocuesUpId(titleView.getItemTextViewAt(i).getId());
					} else {// 普通页面
						if (homePages[i] == null) {
							homePages[i] = new HomePageView(context);
						}
						((HomePageView) homePages[i]).initData(category);
						((HomePageView) homePages[i]).setNextFocuesUpId(titleView.getItemTextViewAt(i).getId());
					}
					homePages[i].setPageIndex(i);
				}
			}
		} else {
			L.d("mainactivity initdata category is null");
		}
		viewPagerAdapter.updateList(getHomePageList(homePages));
		titleView.setFocusItem(0);
		checkUpdate();
		dismissLoadingDialog();
	}

	private List<View> getHomePageList(BasePageView[] basePageViews) {
		List<View> views = new ArrayList<View>();
		if (basePageViews == null || basePageViews.length == 0) {
			return views;
		}
		for (int i = 0; i < basePageViews.length; i++) {
			if (basePageViews[i] != null) {
				views.add(basePageViews[i]);
			}
		}
		return views;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			// 标签按下选择第一个位置
			int titleFocusPos = titleView.getFocuesPosition();
			if (titleFocusPos >= 0 && viewPager.getCurrentItem() == titleFocusPos && titleFocusPos < homePages.length
					&& homePages[titleFocusPos] != null) {
				if (homePages[titleFocusPos].getPageTag() == BasePageView.TAG_TOPIC) {
					if (((TopicView) homePages[titleFocusPos]).getChildViewAt(0) != null) {
						((TopicView) homePages[titleFocusPos]).getChildViewAt(0).requestFocus();
						return true;
					}
				} else {
					if (homePages[titleFocusPos].getCategoryItemViews()[0] != null) {
						homePages[titleFocusPos].getCategoryItemViews()[0].requestFocus();
						return true;
					}
				}
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				DialogUtil.showMyAlertDialog(context, "", context.getString(R.string.sure_exit_appstore), "", "",
						new DialogBtnOnClickListener() {

							@Override
							public void onSubmit(DialogMessage dialogMessage) {
								System.exit(0);
							}

							@Override
							public void onCancel(DialogMessage dialogMessage) {
								if (dialogMessage.dialogInterface != null) {
									dialogMessage.dialogInterface.dismiss();
								}
							}
						});
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// view_homepage.setShandows();
	}

	/**
	 * 初始化动画
	 */
	private void initPageChangeAnimtion() {
		int halfscreen = screenWidth / 2;
		int duration = 300;
		anim_leftin = new TranslateAnimation(-halfscreen, 0, 0, 0);
		anim_rightin = new TranslateAnimation(halfscreen, 0, 0, 0);
		anim_leftin.setFillAfter(false);
		anim_rightin.setFillAfter(false);
		anim_leftin.setDuration(duration);
		anim_rightin.setDuration(duration);
	}

	private TitleItemOnFocuesChangedListener titleItemOnFocuesChangedListener = new TitleItemOnFocuesChangedListener() {

		@Override
		public void onItemFocuesChanged(View view, boolean hasFocues, int position) {
			viewPager.setCurrentItem(position);
		}
	};

	private TitleItemOnClickListener titleItemOnClickListener = new TitleItemOnClickListener() {

		@Override
		public void onItemClick(View view, int position) {
			if (categories.get(position).getName().equals(Config.HOMEPAGE)) {
				return;
			}
			if (categories.get(position).getName().equals(Config.ZHUANTI)) {
				return;
			}
			Intent intent = new Intent(context, PostActivity.class);
			intent.putExtra(Config.KEY_PARENT_CATEGORYID, categories.get(position).getId());
			intent.putExtra(Config.KEY_CURRENT_CATEGORYID, categories.get(position).getId());
			context.startActivity(intent);
			// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		}
	};
	/**
	 * 页卡切换监听
	 */
	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 < 0 || arg0 > homePages.length || homePages[arg0] == null) {
				return;
			}
			BasePageView curPageView = homePages[arg0];
			if (arg0 != homePages.length - 1) {
				((HomePageView) curPageView).setShandows();
			}
			titleView.setSelectedItem(arg0);
			if (!titleView.hasChildFocuesed()) {// 非标签上面切换情况下，处理默认交代呢
				if (arg0 == categories.size() - 1) {
					// 当前页面是专题页面
					if (curPageView.getPageTag() == BasePageView.TAG_TOPIC) {// 从左往右翻页
						if (homePages[currIndex].currentFocuesId == R.id.homepage_item12) {
							((TopicView) curPageView).setOtcItemFocuesByPos(1);// 最底层一排翻页让第下一页最低层第一个获取焦点
						} else {
							((TopicView) curPageView).setOtcItemFocuesByPos(0);// 其它情况让第一个获取焦点
						}
					}
				} else if (arg0 == curPageSize - 2) {
					// 当前页面是其它页面的上一个页面
					if (currIndex == arg0 + 1) {// 从右往左翻页
						if (homePages[currIndex].currentFocuesId == (TopicView.TOPICITEMVIEW_BASEID + 1)) {
							curPageView.setPostItemFocuesByPos(11);// 最底层一排翻页让第上一页最低层最后一个获取焦点
						} else {
							curPageView.setPostItemFocuesByPos(9);// 其它情况让最后一列最上面一个获取焦点
						}
					}
				} else {
					if (currIndex == arg0 - 1) {// 从左往右翻页
						if (homePages[currIndex].currentFocuesId == R.id.homepage_item12) {
							curPageView.setCategoryItemFocuesByPos(3);// 最底层一排翻页让第下一页最低层第一个获取焦点
						} else if (homePages[currIndex].currentFocuesId == R.id.homepage_item11) {
							curPageView.setCategoryItemFocuesByPos(1);
						} else {
							curPageView.setCategoryItemFocuesByPos(0);// 其它情况让第一个获取焦点
						}
					} else if (currIndex == arg0 + 1) {// 从右往左翻页
						if (homePages[currIndex].currentFocuesId == R.id.homepage_itema4) {
							curPageView.setPostItemFocuesByPos(11);// 最底层一排翻页让第上一页最低层最后一个获取焦点
						} else if (homePages[currIndex].currentFocuesId == R.id.homepage_itema2
								|| homePages[currIndex].currentFocuesId == R.id.homepage_itema3) {
							curPageView.setCategoryItemFocuesByPos(10);
						} else {
							curPageView.setPostItemFocuesByPos(9);// 其它情况让最后一列最上面一个获取焦点
						}
					}
				}
			}
			if (currIndex == arg0 - 1) {
				curPageView.startAnimation(anim_rightin);// 从左往右翻页动画
			} else if (currIndex == arg0 + 1) {
				curPageView.startAnimation(anim_leftin);// 从右往左翻页动画
			}
			currIndex = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

}
