package com.changhong.gdappstore;

/**
 * 应用配置文件
 * 
 * @author wangxiufeng
 * 
 */
public class Config {

	/** 客户版本 */
	public static final boolean IS_INDIA_DAS = true;

	/** 是否定死为英文版本 */
	public static final boolean IS_ENGLISH_VERSION = true;
	
	/** 是否允许缓存。只有调试所有情况下都请求接口时候才改为false */
	public static final boolean ISCACHEABLE = true;

	/** 是否采用系统自带安装工具安装，true为用系统自带安装，false为静默安装 */
	public static final boolean ISNORMAL_INSTALL = false;

	/** 是否采用系统自带卸载工具，true为用系统自带卸载，false为静默卸载 */
	public static final boolean ISNORMAL_UNINSTALL = false;

	/** 是否打印json字符串？在调试时候可以打开*/
	public static final boolean IS_JSONSTRING_DEBUG = true;

	/** 请求暂停时间，在这个时间内不能连续请求 */
	public static final int REQUEST_RESTTIEM = 3 * 60 * 1000;

	/** 存放使用自己开发代码下载apk文件目录 */
	public static final String baseUpdatePath = "/data/data/com.changhong.gdappstore/loadapp";
	/** 存放使用Xutil代码下载apk文件目录 */
	public static final String baseXutilDownPath = "/data/data/com.changhong.gdappstore/xutilloadapp";
	/** 在这个时间内出现new标志 **/
	public static final int NEW_TIME = 15 * 24 * 60 * 60 * 1000;
	/***************************** 网络请求配置项 ******************************************/
	/** 请求链接超时 */
	public static int CONNECTION_TIMEOUT = 9000;

	public static final String NATIVE_ZH = "http://192.168.0.55:8080/appmarket/";// 本地中文版
	public static final String NATIVE_EN = "http://192.168.0.55:8081/appmarket/";// 本地英文版
	public static final String SERVER_ZH = "http://www.ottserver.com:8081/appmarket/";// 公网中文版
	public static final String SERVER_EN = "http://chappstore.yuppcdn.net/appmarket/";// 公网英文版
	public static final String SERVER_PG = "http://192.168.0.55:8081/app_market_en/";
	/** 服务器基础地址 在MyApplication的oncreate（）中调用initBaseUrl()初始化 **/
	private static String BASEURL = SERVER_EN;
	/** 获取分类地址 **/
	public static String getCategoryUrl = BASEURL + "client/appcategories.html";
	/** 获取页面数据地址 **/
	public static String getPagesUrl = BASEURL + "client/boxpages.html";
	/** 获取分类下app地址 **/
	public static String getCategoryAppsUrl = BASEURL + "client/categoryapps.html";
	/** 获取分类下app地址 **/
	public static String getTopicAppsUrl = BASEURL + "client/topicapps.html";
	/** 获取app详情地址 **/
	public static String getAppDetailUrl = BASEURL + "client/appdetails.html";
	/** 获取app排行榜地址 **/
	public static String getAppRankListUrl = BASEURL + "client/appranklist.html";
	/** 搜索app地址 **/
	public static String getAppSearchUrl = BASEURL + "client/appsearch.html";
	/** 获取app升级信息地址 **/
	public static String getAppVersionsUrl = BASEURL + "client/appversions.html";
	/** app下载成功后提交统计 **/
	public static String putAppDownloadOK = BASEURL + "client/appdownload.html";
	/** 获取详情推荐位推荐数据 */
	public static String getDetailRecommendUrl = BASEURL + "client/appdetailsrecommend.html";
	/** 获取静默安装静默卸载数据 */
	public static String getSilentInstallUrl = BASEURL + "client/appmust.html";
	/** 获取开机画面图片信息 */
	public static String getBootADUrl = BASEURL + "client/bootadvertise.html";
	/** 检测哪些应用是我们应用市场的，并且返回是否已备份 */
	public static String checkBackUpApp = BASEURL + "client/checkbackupapp.html";
	/** 请求备份 */
	public static String postBackup = BASEURL + "client/requestbackupapp.html";
	/** 获取已备份应用列表 */
	public static String getBackupApps = BASEURL + "client/getbackupapp.html";
	/** 删除已备份应用 */
	public static String deleteBackupApp = BASEURL + "client/deletebackupapp.html";
	/** 获取首页海报信息 **/
	public static String obtainMainPagePoster = BASEURL + "client/homepageposter.html";



	/**
	 * 
	 * 
	 * 
	 */
	/******************************* Intent传递key配置项 *********************************/
	/** key_appid 应用id **/
	public static final String KEY_APPID = "appid";
	/** 父栏目id **/
	public static final String KEY_PARENT_CATEGORYID = "parentcategoryid";
	/** 当前栏目id **/
	public static final String KEY_CURRENT_CATEGORYID = "currentcategoryid";

	public static final String KEY_LOCAL_APP = "native_app";
	/**
	 * 
	 * 
	 * 
	 */
	/***************************** 网络请求配置项 ******************************************/
	/** 首页 在MyApplication里面初始化 **/
	public static String HOMEPAGE;
	public static String ZHUANTI;
	/** 在首页显示几个栏目的页面 **/
	public static final int ID_ZHUANTI = -100;
	/** 默认和apk一样的开机广告图片 */
	public final static String INITIAL = "initial.png";
	/** 广告图片地址获取key */
	public final static String KEY_BOOTADIMG = "bootadimg";

	/**
	 * 
	 * 
	 * 
	 */
	/***************************** 更改值函数区域 ******************************************/
	public static void setBASEURL(String bASEURL) {
		BASEURL = bASEURL;
		updateUrls();// update other urls
	}

	/**
	 * Remenber update serverUrls when you change BASEURL.
	 */
	public static void updateUrls() {
		getCategoryUrl = BASEURL + "client/appcategories.html";
		getPagesUrl = BASEURL + "client/boxpages.html";
		getCategoryAppsUrl = BASEURL + "client/categoryapps.html";
		getTopicAppsUrl = BASEURL + "client/topicapps.html";
		getAppDetailUrl = BASEURL + "client/appdetails.html";
		getAppRankListUrl = BASEURL + "client/appranklist.html";
		getAppSearchUrl = BASEURL + "client/appsearch.html";
		getAppVersionsUrl = BASEURL + "client/appversions.html";
		putAppDownloadOK = BASEURL + "client/appdownload.html";
		getDetailRecommendUrl = BASEURL + "client/appdetailsrecommend.html";
		getSilentInstallUrl = BASEURL + "client/appmust.html";
		getBootADUrl = BASEURL + "client/bootadvertise.html";
		checkBackUpApp = BASEURL + "client/checkbackupapp.html";
		postBackup = BASEURL + "client/requestbackupapp.html";
		getBackupApps = BASEURL + "client/getbackupapp.html";
		deleteBackupApp = BASEURL + "client/deletebackupapp.html";
		obtainMainPagePoster = BASEURL + "client/homepageposter.html";
	}
}
