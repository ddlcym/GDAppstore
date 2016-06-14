package com.changhong.gdappstore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.util.Util;

/**
 * Created by Yves Yang on 2016/3/29.
 */
public class ImageViewWithShadow extends ImageView {
    boolean isShadow;
    NinePatchDrawable drawable = (NinePatchDrawable)getResources().getDrawable(R.drawable.shadow_bg);
    int padding;
    float percent = 1.0f;
    public ImageViewWithShadow(Context context) {
        super(context);
        padding = Util.dipTopx(getContext(),40) ;
    }

    public ImageViewWithShadow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWithShadow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isShadow() {
        return isShadow;
    }

    public void setIsShadow(boolean isShadow) {
        this.isShadow = isShadow;
    }

    public void setPercent(float percent){
        this.percent = percent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isShadow){
            int saveCount = canvas.getSaveCount();
            canvas.save();
            int padding = (int)(this.padding * percent);
            Rect rect = canvas.getClipBounds();
            drawable.setBounds(rect.left - padding, rect.top, rect.right + padding, rect.bottom + padding);
            canvas.clipRect(drawable.getBounds());
            drawable.draw(canvas);

            //Rect rect = canvas.getClipBounds();
//            Rect desRect = new Rect(rect.left + padding
//                    ,rect.top
//                    ,rect.right - padding,
//                    rect.bottom - ((padding )));
            //super.onDraw(canvas);
            //canvas.clipRect(desRect);

            super.onDraw(canvas);
            canvas.restoreToCount(saveCount);
        }else {
            super.onDraw(canvas);
        }

    }
}
