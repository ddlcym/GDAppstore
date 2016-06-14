package com.changhong.gdappstore.util;

import android.widget.ImageView;

import com.changhong.gdappstore.MyApplication;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * imageLoader加载图片工具类
 * 
 * @author wangxiufeng
 * 
 */
public class ImageLoadUtil {
	/**
	 * 加载图片 磁盘缓存和内存缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 */
	public static void displayImgByMemoryDiscCache(String url, ImageView imageView) {
//		if (Config.ISCACHEABLE) {
			MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_memory_disc);
//		} else {
//			displayImgByNoCache(url, imageView);
//		}
	}

	/**
	 * 加载图片 磁盘缓存和内存缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 * @param listener
	 *            下载监听器
	 */
	public static void displayImgByMemoryDiscCache(String url, ImageView imageView, ImageLoadingListener listener) {
//		if (Config.ISCACHEABLE) {
			MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_memory_disc, listener);
//		} else {
//			displayImgByNoCache(url, imageView);
//		}
	}

	/**
	 * 加载图片 磁盘缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 */
	public static void displayImgByonlyDiscCache(String url, ImageView imageView) {
//		if (Config.ISCACHEABLE) {
			MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_nomemory_disc, null);
//		} else {
//			displayImgByNoCache(url, imageView);
//		}
	}

	/**
	 * 加载图片 磁盘缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 * @param listener
	 *            下载监听器
	 */
	public static void displayImgByonlyDiscCache(String url, ImageView imageView, ImageLoadingListener listener) {
//		if (Config.ISCACHEABLE) {
			MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_nomemory_disc, listener);
//		} else {
//			displayImgByNoCache(url, imageView);
//		}
	}

	/**
	 * 加载图片 磁盘缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 */
	public static void displayImgByNoCache(String url, ImageView imageView) {
		MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_nomemory_nodisc, null);
	}

	/**
	 * 加载图片 磁盘缓存
	 * 
	 * @param url
	 *            图片下载地址
	 * @param imageView
	 *            图片imageView
	 * @param listener
	 *            下载监听器
	 */
	public static void displayImgByNoCache(String url, ImageView imageView, ImageLoadingListener listener) {
		MyApplication.imageLoader.displayImage(url, imageView, MyApplication.option_nomemory_nodisc, listener);
	}

}
