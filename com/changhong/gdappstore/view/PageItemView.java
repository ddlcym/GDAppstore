package com.changhong.gdappstore.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BaseRelativeLayout;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.model.PageApp;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 推荐位海报
 * 
 * @author wangxiufeng
 * 
 */
public class PageItemView extends BaseRelativeLayout {

	private ImageView iv_post, iv_appicon, iv_categoryicon,iv_recommend;
	private TextView tv_postname, tv_appname,tv_categoryname;
	private RelativeLayout rl_post, rl_app,rl_category;
	/**推荐位类型，默认应用为0，海报为1，栏目为2**/
	private int itemtype=0;

	public PageItemView(Context context) {
		super(context);
		initView(null);
	}

	public PageItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(attrs);
	}

	public PageItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(attrs);
	}

	protected void initView(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageItem);
			itemtype = typedArray.getInt(R.styleable.PageItem_itemtype, 0);
		}
		if (itemtype==1) {
			LayoutInflater.from(context).inflate(R.layout.item_page_bigpost, this);
			rl_post = findView(R.id.rl_postitem_bigpost);
			iv_post = findView(R.id.iv_post);
			tv_postname = findView(R.id.tv_postname);
		}else if (itemtype==2) {
			LayoutInflater.from(context).inflate(R.layout.item_page_category, this);
			rl_category=findView(R.id.rl_postitem_category);
			iv_categoryicon = findView(R.id.iv_categoryicon);
			tv_categoryname=findView(R.id.tv_categoryname);
		}else {
			LayoutInflater.from(context).inflate(R.layout.item_page_post, this);
			rl_app = findView(R.id.rl_postitem_apppost);
			iv_appicon = findView(R.id.iv_appicon);
			tv_appname = findView(R.id.tv_appname);
			if (Config.IS_INDIA_DAS){
				tv_appname.setVisibility(View.GONE);
			}
			iv_recommend = findView(R.id.iv_recommend);
		}
	}

	/**
	 * 栏目推荐位
	 * 
	 * @param category
	 * @param iconId
	 *            图片临时采用写死
	 */
	public void setCategoryData(Category category) {
		if (rl_category==null ||category == null) {
			return;
		}
		if (!TextUtils.isEmpty(category.getIconFilePath())) {
			ImageLoadUtil.displayImgByMemoryDiscCache(category.getIconFilePath(), iv_categoryicon);
		}
		tv_categoryname.setText(TextUtils.isEmpty(category.getName()) ? "" : category.getName());
		tv_categoryname.setTextSize(context.getResources().getDimension(R.dimen.txtsize_home_categoryname));
	}

	/**
	 * 显示本地图片
	 * 
	 * @param ispost
	 *            是否是海报
	 * @param drawableid
	 *            图片id
	 */
	public void setDrawableIconPost(boolean ispost, int drawableid) {
		if (ispost) {
			iv_post.setImageResource(drawableid);
		} else {
			rl_category.setVisibility(VISIBLE);
			iv_categoryicon.setImageResource(drawableid);
		}
	}

	/**
	 * 页面推荐位
	 * 
	 * @param category
	 */
	public void setPageAppData(PageApp pageApp, ImageLoadingListener imageLoadingListener) {
		if (pageApp == null) {
			return;
		}
		if (pageApp.getPosition() > 6) {
			// 小海报图标
			if (pageApp != null) {
				if (!TextUtils.isEmpty(pageApp.getPosterFilePath())) {
					ImageLoadUtil.displayImgByMemoryDiscCache(pageApp.getPosterFilePath(), iv_appicon,imageLoadingListener);
				}else if(imageLoadingListener!=null){
					imageLoadingListener.onLoadingFailed("", iv_appicon, null);
				}
				tv_appname.setText(TextUtils.isEmpty(pageApp.getAppname()) ? "" : pageApp.getAppname());
				tv_appname.setTextSize(context.getResources().getDimension(R.dimen.txtsize_home_appname));
			}
		} else {
			// 大海报图片
			rl_post.setBackgroundColor(Color.TRANSPARENT);
			if (pageApp != null) {
//				DisplayImageOptions options = new DisplayImageOptions.Builder()
//						.displayer(new RoundedBitmapDisplayer(8)).bitmapConfig(Bitmap.Config.ARGB_8888)
//						.imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheInMemory(true)
//						.showImageForEmptyUri(R.drawable.img_normal_ver).showImageOnFail(R.drawable.img_normal_ver)
//						.cacheOnDisc(true).build();
//				MyApplication.imageLoader.displayImage(pageApp.getPosterFilePath(), iv_post, options);
				if (!TextUtils.isEmpty(pageApp.getPosterFilePath())) {
					ImageLoadUtil.displayImgByMemoryDiscCache(pageApp.getPosterFilePath(), iv_post,imageLoadingListener);
				}else if(imageLoadingListener!=null){
					imageLoadingListener.onLoadingFailed("", iv_appicon, null);
				}
				tv_postname.setText(TextUtils.isEmpty(pageApp.getAppname()) ? "" : pageApp.getAppname());
			}
		}
	}

	/**
	 * 页面推荐位
	 * 
	 * @param category
	 */
	public void setAppData(App app) {
		if (app == null) {
			return;
		}
		// 小海报图标
		iv_recommend.setVisibility(app.isRecommend()?VISIBLE:INVISIBLE);
		if (app != null) {
			ImageLoadUtil.displayImgByMemoryDiscCache(app.getIconFilePath(), iv_appicon);
			tv_appname.setText(TextUtils.isEmpty(app.getAppname()) ? "" : app.getAppname());
			tv_appname.setTextSize(context.getResources().getDimension(R.dimen.txtsize_home_appname));
		}
	}

	public void setItemSelected(boolean selected) {
		if(Config.IS_INDIA_DAS){
			if (tv_appname!=null) {
				tv_appname.setSelected(selected);
				tv_appname.setVisibility(selected ? View.VISIBLE : View.GONE);
			}
			if (iv_appicon != null){
				iv_appicon.setSelected(selected);
			}
		}else {
			if (tv_appname!=null) {
				tv_appname.setSelected(selected);
			}
		}

		if (tv_postname!=null) {
			tv_postname.setVisibility(selected?VISIBLE:INVISIBLE);
			tv_postname.setSelected(selected);
		}
		if (tv_categoryname!=null) {
			tv_categoryname.setSelected(selected);
		}
	}
}
