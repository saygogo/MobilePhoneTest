package com.example.dontworry.mobilephonetest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontworry.mobilephonetest.R;

/**
 * Created by Don't worry on 2017/5/22.
 */

public class Titlebar extends LinearLayout implements View.OnClickListener {

    private TextView tv_sousuo;
    private TextView tv_game;
    private ImageView iv_record;
    private Context context;

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_sousuo = (TextView) getChildAt(1);
        tv_game = (TextView) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);
        tv_sousuo.setOnClickListener(this);
        tv_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sousuo:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
