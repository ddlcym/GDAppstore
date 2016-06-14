package com.changhong.gdappstore.holder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.R;

/**
 * Created by Yves Yang on 2016/3/22.
 */
public class IndiaAppItemHolder extends RecyclerView.ViewHolder {
    public static final int FLAG_NEW = 1;
    public static final int FLAG_INSTALLED = 1 << 1;

    Activity activity;
    public ImageView icon;
    public TextView name;
    public TextView category;
    public TextView status;
    View isNew;

    public IndiaAppItemHolder(Activity activity,View iv) {
        super(iv);
        this.activity = activity;
        icon = findView(iv, R.id.item_pic);
        name = findView(iv, R.id.name);
        category = findView(iv, R.id.category);
        status = findView(iv, R.id.status);
        isNew = findView(iv,R.id.is_new);
    }

    <T> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 参数： FLAG_NEW,FLAG_INSTALLED
     *
     * @param status
     */
    public void setStatus(int status) {
        setNew((status & FLAG_NEW) != 0);
        setInstalled((status & FLAG_INSTALLED) != 0);
    }

    public void setNew(boolean isNew){
        if (isNew) {
            this.isNew.setVisibility(View.VISIBLE);
        }else {
            this.isNew.setVisibility(View.INVISIBLE);
        }
    }

    public void setInstalled(boolean isInstalled){
        if (isInstalled) {
            this.status.setText(R.string.installed);
            this.status.setTextColor(activity.getResources().getColor(R.color.Orange_500));
        }else {
            this.status.setText(R.string.installedNow);
            this.status.setTextColor(activity.getResources().getColor(R.color.Green_500));
        }
    }

}
