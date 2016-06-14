package com.changhong.gdappstore.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BasePageView;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.changhong.gdappstore.util.Util;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 专题页面
 * 
 * @author wangxiufeng
 * 
 */
public class TopicView extends BasePageView implements OnFocusChangeListener {

	/** 焦点缩放动画 */
	protected Animation animationbig, animationsmall;

	/** 外部回调点击监听器 */
	private OnClickListener onClickListener;
	/** 外部回调焦点监听器 */
	private OnFocusChangeListener onFocusChangeListener;

	private RelativeLayout rl_topics;
	
	private HorizontalScrollView sc_topics;
	/** itemview根据这个数一次增加 */
	public static final int TOPICITEMVIEW_BASEID = 20001;

	public TopicView(Context context) {
		super(context);
		init();
	}

	public TopicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TopicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		animationbig = AnimationUtils.loadAnimation(context, R.anim.scale_big);
		animationsmall = AnimationUtils.loadAnimation(context, R.anim.scale_small);
		LayoutInflater.from(context).inflate(R.layout.view_topoic, this);
		sc_topics=findView(R.id.hc_topics);
		rl_topics = findView(R.id.rl_topics);

	}

	public void initData(final Category category) {
		if (category.getCategoyChildren() != null) {
			int size = category.getCategoyChildren().size();
			setChildViews(size);
			setChildData(category.getCategoyChildren());
		}

	}

	private void setChildViews(int size) {
		int curChildCount = rl_topics.getChildCount();
		if (size == curChildCount) {
			return;
		}
		// 移除多余的
		if (size < curChildCount) {
			for (int i = rl_topics.getChildCount(); i > size; i--) {
				rl_topics.removeViewAt(rl_topics.getChildCount() - 1);
			}
		}
		// 添加不够的
		for (int i = curChildCount; i < size; i++) {
			View view = LayoutInflater.from(context).inflate(R.layout.item_topic, null);
			view.setId(TOPICITEMVIEW_BASEID + i);
			RelativeLayout rl_content = (RelativeLayout) view.findViewById(R.id.rl_topiccontent);
			RelativeLayout rl_contentin = (RelativeLayout) view.findViewById(R.id.rl_topiccontentin);
			rl_contentin.setBackgroundColor(Color.TRANSPARENT);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setOnFocusChangeListener(this);
			if (i == 0) {// 第一个无参考条件，直接添加
				rl_topics.addView(view);
				continue;
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			layoutParams.topMargin = (int) context.getResources().getDimension(R.dimen.item_topic_margintop);
			layoutParams.leftMargin = 0;
			if (i == 1) {
				View preView = rl_topics.getChildAt(i - 1);
				layoutParams.addRule(RelativeLayout.BELOW, preView.getId());// 设置item3在
				layoutParams.addRule(RelativeLayout.ALIGN_LEFT, preView.getId());// 设置item3在
				view.setLayoutParams(layoutParams);
				rl_topics.addView(view);
			} else {
				layoutParams.topMargin = 0;
				layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen.item_topic_marginleft);
				View preBeforView = rl_topics.getChildAt(i - 2);
				layoutParams.addRule(RelativeLayout.RIGHT_OF, preBeforView.getId());// 设置item3在
				layoutParams.addRule(RelativeLayout.ALIGN_TOP, preBeforView.getId());// 设置item3在
				view.setLayoutParams(layoutParams);
				rl_topics.addView(view);
			}
		}
	}

	private void setChildData(List<Category> childrens) {
		int childrenSize = childrens.size();
		int[] tv_bgs = { R.color.otc_blue, R.color.otc_green, R.color.otc_lightred, R.color.otc_orange,
				R.color.otc_purple, R.color.otc_yellow };
		int bgpos = 0;
		for (int i = 0; i < rl_topics.getChildCount(); i++) {
			if (i >= childrenSize) {
				return;
			}
			final Category category = childrens.get(i);
			if (category == null) {
				continue;
			}
			View view = rl_topics.getChildAt(i);
			ImageView iv_post = (ImageView) view.findViewById(R.id.iv_topic_icon);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_topic_name);
			final ImageView iv_shandow = (ImageView) view.findViewById(R.id.iv_topic_shandow);
			final RelativeLayout rl_contentin = (RelativeLayout) view.findViewById(R.id.rl_topiccontentin);
			tv_name.setText(TextUtils.isEmpty(category.getName()) ? "" : category.getName());
			if (bgpos >= tv_bgs.length) {
				bgpos = 0;
			}
			tv_name.setBackgroundColor(context.getResources().getColor(tv_bgs[bgpos]));
			bgpos++;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onClickListener != null) {
						onClickListener.onClick(v);
					}
					jumpToPostActivity(Config.ID_ZHUANTI, category.getId());
				}
			});
			if (!TextUtils.isEmpty(category.getIconFilePath())) {
				if (i % 2 == 0) {
					ImageLoadUtil.displayImgByMemoryDiscCache(category.getIconFilePath(), iv_post, null);
					iv_shandow.setVisibility(GONE);
				} else {
					ImageLoadUtil.displayImgByMemoryDiscCache(category.getIconFilePath(), iv_post,
							new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String imageUri, View view) {
								}

								@Override
								public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
									iv_shandow.setImageBitmap(Util.createImages(context,
											Util.convertViewToBitmap(rl_contentin), shandowProportion2));
									iv_shandow.setVisibility(VISIBLE);
								}

								@Override
								public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
									iv_shandow.setImageBitmap(Util.createImages(context,
											Util.convertViewToBitmap(rl_contentin), shandowProportion2));
									iv_shandow.setVisibility(VISIBLE);
								}

								@Override
								public void onLoadingCancelled(String imageUri, View view) {
									iv_shandow.setImageBitmap(Util.createImages(context,
											Util.convertViewToBitmap(rl_contentin), shandowProportion2));
									iv_shandow.setVisibility(VISIBLE);
								}
							});
				}
			}

		}
	}

	/**
	 * set the item view in postItemViews request focus by position .
	 * 
	 * @param position
	 */
	public void setOtcItemFocuesByPos(int position) {
		if (rl_topics != null && position >= 0 && position < rl_topics.getChildCount()
				&& rl_topics.getChildAt(position) != null) {
			if (getChildViewAt(position) != null) {
				getChildViewAt(position).requestFocus();
			}

		}
	}

	/**
	 * 获取某个序号的itemView。 比对id而不适用getChildAt()方法的原因是该方法获取的itemView不对
	 * 
	 * @param position
	 * @return
	 */
	public View getChildViewAt(int position) {
		if (rl_topics == null || rl_topics.getChildCount() <= position) {
			return null;
		}
		for (int i = 0; i < rl_topics.getChildCount(); i++) {
			if (rl_topics.getChildAt(i).getId() == TOPICITEMVIEW_BASEID + position) {
				return rl_topics.getChildAt(i);
			}
		}
		return null;
	}

	/**
	 * 顶排推荐位按上默认焦点位置
	 * 
	 * @param id
	 */
	public void setNextFocuesUpId(int id) {
		if (rl_topics != null && rl_topics.getChildCount() > 0) {
			for (int i = 0; i < rl_topics.getChildCount(); i++) {
				if (i % 2 == 0 && rl_topics.getChildAt(i) != null) {
					rl_topics.getChildAt(i).setNextFocusUpId(id);
				}
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (onFocusChangeListener != null) {
			onFocusChangeListener.onFocusChange(v, hasFocus);
		}
		RelativeLayout rl_content = (RelativeLayout) v.findViewById(R.id.rl_topiccontent);
		RelativeLayout rl_contentin = (RelativeLayout) v.findViewById(R.id.rl_topiccontentin);
		TextView tv_name=(TextView)v.findViewById(R.id.tv_topic_name);
		rl_content.setBackgroundColor(Color.TRANSPARENT);
		if (hasFocus) {
			tv_name.setSelected(true);
			currentFocuesId = v.getId();
			rl_contentin.setBackgroundResource(R.drawable.img_focues_mainpost);
			rl_content.startAnimation(animationbig);
			v.bringToFront();
			if (v.getId()==TOPICITEMVIEW_BASEID||v.getId()==TOPICITEMVIEW_BASEID+1) {
				sc_topics.smoothScrollTo(0, 0);
			}
		} else {
			tv_name.setSelected(false);
			rl_contentin.setBackgroundColor(Color.TRANSPARENT);
			rl_content.startAnimation(animationsmall);
			rl_content.clearAnimation();
		}
	}
}
