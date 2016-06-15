package com.changhong.gdappstore.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.adapter.ViewHolder;
import com.changhong.gdappstore.model.RankingData;
import com.changhong.gdappstore.model.Ranking_Item;
import com.changhong.gdappstore.util.L;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class FocusView {
	private ViewHolder holder;
	private ArrayList<Ranking_Item> mArrayList;
	private Context mContext;
	private RelativeLayout focusItem;
	private RelativeLayout.LayoutParams mlayout;
	private int width;
	private int height;
	private RankingData rankingData;
	private boolean haschanged;
	private int position;
	private Animation scallBigAnimation;
	public FocusView(Context context,RelativeLayout focusItem,int width,int height) {
		// TODO Auto-generated constructor stub
		this.focusItem = focusItem;
		this.mContext = context;
		this.width = width;
		this.height = height;
		holder = new ViewHolder();
		
		holder.ranking_item = (RelativeLayout) focusItem.findViewById(R.id.ranking_item);
		//((RelativeLayout) focusItem.findViewById(R.id.test)).setBackgroundColor(0X00000000);
		
		mlayout = new RelativeLayout.LayoutParams(width + 40, height + 40);
		
		rankingData = RankingData.getInstance();
		
		scallBigAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_big);
		
		haschanged = false;
	}
	
	public void setArrayList(ArrayList<Ranking_Item> arrayList) {
		mArrayList = arrayList;
	}
	
	public void focusViewChange(int position,int leftMargin,int topMargin) {
		L.i("focusViewChange position : " + position);
		if(haschanged == false) {
			haschanged = true;
			focusItem.setVisibility(View.VISIBLE);
		}
		
		if(mArrayList == null || mArrayList.isEmpty()) {
			L.w("focusViewChange mArrayList error!");
			return ;
		}
		if(position >= mArrayList.size()) {
			L.w("focusViewChange parameter error!");
			return ;
		}
		Ranking_Item ranking_Item = mArrayList.get(position);
		Bitmap bitmap = ranking_Item.getAppBitmap();
		
		mlayout.leftMargin = leftMargin - 20;
		mlayout.topMargin = topMargin - 22;
		focusItem.setLayoutParams(mlayout);	
		
		holder.ranking_item.setBackgroundResource(R.drawable.focues_ranking);

		focusItem.startAnimation(scallBigAnimation);
		focusItem.bringToFront();
		
		this.position = position;
		L.i("focusViewChange end!");
	}
	
	public void refreshView() {
		if(mArrayList == null || mArrayList.isEmpty()) {
			L.w("refreshView mArrayList error!");
			return ;
		}
		if(position >= mArrayList.size()) {
			L.w("refreshView position error!");
			return ;
		}
	}
	
	public boolean hasChanged() {
		return haschanged;
	}
	
	public void reset() {
		haschanged = false;
	}
	
	public void setVisibility(int visibility) {
		focusItem.setVisibility(visibility);
	}
	
	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
		
		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub
			holder.app_icon.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			// TODO Auto-generated method stub
			holder.app_icon.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub
			holder.app_icon.setVisibility(View.VISIBLE);
		}
	};
}
