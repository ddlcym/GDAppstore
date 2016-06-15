package com.changhong.gdappstore.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.changhong.gdappstore.base.BaseFragment;
import com.changhong.gdappstore.fragment.RecycleViewFragment;
import com.changhong.gdappstore.fragment.TabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yves Yang on 2016/2/24.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {
    List<RecycleViewFragment> list = new ArrayList<RecycleViewFragment>();

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        RecycleViewFragment fragment = list.get(position);
        if(fragment instanceof TabFragment ){
            return((TabFragment)fragment).getName();
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        int result = list.indexOf(object);
        if (result == -1){
            result = PagerAdapter.POSITION_NONE;
        }
        return result;
    }

    public void setData(){

    }

    public ViewPageAdapter addItemWithoutUpdate(TabFragment fragment){
        list.add(fragment);
        return this;
    }
    public ViewPageAdapter addItem(TabFragment fragment){
        addItemWithoutUpdate(fragment);
        notifyDataSetChanged();
        return this;
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }
}
