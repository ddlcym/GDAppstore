package com.changhong.gdappstore.post;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.changhong.gdappstore.util.L;
import com.post.view.base.BasePosterWallView;
import com.post.view.listener.INeedPageChangeLisenter;

public class PosterWallView extends BasePosterWallView {
	private PostSetting postSetting;// 海报墙配置
	private PosterLayoutView posterLayoutView;
	private PostItem[] shandowPostItems;

	public PosterWallView(Context context) {
		super(context);
		this.context = context;
	}

	public PosterWallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PosterWallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void setiNeedPageChange(INeedPageChangeLisenter iNeedPageChange) {
		this.iNeedPageChange = iNeedPageChange;
	}

	public void setPostSetting(PostSetting postSetting) {
		this.postSetting = postSetting;
		if (postSetting == null) {
			postSetting = new PostSetting();
		}
		setFocusable(false);
		init();
	}

	private void init() {
		// L.d(TAG+" posterwallview init");
		items = new PostItem[postSetting.getPost_num_apage()];
		// 父layout，竖向排列
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout layout_parent = new LinearLayout(context);
		layout_parent.setLayoutParams(layoutParams);
		layout_parent.setOrientation(LinearLayout.VERTICAL);
		layout_parent.setGravity(Gravity.CENTER);
		layout_parent.setFocusable(false);
		this.setLayoutParams(layoutParams);

		// 行layout，横向排列
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout layout_row = new LinearLayout(context);
		layout_row.setFocusable(false);
		layout_row.setLayoutParams(layoutParams2);

		LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout_row.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < items.length; i++) {
			layoutParams3.leftMargin = postSetting.getItemLeftMargin();// item
			layoutParams3.rightMargin = postSetting.getItemRightMargin();
			layoutParams3.topMargin = postSetting.getItemTopMargin();
			layoutParams3.bottomMargin = postSetting.getItemBottomMargin();

			PostItem children = new PostItem(context);
			if (postSetting.isVerticalScroll()) {
				if (postSetting.getNextFocuesLeftId() > 0 && i % postSetting.getPost_column() == 0) {
					children.setNextFocusLeftId(postSetting.getNextFocuesLeftId());// 竖向滚动，第一列往左按默认焦点
				}
			} else {
				if (firstPosInDataList == 0 && postSetting.getNextFocuesLeftId() > 0
						&& i % postSetting.getPost_column() == 0) {
					children.setNextFocusLeftId(postSetting.getNextFocuesLeftId());// 横向滚动，第一列往左按默认焦点
				}
			}
			children.setVisibility(View.INVISIBLE);
			children.init(postSetting);
			children.setFocusable(true);
			children.setId(314313 + i + index * 1000);
			children.setPosition(i);
			children.setOnFocusChangeListener(onFocusChangeListener);
			if (postSetting.getItemFocusDrawableId() > 0) {
				children.setBackgroundResource(postSetting.getItemFocusDrawableId());// TODO
			}
			children.setOnKeyListener(onKeyListener);// 按键监听
			children.setOnClickListener(onClickListener);// 点击事件
			children.setOnLongClickListener(onLongClickListener);// 长按事件
			if (i >= (postSetting.getPost_num_apage() - postSetting.getPost_column())) {
				// 最后一排
				layoutParams3.bottomMargin = postSetting.getItemBottomMargin() + postSetting.getPagePaddingBottom();
				if (!postSetting.isLastRowFocusDown()) {
					children.setNextFocusDownId(children.getId());// 最后一排按下焦点是否会移动
				}
			}
			if (i < postSetting.getPost_column()) {
				// 第一排
				layoutParams3.topMargin = postSetting.getItemTopMargin() + postSetting.getPagePaddingTop();
				if (!postSetting.isFirstRowFocusUp()) {
					children.setNextFocusUpId(children.getId());// 第一排按上焦点是否会移动
				}
			}
			if (i % postSetting.getPost_column() == 0) {
				// 第一列
				layoutParams3.leftMargin = postSetting.getItemLeftMargin() + postSetting.getPagePaddingLeft();
				if (!postSetting.isFirstClumnFocusLeft()) {
					children.setNextFocusLeftId(children.getId());// 第一列按左焦点是否会移动
				}
			}
			if ((i + 1) % postSetting.getPost_column() == 0) {
				// 最后第一列
				layoutParams3.rightMargin = postSetting.getItemRightMargin() + postSetting.getPagePaddingRight();
				if (!postSetting.isLastClumnFocusRight()) {
					children.setNextFocusRightId(children.getId());// 最后一列按右焦点是否会移动
				}
			}

			children.setLayoutParams(layoutParams3);
			items[i] = children;
			layout_row.addView(children);
			if ((i + 1) % postSetting.getPost_column() == 0) {
				layout_parent.addView(layout_row);
				if (i != items.length - 1) {
					layout_row = new LinearLayout(context);
					layout_row.setFocusable(false);
					layout_row.setLayoutParams(layoutParams2);
					layout_row.setOrientation(LinearLayout.HORIZONTAL);
				}
			}
			if (postSetting.isShowShandow() && (i + 1) == items.length) {
				// 添加倒影view
				LinearLayout.LayoutParams layoutParamsS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				layoutParamsS.topMargin = -30;
				LinearLayout layout_rowS = new LinearLayout(context);
				layout_rowS.setFocusable(false);
				layout_rowS.setLayoutParams(layoutParamsS);
				layout_rowS.setOrientation(LinearLayout.HORIZONTAL);
				shandowPostItems = new PostItem[postSetting.getPost_column()];
				for (int j = 0; j < postSetting.getPost_column(); j++) {
					PostItem childrenS = new PostItem(context);
					childrenS.setVisibility(INVISIBLE);
					childrenS.isShandowView = true;
					childrenS.init(postSetting);
					childrenS.setFocusable(false);
					if (postSetting.getItemFocusDrawableId() > 0) {
						childrenS.setBackgroundResource(postSetting.getItemFocusDrawableId());// TODO
					}
					childrenS.setLayoutParams(layoutParams3);
					shandowPostItems[j] = childrenS;
					layout_rowS.addView(childrenS);
				}
				layout_parent.addView(layout_rowS);
			}
		}
		addView(layout_parent);
	}

	@Override
	public void initData(List<Object> datas, int selectedPosition) {
		super.initData(datas, selectedPosition);
		int size = 0;
		if (datas == null || (size = datas.size()) <= 0) {
			size = 0;
		}
		if (shandowPostItems==null) {
			return;
		}
		for (int i = 0; i < shandowPostItems.length; i++) {
			if (size == 0) {
				shandowPostItems[i].setVisibility(INVISIBLE);
			} else {
				int last = size - postSetting.getPost_column() * (postSetting.getPost_row()-1);// 最后一排个数
				if (last > i) {
					shandowPostItems[i].setVisibility(VISIBLE);
				} else {
					shandowPostItems[i].setVisibility(INVISIBLE);
				}
			}
		}
	}

	/**
	 * 及时选中
	 * 
	 * @param position
	 *            每页的位置序号
	 */
	// public void setSelectedPosition(int position) {
	// if (position < 0) {
	// return;
	// }
	// if (position > items.length) {
	// position = items.length - 1;
	// }
	// if (datas != null && datas.size() > 0 && position >= datas.size()) {
	// position = datas.size() - 1;
	// }
	// // selectedPos = position;
	// items[position].requestFocus();
	// items[position].setSelected(true);
	// items[position].setFocuesType(PostItem.TYPE_SELECTED);
	// invalidate();
	// }

	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (iNeedPageChange != null) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					focusPos = ((PostItem) v).getPosition();
				}
				if (postSetting.isVerticalScroll()) {// 竖向模式
					if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
							&& (focusPos >= (postSetting.getPost_num_apage() - postSetting.getPost_column()) || focusPos >= dataSize - 1)) {
						iNeedPageChange.pageNeedChange(PosterWallView.this, true, focusPos, keyCode, event);
						// return true;//返回true会照成在第一排和最后一排无法按上下
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && focusPos < postSetting.getPost_column()) {
						iNeedPageChange.pageNeedChange(PosterWallView.this, false, focusPos, keyCode, event);
					}
				} else {// 横向模式
						// L.debug(TAG + " focusPos=" + focusPos);
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& (((focusPos + 1) % postSetting.getPost_column() == 0) || focusPos >= dataSize - 1)) {
						iNeedPageChange.pageNeedChange(PosterWallView.this, true, focusPos, keyCode, event);
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && focusPos % postSetting.getPost_column() == 0) {
						iNeedPageChange.pageNeedChange(PosterWallView.this, false, focusPos, keyCode, event);
					}
				}
			}
			// 处理竖向滚动时候最后一排和第一排不能继续按
			if (postSetting != null && postSetting.getOnKeyListener() != null) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !postSetting.isLastRowFocusDown()
						&& isLastRowInVisible(focusPos) && dataSize < items.length) {
					postSetting.getOnKeyListener().onKey(v, keyCode, event);
					return true;// 最后一排按下返回TRUE
				}

			} else {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !postSetting.isLastRowFocusDown()
						&& isLastRowInVisible(focusPos) && dataSize < items.length) {
					return true;// 最后一排按下返回TRUE
				}
			}
			// 处理横向滚动时候最后一列和第一列不能继续按
			if (postSetting != null && postSetting.getOnKeyListener() != null) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !postSetting.isLastClumnFocusRight()
						&& isLastClumnInVisible(focusPos)) {
					postSetting.getOnKeyListener().onKey(v, keyCode, event);
					return true;// 最后一排按下返回TRUE
				}
				return postSetting.getOnKeyListener().onKey(v, keyCode, event);
			} else {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !postSetting.isLastClumnFocusRight()
						&& isLastClumnInVisible(focusPos)) {
					return true;// 最后一排按下返回TRUE
				}
			}
			if (postSetting.getOnKeyListener() != null) {
				return postSetting.getOnKeyListener().onKey(v, keyCode, event);
			}

			// if (!postSetting.isVerticalScroll()) {
			// int datapos = firstPosInDataList + focusPos;
			// // 翻页时候returnTRUE 解决横向滚动时候焦点丢失问题
			// if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && datapos >=
			// postSetting.getPost_num_apage() && focusPos %
			// postSetting.getPost_column() == 0) {
			// return true;// 往左按翻页
			// }
			// if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && (firstPosInDataList
			// + datas.size()) < posterLayoutView.getTotalItems()
			// && (focusPos + 1) % postSetting.getPost_column() == 0) {
			// L.debug("11111111111");
			// return true;// 往右按翻页
			// }
			// }
			return false;
		}
	};

	/**
	 * 是否是最后一行
	 * 
	 * @param position
	 *            位置
	 * @return
	 */
	private boolean isLastRowInVisible(int position) {
		int clumn = postSetting.getPost_column();
		int lastBegin = (dataSize % clumn == 0 ? (dataSize / clumn - 1) : dataSize / clumn) * clumn;
		return position >= lastBegin;
	}

	/**
	 * 是否是最后一行
	 * 
	 * @param position
	 *            位置
	 * @return
	 */
	private boolean isLastClumnInVisible(int position) {
		int clumn = postSetting.getPost_column();
		if ((position + 1) % clumn == 0 || position == dataSize - 1) {
			L.d("isLastClumnInVisible true " + clumn + " position " + position);
			return true;
		}
		L.d("isLastClumnInVisible false " + clumn + " position " + position);
		return false;
	}

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// L.debug(TAG + "onfocuschange " + hasFocus + " " + ((PostItem)
			// v).getPosition());
			((PostItem) v).setOnFocuesChange(hasFocus);
			if (postSetting != null && postSetting.getOnItemFocusChangeListener() != null) {
				postSetting.getOnItemFocusChangeListener().onItemFocuesChange(posterLayoutView, v, hasFocus,
						((PostItem) v).getPosition() + firstPosInDataList);
			}
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (postSetting != null && postSetting.getOnItemClickListener() != null) {
				postSetting.getOnItemClickListener().itemOnClick(posterLayoutView, v,
						((PostItem) v).getPosition() + firstPosInDataList);
			}
		}
	};

	private OnLongClickListener onLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (postSetting != null && postSetting.getOnItemLongClickListener() != null) {
				return postSetting.getOnItemLongClickListener().itemOnLongClick(posterLayoutView, v,
						((PostItem) v).getPosition() + firstPosInDataList);
			}
			return false;
		}
	};

	public PosterLayoutView getPosterLayoutView() {
		return posterLayoutView;
	}

	public void setPosterLayoutView(PosterLayoutView posterLayoutView) {
		this.posterLayoutView = posterLayoutView;
	}
}
