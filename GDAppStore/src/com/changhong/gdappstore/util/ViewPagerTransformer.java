package com.changhong.gdappstore.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.view.ImageViewWithShadow;

/**
 * Created by Yves Yang on 2016/3/28.
 */
public class ViewPagerTransformer {

    final static float MAX_SCALE = 1.8f;
    final static float BASE_SCALE = 1.2f;
    final static float SCALE_FACTOR = 1.4f;
    final static float SCALE_FACTOR_Y = 0.3f;
    final static float SCALE_BEGIN = 0f;
    final static float SCALE_END = 1f;

    public static void scaleView(Context context,View page){
        if (page == null ||
                page.getParent() == null)
            return;

        Rect rect = new Rect();
        Rect windoowRect = new Rect();
        page.getGlobalVisibleRect(rect);
        int x = rect.centerX();
        if (x == 0)
            return;
        ((ViewGroup)page.getParent()).getLocalVisibleRect(windoowRect);
        int winWidth = windoowRect.width();
        int winHalfWidth = winWidth >> 1;
        int winQuterWidth = winHalfWidth >> 1;
        float factor = 0;
        if (x > winHalfWidth
                && x < winHalfWidth + winQuterWidth){
            factor = ( winWidth - x - winQuterWidth) / (float)winQuterWidth;
        }else if (x > winQuterWidth && x <= winHalfWidth){
            factor = (x - winQuterWidth) / (float)winQuterWidth;
        }

        float scaleFactor =  1;
        ImageView view = (ImageView)page;
        if ( x >  winQuterWidth
                && x < (winWidth - winQuterWidth)){
            scaleFactor = BASE_SCALE + (factor * ((MAX_SCALE - BASE_SCALE) * SCALE_FACTOR));
            float scaleFactorY = BASE_SCALE + (factor * ((MAX_SCALE - BASE_SCALE) * SCALE_FACTOR * SCALE_FACTOR_Y));
            page.setScaleY(scaleFactorY);
            page.bringToFront();
            page.setBackgroundResource(R.drawable.shadow_bg);
            page.setScaleX(scaleFactor);
            view.clearColorFilter();
        }else{
            scaleFactor = BASE_SCALE;
            page.setScaleY(BASE_SCALE);
            page.setScaleX(1.2f);
            setFakeImage(context, view);
            //page.setBackgroundResource(android.R.color.transparent);
        }

        ((ImageViewWithShadow)view).setPercent(scaleFactor - BASE_SCALE);

       //L.d("POSITION:" + page.getTag().toString() + ":" + x + ":" + winWidth + ":" + scaleFactor);
    }

    static void setFakeImage(Context context,ImageView v){
        if (v == null)
            return;
        v.setColorFilter(context.getResources().getColor(R.color.black_half50), PorterDuff.Mode.DARKEN);

    }
}
