package com.changhong.gdappstore.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BasePageView;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.util.L;

public class UserMayLikeView extends BasePageView implements OnFocusChangeListener, OnClickListener {
	/** 外部回调点击监听器 */
	private OnClickListener onClickListener;
	/** 外部回调焦点监听器 */
	private OnFocusChangeListener onFocusChangeListener;
	/** 焦点框view */
	private ImageView ivFocues;
	
	private View curFocuesView;

	public UserMayLikeView(Context context) {
		super(context);
		initView();
	}

	public UserMayLikeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public UserMayLikeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	protected void initView() {
		postItemCount = 8;
		postItemIds = new int[postItemCount];
		postItemIds[0] = R.id.usermaylike_item0;
		postItemIds[1] = R.id.usermaylike_item1;
		postItemIds[2] = R.id.usermaylike_item2;
		postItemIds[3] = R.id.usermaylike_item3;
		postItemIds[4] = R.id.usermaylike_item4;
		postItemIds[5] = R.id.usermaylike_item5;
		postItemIds[6] = R.id.usermaylike_item6;
		postItemIds[7] = R.id.usermaylike_item7;
		postItemViews = new PageItemView[postItemCount];
		View rootView = LayoutInflater.from(context).inflate(R.layout.view_usermaylike, this);
		ivFocues = findView(R.id.iv_detail_focues);
		ivFocues.setVisibility(INVISIBLE);
		for (int i = 0; i < postItemCount; i++) {
			postItemViews[i] = findView(postItemIds[i]);
			postItemViews[i].setFocusable(true);
			postItemViews[i].setClickable(true);
			postItemViews[i].setOnFocusChangeListener(this);
			postItemViews[i].setOnClickListener(this);
			postItemViews[i].setVisibility(INVISIBLE);
		}
	}

	public void initData(List<App> apps) {
		int appsSize=0;
		if (apps != null && apps.size() > 0) {
			 appsSize=apps.size();
		}
		for (int i = 0; i < postItemCount; i++) {
			if (i<appsSize) {
				postItemViews[i].setAppData(apps.get(i));
				postItemViews[i].setTag(apps.get(i));
				postItemViews[i].setVisibility(VISIBLE);
			}else {
				postItemViews[i].setVisibility(INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (onClickListener != null) {
			onClickListener.onClick(v);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (onFocusChangeListener != null) {
			onFocusChangeListener.onFocusChange(v, hasFocus);
		}
		if (hasFocus) {
			L.d(TAG + "jingpinview onfocueschange " + v.getId());
			currentFocuesId = v.getId();
			curFocuesView=v;
//			RelativeLayout.LayoutParams mlayout = new RelativeLayout.LayoutParams(100, 100);
//			// RelativeLayout.LayoutParams tmplayout = (LayoutParams) v
//			// .getLayoutParams();
//			RelativeLayout.LayoutParams tmplayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			tmplayout.leftMargin = v.getLeft();
//			tmplayout.topMargin = v.getTop();
//			tmplayout.width = v.getWidth();
//			tmplayout.height = v.getHeight();
//			mlayout.leftMargin = tmplayout.leftMargin - 15 - tmplayout.width / 20;
//			mlayout.topMargin = tmplayout.topMargin - 15 - tmplayout.height / 20;
//			mlayout.width = tmplayout.width + 23 + (tmplayout.width / 10);
//			mlayout.height = tmplayout.height + 21 + (tmplayout.height / 10);
//			ivFocues.setBackgroundResource(R.drawable.focues_post);
//			ivFocues.setLayoutParams(mlayout);
//			ivFocues.setVisibility(View.VISIBLE);
			v.startAnimation(animationbig);
			ivFocues.startAnimation(animationbig);
			v.bringToFront();
			ivFocues.bringToFront();
			((PageItemView) v).setItemSelected(true);
			((PageItemView) v).setSelected(true);
		} else {
			v.startAnimation(animationsmall);
			ivFocues.startAnimation(animationsmall);
			v.clearAnimation();
			ivFocues.clearAnimation();
			ivFocues.setVisibility(View.INVISIBLE);
			((PageItemView) v).setItemSelected(false);
			((PageItemView) v).setSelected(false);
		}
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
		this.onFocusChangeListener = onFocusChangeListener;
	}

	public View getCurFocuesView() {
		return curFocuesView;
	}

	public void setCurFocuesView(View curFocuesView) {
		this.curFocuesView = curFocuesView;
	}
	
	

}
