package com.changhong.gdappstore.datacenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.database.DBManager;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.model.AppDetail;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.model.HomePagePoster;
import com.changhong.gdappstore.model.NativeApp;
import com.changhong.gdappstore.model.PageApp;
import com.changhong.gdappstore.model.RankingData;
import com.changhong.gdappstore.model.Ranking_Item;
import com.changhong.gdappstore.model.SynchApp;
import com.changhong.gdappstore.model.SynchApp.Type;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.service.AppBroadcastReceiver;
import com.changhong.gdappstore.util.DateUtils;
import com.changhong.gdappstore.util.DesUtils;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.SharedPreferencesUtil;
import com.changhong.gdappstore.util.Util;

/**
 * 数据解析类
 *
 * @author wangxiufeng
 */
public class Parse {

    /**************************************************************************/
    public final static String HOST = "host";
    public final static String VALUES = "values";
    /**
     * *******************************Category
     * Part*********************************************
     */

    public final static String CATEGORY_ID = "id";

    public final static String CATEGORY_NAME = "name";

    public final static String SEQUENCE_NAME = "sequence";

    public final static String CATEGORY_PARENTID = "pid";

    public final static String CATEGORY_FILENAME = "fn";

    /************************************
     * APP Part
     ************************************************/

    public final static String APP_ID = "id";

    public final static String APP_NAME = "name";

    public final static String APP_KEY = "key";

    public final static String APP_VERSION_INT = "ver_int";

    public final static String APP_VERSION = "ver";

    public final static String APP_PACKAGE = "package";

    public final static String APP_SIZE = "size";

    public final static String APP_SUBTITLE = "subtitle";

    public final static String APP_TIME = "time";

    public final static String APP_DOWNLOAD = "download";

    public final static String APP_UPDATE_DATE = "date";

    public final static String APP_DESCRIPTION = "desc";

    public final static String APP_ICON_FILEPATH = "icon_fp";

    public final static String APP_POSTER_FILEPATH = "poster_fp";

    public final static String APP_APK_FILEPATH = "apk_fp";

    public final static String APP_CATEGORY_ID = "cate_id";

    public final static String APP_SCORES = "scores";

    public final static String APP_RECOMMEND = "recommend";
    
    public final static String APP_MUSTTYPE = "appMustType";
    /**
     * VIP应用
     */
    public final static String APP_IS_VIP = "is_vip";
    /**
     * 详情应用是否允许下载
     */
    public final static String APP_CAN_DOWNLOAD = "enable_download";

    /**
     * NEW 2016-03-22
     */
    public final static String HOMEPAGE_POSTER_ID = "id";
    public final static String HOMEPAGE_POSTER_PIC = "poster_fp";
    /**
     * 请求栏目接口，包含：apk版本，apk下载地址，栏目信息，专题信息
     *
     * @param categoryJson
     * @return list.get(0)为栏目，list.get(1)为专题
     */
    public static List<Category> parseCategory(String categoryJson) {
        List<Category> categories = new ArrayList<Category>();
        // 本地定义首页和专题栏目
        Category zhuantiCategory = new Category(Config.ID_ZHUANTI, Config.ID_ZHUANTI, Config.ZHUANTI, "", true,
                new ArrayList<Category>(), new ArrayList<PageApp>());
        Category homepageCategory = new Category(0, -1, Config.HOMEPAGE, "", false, new ArrayList<Category>(),
                new ArrayList<PageApp>());
        // 本地添加首页栏目
        categories.add(0, homepageCategory);
        if (TextUtils.isEmpty(categoryJson)) {
            return categories;
        }
        try {
            JSONObject categoryObject = new JSONObject(categoryJson);
            String host = "";
            if (categoryObject.has(HOST)) {
                host = categoryObject.getString(HOST).trim();
                host = DesUtils.getDesString(host);
            }
            if (categoryObject.has("client_url")) {
                MyApplication.UPDATE_APKURL = DesUtils.getDesString(categoryObject.getString("client_url").trim());
            }
            if (categoryObject.has("client_en")) {
                MyApplication.UPDATE_ENABLE = categoryObject.getBoolean("client_en");
            }
            if (categoryObject.has("client_v")) {
                MyApplication.SERVER_VERSION = categoryObject.getInt("client_v");
            }
            L.d("server apk data  version=" + MyApplication.SERVER_VERSION + " apkurl=" + MyApplication.UPDATE_APKURL);
            {// 解析栏目数据
                JSONArray array = categoryObject.getJSONArray("category");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Category category = new Category();
                    category.setIstopic(false);
                    if (object.has(CATEGORY_ID)) {
                        category.setId(object.getInt(CATEGORY_ID));
                    }
                    if (object.has(CATEGORY_NAME)) {
                        category.setName(object.getString(CATEGORY_NAME).trim());
                    }
                    if (object.has(SEQUENCE_NAME)) {
                        try {
                            category.setCategoryNumber(Integer.parseInt(object.getString(SEQUENCE_NAME).trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (object.has(CATEGORY_FILENAME) && !TextUtils.isEmpty(object.getString(CATEGORY_FILENAME))) {
                        category.setIconFilePath(host + "category/" + object.getString(CATEGORY_FILENAME).trim());
                    }
                    int parentid = -1;
                    if (object.has(CATEGORY_PARENTID)) {
                        parentid = object.getInt(CATEGORY_PARENTID);
                        category.setParentId(parentid);
                    }
                    if (parentid == -1) {// 添加为父栏目
                        categories.add(category);
                    } else {
                        category.setCategoryPageApps(null);// 子栏目没有推荐位应用
                        category.setCategoyChildren(null);// 子栏目没有子栏目
                        for (int j = 0; j < categories.size(); j++) {
                            if (categories.get(j).getId() == parentid) {
                                // 添加为子栏目
                                categories.get(j).getCategoyChildren().add(category);
                                break;
                            }
                        }
                    }
                }
            }
            {// 解析专题数据
                JSONArray array = categoryObject.getJSONArray("topics");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Category topic = new Category();
                    topic.setIstopic(true);
                    if (object.has(CATEGORY_ID)) {
                        topic.setId(object.getInt(CATEGORY_ID));
                    }
                    if (object.has(CATEGORY_NAME)) {
                        topic.setName(object.getString(CATEGORY_NAME).trim());
                    }
                    if (object.has(CATEGORY_FILENAME) && !TextUtils.isEmpty(object.getString(CATEGORY_FILENAME))) {
                        topic.setIconFilePath(host + "topic/" + object.getString(CATEGORY_FILENAME).trim());
                    }
                    zhuantiCategory.getCategoyChildren().add(topic);
                }
                // 本地添加专题栏目
                categories.add(zhuantiCategory);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.d("parseCategory-categories:"+categories.toString());
        return categories;
    }

    /**
     * 整理列表，去掉number为0的
     * @param context
     * @param categories
     * @return
     */
    public static LinkedList<Category> sortUsefulCategory(Context context,List<Category> categories){
        LinkedList<Category> result = new LinkedList<>();
        for(Category category : categories){
            if (category.getCategoryNumber() != 0){
                result.add(category);
            }
        }
        sortCategory(result, 0, result.size() - 1);
        result.add(new Category(0,-1,context.getString(R.string.myapps)));
        return result;
    }

    /**
     * 生成ALL分类
     * @param context
     * @param categories
     * @return
     */
    public static Category sorALLCategory(Context context,List<Category> categories){
        Category all = new Category(-1,-1,context.getString(R.string.all));
//        for(Category category : categories){
//            all.getCategoryPageApps().addAll(category.getCategoryPageApps());
//        }

        return all;
    }

    private static void sortCategory(LinkedList<Category> categories,int low,int high){
        if(low >= high)
        {
            return;
        }
        int first = low;
        int last = high;
        Category keyCategory = categories.get(first);/*用字表的第一个记录作为枢轴*/
        int key = keyCategory.getCategoryNumber();/*用字表的第一个记录作为枢轴*/

        while(first < last)
        {
            while(first < last && categories.get(last).getCategoryNumber() >= key)
            {
                --last;
            }

            categories.set(first,categories.get(last));/*将比第一个小的移到低端*/

            while(first < last && categories.get(first).getCategoryNumber() <= key)
            {
                ++first;
            }

            categories.set(last,categories.get(first));/*将比第一个大的移到高端*/
        }
        categories.set(first,keyCategory);

        sortCategory(categories, low, first - 1);
        sortCategory(categories, first + 1, high);
    }

    /**
     * 解析一级页面推荐位应用
     *
     * @param pageAppJson
     */
    public static void parsePageApps(String pageAppJson) {
        if (TextUtils.isEmpty(pageAppJson)) {
            L.w("returned by pageAppJson is empty when parsePageApps");
            return;
        }
        DataCenter dataCenter = DataCenter.getInstance();
        if (dataCenter.getCategories() == null || dataCenter.getCategories().size() == 0) {
            L.w("returned by dataCenter.getCategories() is empty when parsePageApps");
            return;
        }
        try {
            JSONObject object = new JSONObject(pageAppJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray("pages");
            for (int i = 0; i < array.length(); i++) {
                PageApp app = new PageApp();
                JSONObject appobject = array.getJSONObject(i);
                if (appobject.has(APP_ID)) {
                    app.setAppid(appobject.getInt(APP_ID));
                }
                if (appobject.has(APP_KEY)) {
                    app.setAppkey(appobject.getString(APP_KEY));
                }
                if (appobject.has(APP_NAME)) {
                    app.setAppname(appobject.getString(APP_NAME));
                }
                if (appobject.has("page")) {
                    app.setPageid(appobject.getInt("page"));
                }
                if (appobject.has("position")) {
                    app.setPosition(appobject.getInt("position"));
                }
                if (appobject.has(APP_POSTER_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_POSTER_FILEPATH))) {
                    app.setPosterFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_POSTER_FILEPATH));
                }
                if (appobject.has(APP_ICON_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_ICON_FILEPATH))) {
                    app.setIconFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_ICON_FILEPATH));
                }
                // TODO 临时写死的匹配
                for (int j = 0; j < dataCenter.getCategories().size(); j++) {
                    // L.d("dataCenter.getCategories().get(j).getId()==="+dataCenter.getCategories().get(j).getId());
                    if (dataCenter.getCategories().get(j).getId() == 0 && app.getPageid() == 1) {
                        dataCenter.getCategories().get(j).getCategoryPageApps().add(app);
                    } else if (dataCenter.getCategories().get(j).getId() == 1 && app.getPageid() == 2) {
                        dataCenter.getCategories().get(j).getCategoryPageApps().add(app);
                    } else if (dataCenter.getCategories().get(j).getId() == 6 && app.getPageid() == 3) {
                        dataCenter.getCategories().get(j).getCategoryPageApps().add(app);
                    } else if (dataCenter.getCategories().get(j).getId() == 11 && app.getPageid() == 4) {
                        dataCenter.getCategories().get(j).getCategoryPageApps().add(app);
                    }
                }
            }
            // for (int i = 0; i < dataCenter.getCategories().size(); i++) {
            // L.d("parse pageapps--" + dataCenter.getCategories().get(i));
            // }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析栏目（分类）下的app列表
     *
     * @param categoryAppJson
     * @return
     */
    public static List<Object> parseCategoryApp(String categoryAppJson) {
        List<Object> apps = new ArrayList<Object>();

        if (TextUtils.isEmpty(categoryAppJson)) {
            L.w("returned by categoryAppJson is empty when parseCategoryApp");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(categoryAppJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray(VALUES);
            for (int i = 0; i < array.length(); i++) {
                App app = new App();
                JSONObject appobject = array.getJSONObject(i);
                parseApp(appobject, app, host);
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sortAppByRecommend(apps);
    }

    private static List<Object> sortAppByRecommend(List<Object> apps) {
        // 将推荐标识的放在前面
        List<Object> sortapps = new ArrayList<Object>();
        if (apps == null) {
            return sortapps;
        }
        for (int i = 0; i < apps.size(); i++) {
            App app = (App) apps.get(i);
            if (app.isRecommend()) {
                sortapps.add(0, app);
            } else {
                sortapps.add(app);
            }
        }
        return sortapps;
    }

    /**
     * 解析详情页面推荐位app
     *
     * @param categoryAppJson
     * @return
     */
    public static List<Object> parseRecommendApp(String categoryAppJson) {
        List<Object> apps = new ArrayList<Object>();

        if (TextUtils.isEmpty(categoryAppJson)) {
            L.w("returned by categoryAppJson is empty when parseRecommendApp");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(categoryAppJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray(VALUES);
            for (int i = 0; i < array.length(); i++) {
                App app = new App();
                JSONObject appobject = array.getJSONObject(i);
                parseApp(appobject, app, host);
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sortAppByRecommend(apps);
    }

    /**
     * 解析静默安装静默卸载应用
     *
     * @param categoryAppJson
     * @return
     */
    public static List<List<Object>> parseSilentInstallApp(String categoryAppJson) {
        List<List<Object>> apps = new ArrayList<List<Object>>();
        List<Object> silentInstallApps = new ArrayList<Object>();
        List<Object> silentUnInstallApps = new ArrayList<Object>();
        List<Object> silentUpdateApps = new ArrayList<Object>();
        apps.add(silentInstallApps);// 安装列表
        apps.add(silentUnInstallApps);// 卸载列表
//        apps.add(silentUpdateApps);// 更新列表
        if (TextUtils.isEmpty(categoryAppJson)) {
            L.w("returned by categoryAppJson is empty when parseRecommendApp");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(categoryAppJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray(VALUES);
            for (int i = 0; i < array.length(); i++) {
                App app = new App();
                JSONObject appobject = array.getJSONObject(i);
                if (appobject.has(APP_ID)) {
                    app.setAppid(appobject.getInt(APP_ID));
                }
                if (appobject.has(APP_PACKAGE)) {
                    app.setPackageName(appobject.getString(APP_PACKAGE).trim());
                }
                if (appobject.has(APP_KEY)) {
                    app.setAppkey(appobject.getString(APP_KEY).trim());
                }
                if (appobject.has(APP_VERSION_INT)) {
                    app.setVersionInt(appobject.getInt(APP_VERSION_INT));
                }
                if (appobject.has(APP_APK_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_APK_FILEPATH))) {
                    app.setPosterFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_APK_FILEPATH).trim());
                    // TODO 使用海报图片路径代替apk路径
                }
//                boolean install = appobject.getBoolean("install");
//                if (install) {
//                    silentInstallApps.add(app);
//                } else {
//                    silentUnInstallApps.add(app);
//                }
                if (appobject.has(APP_MUSTTYPE)) {
                	String appMustType=appobject.getString(APP_MUSTTYPE).trim();
                    app.setAppMustType(appMustType);
                	if(appMustType.equals("UN_INSTALL")){
                		silentUnInstallApps.add(app);
                	}else if(appMustType.equals("INSTALL")){
                		silentInstallApps.add(app);
                	}else if(appMustType.equals("UPDATE")){
                		silentInstallApps.add(app);
                	}
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return apps;
    }

    /**
     * 解析广告数据
     *
     * @param bootADJson
     * @return
     */
    public static String parseBootAD(String bootADJson, Context context) {
        if (TextUtils.isEmpty(bootADJson)) {
            L.w("returned by appdetailJson is empty when bootADJson");
            return "";
        }
        String bootADUrl = "";
        try {
            JSONObject object = new JSONObject(bootADJson);
            String host = object.getString(HOST);
            host = DesUtils.getDesString(host);
            String bootImg = object.getString("boot_img");
            bootADUrl = host + bootImg;
            if (object.has("access")) {
                SharedPreferencesUtil.putAccessCache(context, object.getBoolean("access"));
                MyApplication.ACCESSUSER_INITED = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bootADUrl;
    }

    public static AppDetail parseAppDetail(String appdetailJson) {
        AppDetail appDetail = new AppDetail();
        if (TextUtils.isEmpty(appdetailJson)) {
            L.w("returned by appdetailJson is empty when parseAppDetail");
            return null;
        }
        try {
            JSONObject object = new JSONObject(appdetailJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            String appKey = object.getString(APP_KEY).trim();
            appDetail.setAppkey(appKey);
            appDetail.setHost(host);
            appDetail.setApkFilePath(host + appKey + "/" + object.getString(APP_APK_FILEPATH).trim());
            appDetail.setAppid(object.getInt(APP_ID));
            appDetail.setAppname(object.getString(APP_NAME).trim());
            appDetail.setApkSize(object.getString(APP_SIZE).trim());
            if (object.has(APP_SUBTITLE)) {
                appDetail.setSubtitle(object.getString(APP_SUBTITLE).trim());
            }
            if (object.has(APP_TIME)) {
                appDetail.setTime(object.getString(APP_TIME).trim());
            }
            appDetail.setVersion(object.getString(APP_VERSION).trim());
            appDetail.setVersionInt(object.getInt(APP_VERSION_INT));
            appDetail.setDescription(object.getString(APP_DESCRIPTION).trim());
            appDetail.setDownload(object.getString(APP_DOWNLOAD).trim());
            appDetail.setPackageName(object.getString(APP_PACKAGE).trim());
            appDetail.setUpdateDate(object.getString(APP_UPDATE_DATE).trim());
            appDetail.setCategoryId(object.getInt(APP_CATEGORY_ID));
            appDetail.setScores(object.getInt(APP_SCORES));
            appDetail.setRecommend(object.getBoolean(APP_RECOMMEND));
            appDetail.setState(object.getString("sta").trim());
            if (object.has(APP_IS_VIP)) {
                appDetail.setVipApp(object.getBoolean(APP_IS_VIP));
            }
            if (object.has(APP_CAN_DOWNLOAD)) {
                appDetail.setCanDownload(object.getBoolean(APP_CAN_DOWNLOAD));
            }
            if (object.has(APP_ICON_FILEPATH) && !TextUtils.isEmpty(object.getString(APP_ICON_FILEPATH))) {
                appDetail.setIconFilePath(host + appKey + "/" + object.getString(APP_ICON_FILEPATH).trim());
            }
            if (object.has(APP_POSTER_FILEPATH) && !TextUtils.isEmpty(object.getString(APP_POSTER_FILEPATH))) {
                appDetail.setPosterFilePath(host + appKey + "/" + object.getString(APP_POSTER_FILEPATH).trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return appDetail;
    }

    /**
     * 解析搜索json数据
     *
     * @param searchAppsJson
     * @return
     */
    public static List<Object> parseSearchApps(String searchAppsJson) {
        List<Object> apps = new ArrayList<Object>();

        if (TextUtils.isEmpty(searchAppsJson)) {
            L.w("returned by searchAppsJson is empty when parseSearchApps");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(searchAppsJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray(VALUES);
            for (int i = 0; i < array.length(); i++) {
                App app = new App();
                JSONObject appobject = array.getJSONObject(i);
                parseApp(appobject, app, host);
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sortAppByRecommend(apps);
    }

    /**
     * 解析需要升级的json数据
     *
     * @param appUpdateJson
     * @return
     */
    public static List<Object> parseAppVersions(String appVersionsJson) {
        List<Object> apps = new ArrayList<Object>();

        if (TextUtils.isEmpty(appVersionsJson)) {
            L.w("returned by categoryAppJson is empty when parseAppVersions");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(appVersionsJson);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray(VALUES);
            for (int i = 0; i < array.length(); i++) {
                App app = new App();
                JSONObject appobject = array.getJSONObject(i);
                parseApp(appobject, app, host);
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }

    /**
     * 检测属于我们应用市场app以及已经备份app
     *
     * @param json
     * @return
     */
    public static List<SynchApp> parseBackUpApps(String json) {
        List<SynchApp> apps = new ArrayList<SynchApp>();

        if (TextUtils.isEmpty(json)) {
            L.w("returned by json is empty when parseBackUpApps");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("checkbackupapps");
            for (int i = 0; i < array.length(); i++) {
                SynchApp app = new SynchApp();
                JSONObject appobject = array.getJSONObject(i);
                app.setAppid(appobject.getInt(APP_ID));
                app.setScores(appobject.getInt(APP_SCORES));
                if (appobject.has(APP_NAME)) {
                    app.setAppname(appobject.getString(APP_NAME).trim());
                }
                app.setApkSize(appobject.getString(APP_SIZE).trim());
                app.setPackageName(appobject.getString(APP_PACKAGE).trim());
                int isbacked = appobject.getInt("is_backup");
                if (isbacked == 1) {
                    app.setSynchType(Type.BACKUPED);
                } else {
                    app.setSynchType(Type.NORMAL);
                }
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }

    /**
     * 解析备份成功数据
     */
    public static List<Integer> parsePostBackUpApps(String json) {
        List<Integer> successIds = new ArrayList<Integer>();
        if (TextUtils.isEmpty(json)) {
            L.w("returned by json is empty when parsePostBackUpApps");
            return successIds;
        }
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("requestbackupapps");
            for (int i = 0; i < array.length(); i++) {
                JSONObject idobject = array.getJSONObject(i);
                successIds.add(idobject.getInt(APP_ID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return successIds;
    }

    /**
     * 解析删除备份成功数据
     */
    public static List<Integer> parseDeleteBackUpApps(String json) {
        List<Integer> successIds = new ArrayList<Integer>();
        if (TextUtils.isEmpty(json)) {
            L.w("returned by json is empty when parsePostBackUpApps");
            return successIds;
        }
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("deletebackupapps");
            for (int i = 0; i < array.length(); i++) {
                JSONObject idobject = array.getJSONObject(i);
                successIds.add(idobject.getInt(APP_ID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return successIds;
    }

    /**
     * 解析我的备份应用数据
     */
    public static List<SynchApp> parseGetBackUpApps(String json) {
        List<SynchApp> apps = new ArrayList<SynchApp>();
        if (TextUtils.isEmpty(json)) {
            L.w("returned by json is empty when parseGetBackUpApps");
            return apps;
        }
        try {
            JSONObject object = new JSONObject(json);
            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray array = object.getJSONArray("getbackupapps");
            for (int i = 0; i < array.length(); i++) {
                SynchApp app = new SynchApp();
                JSONObject appobject = array.getJSONObject(i);
                app.setAppkey(appobject.getString(APP_KEY).trim());
                app.setAppid(appobject.getInt(APP_ID));
                app.setAppname(appobject.getString(APP_NAME).trim());
                app.setPackageName(appobject.getString(APP_PACKAGE).trim());
                app.setScores(appobject.getInt(APP_SCORES));
                app.setApkSize(appobject.getString(APP_SIZE).trim());
                app.setApkFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_APK_FILEPATH).trim());
                if (appobject.has(APP_ICON_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_ICON_FILEPATH))) {
                    app.setIconFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_ICON_FILEPATH).trim());
                }
                apps.add(app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return apps;
    }

    private static void parseApp(JSONObject appobject, App app, String host) {
        try {
            if (appobject.has(APP_ID)) {
                app.setAppid(appobject.getInt(APP_ID));
            }
            if (appobject.has(APP_PACKAGE)) {
                app.setPackageName(appobject.getString(APP_PACKAGE).trim());
            }
            if (appobject.has(APP_KEY)) {
                app.setAppkey(appobject.getString(APP_KEY).trim());
            }
            if (appobject.has(APP_NAME)) {
                app.setAppname(appobject.getString(APP_NAME).trim());
            }
            if (appobject.has(APP_SIZE)) {
                app.setApkSize(appobject.getString(APP_SIZE).trim());
            }
            if (appobject.has(APP_SUBTITLE)) {
                app.setSubtitle(appobject.getString(APP_SUBTITLE).trim());
            }
            if (appobject.has(APP_TIME)) {
                app.setTime(appobject.getString(APP_TIME).trim());
            }
            if (appobject.has(APP_VERSION)) {
                app.setVersion(appobject.getString(APP_VERSION).trim());
            }
            if (appobject.has(APP_VERSION_INT)) {
                app.setVersionInt(appobject.getInt(APP_VERSION_INT));
            }
            if (appobject.has(APP_SCORES)) {
                app.setScores(appobject.getInt(APP_SCORES));
            }
            if (appobject.has(APP_RECOMMEND)) {
                app.setRecommend(appobject.getBoolean(APP_RECOMMEND));
            }
            if (appobject.has(APP_POSTER_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_POSTER_FILEPATH))) {
                app.setPosterFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_POSTER_FILEPATH).trim());
            }
            if (appobject.has(APP_ICON_FILEPATH) && !TextUtils.isEmpty(appobject.getString(APP_ICON_FILEPATH))) {
                app.setIconFilePath(host + app.getAppkey() + "/" + appobject.getString(APP_ICON_FILEPATH).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean parseRankingList(String rankingListJson) {
        if (TextUtils.isEmpty(rankingListJson)) {
            L.w("rankingListJson is null!");
            return false;
        }
        RankingData rankingData = RankingData.getInstance();

        try {
            JSONObject object = new JSONObject(rankingListJson);

            String host = object.getString(HOST).trim();
            host = DesUtils.getDesString(host);
            JSONArray popularListJsonArray = object.getJSONArray("POPULAR");

            ArrayList<Ranking_Item> popularArrayList = parseRankingApp(popularListJsonArray);

            rankingData.setHost(host);

            if (popularArrayList != null && !popularArrayList.isEmpty()) {
                rankingData.setPopularArrayList(popularArrayList);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    private static ArrayList<Ranking_Item> parseRankingApp(JSONArray jsonArray) {
        ArrayList<Ranking_Item> rankingList = null;

        if (jsonArray == null) {
            return rankingList;
        }
        rankingList = new ArrayList<Ranking_Item>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Ranking_Item ranking_Item = new Ranking_Item();
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.has(APP_ID)) {
                    ranking_Item.setAppId(object.getInt(APP_ID));
                }
                if (object.has(APP_KEY)) {
                    ranking_Item.setAppKey(object.getString(APP_KEY).trim());
                }
                if (object.has(APP_NAME)) {
                    ranking_Item.setAppName(object.getString(APP_NAME).trim());
                }
                if (object.has(APP_ICON_FILEPATH)) {
                    ranking_Item.setAppIconPath(object.getString(APP_ICON_FILEPATH).trim());
                }
                if (object.has(APP_DOWNLOAD)) {
                    ranking_Item.setDownload_num(Util.intToStr(object.getInt(APP_DOWNLOAD)));
                }
                if (object.has(APP_SIZE)) {
                    ranking_Item.setAppSize(object.getString(APP_SIZE).trim());
                }
                if (object.has(APP_SCORES)) {
                    ranking_Item.setScores(object.getInt(APP_SCORES));
                }
                if (object.has(APP_SUBTITLE)) {
                    ranking_Item.setSubtitle(object.getString(APP_SUBTITLE));
                }
                ranking_Item.setTopNum(i + 1);

                rankingList.add(ranking_Item);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            L.w("Ranking_Item parse error!");
            if (rankingList != null) {
                rankingList.clear();
            }
            rankingList = null;
        }
        return rankingList;
    }

    static List<HomePagePoster> parserHomePagePoster(String raw) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(raw);
            if (jsonObject.has(HOST)) {
                String host = jsonObject.getString(HOST).trim();
                if (host == null) {
                    return null;
                }
                host = DesUtils.getDesString(host);
                JSONArray values = jsonObject.getJSONArray("values");
                List<HomePagePoster> posters = new ArrayList<HomePagePoster>();
                for(int i = 0; i < values.length();i ++){
                     JSONObject object = values.getJSONObject(i);
                    if (object.has(HOMEPAGE_POSTER_ID) && object.has(HOMEPAGE_POSTER_PIC)){
                        posters.add(new HomePagePoster(object.getInt(HOMEPAGE_POSTER_ID),host + object.getString(HOMEPAGE_POSTER_PIC).trim()));
                    }
                }
                return posters;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
