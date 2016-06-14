package com.changhong.gdappstore.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
/**
 * 基类RelativeLayout
 * @author wangxiufeng
 *
 */
public abstract class BaseRelativeLayout extends RelativeLayout {
	protected static final String TAG="myview";
	protected Context context;

	public BaseRelativeLayout(Context context) {
		super(context);
		this.context = context;
	}

	public BaseRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}


	protected <T>T findView(int id) {
		return (T)findViewById(id);
	}
}
