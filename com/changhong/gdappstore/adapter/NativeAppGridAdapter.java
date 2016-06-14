package com.changhong.gdappstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.model.NativeApp;

public class NativeAppGridAdapter extends BaseAdapter {

	private List<NativeApp> items = new ArrayList<NativeApp>();

	private Context context;
	/** 是否是批量操作 */
	private boolean isBatch = false;

	public NativeAppGridAdapter(Context context, List<NativeApp> items) {
		this.context = context;
		this.items = items;
	}

	public void updateList(List<NativeApp> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	public void updateList(List<NativeApp> items, boolean isbatch) {
		this.items = items;
		this.isBatch = isbatch;
		notifyDataSetChanged();
	}

	public List<NativeApp> getItems() {
		return items;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null ? null : items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_nativeapppost, parent, false);
			viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_appicon);
			viewHolder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
			viewHolder.iv_update = (ImageView) convertView.findViewById(R.id.iv_update);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_appname);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// **************************赋值部分*****************************
		NativeApp app = items.get(position);
		if (app != null) {
			viewHolder.tv_name.setText(app.getAppname() + "");
			// 批量操作选中图标
			if (isBatch) {
				viewHolder.iv_check.setVisibility(View.VISIBLE);
				viewHolder.iv_check.setImageResource(app.isChecked() ? R.drawable.img_checked : R.drawable.img_check);
			} else {
				viewHolder.iv_check.setVisibility(View.INVISIBLE);
			}
			// 有没有更新提示
			if (app.ServerVersionInt > app.nativeVersionInt) {
				viewHolder.iv_update.setVisibility(View.VISIBLE);
			} else {
				viewHolder.iv_update.setVisibility(View.INVISIBLE);
			}
			// 应用icon
			viewHolder.iv_icon.setImageDrawable(app.getAppIcon());
		} else {
			viewHolder.iv_check.setVisibility(View.INVISIBLE);
			viewHolder.iv_update.setVisibility(View.INVISIBLE);
			viewHolder.tv_name.setText("");
			viewHolder.iv_icon.setImageResource(0);
		}

		return convertView;
	}

	public boolean isBatch() {
		return isBatch;
	}

	/**
	 * 转向批量操作，会调用notifyDataSetChanged();
	 * 
	 * @param isBatch
	 */
	public void setBatch(boolean isBatch) {
		this.isBatch = isBatch;
		notifyDataSetChanged();
	}

	private class ViewHolder {
		public ImageView iv_icon;
		public ImageView iv_check;
		public ImageView iv_update;
		public TextView tv_name;
	}

}
