package com.changhong.gdappstore;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.changhong.gdappstore.util.DesUtils;
import com.changhong.gdappstore.util.FileNameGeneratorHelper;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;
import com.changhong.gdappstore.util.Util;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ots.deviceinfoprovide.DeviceInfo;

/**
 * 应用Application
 * 
 * @author wangxiufeng
 * 
 */
public class MyApplication extends Application {

	/** 设备MAC地址 */
	private static String deviceMac = "";

	public static ImageLoader imageLoader;// 图片加载器

	public static DiskCache discCacheAware;// 图片加载器缓存目录
	/** 图片加载：内存缓存+磁盘缓存 */
	public static DisplayImageOptions option_memory_disc;
	/** 图片加载：磁盘缓存，内存不缓存 */
	public static DisplayImageOptions option_nomemory_disc;
	/** 图片加载：磁盘内存都不缓存 */
	public static DisplayImageOptions option_nomemory_nodisc;// 图片加载器设置（含缓存）
	/** 磁盘缓存大小 **/
	private static int discCacheSize = 10 * 1024 * 1024;
	/** 磁盘缓存最大时间 ,以秒为单位 **/
	private static int discCacheMaxSeconds = 60 * 60 * 24 * 10;// 10天
	/** 服务器端版本号 */
	public static int SERVER_VERSION = 0;
	/** 应用商城apk更新地址 */
	public static String UPDATE_APKURL = "";
	/** 应用商城apk是否许可 */
	public static boolean UPDATE_ENABLE = true;
	/** 是否是中文语言 */
	public static boolean IS_ZH_LANGUAGE = true;
	/** 是否拥有权限使用 */
	public static boolean ACCESSUSER_INITED = false;

	private Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
		this.IS_ZH_LANGUAGE = Util.getLanguageIsZH(context);// 系统语言
		NetworkUtils.isConnectInternet(this);// 网络链接

		//印度系统
		DeviceInfo.CollectInfo();// 获取设备信息
		this.deviceMac = DeviceInfo.DeviceMac;
		//川网系统
//		this.deviceMac = "00:00:00:00:00:00";
		L.d("devicemac=" + deviceMac + "  netconnect=" + NetworkUtils.ISNET_CONNECT + " IS_ZH_LANGUAGE="
				+ IS_ZH_LANGUAGE);

		initImageLoaderCacheDir();// 初始化imageloader 缓存路径
		initImageLoader(this);

		initStrings();// 初始化string字符串
		initBaseUrl();// 初始化服务端请求地址

	}

	/**
	 * 初始化图片加载插件
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// int lruMemCachSize = 10 * 1024 * 1024;
		int threadPoolSize = 4;
		Bitmap.Config bmConfig = Bitmap.Config.ARGB_4444;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).discCacheSize(discCacheSize)
				.tasksProcessingOrder(QueueProcessingType.FIFO).denyCacheImageMultipleSizesInMemory()
				.memoryCache(new WeakMemoryCache()).threadPoolSize(threadPoolSize)
				.discCacheFileNameGenerator(FileNameGeneratorHelper.getInstance()).threadPriority(Thread.NORM_PRIORITY)
				.discCacheSize(discCacheSize).discCache(discCacheAware).build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();

		option_memory_disc = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(bmConfig).imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.img_normal_square_cicle)
				.showImageOnFail(R.drawable.img_normal_square_cicle).cacheOnDisc(true).build();
		option_nomemory_disc = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(bmConfig).imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheInMemory(false)
				.showImageForEmptyUri(R.drawable.img_normal_square_cicle)
				.showImageOnFail(R.drawable.img_normal_square_cicle).cacheOnDisc(true).build();
		option_nomemory_nodisc = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(bmConfig).imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheInMemory(false)
				.showImageForEmptyUri(R.drawable.img_normal_square_cicle)
				.showImageOnFail(R.drawable.img_normal_square_cicle).cacheOnDisc(false).build();
	}

	/**
	 * 初始化图片缓存目录
	 */
	private void initImageLoaderCacheDir() {
		// String imgCacheDirStr = context.getCacheDir() + File.separator +
		// "picCache" + File.separator;
		// File cacheDir = new File(imgCacheDirStr);// 图片缓存目录文件
		File cacheDir = StorageUtils.getCacheDirectory(this);// 图片缓存目录文件
		L.d("imageloader cacheDir " + cacheDir);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		} else {
			if (!Config.ISCACHEABLE && cacheDir.listFiles() != null) {
				L.d("imageloader cacheDir " + cacheDir.listFiles().length);
				for (int i = 0; i < cacheDir.listFiles().length; i++) {
					L.d("imageloader delete file " + cacheDir.listFiles()[i].getAbsolutePath());
					Util.deleteFile(cacheDir.listFiles()[i].getAbsolutePath());
				}
			}
		}

		discCacheAware = new LimitedAgeDiskCache(cacheDir, discCacheMaxSeconds);
		// discCacheAware = new FileCountLimitedDiscCache(cacheDir,
		// discCacheSize);
	}

	/**
	 * 加载字符串
	 */
	private void initStrings() {
		Config.HOMEPAGE = context.getString(R.string.homepage);
		Config.ZHUANTI = context.getString(R.string.zhuanti);
	}

	private void initBaseUrl() {
		if (!Config.IS_ENGLISH_VERSION && IS_ZH_LANGUAGE) {
			Config.setBASEURL(Config.SERVER_EN);
			Config.CONNECTION_TIMEOUT = 9000;
		} else {
			Config.setBASEURL(Config.SERVER_EN);
			Config.CONNECTION_TIMEOUT = 15000;
		}
//		Config.setBASEURL(Config.SERVER_PG);//TODO 临时测试修改IP地址时候使用
	}

	/**
	 * 返回Mac地址
	 * 
	 * @return
	 */
	public static String getDeviceMac() {
		return deviceMac;
	}

	/**
	 * 返回加密后的Mac地址
	 * 
	 * @return
	 */
	public static String getEncDeviceMac() {
		String encMac = DesUtils.getEncString(getDeviceMac());
		return encMac;
	}

}
