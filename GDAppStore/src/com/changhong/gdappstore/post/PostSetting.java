package com.changhong.gdappstore.post;

import android.view.View.OnKeyListener;

import com.post.view.listener.IPosteDateListener;
import com.post.view.listener.Listener.IItemOnClickListener;
import com.post.view.listener.Listener.IItemOnFocuesChangeListener;
import com.post.view.listener.Listener.IItemOnLongClickListener;

/**
 * 
 * 
 * <p>
 * ---海报墙设置，监听器没有可以设为空，行列设为负数则使用默认值--- 如果需要海报墙使用自己的设置，要先调用设置设置方法，在调用设置数据
 * 构造函数里面没有列举所有的设置参数
 * ----------------------------------------------------------------------- <br>
 */
public class PostSetting {
	/** 海报墙的行数 */
	private int post_row = 2;
	/** 海报墙的列数 */
	private int post_column = 6;
	/** 显示的页数个数 */
	private float visibleRow = 1.2f;
	/** 显示的页数个数 */
	private float visibleClumn = 1.0f;
	/** item焦点selector **/
	private int itemFocusDrawableId = -1;
	/** 海报墙一页的海报总数 */
	private int post_num_apage = post_row * post_column;
	/** 数据变化监听器 **/
	private IPosteDateListener iPosteDateListener;
	/** item焦点 */
	private IItemOnFocuesChangeListener onItemFocusChangeListener;
	/** item点击 */
	private IItemOnClickListener onItemClickListener;
	/** item长按 */
	private IItemOnLongClickListener onItemLongClickListener;
	/** 系统按键事件 */
	private OnKeyListener onKeyListener;
	/** 海报item的margin距离 **/
	private int itemLeftMargin = 5;
	private int itemRightMargin = 5;
	private int itemTopMargin = 5;
	private int itemBottomMargin = 5;
	/** 是否在海报墙显示出来时候让第一个item默认焦点 */
	private boolean isFristItemFocus = true;
	/** 在第一排按上焦点是否会移动，TRUE会移动，FALSE 在原位置 */
	private boolean isFirstRowFocusUp = false;
	/** 在最后一排按下焦点是否会移动，TRUE会移动，FALSE 在原位置 */
	private boolean isLastRowFocusDown = false;
	/** 在第一排按上焦点是否会移动，TRUE会移动，FALSE 在原位置 */
	private boolean isFirstClumnFocusLeft = true;
	/** 在最后一排按下焦点是否会移动，TRUE会移动，FALSE 在原位置 */
	private boolean isLastClumnFocusRight = true;
	/** 向左下一个焦点 */
	private int nextFocuesLeftId = -1;
	/** 翻页后默认焦点位置: false表示翻页后焦点在原位置，true表示翻页后焦点在默认获取位置 */
	private boolean isFocusPosBySystem = true;
	/** 翻页滚动动画时间，默认500，建议使用默认值，界面内容多的时间改太小了会出现闪动 */
	private int scrollDuration = 500;
	/** 滚动方向， */
	private boolean isVerticalScroll = true;
	/** 每页四周padding */
	private int pagePaddingLeft = 0, pagePaddingRight = 0, pagePaddingBottom = 0, pagePaddingTop = 0;
	/**是否显示倒影，暂时只支持静态图片*/
	private boolean isShowShandow=false;
	// 项目私用 //TODO 适用于不同的海报墙根据此参数来判断选择使用不同的item布局方式
	private int posttype = 0;
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_NATIVEAPP = 1;
	public static final int TYPE_SEARCHAPP = 2;

	public PostSetting() {

	}

	public PostSetting(int post_row, int post_column) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
	}

	public PostSetting(int post_row, int post_column, float visibleRow, int itemFocusDrawableId) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
		this.visibleRow = visibleRow;
		this.itemFocusDrawableId = itemFocusDrawableId;
	}

	public PostSetting(int post_row, int post_column, int itemFocusDrawableId, IPosteDateListener iPosteDateListener) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
		this.itemFocusDrawableId = itemFocusDrawableId;
		this.iPosteDateListener = iPosteDateListener;
	}

	public PostSetting(int post_row, int post_column, int itemFocusDrawableId, IPosteDateListener iPosteDateListener,
			IItemOnClickListener onItemClickListener) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
		this.itemFocusDrawableId = itemFocusDrawableId;
		this.iPosteDateListener = iPosteDateListener;
		this.onItemClickListener = onItemClickListener;
	}

	public PostSetting(int post_row, int post_column, int itemFocusDrawableId, IPosteDateListener iPosteDateListener,
			IItemOnFocuesChangeListener onItemFocusChangeListener, IItemOnClickListener onItemClickListener) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
		this.itemFocusDrawableId = itemFocusDrawableId;
		this.iPosteDateListener = iPosteDateListener;
		this.onItemFocusChangeListener = onItemFocusChangeListener;
		this.onItemClickListener = onItemClickListener;
	}

	public PostSetting(int post_row, int post_column, int itemFocusDrawableId, IPosteDateListener iPosteDateListener,
			IItemOnFocuesChangeListener onItemFocusChangeListener, IItemOnClickListener onItemClickListener,
			IItemOnLongClickListener onItemLongClickListener, OnKeyListener onKeyListener) {
		super();
		setPost_row(post_row);
		setPost_column(post_column);
		this.itemFocusDrawableId = itemFocusDrawableId;
		this.iPosteDateListener = iPosteDateListener;
		this.onItemFocusChangeListener = onItemFocusChangeListener;
		this.onItemClickListener = onItemClickListener;
		this.onItemLongClickListener = onItemLongClickListener;
		this.onKeyListener = onKeyListener;
	}

	public void setMargins(int left, int right, int top, int bottom) {
		this.itemLeftMargin = left;
		this.itemRightMargin = right;
		this.itemTopMargin = top;
		this.itemBottomMargin = bottom;
	}

	public int getPost_row() {
		return post_row;
	}

	public void setPost_row(int post_row) {
		if (post_row < 0) {
			return;
		}
		setPost_num_apage(post_row, post_column);
		this.post_row = post_row;
	}

	public int getPost_column() {
		return post_column;
	}

	public void setPost_column(int post_column) {
		if (post_column < 0) {
			return;
		}
		setPost_num_apage(post_row, post_column);
		this.post_column = post_column;
	}

	public int getPost_num_apage() {
		return post_num_apage;
	}

	private void setPost_num_apage(int row, int clumn) {
		if (row < 0 || clumn < 0) {
			return;
		}
		this.post_num_apage = row * clumn;
	}

	public IPosteDateListener getiPosteDateListener() {
		return iPosteDateListener;
	}

	public void setiPosteDateListener(IPosteDateListener iPosteDateListener) {
		this.iPosteDateListener = iPosteDateListener;
	}

	public IItemOnFocuesChangeListener getOnItemFocusChangeListener() {
		return onItemFocusChangeListener;
	}

	public void setOnItemFocusChangeListener(IItemOnFocuesChangeListener onItemFocusChangeListener) {
		this.onItemFocusChangeListener = onItemFocusChangeListener;
	}

	public IItemOnClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(IItemOnClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public IItemOnLongClickListener getOnItemLongClickListener() {
		return onItemLongClickListener;
	}

	public void setOnItemLongClickListener(IItemOnLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public OnKeyListener getOnKeyListener() {
		return onKeyListener;
	}

	public void setOnKeyListener(OnKeyListener onKeyListener) {
		this.onKeyListener = onKeyListener;
	}

	public int getItemLeftMargin() {
		return itemLeftMargin;
	}

	public void setItemLeftMargin(int itemLeftMargin) {
		this.itemLeftMargin = itemLeftMargin;
	}

	public int getItemRightMargin() {
		return itemRightMargin;
	}

	public void setItemRightMargin(int itemRightMargin) {
		this.itemRightMargin = itemRightMargin;
	}

	public int getItemTopMargin() {
		return itemTopMargin;
	}

	public void setItemTopMargin(int itemTopMargin) {
		this.itemTopMargin = itemTopMargin;
	}

	public int getItemBottomMargin() {
		return itemBottomMargin;
	}

	public void setItemBottomMargin(int itemBottomMargin) {
		this.itemBottomMargin = itemBottomMargin;
	}

	public int getItemFocusDrawableId() {
		return itemFocusDrawableId;
	}

	public void setItemFocusDrawableId(int itemFocusDrawableId) {
		this.itemFocusDrawableId = itemFocusDrawableId;
	}

	public int getPosttype() {
		return posttype;
	}

	public void setPosttype(int posttype) {
		this.posttype = posttype;
	}

	public boolean isFristItemFocus() {
		return isFristItemFocus;
	}

	public void setFristItemFocus(boolean isFristItemFocus) {
		this.isFristItemFocus = isFristItemFocus;
	}

	public float getVisibleRow() {
		return visibleRow;
	}

	public void setVisibleRow(float visibleRow) {
		this.visibleRow = visibleRow;
	}

	public boolean isFirstRowFocusUp() {
		return isFirstRowFocusUp;
	}

	public void setFirstRowFocusUp(boolean isFirstRowFocusUp) {
		this.isFirstRowFocusUp = isFirstRowFocusUp;
	}

	public boolean isLastRowFocusDown() {
		return isLastRowFocusDown;
	}

	public void setLastRowFocusDown(boolean isLastRowFocusDown) {
		this.isLastRowFocusDown = isLastRowFocusDown;
	}

	public int getNextFocuesLeftId() {
		return nextFocuesLeftId;
	}

	public void setNextFocuesLeftId(int nextFocuesLeftId) {
		this.nextFocuesLeftId = nextFocuesLeftId;
	}

	public boolean isFocusPosBySystem() {
		return isFocusPosBySystem;
	}

	public void setFocusPosBySystem(boolean isFocusPosBySystem) {
		this.isFocusPosBySystem = isFocusPosBySystem;
	}

	public int getScrollDuration() {
		return scrollDuration;
	}

	public void setScrollDuration(int scrollDuration) {
		this.scrollDuration = scrollDuration;
	}

	public boolean isVerticalScroll() {
		return isVerticalScroll;
	}

	public void setVerticalScroll(boolean isVerticalScroll) {
		this.isVerticalScroll = isVerticalScroll;
	}

	public boolean isFirstClumnFocusLeft() {
		return isFirstClumnFocusLeft;
	}

	public void setFirstClumnFocusLeft(boolean isFirstClumnFocusLeft) {
		this.isFirstClumnFocusLeft = isFirstClumnFocusLeft;
	}

	public boolean isLastClumnFocusRight() {
		return isLastClumnFocusRight;
	}

	public void setLastClumnFocusRight(boolean isLastClumnFocusRight) {
		this.isLastClumnFocusRight = isLastClumnFocusRight;
	}

	public float getVisibleClumn() {
		return visibleClumn;
	}

	public void setVisibleClumn(float visibleClumn) {
		this.visibleClumn = visibleClumn;
	}

	public void setPagePadding(int left, int top, int right, int bottom) {
		this.pagePaddingLeft = left;
		this.pagePaddingTop = top;
		this.pagePaddingRight = right;
		this.pagePaddingBottom = bottom;
	}

	public int getPagePaddingLeft() {
		return pagePaddingLeft;
	}

	public void setPagePaddingLeft(int pagePaddingLeft) {
		this.pagePaddingLeft = pagePaddingLeft;
	}

	public int getPagePaddingRight() {
		return pagePaddingRight;
	}

	public void setPagePaddingRight(int pagePaddingRight) {
		this.pagePaddingRight = pagePaddingRight;
	}

	public int getPagePaddingBottom() {
		return pagePaddingBottom;
	}

	public void setPagePaddingBottom(int pagePaddingBottom) {
		this.pagePaddingBottom = pagePaddingBottom;
	}

	public int getPagePaddingTop() {
		return pagePaddingTop;
	}

	public void setPagePaddingTop(int pagePaddingTop) {
		this.pagePaddingTop = pagePaddingTop;
	}

	public boolean isShowShandow() {
		return isShowShandow;
	}

	public void setShowShandow(boolean isShowShandow) {
		this.isShowShandow = isShowShandow;
	}

	
}
