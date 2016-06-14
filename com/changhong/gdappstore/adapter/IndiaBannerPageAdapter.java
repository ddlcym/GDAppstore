package com.changhong.gdappstore.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.activity.IndiaMainActivity;
import com.changhong.gdappstore.model.HomePagePoster;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.util.ViewPagerTransformer;
import com.changhong.gdappstore.view.ImageViewWithShadow;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Yves Yang on 2016/3/28.
 */
public class IndiaBannerPageAdapter extends PagerAdapter {
    List<HomePagePoster> posters = new ArrayList<HomePagePoster>();
    Vector<ImageView> views = new Vector<ImageView>();
    Activity activity;

    public IndiaBannerPageAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageViewWithShadow view = null;
        if(position < views.size()){
            view = (ImageViewWithShadow)views.get(position);
        }
        if(view == null){
            view = new ImageViewWithShadow(activity);
            view.setTag(String.valueOf(position));
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setPadding(2, 2, 2, 2);
            view.setFocusable(false);
            view.setBackgroundResource(R.drawable.shadow_bg);
            view.setIsShadow(false);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(Util.pxTodip(activity, activity.getWindow().getDecorView().getWidth() / (float)IndiaMainActivity.BANNER_PIC_COUNT),
                    Util.pxTodip(activity,container.getHeight()));
            view.setLayoutParams(params);

            ImageLoadUtil.displayImgByMemoryDiscCache(posters.get(position).getPicAddress(), view, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    ViewPagerTransformer.scaleView(activity,view);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    ViewPagerTransformer.scaleView(activity,view);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
            views.add(view);
        }

        if (view.getParent() != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    public IndiaBannerPageAdapter setData(List<HomePagePoster> categories){
        if(categories == null)
            return this;

        if (categories.size() <= IndiaMainActivity.BANNER_PIC_COUNT){
            this.posters.clear();
            this.posters.addAll(categories);
        }else {
            this.posters.clear();
            this.posters.add(categories.get(categories.size() - 1));
            this.posters.addAll(categories);
            this.posters.add(categories.get(0));
            this.posters.add(categories.get(1));
        }


        notifyDataSetChanged();
        return this;
    }

    @Override
    public int getCount() {
        return posters.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public float getPageWidth(int position) {
        return 1 / (float)IndiaMainActivity.BANNER_PIC_COUNT;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
    }


}
