package com.example.dontworry.mobilephonetest.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dontworry.mobilephonetest.R;
import com.example.dontworry.mobilephonetest.bean.MediaItem;
import com.example.dontworry.mobilephonetest.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Don't worry on 2017/5/22.
 */

public class LocalVideoAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<MediaItem> mediaItems;
    private Utils utils;
    private boolean isVideo;

    public LocalVideoAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean isVideo) {
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
        utils = new Utils();
        this.isVideo = isVideo;
    }

    @Override
    public int getCount() {
        return mediaItems == null ? 0 : mediaItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mediaItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_local_video, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(i);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));


        if (!isVideo) {
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}
