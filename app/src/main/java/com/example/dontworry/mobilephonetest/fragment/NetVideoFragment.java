package com.example.dontworry.mobilephonetest.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.dontworry.mobilephonetest.R;
import com.example.dontworry.mobilephonetest.adapter.NetVideoAdapter;
import com.example.dontworry.mobilephonetest.avtivity.SystemVideoPlayerActivity;
import com.example.dontworry.mobilephonetest.base.BaseFragment;
import com.example.dontworry.mobilephonetest.bean.MediaItem;
import com.example.dontworry.mobilephonetest.bean.MoveInfo;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Don't worry on 2017/5/22.
 */

public class NetVideoFragment extends BaseFragment {
    private NetVideoAdapter adapter;

    private ListView lv;
    private TextView tv_nodata;
    private MaterialRefreshLayout materialRefreshLayout;
    private List<MoveInfo.TrailersBean> datas;

    //是否加载更多(上拉刷新)
    private boolean isLoadMore = false;

    private ArrayList<MediaItem> mediaItem1;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
            }

            //上拉加载更多
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLoadMore = true;
                getMoreDate();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MoveInfo.TrailersBean item = adapter.getItem(position);
//
//                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);

                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);

                Bundle bunlder = new Bundle();
                bunlder.putSerializable("videolist", mediaItem1);
                intent.putExtra("position", position);
                intent.putExtras(bunlder);
                startActivity(intent);

            }
        });
        return view;
    }

    private void getMoreDate() {
        //配置联网请求地址
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "加载更多xUtils联网成功==" + result);
                processData(result);
                // 结束上拉刷新...
                materialRefreshLayout.finishRefreshLoadMore();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "加载更xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void initDate() {
        super.initDate();
        getDataFromNet();


    }

    private void getDataFromNet() {

        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                processData(result);
                materialRefreshLayout.finishRefresh();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        if (!isLoadMore) {
            datas = moveInfo.getTrailers();
            if (datas != null && datas.size() > 0) {
                mediaItem1 = new ArrayList<>();
                for (int i = 0; i < datas.size(); i++) {
                    String movieName = datas.get(i).getMovieName();
                    int videoLength = datas.get(i).getVideoLength();
                    String url = datas.get(i).getUrl();
                    mediaItem1.add(new MediaItem(movieName, 100, videoLength, url));
                }
                tv_nodata.setVisibility(View.GONE);
                adapter = new NetVideoAdapter(context, datas);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            List<MoveInfo.TrailersBean> trailersBeanList = moveInfo.getTrailers();
            for (int i = 0; i < trailersBeanList.size(); i++) {
                String movieName = trailersBeanList.get(i).getMovieName();
                int videoLength = trailersBeanList.get(i).getVideoLength();
                String url = trailersBeanList.get(i).getUrl();
                mediaItem1.add(new MediaItem(movieName, 100, videoLength, url));
            }
            datas.addAll(trailersBeanList);
            adapter.notifyDataSetChanged();
        }
    }
}
