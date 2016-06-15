package com.changhong.gdappstore.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BasePageView;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class OtherCategoryView extends BasePageView implements OnFocusChangeListener, OnClickListener {

	/** 焦点缩放动画 */
	protected Animation animationbig, animationsmall;

	/** 外部回调点击监听器 */
	private OnClickListener onClickListener;
	/** 外部回调焦点监听器 */
	private OnFocusChangeListener onFocusChangeListener;
	/** 焦点框view */
	private ImageView ivFocues;

	private ImageView iv_shandow1, iv_shandow2, iv_shandow3, iv_shandow4, iv_shandow5;

	/** 应用推荐位ID */
	private int[] otcItemIds = { R.id.othercat_item1, R.id.othercat_item2, R.id.othercat_item3, R.id.othercat_item4,
			R.id.othercat_item5, R.id.othercat_item6, R.id.othercat_item7, R.id.othercat_item8, R.id.othercat_item9,
			R.id.othercat_item10 };
	/** 应用推荐位个数 */
	private int otcItemCount = 10;
	/** 应用推荐位view */
	private OtherCategoryItemView[] otcItemViews = new OtherCategoryItemView[otcItemCount];

	public OtherCategoryView(Context context) {
		super(context);
		init();
	}

	public OtherCategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OtherCategoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		animationbig = AnimationUtils.loadAnimation(context, R.anim.scale_big);
		animationsmall = AnimationUtils.loadAnimation(context, R.anim.scale_small);
		LayoutInflater.from(context).inflate(R.layout.view_othercategory, this);
		ivFocues = findView(R.id.iv_othercat_focues);
		ivFocues.setVisibility(INVISIBLE);
		for (int i = 0; i < otcItemCount; i++) {
			otcItemViews[i] = findView(otcItemIds[i]);
			otcItemViews[i].setFocusable(true);
			otcItemViews[i].setClickable(true);
			otcItemViews[i].setOnClickListener(this);
			otcItemViews[i].setOnFocusChangeListener(this);
		}
		iv_shandow1 = findView(R.id.iv_shandow1);
		iv_shandow2 = findView(R.id.iv_shandow2);
		iv_shandow3 = findView(R.id.iv_shandow3);
		iv_shandow4 = findView(R.id.iv_shandow4);
		iv_shandow5 = findView(R.id.iv_shandow5);
	}

	public void initData(final Category category) {
		if (category.getCategoyChildren() != null) {
			int size = category.getCategoyChildren().size();
			for (int i = 0; i < otcItemCount; i++) {
				if (i < size) {//这里可以用i和size比较是因为这里的栏目里面没有序号信息，就挨着放就是
					final Category childCategory = category.getCategoyChildren().get(i);
					otcItemViews[i].setCategoryData(childCategory, imageLoadingListener);
					otcItemViews[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (onClickListener != null) {
								onClickListener.onClick(v);
							}
							jumpToPostActivity(category.getId(), childCategory.getId());
						}
					});
				} else {
					otcItemViews[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (onClickListener != null) {
								onClickListener.onClick(v);
							}
							DialogUtil.showShortToast(context, context.getResources().getString(R.string.weipeizhi));
						}
					});
				}

			}
		}
		// setShandows();
	}

	@Override
	public void onClick(View v) {

		if (onClickListener != null) {
			onClickListener.onClick(v);
		}
//		if (NetworkUtils.ISNET_CONNECT) {
//		} else {
//			DialogUtil.showLongToast(context, context.getResources().getString(R.string.net_disconnected_pleasecheck));
//		}
	}

	/**
	 * 最上一排海报往上按默认焦点
	 * 
	 * @param id
	 */
	public void setNextFocuesUpId(int id) {
		otcItemViews[0].setNextFocusUpId(id);
		otcItemViews[2].setNextFocusUpId(id);
		otcItemViews[4].setNextFocusUpId(id);
		otcItemViews[6].setNextFocusUpId(id);
		otcItemViews[8].setNextFocusUpId(id);
	}

	public void setShandows() {
		iv_shandow1.setImageBitmap(Util.createImages(context, Util.convertViewToBitmap(otcItemViews[1]),
				shandowProportion2));
		iv_shandow2.setImageBitmap(Util.createImages(context, Util.convertViewToBitmap(otcItemViews[3]),
				shandowProportion2));
		iv_shandow3.setImageBitmap(Util.createImages(context, Util.convertViewToBitmap(otcItemViews[5]),
				shandowProportion2));
		iv_shandow4.setImageBitmap(Util.createImages(context, Util.convertViewToBitmap(otcItemViews[7]),
				shandowProportion2));
		iv_shandow5.setImageBitmap(Util.createImages(context, Util.convertViewToBitmap(otcItemViews[9]),
				shandowProportion2));
	}

	/**
	 * set the item view in postItemViews request focus by position .
	 * 
	 * @param position
	 */
	public void setOtcItemFocuesByPos(int position) {
		if (otcItemViews != null && position >= 0 && position < otcItemViews.length && otcItemViews[position] != null) {
			otcItemViews[position].requestFocus();
		}
	}

	public OtherCategoryItemView[] getOtcItemViews() {
		return otcItemViews;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (onFocusChangeListener != null) {
			onFocusChangeListener.onFocusChange(v, hasFocus);
		}
		if (hasFocus) {
			int viewId = v.getId();
			currentFocuesId = viewId;
			RelativeLayout.LayoutParams mlayout = new RelativeLayout.LayoutParams(100, 100);
			// RelativeLayout.LayoutParams tmplayout = (LayoutParams) v
			// .getLayoutParams();
			RelativeLayout.LayoutParams tmplayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			tmplayout.leftMargin = v.getLeft();
			tmplayout.topMargin = v.getTop();
			tmplayout.width = v.getWidth();
			tmplayout.height = v.getHeight();
			if (viewId == R.id.othercat_item4 || viewId == R.id.othercat_item5 || viewId == R.id.othercat_item8
					|| viewId == R.id.othercat_item9) {
				mlayout.leftMargin = (int) (tmplayout.leftMargin + 2 - tmplayout.width * 0.1);
				mlayout.topMargin = (int) (tmplayout.topMargin + 5 - tmplayout.height * 0.1);
				mlayout.width = (int) (tmplayout.width + 8 + (tmplayout.width * 0.1));
				mlayout.height = (int) (tmplayout.height + 7 + (tmplayout.height * 0.1));
			} else {
				mlayout.leftMargin = (int) (tmplayout.leftMargin + 14 - tmplayout.width * 0.1);
				mlayout.topMargin = (int) (tmplayout.topMargin + 5 - tmplayout.height * 0.1);
				mlayout.width = (int) (tmplayout.width + 0 + (tmplayout.width * 0.1));
				mlayout.height = (int) (tmplayout.height + 7 + (tmplayout.height * 0.1));
			}
			ivFocues.setBackgroundResource(R.drawable.img_focues_mainpost);
			ivFocues.setLayoutParams(mlayout);
			ivFocues.setVisibility(View.VISIBLE);

			v.startAnimation(animationbig);
			ivFocues.startAnimation(animationbig);
			v.bringToFront();
			ivFocues.bringToFront();
		} else {
			v.startAnimation(animationsmall);
			ivFocues.startAnimation(animationsmall);
			v.clearAnimation();
			ivFocues.clearAnimation();
			ivFocues.setVisibility(View.INVISIBLE);
		}
		((OtherCategoryItemView) v).setItemSelected(hasFocus);
	}

	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			// setShandows();
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			// setShandows();
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}
	};
}
