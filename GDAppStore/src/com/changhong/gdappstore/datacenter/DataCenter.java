package com.changhong.gdappstore.datacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.model.AppDetail;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.model.HomePagePoster;
import com.changhong.gdappstore.model.SynchApp;
import com.changhong.gdappstore.net.HttpRequestUtil;
import com.changhong.gdappstore.net.LoadListener.LoadCompleteListener;
import com.changhong.gdappstore.net.LoadListener.LoadListListener;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.service.CacheManager;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.SharedPreferencesUtil;

/**
 * 数据中心类
 * 
 * @author wangxiufeng
 * 
 */
public class DataCenter {

	public static final int LOAD_CACHEDATA_SUCCESS = 1;
	public static final int LOAD_CACHEDATA_FAIL = 2;
	public static final int LOAD_CACHEDATA_NO_UPDATE = 3;
	public static final int LOAD_SERVERDATA_SUCCESS = 4;
	public static final int LOAD_SERVERDATA_FAIL = 5;
	public static final int LOAD_HOMEPAGE_POSTER = LOAD_SERVERDATA_FAIL + 1;

	public static final int LOAD_BY_CACHE = 51;
	public static final int LOAD_BY_SERVER = 52;


	private static DataCenter dataCenter = null;
	private static boolean isNeedUpdate = false;

	private DataCenter() {

	}

	// 单例模式
	public static DataCenter getInstance() {
		if (dataCenter == null) {
			dataCenter = new DataCenter();
		}
		return dataCenter;
	}

	/************************** 缓存数据定义区域begin *************************/

	/** 栏目分类列表 **/
	private List<Category> categories = new ArrayList<Category>();
	/** 上次请求分类数据的时间 */
	private static long lastRequestCategoriesTime = 0;
	/** 上次请求分类数据的时间 */
	private static long lastRequestPageAppsTime = 0;
	/** 上一次请求的排行榜数据的时间 **/
	private static long lastRequestRankListTime = 0;
	/** 上一次获取主页海报时间 **/
	private static long getLastRequestHomePagePosterTime = 0;
	/** 上次请求分类下app列表时间 **/
	private static Map<Integer, Long> lastRequestAppsByCategoryId = new HashMap<Integer, Long>();
	/** 上次请求专题下app列表时间 **/
	private static Map<Integer, Long> lastRequestAppsByTopicId = new HashMap<Integer, Long>();
	/** 上次请求详情推荐列表时间 **/
	private static Map<Integer, Long> lastRequestRecommendApps = new HashMap<Integer, Long>();

	/************************** 缓存数据定义区域end *************************/
	//
	//

	public static boolean isNeedUpdate() {
		return isNeedUpdate;
	}

	public static void setIsNeedUpdate(boolean isNeedUpdate) {
		DataCenter.isNeedUpdate = isNeedUpdate;
	}

	//
	//
	/************************** 请求方法定义区域begin *************************/
	/**
	 * 请求解析栏目分类和每页应用数据
	 * 
	 * @param completeListener
	 */
	public void loadCategoryAndPageData(final Context context, final LoadCompleteListener completeListener) {
		loadCategories(context, new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				loadPageApps(context, completeListener);
			}
		});
	}

	/**
	 * 请求解析栏目分类和每页应用数据
	 * 
	 * @param context
	 * @param completeListener
	 * @param getCacheData
	 *            是否预加载本地缓存
	 */
	public void loadCategoryAndPageData(final Context context, final LoadCompleteListener completeListener,
			final boolean getCacheData) {
		loadCategories(context, new LoadObjectListener() {
			@Override
			public void onComplete(Object object) {
				// 如果栏目是请求的服务器，那么页面应用就不加载缓存了，因为如果要加载缓存在栏目加载缓存时候就已经加载过缓存了
				loadPageApps(context, completeListener, !object.equals(LOAD_BY_SERVER), object);
			}
		}, getCacheData);
	}

	/**
	 * 加载栏目分类数据
	 * 
	 * @param context
	 * @param completeListener
	 * @param getCacheData
	 *            是否预加载本地缓存
	 */
	public void loadCategories(final Context context, final LoadObjectListener completeListener, boolean getCacheData) {
		if (getCacheData && Config.ISCACHEABLE) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_CATEGORIES);
			categories = Parse.parseCategory(json);
			if (categories != null && categories.size() > 0 && completeListener != null) {
				completeListener.onComplete(LOAD_BY_CACHE);
			}
		}
		loadCategories(context, completeListener);
	}

	/**
	 * 加载栏目分类数据
	 * 
	 * @param context
	 * @param completeListener
	 */
	public void loadCategories(final Context context, final LoadObjectListener completeListener) {
		if (!isNeedUpdate() && Config.ISCACHEABLE && (System.currentTimeMillis() - lastRequestCategoriesTime) < Config.REQUEST_RESTTIEM) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_CATEGORIES);
			categories = Parse.parseCategory(json);
			if (categories != null && categories.size() > 0 && completeListener != null) {
				completeListener.onComplete(LOAD_BY_CACHE);
				return;// 在一定的时间段内使用缓存数据不用重复请求服务器。
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String json = HttpRequestUtil.getEntityString(
						HttpRequestUtil.doGetRequest(Config.getCategoryUrl, context), context);
				L.d("loadCategories:json:"+json);
				if (!TextUtils.isEmpty(json)) {
					lastRequestCategoriesTime = System.currentTimeMillis();// 更改上次请求时间
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_CATEGORIES, json);// 缓存json数据
				} else {
					L.d("datacenter-loadCategories--server json is null,getting cache data");
					// 没有请求到服务器数据使用缓存文件
					json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_CATEGORIES);
				}
				categories = Parse.parseCategory(json);
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (completeListener != null) {
					completeListener.onComplete(LOAD_BY_SERVER);// 请求操作完毕
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 加载一级页面推荐应用
	 * 
	 * @param context
	 * @param completeListener
	 * @param getCacheData
	 *            是否预加载本地缓存
	 */
	public void loadPageApps(final Context context, final LoadCompleteListener completeListener, boolean getCacheData,
			Object object) {
		if (!isNeedUpdate() && getCacheData && Config.ISCACHEABLE) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_PAGEAPPS);
			Parse.parsePageApps(json);
			if (categories != null && categories.size() > 0 && completeListener != null) {
				completeListener.onComplete();
				if (object.equals(LOAD_BY_CACHE)) {
					return;// 如果栏目是只拿的缓存数据，那么页面还是只拿缓存应用
				}
			}
		}
		if (categories == null || categories.size() <= 0) {
			loadCategories(context, new LoadObjectListener() {

				@Override
				public void onComplete(Object object) {
					loadPageApps(context, completeListener);
				}
			});
		} else {
			loadPageApps(context, completeListener);
		}
	}

	/**
	 * 加载一级页面推荐应用
	 * 
	 * @param context
	 * @param completeListener
	 */
	public void loadPageApps(final Context context, final LoadCompleteListener completeListener) {
		if (!isNeedUpdate() && Config.ISCACHEABLE && (System.currentTimeMillis() - lastRequestPageAppsTime) < Config.REQUEST_RESTTIEM) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_PAGEAPPS);
			Parse.parsePageApps(json);
			if (categories != null && categories.size() > 0 && completeListener != null) {
				completeListener.onComplete();
				return;// 在一定的时间段内使用缓存数据不用重复请求服务器。
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.getPagesUrl + "?" + "boxMac=" + MyApplication.getEncDeviceMac();
				String json = HttpRequestUtil.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);

				if (!TextUtils.isEmpty(json)) {
					lastRequestPageAppsTime = System.currentTimeMillis();// 更改上次请求时间
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_PAGEAPPS, json);// 缓存json数据
				} else {
					L.d("datacenter-loadpageapps--server json is null,getting cache data");
					// 没有请求到服务器数据使用缓存文件
					json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_PAGEAPPS);
				}
				Parse.parsePageApps(json);
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (completeListener != null) {
					completeListener.onComplete();// 请求操作完毕
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 请求某栏目下面的应用
	 * 
	 * @param categoryId
	 *            分类id
	 */
	public void loadAppsByCategoryId(final Context context, final int categoryId,
			final LoadListListener loadAppListListener) {
		if (!isNeedUpdate() && Config.ISCACHEABLE && lastRequestAppsByCategoryId.get(categoryId) != null
				&& (System.currentTimeMillis() - lastRequestAppsByCategoryId.get(categoryId)) < Config.REQUEST_RESTTIEM) {
			String jsonString = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_CATEGORYAPPS + categoryId);
			if (!TextUtils.isEmpty(jsonString) && loadAppListListener != null) {
				loadAppListListener.onComplete(Parse.parseCategoryApp(jsonString));
				return;
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {

				// 缓存中没有就去服务器请求
				String url = Config.getCategoryAppsUrl + "?categoryId=" + categoryId + "&boxMac="
						+ MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				if (!TextUtils.isEmpty(jsonString)) {
					lastRequestAppsByCategoryId.put(categoryId, System.currentTimeMillis());
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_CATEGORYAPPS + categoryId, jsonString);
				} else {
					jsonString = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_CATEGORYAPPS + categoryId);
				}
				return Parse.parseCategoryApp(jsonString);
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadAppListListener != null) {
					loadAppListListener.onComplete((List<Object>) result);
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 请求某一专题下应用
	 * 
	 * @param context
	 * @param topicId
	 * @param loadAppListListener
	 */
	public void loadAppsByTopicId(final Context context, final int topicId, final LoadListListener loadAppListListener) {
		if (!isNeedUpdate() && Config.ISCACHEABLE && lastRequestAppsByTopicId.get(topicId) != null
				&& (System.currentTimeMillis() - lastRequestAppsByTopicId.get(topicId)) < Config.REQUEST_RESTTIEM) {
			String jsonString = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_TOPICAPPS + topicId);
			if (!TextUtils.isEmpty(jsonString) && loadAppListListener != null) {
				loadAppListListener.onComplete(Parse.parseCategoryApp(jsonString));
				return;
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				// 缓存中没有就去服务器请求
				String url = Config.getTopicAppsUrl + "?topicId=" + topicId + "&boxMac="
						+ MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				if (!TextUtils.isEmpty(jsonString)) {
					lastRequestAppsByTopicId.put(topicId, System.currentTimeMillis());
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_TOPICAPPS + topicId, jsonString);
				} else {
					jsonString = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_TOPICAPPS + topicId);
				}
				return Parse.parseCategoryApp(jsonString);
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadAppListListener != null) {
					loadAppListListener.onComplete((List<Object>) result);
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 请求应用详情
	 * 
	 * @param appId
	 *            应用id
	 * @param completeListener
	 */
	public void loadAppDetail(final int appId, final LoadObjectListener loadObjectListener, final Context context) {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				// 缓存中没有就去服务器请求
				String url = Config.getAppDetailUrl + "?appId=" + appId + "&boxMac=" + MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				AppDetail appDetail = Parse.parseAppDetail(jsonString);
				return appDetail;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(result);
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 加载搜索列表
	 * 
	 * @param keywords
	 * @param loadListListener
	 */
	public void loadAppSearch(final String keywords, final LoadListListener loadListListener, final Context context) {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.getAppSearchUrl + "?keywords=" + keywords + "&boxMac="
						+ MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				List<Object> categoryApps = Parse.parseSearchApps(jsonString);
				return categoryApps;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadListListener != null) {
					loadListListener.onComplete((List<Object>) result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 获取需要更新的应用
	 * 
	 * @param packages
	 *            需要检测应用的包名
	 * @param loadListListener
	 */
	public void loadAppsUpdateData(final List<String> packages, final LoadListListener loadListListener,
			final Context context) {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.getAppVersionsUrl;
				if (packages == null || packages.size() == 0) {
					return null;
				}
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				paramList.add(new BasicNameValuePair("boxMac", MyApplication.getEncDeviceMac()));
				for (int i = 0; i < packages.size(); i++) {
					paramList.add(new BasicNameValuePair("appPackages", packages.get(i)));
				}
				String jsonString = HttpRequestUtil.getEntityString(
						HttpRequestUtil.doPostRequest(url, paramList, context), context);
				List<Object> apps = Parse.parseSearchApps(jsonString);
				return apps;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadListListener != null) {
					loadListListener.onComplete((List<Object>) result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 检测本地应用属于我们市场应用，并且检测哪些应用已备份
	 * 
	 * @param packages
	 * @param context
	 * @param loadObjectListener
	 */
	public void checkBackUpApp(final List<String> packages, final Context context,
			final LoadObjectListener loadObjectListener) {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.checkBackUpApp;
				if (packages == null || packages.size() == 0) {
					return null;
				}
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				paramList.add(new BasicNameValuePair("boxMac", MyApplication.getEncDeviceMac()));
				for (int i = 0; i < packages.size(); i++) {
					paramList.add(new BasicNameValuePair("appPackages", packages.get(i)));
				}
				String jsonString = HttpRequestUtil.getEntityString(
						HttpRequestUtil.doPostRequest(url, paramList, context), context);
				List<SynchApp> apps = Parse.parseBackUpApps(jsonString);
				return apps;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 提交备份
	 * 
	 * @param appIds
	 *            需要备份应用id列表，用,隔开
	 * @param context
	 * @param loadObjectListener
	 */
	public void postBackup(final String appIds, final Context context, final LoadObjectListener loadObjectListener) {
		CacheManager.useCacheBackupedApps = false;
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.postBackup;
				if (TextUtils.isEmpty(appIds)) {
					return null;
				}
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				paramList.add(new BasicNameValuePair("boxMac", MyApplication.getEncDeviceMac()));
				paramList.add(new BasicNameValuePair("appIds", appIds));
				String jsonString = HttpRequestUtil.getEntityString(
						HttpRequestUtil.doPostRequest(url, paramList, context), context);
				List<Integer> ids = Parse.parsePostBackUpApps(jsonString);
				return ids;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 删除备份
	 * 
	 * @param appIds
	 *            需要备份应用id列表，用,隔开
	 * @param context
	 * @param loadObjectListener
	 */
	public void deleteBackup(final String appIds, final Context context, final LoadObjectListener loadObjectListener) {
		CacheManager.useCacheBackupedApps = false;
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.deleteBackupApp;
				if (TextUtils.isEmpty(appIds)) {
					return null;
				}
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				paramList.add(new BasicNameValuePair("boxMac", MyApplication.getEncDeviceMac()));
				paramList.add(new BasicNameValuePair("appIds", appIds));
				String jsonString = HttpRequestUtil.getEntityString(
						HttpRequestUtil.doPostRequest(url, paramList, context), context);
				List<Integer> ids = Parse.parseDeleteBackUpApps(jsonString);
				return ids;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 获取已备份应用信息
	 * 
	 * @param context
	 * @param isUserCache
	 *            是否使用缓存数据。true时候使用缓存数据，缓存为空再请求。false直接请求
	 * @param loadObjectListener
	 */
	public void loadBackUpApps(final Context context, boolean isUserCache, final LoadObjectListener loadObjectListener) {
		CacheManager.useCacheBackupedApps = true;
		if (!isNeedUpdate() && Config.ISCACHEABLE && isUserCache) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_BACKUPEDAPPS);
			List<SynchApp> ids = Parse.parseGetBackUpApps(json);
			if (loadObjectListener != null && ids != null && ids.size() > 0) {
				loadObjectListener.onComplete(ids);
				return;// 在一定的时间段内使用缓存数据不用重复请求服务器。
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String url = Config.getBackupApps + "?" + "boxMac=" + MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				if (!TextUtils.isEmpty(jsonString)) {
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_BACKUPEDAPPS, jsonString);
				}
				List<SynchApp> ids = Parse.parseGetBackUpApps(jsonString);
				return ids;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(result);
				}
				super.onPostExecute(result);
			}
		}.execute("");
	}

	/**
	 * 获取详情页面推荐应用
	 * 
	 * @param packages
	 *            需要检测应用的包名
	 * @param loadListListener
	 */
	public void loadRecommendData(final Context context, final int categoryId,
			final LoadListListener loadAppListListener) {
		if ( !isNeedUpdate() && Config.ISCACHEABLE && lastRequestRecommendApps.get(categoryId) != null
				&& (System.currentTimeMillis() - lastRequestRecommendApps.get(categoryId)) < Config.REQUEST_RESTTIEM) {
			String jsonString = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_RECOMMENDAPPS + categoryId);
			if (!TextUtils.isEmpty(jsonString) && loadAppListListener != null) {
				loadAppListListener.onComplete(Parse.parseRecommendApp(jsonString));
				return;
			}
		}
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				// 缓存中没有就去服务器请求
				String url = Config.getDetailRecommendUrl + "?categoryId=" + categoryId + "&boxMac="
						+ MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil
						.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				if (!TextUtils.isEmpty(jsonString)) {
					lastRequestRecommendApps.put(categoryId, System.currentTimeMillis());
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_RECOMMENDAPPS + categoryId, jsonString);
				} else {
					jsonString = CacheManager
							.getJsonFileCache(context, CacheManager.KEYJSON_RECOMMENDAPPS + categoryId);
				}
				return Parse.parseRecommendApp(jsonString);
			}

			@Override
			protected void onPostExecute(Object result) {
				if (loadAppListListener != null) {
					loadAppListListener.onComplete((List<Object>) result);
				}
				super.onPostExecute(result);
			}

		}.execute("");
	}

	/**
	 * 获取静默安装和卸载数据
	 * 
	 * @param context
	 * @param loadObjectListener
	 */
	public void loadSilentInstallData(final Context context, final LoadObjectListener loadObjectListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Config.getSilentInstallUrl;
				String jsonString = HttpRequestUtil.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				L.d("datacenter-getSilentInstallUrl"+jsonString);
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(Parse.parseSilentInstallApp(jsonString));
				}
			}
		}).start();
	}

	/**
	 * 下载广告图片
	 * 
	 * @param context
	 * @param loadObjectListener
	 */
	public void loadBootADData(final Context context, final LoadObjectListener loadObjectListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Config.getBootADUrl + "?" + "boxMac=" + MyApplication.getEncDeviceMac();
				String jsonString = HttpRequestUtil.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
				String bootADUrl = Parse.parseBootAD(jsonString,context);

				if (!TextUtils.isEmpty(bootADUrl)) {
					if (bootADUrl.endsWith(Config.INITIAL)) {// 和apk默认广告图片一样
						bootADUrl = "";
					}
					SharedPreferencesUtil.putJsonCache(context, Config.KEY_BOOTADIMG, bootADUrl);
				}
				if (loadObjectListener != null) {
					loadObjectListener.onComplete(bootADUrl);
				}
			}
		}).start();
	}

	/**
	 * app下载成功后提交服务器用于服务器统计
	 * 
	 * @param appId
	 *            appid信息
	 */
	public void submitAppDownloadOK(final String appId, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				L.d("submitAppDownloadOK id=" + appId);
				String url = Config.putAppDownloadOK + "?" + "appId=" + appId + "&boxMac="
						+ MyApplication.getEncDeviceMac();
				HttpRequestUtil.doGetRequest(url, context);
			}
		}).start();
	}

	/************************** 请求方法定义区域end *************************/
	//
	//
	//
	/************************** 其它方法定义区域 *************************/
	/**
	 * 获取栏目数据
	 * 
	 * @return
	 */
	public synchronized List<Category> getCategories() {
		return categories;
	}

	/**
	 * 根据栏目id获取栏目
	 * 
	 * @param categoryId
	 *            栏目id
	 * @return
	 */
	public Category getCategoryById(int categoryId) {
		if (categories == null || categories.size() == 0) {
			return null;
		}
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
			if (category.getId() == categoryId) {
				return category;
			} else {
				// 查询子栏目
				if (category.getCategoyChildren() != null && category.getCategoyChildren().size() > 0) {
					for (int j = 0; j < category.getCategoyChildren().size(); j++) {
						if (category.getCategoyChildren().get(j).getId() == categoryId) {
							return category.getCategoyChildren().get(j);
						}
					}
				}
			}
		}
		return null;
	}

	public void loadRankingList(final Context context, final LoadObjectListener objectListener, boolean getCacheData) {
		boolean result;
		if (getCacheData && Config.ISCACHEABLE) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_RANKLIST);
			result = Parse.parseRankingList(json);
			if (result == true) {
				objectListener.onComplete(LOAD_CACHEDATA_SUCCESS);
			}
		}

		loadRankingList(context, objectListener);
	}

	public void loadRankingList(final Context context, final LoadObjectListener objectListener) {
		boolean result;
		if ( !isNeedUpdate() && Config.ISCACHEABLE && (System.currentTimeMillis() - lastRequestRankListTime) < Config.REQUEST_RESTTIEM
				&& lastRequestRankListTime != 0) {
			String json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_RANKLIST);
			result = Parse.parseRankingList(json);
			if (result == true) {
				objectListener.onComplete(LOAD_CACHEDATA_NO_UPDATE);
				return;// 在一定的时间段内使用缓存数据不用重复请求服务器。
			}
		}

		new AsyncTask<Void, Void, Boolean>() {
			boolean result;

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				String url = Config.getAppRankListUrl + "?boxMac=" + MyApplication.getEncDeviceMac();
				String json = HttpRequestUtil.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);

				if (!TextUtils.isEmpty(json)) {
					lastRequestRankListTime = System.currentTimeMillis();// 更改上次请求时间
					CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_RANKLIST, json);// 缓存json数据
				} else {
					L.d("datacenter-loadRankingList--server json is null");
					// 没有请求到服务器数据使用缓存文件
					return false;
				}
				result = Parse.parseRankingList(json);
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result == true) {
					objectListener.onComplete(LOAD_SERVERDATA_SUCCESS);
				} else {
					objectListener.onComplete(LOAD_SERVERDATA_FAIL);
				}
			}

		}.execute((Void[]) null);
	}

	public void loadHomePagePoster(final Context context, final LoadObjectListener objectListener) {
		new AsyncTask<Void, Void, List<HomePagePoster>>() {
			@Override
			protected List<HomePagePoster> doInBackground(Void... params) {
				String json;
				if (!isNeedUpdate() && Config.ISCACHEABLE && (System.currentTimeMillis() - getLastRequestHomePagePosterTime) < Config.REQUEST_RESTTIEM
						&& getLastRequestHomePagePosterTime != 0) {
					json = CacheManager.getJsonFileCache(context, CacheManager.KEYJSON_HOMEPAGEPOSTER);
					
				}else{
					String url = Config.obtainMainPagePoster;
					json = HttpRequestUtil.getEntityString(HttpRequestUtil.doGetRequest(url, context), context);
					
					if (!TextUtils.isEmpty(json)) {
						getLastRequestHomePagePosterTime = System.currentTimeMillis();// 更改上次请求时间
						CacheManager.putJsonFileCache(context, CacheManager.KEYJSON_HOMEPAGEPOSTER, json);// 缓存json数据
					} else {
						L.d("datacenter-loadRankingList--server json is null");
						// 没有请求到服务器数据使用缓存文件
						return null;
					}
				}
				L.d("loadHomePagePoster-json"+json);
				return Parse.parserHomePagePoster(json);
			}

			@Override
			protected void onPostExecute(List<HomePagePoster> result) {
					objectListener.onComplete(result);
			}
		}.execute((Void[]) null);
	}
}
