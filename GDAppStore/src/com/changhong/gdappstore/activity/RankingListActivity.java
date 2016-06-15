package com.changhong.gdappstore.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.adapter.RankingListViewAdapter;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.model.RankingData;
import com.changhong.gdappstore.model.Ranking_Item;
import com.changhong.gdappstore.net.LoadListener;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;

import java.util.ArrayList;

/**
 * Created by pengjie on 2016/3/22.
 */
public class RankingListActivity extends Activity {

    public static final int LOAD_RANKING_OK = 0x1001;
    public static final int LOAD_RANKING_FAIL = 0x1002;
    public static final int INIT_VIEW_OK = 0x2001;

    private ProgressDialog dialog;

    private DataCenter dataCenter;

    private GridView gridView_popular;

    private ArrayList<Ranking_Item> popularList = new ArrayList<Ranking_Item>();

    private RankingListViewAdapter popularListAdapter;

    private Drawable drawable;

    private View oldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_list);
        dialog = DialogUtil.showCirculProDialog(this, getString(R.string.tishi), getString(R.string.dataloading), true);
        initView();
        initData();
    }

    private void initView() {
        gridView_popular = (GridView) findViewById(R.id.gridview_popular);
        ((TextView)findViewById(R.id.popular_list)).setText(getString(R.string.rank_list));
        gridView_popular.setVerticalScrollBarEnabled(false);
    }

    private void initData() {
        drawable = getResources().getDrawable(R.drawable.focues_ranking_item);
        dataCenter = DataCenter.getInstance();
        dataCenter.loadRankingList(this, new LoadListener.LoadObjectListener() {

            @Override
            public void onComplete(Object object) {
                // TODO Auto-generated method stub
                boolean dismissDialog = false;
                L.i("loadRankingList result : " + (Integer) object);
                switch((Integer)object) {
                    case DataCenter.LOAD_CACHEDATA_SUCCESS :
                        handler.sendEmptyMessage(LOAD_RANKING_OK);
                        break;
                    case DataCenter.LOAD_CACHEDATA_NO_UPDATE:
                        dismissDialog = true;
                        break;
                    case DataCenter.LOAD_SERVERDATA_SUCCESS:
                        handler.sendEmptyMessage(LOAD_RANKING_OK);
                        dismissDialog = true;
                        break;
                    case DataCenter.LOAD_SERVERDATA_FAIL:
                        handler.sendEmptyMessage(LOAD_RANKING_FAIL);
                        dismissDialog = true;
                        break;
                }

                if(true == dismissDialog && dialog != null) {
                    dialog.dismiss();
                }

            }
        },true);
    }

    private AdapterView.OnItemSelectedListener popularOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (view != null) {
                if (oldView != null) {
                    oldView.findViewById(R.id.popular_item).setBackgroundColor(Color.TRANSPARENT);
                }
                view.findViewById(R.id.popular_item).setBackgroundDrawable(drawable);
                oldView = view;
            }

            L.i("popularOnItemSelectedListener : " + id + " view:" + view);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void jumpToDetailActivity(ArrayList<Ranking_Item> arrayList, int position) {
        if(arrayList == null || arrayList.isEmpty()) {
            L.w("jumpToDetailActivity : arrayList error!");
            return ;
        }
        if(position >= arrayList.size()) {
            L.w("jumpToDetailActivity : position error!");
            return ;
        }

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Config.KEY_APPID, arrayList.get(position).getAppId());
        startActivity(intent);
    }

    private AdapterView.OnItemClickListener popularOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            jumpToDetailActivity(popularList, position);
        }
    };

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg != null) {
                L.i("Handler msg : " + msg.what);
                switch(msg.what) {
                    case LOAD_RANKING_OK:
                        RankingData rankingData = RankingData.getInstance();
                        ArrayList<Ranking_Item> itemList = rankingData.getPopularArrayList();

                        if(itemList != null) {
                            L.i(itemList.toString());

                            popularList = itemList;
                            popularListAdapter = new RankingListViewAdapter(RankingListActivity.this, popularList, handler);
                            gridView_popular.setAdapter(popularListAdapter);
                            popularListAdapter.notifyDataSetChanged();
                            gridView_popular.setOnItemSelectedListener(popularOnItemSelectedListener);
                            gridView_popular.setOnItemClickListener(popularOnItemClickListener);
                            gridView_popular.setSelection(0);
                            gridView_popular.requestFocus();
                        }

                        break;
                    case LOAD_RANKING_FAIL:
                        Toast.makeText(RankingListActivity.this, getApplicationContext().getString(R.string.error_netconnect_please_checknet), Toast.LENGTH_LONG).show();
                        break;

                    case INIT_VIEW_OK:
                        oldView = (View) msg.obj;
                        L.i("INIT_VIEW_OK" + oldView);
                    default:
                        break;
                }
            }
        };
    };
}
