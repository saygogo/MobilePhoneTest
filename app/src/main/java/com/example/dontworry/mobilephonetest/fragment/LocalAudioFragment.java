package com.example.dontworry.mobilephonetest.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dontworry.mobilephonetest.R;
import com.example.dontworry.mobilephonetest.adapter.LocalVideoAdapter;
import com.example.dontworry.mobilephonetest.avtivity.SystemVideoPlayerActivity;
import com.example.dontworry.mobilephonetest.base.BaseFragment;
import com.example.dontworry.mobilephonetest.bean.MediaItem;

import java.util.ArrayList;

/**
 * Created by Don't worry on 2017/5/22.
 */

public class LocalAudioFragment extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private LocalVideoAdapter adapter;
    private ArrayList<MediaItem> mediaItems;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_local_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);

                Bundle bunlder = new Bundle();
                bunlder.putSerializable("videolist", mediaItems);
                intent.putExtra("position", i);
                intent.putExtras(bunlder);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initDate() {
        super.initDate();
        getDate();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                tv_nodata.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(context, mediaItems, false);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        }
    };

    private void getDate() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频在sdcard上的名称
                        MediaStore.Audio.Media.DURATION,//视频时长
                        MediaStore.Audio.Media.SIZE,//视频文件的大小
                        MediaStore.Audio.Media.DATA//视频播放地址
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data);

                        mediaItems.add(new MediaItem(name, duration, size, data));
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
