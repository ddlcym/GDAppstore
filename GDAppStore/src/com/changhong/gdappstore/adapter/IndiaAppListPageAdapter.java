package com.changhong.gdappstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.database.DBManager;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.fragment.TabFragment;
import com.changhong.gdappstore.model.App;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.model.NativeApp;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.util.DateUtils;
import com.changhong.gdappstore.util.Executor;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.SharedPreferencesUtil;
import com.changhong.gdappstore.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Yves Yang on 2016/3/28.
 */
public class IndiaAppListPageAdapter extends PagerAdapter {
    static final int APP_COL = 4;
    static final int APP_COUNT_PERPAGE = 16;
    static String NATIVE_APPTABNAME;
    Map<Integer,Collection<? extends Object>> appMap = new HashMap<>();
    Activity activity;
    List<ViewPager> viewPagers = new ArrayList<ViewPager>();
    List<Category> categories = new ArrayList<Category>();

    public IndiaAppListPageAdapter(Activity activity) {
        this.activity = activity;
        NATIVE_APPTABNAME = activity.getResources().getString(R.string.myapps);
    }

    void perLoad(){
        Executor.execute(new Runnable() {
            @Override
            public void run() {
                appMap.clear();
                if (viewPagers.size() > 0) {
                    initAppPage(viewPagers.get(0), categories.get(viewPagers.get(0).getId()));
                    for (int i = 1; i < viewPagers.size(); i++) {
                        initAppPage(viewPagers.get(i), categories.get(viewPagers.get(i).getId() - 1));
                    }
                }

            }
        });
    }

    public void update(){
        perLoad();
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ViewPager viewPager = viewPagers.get(position + 1);

        if (viewPager.getParent() != null){
            ((ViewGroup)viewPager.getParent()).removeView(viewPager);
        }

        container.addView(viewPager);
        return viewPager;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewPagers.get(position));
    }

    public IndiaAppListPageAdapter setData(List<ViewPager> viewPagers,List<Category> categories){
        if(viewPagers == null
                || categories == null)
            return this;
        this.viewPagers.clear();
        this.viewPagers.addAll(viewPagers);
        this.categories = categories;
        notifyDataSetChanged();
        perLoad();
        return this;
    }

    @Override
    public int getCount() {
        return (viewPagers.size() - 1) < 0 ? 0 :viewPagers.size() - 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        int result = viewPagers.lastIndexOf(object);
        if (result == -1){
            result = PagerAdapter.POSITION_NONE;
        }
        return PagerAdapter.POSITION_NONE;
    }

//    void initAppPageAll(final ViewPager viewPager){
//        ArrayList<Object> appList = new ArrayList<>();
//        Iterator<Collection<? extends Object>> it = appMap.values().iterator();
//        while (it.hasNext()){
//            appList.addAll(it.next());
//        }
//
//        obtainOnePage(viewPager,appList);
//    }

    void initAppPage(final ViewPager viewPager,Category category){
        if (initNativeAppPage(viewPager,category)){
            return;
        }

        DataCenter.getInstance().loadAppsByCategoryId(activity, category.getId(), new LoadListener.LoadListListener() {
            @Override
            public void onComplete(final List<Object> items) {
                if (items == null
                        || items.size() < 1)
                    return;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        obtainOnePage(viewPager, items);
//                        appMap.put(viewPagers.indexOf(viewPager), items);
//                        if(appMap.size() == categories.size() - 2){
//                            initAppPageAll(viewPagers.get(1));
//                        }
                    }
                });


            }
        });
    }

    private void obtainOnePage(final ViewPager viewpager,final List<? extends Object> items){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewPageAdapter pageAdapter = (ViewPageAdapter) viewpager.getAdapter();
                pageAdapter.clear();
                final List<Object> apps = new ArrayList<Object>();
                for (int i = 0; i < items.size(); i++) {
                    apps.add(items.get(i));
                    if ((i + 1) % APP_COUNT_PERPAGE == 0) {
                        IndiaCategoryAdapter adapter = new IndiaCategoryAdapter(activity);
                        TabFragment fragment = TabFragment.newInstance(adapter, String.valueOf((i + 1) % APP_COUNT_PERPAGE), APP_COL);
                        pageAdapter.addItem(fragment);
                        adapter.setDataWithUpdate(apps);
                        apps.clear();
                    }
                }
                if (apps.size() > 0) {
                    IndiaCategoryAdapter adapter = new IndiaCategoryAdapter(activity);
                    TabFragment fragment = TabFragment.newInstance(adapter, String.valueOf("last"), APP_COL);
                    pageAdapter.addItem(fragment);
                    adapter.setDataWithUpdate(apps);
                }
                notifyDataSetChanged();
            }
        });

        Executor.execute(new Runnable() {
            @Override
            public void run() {
                while (viewpager.getParent() == null){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewpager.getAdapter().notifyDataSetChanged();
//                        ArrayList<View> views = new ArrayList<View>();
//                        for (int i = 0; i < viewpager.getAdapter().getCount();i ++) {
//                            View v = ((TabFragment)((ViewPageAdapter) viewpager.getAdapter()).getItem(i)).getView();
//                            if (v != null && v.getParent() == null){
//                                if (i == 0){
//                                    viewpager.removeAllViews();
//                                }
//                                views.add(v);
//                                v.setFocusable(true);
//                                viewpager.setFocusable(true);
//                                viewpager.addView(v);
//                            }
//                        }
//                        viewpager.addFocusables(views,View.FOCUS_DOWN,View.FOCUSABLES_ALL);
//                        viewpager.requestLayout();
                    }
                });
            }
        });
    }

    boolean initNativeAppPage(final ViewPager viewPager,Category category){
        if (category.getName().equalsIgnoreCase(NATIVE_APPTABNAME)){
            obtainOnePage( viewPager, obtainNativeAppCategory(activity));
            return true;
            }
        return false;
    }

    public List<NativeApp> obtainNativeAppCategory(final Context context){
        final List<NativeApp> nativeApps = Util.getApp(context);
        if (nativeApps != null) {
            for (int i = 0; i < nativeApps.size(); i++) {
                if (nativeApps.get(i) != null && ((NativeApp) nativeApps.get(i)).getAppPackage().equals(context.getPackageName())) {
                    nativeApps.remove(i);//去掉我们市场应用
                    break;
                }
            }
        }

        // 获取包名，用于请求版本号
        List<String> packages = new ArrayList<String>();
        for (int i = 0; i < nativeApps.size(); i++) {
            if (nativeApps.get(i) != null && !TextUtils.isEmpty(((NativeApp) nativeApps.get(i)).getAppPackage())) {
                packages.add(((NativeApp) nativeApps.get(i)).getAppPackage());
            }
        }
        /****************** 每天只请求所有应用一次，其它时候都剔除缓存中非我们市场应用 *************************/
        final String thisDay = DateUtils.getDayByyyyyMMdd();
        final String lastRequestDay = SharedPreferencesUtil.getAppSynch(context,
                SharedPreferencesUtil.KEY_REQUESTDAY);
        L.d("checkotherapp nativeAppActivity thisday==" + thisDay + "  lastday==" + lastRequestDay + " compare=="
                + thisDay.compareTo(lastRequestDay));
        if (thisDay.compareTo(lastRequestDay) <= 0) {
            // 当天已经请求过了。
            List<String> otherApps = DBManager.getInstance(context).queryOtherApps();
            if (otherApps != null && otherApps.size() > 0 && packages != null && packages.size() > 0) {
                for (int i = 0; i < otherApps.size(); i++) {
                    String otherAppPackage = otherApps.get(i);
                    boolean isInstalled = false;
                    for (int j = 0; j < packages.size(); j++) {
                        if (packages.get(j).equals(otherAppPackage)) {
                            isInstalled = true;
                            L.d("checkotherapp nativeAppActivity removed befor request package==" + otherAppPackage);
                            packages.remove(j);
                            break;
                        }
                    }
                    if (!isInstalled) {
                        // 删除已经卸载应用在数据库中记录
                        L.d("checkotherapp nativeAppActivity delete uninstalled app package==" + otherAppPackage);
                        DBManager.getInstance(context).deleteOtherApp(otherAppPackage);
                    }
                }
            }
            SharedPreferencesUtil.putAppSynch(context, SharedPreferencesUtil.KEY_REQUESTDAY, thisDay);
        }
        /*****************************************************************/

        // 请求版本号
        DataCenter.getInstance().loadAppsUpdateData(packages, new LoadListener.LoadListListener() {

            @Override
            public void onComplete(List<Object> items) {
                List<Object> versionApps = items;
                List<String> otherAppsInDb = DBManager.getInstance(context).queryOtherApps();
                if (versionApps != null && versionApps.size() > 0) {
                    // 设置服务端id和服务端配置版本号
                    for (int i = 0; i < nativeApps.size(); i++) {
                        NativeApp nativeApp = (NativeApp) nativeApps.get(i);
                        boolean isotherApp = true;
                        for (int j = 0; j < versionApps.size(); j++) {
                            App versionApp = (App) versionApps.get(j);
                            if (versionApp != null && nativeApp != null
                                    && nativeApp.getAppPackage().equals(versionApp.getPackageName())) {
                                isotherApp = false;
                                nativeApp.setAppid(versionApp.getAppid());
                                nativeApp.setServerVersionInt(versionApp.getVersionInt());
                            }
                        }
                        if (isotherApp && !otherAppsInDb.contains(nativeApp.getAppPackage())) {
                            // 非我们市场的应用保存到数据库缓存中
                            L.d("checkotherapp nativeAppActivity +insert " + nativeApp.getAppPackage());
                            DBManager.getInstance(context).insertOtherApp(nativeApp.getAppPackage());
                        }
                    }
                    // 非我们应用市场应用缓存操作，只有请求所有本地应用时候才用得着，所以要判断是否是同一天
                    if (thisDay.compareTo(lastRequestDay) > 0) {
                        // 删除本地安装属于我们市场的应用。
                        for (int i = 0; i < items.size(); i++) {
                            for (int j = 0; j < otherAppsInDb.size(); j++) {
                                if (otherAppsInDb.get(j).equals(((App) items.get(i)).getPackageName())) {
                                    DBManager.getInstance(context).deleteOtherApp(otherAppsInDb.get(j));
                                    L.d("checkotherapp nativeAppActivity delete otherapp in db "
                                            + otherAppsInDb.get(j));
                                    otherAppsInDb.remove(j);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }, context);

        return nativeApps;
    }
}
