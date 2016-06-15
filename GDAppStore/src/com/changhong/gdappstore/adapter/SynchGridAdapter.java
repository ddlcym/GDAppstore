package com.changhong.gdappstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.model.SynchApp;
import com.changhong.gdappstore.model.SynchApp.Type;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.changhong.gdappstore.view.ScoreView;

public class SynchGridAdapter extends BaseAdapter {

	private List<SynchApp> items = new ArrayList<SynchApp>();

	private Context context;
	/** 是否是批量操作 */
	private boolean isBatch = false;

	public SynchGridAdapter(Context context, List<SynchApp> items) {
		this.context = context;
		this.items = items;
	}

	public void updateList(List<SynchApp> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	public void updateList(List<SynchApp> items, boolean isbatch) {
		this.items = items;
		this.isBatch = isbatch;
		notifyDataSetChanged();
	}

	public List<SynchApp> getItems() {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_synch, parent, false);
			viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			viewHolder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
			viewHolder.iv_synchtype = (ImageView) convertView.findViewById(R.id.iv_synchtype);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_appname);
			viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			viewHolder.scoreView=(ScoreView)convertView.findViewById(R.id.view_score);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// **************************赋值部分*****************************
		SynchApp app = items.get(position);
		if (app != null) {
			viewHolder.tv_name.setText(app.getAppname() + "");
			viewHolder.tv_size.setText(app.getApkSize() + "M");
			viewHolder.scoreView.setScoreBy10Total(app.getScores());
			// 批量操作选中图标
			if (isBatch && app.getSynchType() == Type.NORMAL) {
				viewHolder.iv_check.setVisibility(View.VISIBLE);
				viewHolder.iv_check.setImageResource(app.isChecked() ? R.drawable.img_checked : R.drawable.img_check);
			} else {
				viewHolder.iv_check.setVisibility(View.INVISIBLE);
			}
			// 已备份或者已恢复图标
			if (app.getSynchType() == Type.BACKUPED) {
				viewHolder.iv_synchtype.setVisibility(View.VISIBLE);
				viewHolder.iv_synchtype.setImageResource(R.drawable.lug_img_backuped);
			} else if (app.getSynchType() == Type.RECOVERED) {
				viewHolder.iv_synchtype.setVisibility(View.VISIBLE);
				viewHolder.iv_synchtype.setImageResource(R.drawable.lug_img_recovered);
			} else {
				viewHolder.iv_synchtype.setVisibility(View.INVISIBLE);
			}
			// 应用icon
			if (app.getAppIcon() != null) {
				viewHolder.iv_icon.setImageDrawable(app.getAppIcon());
			} else if (!TextUtils.isEmpty(app.getIconFilePath())) {
				ImageLoadUtil.displayImgByonlyDiscCache(app.getIconFilePath(), viewHolder.iv_icon);
			} else {
				viewHolder.iv_icon.setImageResource(0);
			}
		} else {
			viewHolder.iv_check.setVisibility(View.INVISIBLE);
			viewHolder.iv_synchtype.setVisibility(View.INVISIBLE);
			viewHolder.tv_name.setText("");
			viewHolder.tv_size.setText("");
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
		public ImageView iv_synchtype;
		public TextView tv_name;
		public TextView tv_size;
		public ScoreView scoreView;
	}

}
