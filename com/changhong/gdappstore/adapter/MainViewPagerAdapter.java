package com.changhong.gdappstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
/**
 * 首页viewpageradapter
 * @author wangxiufeng
 *
 */
public class MainViewPagerAdapter extends PagerAdapter {

	private List<View> pageViews = new ArrayList<View>();

	public MainViewPagerAdapter() {
	}

	public MainViewPagerAdapter(List<View> pageViews) {
//		setPageViews(pageViews);
		this.pageViews=pageViews;
	}

	public void updateList(List<View> pageViews) {
//		setPageViews(pageViews);
		this.pageViews=pageViews;
		notifyDataSetChanged();
	}


	
//	private void setPageViews(List<View> pageViews) {
//		if (pageViews==null || pageViews.size()==0) {
//			this.pageViews=pageViews;
//			notifyDataSetChanged();
//			return;
//		}
//		this.pageViews=new ArrayList<View>(pageViews);//Arrays.asList(homePages)得到的List和List不一样
//		for (int i = 0; i < this.pageViews.size(); i++) {
//			if (this.pageViews.get(i)==null) {
//				L.d("MainViewPagerAdapter there is a null pageView pos is "+i+" size is "+pageViews.size() );
//				this.pageViews.remove(i);
//				i--;
//			}
//		}
//		notifyDataSetChanged();
//	}

	@Override
	public int getCount() {
		return pageViews==null?0:pageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(pageViews.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		ViewGroup v = (ViewGroup) pageViews.get(arg1).getParent();
        if (v != null) {
            v.removeView(pageViews.get(arg1));
        } 
		((ViewPager) arg0).addView(pageViews.get(arg1), 0);
		return pageViews.get(arg1);
	}

}
