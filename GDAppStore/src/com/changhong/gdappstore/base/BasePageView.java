package com.changhong.gdappstore.base;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.activity.DetailActivity;
import com.changhong.gdappstore.activity.PostActivity;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.view.PageItemView;

/**
 * 基类pageView
 * 
 * @author wangxiufeng
 * 
 */
public class BasePageView extends BaseRelativeLayout {

	/** 应用推荐位ID */
	protected int[] postItemIds = { R.id.homepage_item1, R.id.homepage_item2, R.id.homepage_item3, R.id.homepage_item4,
			R.id.homepage_item5, R.id.homepage_item6, R.id.homepage_item7, R.id.homepage_item8, R.id.homepage_item9,
			R.id.homepage_item10, R.id.homepage_item11, R.id.homepage_item12 };
	/** 栏目推荐位ID */
	protected int[] categroyItemIds = { R.id.homepage_itema1, R.id.homepage_itema2, R.id.homepage_itema3,
			R.id.homepage_itema4 };
	/** 应用推荐位个数 */
	protected  int postItemCount = 12;
	/** 应用推荐位view */
	protected PageItemView[] postItemViews = new PageItemView[postItemCount];
	/** 栏目推荐位view */
	protected PageItemView[] categoryItemViews = new PageItemView[4];
	/** 焦点缩放动画 */
	protected Animation animationbig, animationsmall;
	/** 当前焦点位置 */
	public int currentFocuesId;
	
	protected float shandowProportion1=0.6f,shandowProportion2=0.4f;

	protected final int bigLeftMar_add =23, bigTopMar_add = 6, bigWidth_add = -10, bigHeight_add = 6;
	protected final int horLeftMar_add = 10, horTopMar_add = -3, horWidth_add = 3, horHeight_add = 13;
	protected final int smallLeftMar_add = 7, smallTopMar_add = 0, smallWidth_add = 2, smallHeight_add = 7;

	protected int pageIndex=0;
	/**普通首页类型标识*/
	public static final int TAG_HOMEPAGE=1;
	/**专题类型标识*/
	public static final int TAG_TOPIC=2;
	/**页面类型标识*/
	protected int pageTag=TAG_HOMEPAGE;

	public BasePageView(Context context) {
		super(context);
		initBaseView();
	}

	public BasePageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBaseView();
	}

	public BasePageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBaseView();
	}

	private void initBaseView() {
		animationbig = AnimationUtils.loadAnimation(context, R.anim.scale_big);
		animationsmall = AnimationUtils.loadAnimation(context, R.anim.scale_small);
	}

	/**
	 * set the item view in categoryItemViews request focus by position .
	 * 
	 * @param position
	 */
	public void setCategoryItemFocuesByPos(int position) {
		if (categoryItemViews != null && position >= 0 && position < categoryItemViews.length
				&& categoryItemViews[position] != null) {
			categoryItemViews[position].requestFocus();
		}
	}

	/**
	 * set the item view in postItemViews request focus by position .
	 * 
	 * @param position
	 */
	public void setPostItemFocuesByPos(int position) {
		if (postItemViews != null && position >= 0 && position < postItemViews.length
				&& postItemViews[position] != null) {
			postItemViews[position].requestFocus();
		}
	}

	/**
	 * 跳转到海报墙页面
	 * 
	 * @param parentCategoryId
	 *            父栏目id
	 * @param currentCategoryId
	 *            子栏目id
	 */
	protected void jumpToPostActivity(int parentCategoryId, int currentCategoryId) {
		if (currentCategoryId <= 0) {
			DialogUtil.showShortToast(context, context.getResources().getString(R.string.weipeizhi));
			return;
		}
//		if (!NetworkUtils.ISNET_CONNECT) {
//			DialogUtil.showShortToast(context, context.getString(R.string.net_disconnected_pleasecheck));
//			return;
//		}
		Intent intent = new Intent(context, PostActivity.class);
		intent.putExtra(Config.KEY_PARENT_CATEGORYID, parentCategoryId);
		intent.putExtra(Config.KEY_CURRENT_CATEGORYID, currentCategoryId);
		context.startActivity(intent);
	}

	/**
	 * 跳转到应用详情页面
	 * 
	 * @param appid
	 */
	protected void jumpToDetailActivity(int appid) {
		if (appid < 0) {
			DialogUtil.showShortToast(context,context.getResources().getString(R.string.weipeizhi));
			return;
		}
//		if (!NetworkUtils.ISNET_CONNECT) {
//			DialogUtil.showShortToast(context, context.getString(R.string.net_disconnected_pleasecheck));
//			return;
//		}
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra(Config.KEY_APPID, appid);
		context.startActivity(intent);
	}

	public int[] getCategroyItemIds() {
		return categroyItemIds;
	}

	public PageItemView[] getCategoryItemViews() {
		return categoryItemViews;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageTag() {
		return pageTag;
	}

	public void setPageTag(int pageTag) {
		this.pageTag = pageTag;
	}
	
}
