package com.changhong.gdappstore.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by Yves Yang on 2016/3/28.
 */
public class ViewPagerFoucusChildPrority extends ViewPager {
    public ViewPagerFoucusChildPrority(Context context) {
        super(context);
    }

    public ViewPagerFoucusChildPrority(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View foucusView = getChildAt(getCurrentItem());
        if (foucusView != null
                && foucusView.onKeyDown(event.getKeyCode(),event)){
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }

    }
}
