package com.changhong.gdappstore.model;

import com.changhong.gdappstore.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class Ranking_Item implements Comparable<Ranking_Item>{
	private int topNum;
	private String imgPath;
	private String appIconPath;
	private String appName;
	private String download_num;
	private String appSize;
	private int scores;//评分
	private int appId;
	private String appKey;
	private String subtitle;
	public Bitmap appBitmap;

	public Ranking_Item() {
		
	}
	
	public Ranking_Item(int topNum, String imgPath, String appName,
			String download_num, String appSize, String subtitle) {
		this.topNum = topNum;
		this.imgPath = imgPath;
		this.appName = appName;
		this.download_num = download_num;
		this.appSize = appSize;
		this.subtitle = subtitle;
	}
	
	

	public int getTopNum() {
		return topNum;
	}
	
	public void setTopNum(int topNum) {
		this.topNum = topNum;
	}
	
	public String getImgPath() {
		return imgPath;
	}
	
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getDownload_num() {
		return download_num;
	}
	
	public void setDownload_num(String download_num) {
		this.download_num = download_num;
	}
	
	public String getAppSize() {
		return appSize;
	}
	
	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}
	
	public int getAppId() {
		return appId;
	}
	
	public void setAppId(int appId) {
		this.appId = appId;
	}
	
	public String getAppKey() {
		return appKey;
	}
	
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	public String getAppIconPath() {
		return appIconPath;
	}

	public void setAppIconPath(String appIconPath) {
		this.appIconPath = appIconPath;
	}
	
	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Bitmap getAppBitmap() {
		if(null == appBitmap && null != imgPath) {
			appBitmap = BitmapFactory.decodeFile(imgPath);
		}
		
		return appBitmap;
	}

	@Override
	public int compareTo(Ranking_Item another) {
		// TODO Auto-generated method stub
		return (topNum < another.topNum) ? 1 : -1;
	}

	@Override
	public String toString() {
		return "Ranking_Item{" +
				"topNum=" + topNum +
				", imgPath='" + imgPath + '\'' +
				", appIconPath='" + appIconPath + '\'' +
				", appName='" + appName + '\'' +
				", download_num='" + download_num + '\'' +
				", appSize='" + appSize + '\'' +
				", scores=" + scores +
				", appId=" + appId +
				", appKey='" + appKey + '\'' +
				", subtitle='" + subtitle + '\'' +
				", appBitmap=" + appBitmap +
				'}';
	}

	public void setTopImg(View view) {
		if(view == null) {
			return ;
		}
		switch(getTopNum()) {
		case 1:
			view.setBackgroundResource(R.drawable.img_top1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.img_top2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.img_top3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.img_top4);
			break;
		default :
			view.setBackgroundResource(R.drawable.img_top5);
			break;
		}
	}
}
