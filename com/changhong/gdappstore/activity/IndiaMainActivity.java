package com.changhong.gdappstore.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.adapter.IndiaAppListPageAdapter;
import com.changhong.gdappstore.adapter.IndiaBannerPageAdapter;
import com.changhong.gdappstore.adapter.ViewPageAdapter;
import com.changhong.gdappstore.base.BaseActivity;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.datacenter.Parse;
import com.changhong.gdappstore.fragment.RecycleViewFragment;
import com.changhong.gdappstore.model.Category;
import com.changhong.gdappstore.model.HomePagePoster;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.Executor;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.Util;
import com.changhong.gdappstore.util.ViewPagerTransformer;
import com.changhong.gdappstore.view.FrontViewViewPager;
import com.changhong.gdappstore.view.ViewPagerFoucusChildPrority;

/**
 * Created by Yves Yang on 2016/3/17.
 */
public class IndiaMainActivity extends BaseActivity implements RecycleViewFragment.OnFragmentInteractionListener {
    final static int BANNER_CHANGE_DELAY = 3000;
    public static final int BANNER_PIC_COUNT = 3;
    public static final int BANNER_SCROLL_VELOCITY = 1;

    FrontViewViewPager banner;
    ViewPager viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_india);
        initView();
        initData();
        startSilentInstallService();
    }



    @Override
    protected void onResume() {
        super.onResume();
        tabs.requestFocus();//当数据更新的时候，App列表将会被清空，那么这个时候，item的焦点会乱跳，所以强行设置更新时候的焦点
        ((IndiaAppListPageAdapter) viewPager.getAdapter()).update(); // 更新App列表
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        ;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN) { // 应用退出
                    DialogUtil.showMyAlertDialog(context, "", context.getString(R.string.sure_exit_appstore), "", "",
                            new DialogUtil.DialogBtnOnClickListener() {
                                @Override
                                public void onSubmit(DialogUtil.DialogMessage dialogMessage) {
                                    System.exit(0);
                                }
                                @Override
                                public void onCancel(DialogUtil.DialogMessage dialogMessage) {
                                    if (dialogMessage.dialogInterface != null) {
                                        dialogMessage.dialogInterface.dismiss();
                                    }
                                }
                            });
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * ======================================================================================================
     * Private Functions
     */

    void initView() {
        // 海报栏初始化
        banner = findView(R.id.banner);
        banner.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                ViewPagerTransformer.scaleView(IndiaMainActivity.this, page);// 滚动动画
            }
        });
        banner.setAdapter(new IndiaBannerPageAdapter(this));
        banner.setOffscreenPageLimit(BANNER_PIC_COUNT);
        banner.disableDrawingOrder();
        banner.setFocusable(false);
        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE: {
                        int count = banner.getChildCount();
                        if (count > BANNER_PIC_COUNT) { //多于1，才会循环跳转
                            if (banner.getCurrentItem() >= (count - BANNER_PIC_COUNT)) { //末位之后，跳转到首位（1）
                                banner.setCurrentItem(0, false, BANNER_SCROLL_VELOCITY); //false:不显示跳转过程的动画
                            }
                        }
                    }
                    break;
                }
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //search事件
                Intent intent = new Intent();
                intent.setClass(IndiaMainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.search).setOnFocusChangeListener(new View.OnFocusChangeListener() { //search按钮变色
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((ImageView) v).setColorFilter(getResources().getColor(R.color.Orange_500));
                } else {
                    ((ImageView) v).setColorFilter(getResources().getColor(R.color.Grey_500));
                }
            }
        });

        findViewById(R.id.ranking_list).setOnClickListener(new View.OnClickListener() { //search事件
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IndiaMainActivity.this, RankingListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.ranking_list).setOnFocusChangeListener(new View.OnFocusChangeListener() { //按钮变色
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((ImageView) v).setColorFilter(getResources().getColor(R.color.Orange_500));
                } else {
                    ((ImageView) v).setColorFilter(getResources().getColor(R.color.Grey_500));
                }
            }
        });

        // App列表初始化 & tab初始化
        viewPager = findView(R.id.viewpager);
        IndiaAppListPageAdapter pagerAdapter = new IndiaAppListPageAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        tabs = findView(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.requestFocus();
        ((LinearLayout) tabs.getChildAt(0)).setGravity(Gravity.CENTER);
        runBanner(); //让海报滚动起来
    }

    void runBanner() {
        Executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(BANNER_CHANGE_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nextPoster();
                        }
                    });
                }
            }
        });
    }

    /**
     * 移动到下一个海报，完成海报滚动
     */
    void nextPoster() {
        if (banner == null)
            return;
        int current = banner.getCurrentItem();
        current += 1;
        banner.setCurrentItem(current, true, BANNER_SCROLL_VELOCITY);
    }

    protected void initData() {
        // 初始化APP列表数据
        Executor.execute(new Runnable() {
            @Override
            public void run() {
                DataCenter.getInstance().loadCategories(getBaseContext(), new LoadListener.LoadObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initViewData();
                            }
                        });
                    }
                });
                // 初始化海报数据
                DataCenter.getInstance().loadHomePagePoster(getBaseContext(), new LoadListener.LoadObjectListener() {
                    @Override
                    public void onComplete(final Object object) {
                        if (object == null)
                            return;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBannerData((List<HomePagePoster>) object);
                            }
                        });
                    }
                });
            }
        });
    }

    void initViewData() {
        checkUpdate();
        // 获取所有的分类信息
        List<Category> categories = DataCenter.getInstance().getCategories();

        
        //构建ALL分类
        Category all = Parse.sorALLCategory(getBaseContext(), categories);

        // 屏蔽掉无用的分类信息
        categories = Parse.sortUsefulCategory(getBaseContext(), categories);

        //把第一个分类改为ALL
        ((LinkedList<Category>) categories).addFirst(all);
        
        L.d("IndiaMainActivity-categories:"+categories);
        //遍历并且完成TAB，和内容Viewpager的生成
        List<ViewPager> viewPagers = new ArrayList<ViewPager>();
        for (int i = 0; i < categories.size(); i++) {
            //遍历元素
            Category category = categories.get(i);

            //构建TAB里面的view，主要为了修改文字的大小
            TextView textView = new TextView(getBaseContext());

            //左边为文本内容
//            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
            textView.setText(category.getName());
            textView.setTypeface(Typeface.SANS_SERIF);
            textView.setTextSize(33.0F);
            textView.setSingleLine();
            ColorStateList colorList = getResources().getColorStateList(R.color.tab_text_color);
            textView.setTextColor(colorList);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(Util.dipTopx(getBaseContext(), 24), 0, Util.dipTopx(getBaseContext(), 24), 0);

            // 设置好布局后，把布局添加进view
            tabs.addTab(tabs.newTab().setCustomView(textView));
            obtainViewPage(viewPagers, i);
        }
        // 多创建一个，用于修复viewpage加载错误
        obtainViewPage(viewPagers, categories.size());

        // 设置预加载个数，优化滑动
        viewPager.setOffscreenPageLimit(categories.size());

        // 设置数据
        ((IndiaAppListPageAdapter) viewPager.getAdapter()).setData(viewPagers, categories);
    }

    private void obtainViewPage(List<ViewPager> viewPagers, int index) {
        // 创建内嵌viewpage，用于放置app列表
        ViewPagerFoucusChildPrority viewPager = new ViewPagerFoucusChildPrority(getBaseContext());
        viewPager.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                ViewPager viewpager = (ViewPager) v;
                View childView = viewpager.getChildAt(viewpager.getCurrentItem());
                if (childView != null
                        && childView.onKeyDown(keyCode, event)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        viewPager.setId(index);
        viewPager.setAdapter(new ViewPageAdapter(getSupportFragmentManager()));
        viewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPagers.add(viewPager);
    }

    void initBannerData(List<HomePagePoster> posters) {
        if (posters == null)
            return;
        ((IndiaBannerPageAdapter) banner.getAdapter()).setData(posters);
    }


}


