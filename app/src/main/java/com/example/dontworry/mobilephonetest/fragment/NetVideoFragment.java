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
    private List<MoveInfo.TrailersBean> datas;

    private ArrayList<MediaItem> mediaItem1;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

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
                //放入Bundler
                intent.putExtras(bunlder);
                startActivity(intent);

            }
        });
        return view;
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
        datas = moveInfo.getTrailers();
        if (datas != null && datas.size() > 0) {
            tv_nodata.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context, datas);
            lv.setAdapter(adapter);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }

        mediaItem1 = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            String movieName = datas.get(i).getMovieName();
            int videoLength = datas.get(i).getVideoLength();
            String url = datas.get(i).getUrl();
            mediaItem1.add(new MediaItem(movieName, 100, videoLength, url));
        }
    }

}
