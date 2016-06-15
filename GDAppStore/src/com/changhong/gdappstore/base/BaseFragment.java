package com.changhong.gdappstore.base;

import android.support.v4.app.Fragment;

/**
 * Created by Yves Yang on 2016/2/24.
 */
public class BaseFragment extends Fragment {

    String tabName = null;

    public String getTabName() {
        return tabName;
    }

    public BaseFragment setTabName(String tabName) {
        this.tabName = tabName;
        return this;
    }
}
