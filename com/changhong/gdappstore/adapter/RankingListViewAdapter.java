package com.changhong.gdappstore.adapter;

import java.util.ArrayList;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.model.RankingData;
import com.changhong.gdappstore.model.Ranking_Item;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.changhong.gdappstore.util.L;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RankingListViewAdapter extends BaseAdapter {
	private Context mContext = null;
	private ArrayList<Ranking_Item> mArrayList = null;
	private LayoutInflater flater = null;
	private RankingData rankingData;
    private Handler handler;
	private boolean firstInit;

	public RankingListViewAdapter(Context context,ArrayList<Ranking_Item> array, Handler handler) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mArrayList = array;
		flater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rankingData = RankingData.getInstance();
        this.handler = handler;
		firstInit = true;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mArrayList != null) {
			return mArrayList.size();
		}
		
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(mArrayList != null) {
			return mArrayList.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		L.i("位置：" + position);
		
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = (RelativeLayout) flater.inflate(R.layout.rankings_item, null);
			
			holder = new ViewHolder();

			holder.top_num = (TextView)convertView.findViewById(R.id.top_num);
			holder.app_icon = (ImageView)convertView.findViewById(R.id.item_app_icon);
			holder.app_name = (TextView)convertView.findViewById(R.id.item_app_name);
			holder.app_subtitle = (TextView) convertView.findViewById(R.id.item_app_subtitle);
			holder.download_num = (TextView)convertView.findViewById(R.id.item_app_download_num);
			holder.app_size = (TextView)convertView.findViewById(R.id.item_app_size);

			convertView.setTag(holder);
            if (firstInit && position == 0) {
                L.i("firstInit：" + firstInit);
                firstInit = false;
                Message msg = new Message();
                msg.what = 0x2001;
                msg.obj = convertView;
                handler.sendMessage(msg);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.focues_ranking_item);
                convertView.findViewById(R.id.popular_item).setBackgroundDrawable(drawable);
            }
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Ranking_Item ranking_Item = mArrayList.get(position);
		holder.top_num.setText(ranking_Item.getTopNum() + "");
		ranking_Item.setTopImg(holder.top_num);

		Bitmap bitmap = ranking_Item.getAppBitmap();
		if (null != bitmap) {
			holder.app_icon.setImageBitmap(bitmap);
		}
		else {
			ImageLoadUtil.displayImgByMemoryDiscCache(rankingData.getHost() + ranking_Item.getAppKey() + "/" + ranking_Item.getAppIconPath(), holder.app_icon);
		}

		holder.app_name.setText(ranking_Item.getAppName());
		holder.app_subtitle.setText(ranking_Item.getSubtitle());
		holder.download_num.setText(mContext.getString(R.string.str_download) + ranking_Item.getDownload_num());
		holder.app_size.setText(mContext.getString(R.string.str_size) + ranking_Item.getAppSize() + "M");
		
		return convertView;
	}
}
