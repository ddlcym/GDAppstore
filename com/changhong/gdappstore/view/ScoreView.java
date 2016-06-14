package com.changhong.gdappstore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BaseRelativeLayout;

/**
 * 频分星星显示view，可根据自己需要扩展。
 * 
 * @author wangxiufeng
 * 
 */
public class ScoreView extends BaseRelativeLayout {
	/** 存放星星布局 */
	private LinearLayout ll_scrollview;
	/** 星星总数 */
	private int totalStarCount = 5;
	/** 满颗星 */
	public static final int SCORE_FULL = 2;
	/** 半颗星 */
	public static final int SCORE_HALF = 1;
	/** 空星 */
	public static final int SCORE_NONE = 0;

	public ScoreView(Context context) {
		super(context);
		initView();
	}

	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ScoreView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public void initView() {
		ll_scrollview = new LinearLayout(context);
		ll_scrollview.setOrientation(LinearLayout.HORIZONTAL);
		ll_scrollview.removeAllViews();
		for (int i = 0; i < totalStarCount; i++) {
			ll_scrollview.addView(getImageView(SCORE_NONE));
		}
		addView(ll_scrollview);
	}

	/**
	 * 按照5颗星5分来计算，1分为一颗星
	 * 
	 * @param score
	 */
	public void setScoreBy5Total(float score) {
		if (score < 0) {
			return;
		}
		int fullstar = (int) Math.floor(score);
		float decimal = score - fullstar;
		
		for (int i = 0; i < ll_scrollview.getChildCount(); i++) {
			ImageView imageView = (ImageView) ll_scrollview.getChildAt(i);
			if (imageView == null) {
				continue;
			}
			if ((i + 1) <= fullstar) {
				updateStarViewState(imageView, SCORE_FULL);
			} else if (decimal > 0 && i == fullstar) {
				updateStarViewState(imageView, SCORE_HALF);
			} else {
				updateStarViewState(imageView, SCORE_NONE);
			}
		}
		ll_scrollview.invalidate();
	}
	/**
	 * 按照5颗星10分来计算，1分为半颗星
	 * 
	 * @param score
	 */
	public void setScoreBy10Total(int score) {
		if (score < 0) {
			score = 0;
		}
		int full = score / 2;
		boolean hashalf = (score % 2) != 0;
		for (int i = 0; i < ll_scrollview.getChildCount(); i++) {
			ImageView imageView = (ImageView) ll_scrollview.getChildAt(i);
			if (imageView == null) {
				continue;
			}
			if (i < full) {
				updateStarViewState(imageView, SCORE_FULL);
			} else if (hashalf && i == full) {
				updateStarViewState(imageView, SCORE_HALF);
			} else {
				updateStarViewState(imageView, SCORE_NONE);
			}
		}
		ll_scrollview.invalidate();
	}

	/**
	 * 获取imageView对象
	 * 
	 * @param state
	 *            状态1为半颗星，2为满颗星，3为空心星
	 * @return
	 */
	private ImageView getImageView(int state) {
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.FIT_XY);
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(25, 25);
		imageView.setLayoutParams(layoutParams);
		updateStarViewState(imageView, state);
		return imageView;
	}
	/**
	 * 更改imageview星星图片状态
	 * @param imageView
	 * @param state 状态
	 */
	private void updateStarViewState(ImageView imageView, int state) {
		if (state == SCORE_HALF) {
			imageView.setImageResource(R.drawable.star_half);
		} else if (state == SCORE_FULL) {
			imageView.setImageResource(R.drawable.star_full);
		} else {
			imageView.setImageResource(R.drawable.star_none);
		}
		imageView.invalidate();
	}

}
