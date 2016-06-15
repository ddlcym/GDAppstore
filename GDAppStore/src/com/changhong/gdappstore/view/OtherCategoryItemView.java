package com.changhong.gdappstore.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BaseRelativeLayout;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class OtherCategoryItemView extends BaseRelativeLayout {

	private ImageView iv_icon;
	private TextView tv_name;

	public OtherCategoryItemView(Context context) {
		super(context);
		initView(null);
	}

	public OtherCategoryItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(attrs);
	}

	public OtherCategoryItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(attrs);
	}

	protected void initView(AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.item_othercategory, this);
		// if (attrs != null) {
		// TypedArray typedArray = context.obtainStyledAttributes(attrs,
		// R.styleable.PageItem);
		// itemtype = typedArray.getInt(R.styleable.PageItem_itemtype, 0);
		// }
		iv_icon = findView(R.id.iv_otc_icon);
		tv_name = findView(R.id.tv_otc_name);
	}

	/**
	 * 栏目推荐位
	 * 
	 * @param category
	 * @param iconId
	 *            图片临时采用写死
	 */
	public void setCategoryData(Category category, ImageLoadingListener imageLoadingListener) {
		if (category == null) {
			return;
		}
		if (!TextUtils.isEmpty(category.getIconFilePath())) {
			ImageLoadUtil.displayImgByMemoryDiscCache(category.getIconFilePath(), iv_icon,imageLoadingListener);
		}
		iv_icon.setVisibility(VISIBLE);
		tv_name.setText(TextUtils.isEmpty(category.getName()) ? "" : category.getName());
	}

	public void setItemSelected(boolean selected) {
		tv_name.setSelected(selected);
	}
}
