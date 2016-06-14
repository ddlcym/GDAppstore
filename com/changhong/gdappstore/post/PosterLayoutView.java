package com.changhong.gdappstore.post;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.changhong.gdappstore.util.L;
import com.post.view.AbsPostView.OnItemFocusChangedListener;
import com.post.view.GalleryPostHor;
import com.post.view.GalleryPostVer;
import com.post.view.PostCenterAdapter;
import com.post.view.base.BasePosterLayoutView;
import com.post.view.base.BasePosterWallView;
import com.post.view.listener.INeedPageChangeLisenter;

/**
 * 海报墙的一页，使用时候需要先调用：public void setPostSetting(PostSetting postSetting)
 * 方法设置海报墙配置 然后调用：initData(List<Object> loadItems,int totalNum)添加数据。 <br>
 * 作者 : wangxiufeng <br>
 * 改变类型 : 创建
 */
public class PosterLayoutView extends BasePosterLayoutView {
	private PostSetting postSetting;// 海报墙配置

	public PosterLayoutView(Context context) {
		super(context);
		this.context = context;
	}

	public PosterLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PosterLayoutView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void init(final PostSetting postSetting) {
		resetData();
		this.postSetting = postSetting;
		posteDateListener = postSetting.getiPosteDateListener();

		wallView0 = new PosterWallView(context);
		wallView1 = new PosterWallView(context);
		wallView2 = new PosterWallView(context);
		viewList = new ArrayList<BasePosterWallView>();
		viewList.add(wallView0);
		viewList.add(wallView1);
		viewList.add(wallView2);
		// 隐藏view获取乱跑时候的焦点
		ll_focus = new LinearLayout(context);
		ll_focus.setId(172378);
		ll_focus.setFocusable(false);
		ll_focus.setNextFocusDownId(ll_focus.getId());
		ll_focus.setNextFocusUpId(ll_focus.getId());
		ll_focus.setNextFocusLeftId(ll_focus.getId());
		ll_focus.setNextFocusRightId(ll_focus.getId());

		for (int i = 0; i < viewList.size(); i++) {
			PosterWallView wallView = (PosterWallView) viewList.get(i);
			wallView.setPostSetting(postSetting);
			wallView.setiNeedPageChange(iNeedPageChange);
			wallView.setVisibility(View.VISIBLE);
			wallView.setFocusable(false);
			wallView.setPosterLayoutView(this);
			wallView.setIndex(i);
		}
		initPostContainer();
	}

	/**
	 * 初始化海报墙view容器
	 */
	private void initPostContainer() {
		if (postSetting.isVerticalScroll()) {
			container = new GalleryPostVer(context);
		} else {
			container = new GalleryPostHor(context);
		}
//		Drawable localDrawable1 = getResources().getDrawable(R.drawable.draw_transparent);
		container.setMainFocusDrawable(null);
		container.setFocusPadding(-1, -2, -1, -2);
		container.setFocusViewScale(false, 1.0F, 1.0F);
		container.setVisiblePadding(0, 0, 0, 0);
		container.setFocusRowInScreen(0);
		container.setLoopFlag(true);
		container.setVisibleColumnNum(postSetting.getVisibleClumn());
		container.setVisibleRowNum(postSetting.getVisibleRow());
		container.setSINGLE_SCROLL_DURATION(postSetting.getScrollDuration());
		adapterCenter = new PostCenterAdapter(context, viewList);
		container.setAdapter(adapterCenter);
		container.setFocusable(false);
		container.setFocusableInTouchMode(true);
		container.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		addView(ll_focus, lp);
		addView(container);
		// container.setBackgroundColor(Color.YELLOW);
		container.setOnItemFocusChangedListener(new OnItemFocusChangedListener() {

			@Override
			public void onFocusChanged(View paramView, int paramInt1, boolean paramBoolean, int paramInt2) {
				L.d(TAG + "onitemfocuschange  " + paramInt1 + "  " + paramBoolean + "  " + paramInt2 + " ");
				if (paramBoolean) {
					PosterWallView wallView = (PosterWallView) viewList.get(paramInt1);
					wallView.setItemFocusbale(true);
					wallView.setFocusPosition(curFoucsPos);// 默认焦点
					ll_focus.setFocusable(false);
					lastPagePos = paramInt1;
					// 设置页码
					curpage = (wallView.getFirstPosInDataList() + postSetting.getPost_num_apage()) / postSetting.getPost_num_apage();
					if (posteDateListener != null) {
						posteDateListener.changePage(isNext, curpage, totalpage);
					}
				}
			}
		});
	}


	/**
	 * 更新数据及显示
	 * 
	 * @param loadItems
	 */
	public void initData(List<Object> loadItems, int totalNum) {
		this.totalItems = totalNum;
		if (items == null) {
			items = new ArrayList<Object>();
		}
		if (postSetting == null) {
			postSetting = new PostSetting();// 如果没有设，那么使用默认设置
		}
		int numapage = postSetting.getPost_num_apage();
		totalpage = totalNum % numapage == 0 ? totalNum / numapage : totalNum / numapage + 1;
//		if (loadItems != null && loadItems.size() > 0) {
			items.addAll(loadItems);
			if (isfirst) {
				initFirstData();
			} else {
				dodataChange(lastPagePos);
			}
//		}
	}

	/**
	 * 更新数据及显示 refreshAllData = init(postSetting)+initData(loadItems, totalNum);
	 * 
	 * @param loadItems
	 */
	public void refreshAllData(List<Object> loadItems, PostSetting postSetting, int totalNum) {
		init(postSetting);
		initData(loadItems, totalNum);
	}

	/**
	 * 重置数据
	 */
	public void resetData() {
		super.resetData();
		postSetting = null;// 海报墙配置
	}

	/**
	 * 第一次加载数据
	 */
	private void initFirstData() {
		int postnum = postSetting.getPost_num_apage();
		isNext = true;
		curFoucsPos = 0;
		curpage = 1;
		 L.d(TAG+"curfirst=1="+curFirstPos+" postnum="+postnum+"  "+(curFirstPos
		 / postnum)+"  "+((curFirstPos / postnum) * postnum));
		{// 矫正初始位置
			curFirstPos = curFirstPos < 0 ? 0 : curFirstPos;// 处理小于0
			curFirstPos = curFirstPos >= items.size() ? items.size() - 1 : curFirstPos;// 处理大于长度
			if (curFirstPos % postnum != 0) {
				curFirstPos = (curFirstPos / postnum) * postnum;// 把初始位置调整为页数的整数倍
			}
		}
		List<Object> curItems;
		List<Object> preItems;
		{// 初始化当前页
			if (items.size() >= curFirstPos + postnum) {
				curItems = items.subList(curFirstPos, curFirstPos + postnum);
				// L.d(TAG+" "+curFirstPos + postnum+" "+nextItems.size());
			} else {
				if (items.size() >= totalItems) {
					// 已经下载了所有数据了,显示最后一页
					curItems = items.subList(curFirstPos, items.size());
				} else {
					isfirst = true;//
					if (posteDateListener != null) {
						posteDateListener.requestNextPageDate(items.size());
					}
					return;
				}
			}
			wallView0.initData(curItems, selectedDataPosition);
			wallView0.setFirstPosInDataList(curFirstPos);
		}
		{// 初始化下一页
			wallView1.setItemFocusbale(false);// 将其它页面设置成不可焦点状态
			int nextFirst = curFirstPos + postnum;
			// L.debug(TAG+"firstinitnext nextfirst=="+nextFirst+" itemsize="+items.size()+" curfirst="+curFirstPos);
			if (items.size() >= nextFirst + postnum) {
				wallView1.initData(items.subList(nextFirst, nextFirst + postnum), -1);
				wallView1.setFirstPosInDataList(nextFirst);
			} else if (items.size() > nextFirst) {
				wallView1.initData(items.subList(nextFirst, items.size()), -1);
				wallView1.setFirstPosInDataList(nextFirst);
			}else {
				wallView1.initData(null, -1);
			}
		}
		{// 初始化上一页
			wallView2.setItemFocusbale(false);
			if (curFirstPos > 0) {
				preItems = items.subList(curFirstPos - postnum, curFirstPos);
				wallView2.setFirstPosInDataList(curFirstPos - postnum);
			} else {
				preItems = curItems;
			}
			wallView2.initData(preItems, selectedDataPosition);
		}
		isfirst = false;
		preWallView = wallView0;
		if (postSetting.isFristItemFocus()) {
			wallView0.setFocusPosition(0);
		}
		if (posteDateListener != null) {
			posteDateListener.changePage(isNext, 1, totalpage);
		}
	}

	/**
	 * 翻页数据处理
	 * 
	 * @param curpos
	 */
	private void dodataChange(int curpos) {
		List<Object> preList = new ArrayList<Object>();// 上一页数据
		List<Object> nextList = new ArrayList<Object>();// 下一页数据
		int curFirstPos = 0;
		int prefirst = 0;
		int nextfirst = 0;
		if (curpos == 0) {
			curFirstPos = wallView0.getFirstPosInDataList();
			prefirst = wallView2.getFirstPosInDataList();
			nextfirst = wallView1.getFirstPosInDataList();
		} else if (curpos == 1) {
			curFirstPos = wallView1.getFirstPosInDataList();
			prefirst = wallView0.getFirstPosInDataList();
			nextfirst = wallView1.getFirstPosInDataList();
		} else if (curpos == 2) {
			curFirstPos = wallView2.getFirstPosInDataList();
			prefirst = wallView1.getFirstPosInDataList();
			nextfirst = wallView0.getFirstPosInDataList();
		}
		L.d(TAG + "dodatachange0 curpos=" + curpos + " prefirst=" + prefirst + " curFirstPos= " + curFirstPos + "  nextfirst==" + nextfirst);

		int pagnum = postSetting.getPost_num_apage();
		if (curFirstPos >= pagnum) {
			preList = items.subList(curFirstPos - pagnum, curFirstPos);
			prefirst = curFirstPos - pagnum;
		}
		if (curFirstPos + pagnum * 2 <= items.size()) {
			nextList = items.subList(curFirstPos + pagnum, curFirstPos + pagnum * 2);
			nextfirst = curFirstPos + pagnum;
		} else if (curFirstPos + pagnum <= items.size()) {
			nextList = items.subList(curFirstPos + pagnum, items.size());
			nextfirst = curFirstPos + pagnum;
		}
		L.d(TAG + "dodatachange1 curpos=" + curpos + " prefirst=" + prefirst + " curFirstPos= " + curFirstPos + "  nextfirst==" + nextfirst);

		if (curpos == 0) {
			wallView2.setFirstPosInDataList(prefirst);
			wallView1.setFirstPosInDataList(nextfirst);
			wallView2.initData(preList, selectedDataPosition);
			wallView1.initData(nextList, selectedDataPosition);
			if (nextList == null || nextList.size() <= 0) {
				wallView1.setVisibility(View.INVISIBLE);
			}
			preWallView=wallView0;
		} else if (curpos == 1) {
			wallView0.setFirstPosInDataList(prefirst);
			wallView2.setFirstPosInDataList(nextfirst);
			wallView0.initData(preList, selectedDataPosition);
			wallView2.initData(nextList, selectedDataPosition);
			if (nextList == null || nextList.size() <= 0) {
				wallView2.setVisibility(View.INVISIBLE);
			}
			preWallView=wallView1;
		} else if (curpos == 2) {
			wallView1.setFirstPosInDataList(prefirst);
			wallView0.setFirstPosInDataList(nextfirst);
			wallView1.initData(preList, selectedDataPosition);
			wallView0.initData(nextList, selectedDataPosition);
			if (nextList == null || nextList.size() <= 0) {
				wallView0.setVisibility(View.INVISIBLE);
			}
			preWallView=wallView2;
		}
		if (postSetting.isVerticalScroll()) {// 纵向滚动焦点设置
			if (postSetting.isFocusPosBySystem()) {// 翻页焦点在自动位置
				if (isNext) {
					curFoucsPos = curFoucsPos - postSetting.getPost_column() * (postSetting.getPost_row() - 1);
				} else {
					curFoucsPos = curFoucsPos + postSetting.getPost_column() * (postSetting.getPost_row() - 1);
				}
			}
			// 翻页焦点在原位置
		} else {// 横向滚动焦点设置
			if (postSetting.isFocusPosBySystem()) {// 翻页焦点在自动位置
				if (isNext) {
					curFoucsPos = curFoucsPos - postSetting.getPost_column() + 1;
				} else {
					curFoucsPos = curFoucsPos + postSetting.getPost_column() - 1;
				}
			}
			// 翻页焦点在原位置
		}

	}

	/**
	 * 准备翻页操作
	 */
	private synchronized void dopageChange() {
		PosterWallView wallview;// 没翻页前的当前view
		int curpos = 0;
		if (preWallView != null) {
			curpos = preWallView.getIndex();
		}// 在这个位置lastPagePos和curpos应该是相等的，如果不相等就是BUG。
		if (lastPagePos == 0) {
			wallview = (PosterWallView) wallView0;
			if (isNext) {
				curpos = 1;
			} else {
				curpos = 2;
			}
		} else if (lastPagePos == 1) {
			wallview = (PosterWallView) wallView1;
			if (isNext) {
				curpos = 2;
			} else {
				curpos = 0;
			}
		} else if (lastPagePos == 2) {
			wallview = (PosterWallView) wallView2;
			if (isNext) {
				curpos = 0;
			} else {
				curpos = 1;
			}
		} else {
			wallview = null;
			return;
		}
		Log.d("",
				"PLV---dopageChange  " + isNext + " lastPagePos=" + lastPagePos + " firstpos=" + wallview.getFirstPosInDataList() + " datasize= "
						+ wallview.getDataSize());
		if (isNext) {
			int datapos = wallview.getFirstPosInDataList();
			int datasize = wallview.getDataSize();
			int curlastpos = datapos + datasize - 1;
			if ((curlastpos) >= (totalItems - 1)) {
				if (posteDateListener != null && event.getAction() == KeyEvent.ACTION_DOWN) {
					posteDateListener.lastPageOnKeyDpadDown();
				}
				return;
			}
			if (curlastpos + 1 + postSetting.getPost_num_apage() > items.size() && items.size() < totalItems && posteDateListener != null) {
				posteDateListener.requestNextPageDate(items.size());
				return;// 请求下一页数据
			}
		} else {
			if (wallview.getFirstPosInDataList() <= 0) {
				if (posteDateListener != null && event.getAction() == KeyEvent.ACTION_DOWN) {
					posteDateListener.firstPageOnKeyDpadup();
				}
				return;
			}
		}
		ll_focus.setFocusable(true);// 临时焦点view获取焦点
		ll_focus.requestFocus();
		L.d(TAG+" focusPos=----"+ll_focus.isFocused());
		setAllPageItemFocusbale(false);// 设置item不能焦点
		setAllPageVisible(View.VISIBLE);// 设置item显示
		dodataChange(curpos);
		lastPagePos = curpos;
		if (event != null) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				container.myOnKeyDown(keycode, event);
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				container.myOnKeyDown(keycode, event);
			}
		}
	}


	/**
	 * 翻页监听器
	 */
	private INeedPageChangeLisenter iNeedPageChange = new INeedPageChangeLisenter() {

		@Override
		public void pageNeedChange(BasePosterWallView view, boolean isnext, int curFocusPos, int keyCode, KeyEvent keyevent) {
			curFoucsPos = curFocusPos;
			isNext = isnext;
			keycode = keyCode;
			event = keyevent;
			preWallView = (PosterWallView) view;
//			L.d(TAG + " focusPos=" + curFocusPos + " isnext==" + isnext + " view.getfirstpos==" + view.getFirstPosInDataList());
			dopageChange();
		}
	};


	public PostSetting getPostSetting() {
		return postSetting;
	}
	
}
