package com.example.dontworry.mobilephonetest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.dontworry.mobilephonetest.bean.Lyric;

import java.util.ArrayList;

/**
 * Created by Don't worry on 2017/5/26.
 */

public class LyricView extends TextView {

    private Paint paintGreen;
    private Paint paintWhite;
    private int widch;
    private int height;
    private ArrayList<Lyric> lyrics;
    private int index = 0;
    private float textHeight = 20;
    private int currentPosition;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        widch = w;
        height = h;
    }


    private void initView() {
        paintWhite = new Paint();
        //设置画笔颜色
        paintWhite.setColor(Color.WHITE);
        //设置抗锯齿
        paintWhite.setAntiAlias(true);
        //设置文字大小
        paintWhite.setTextSize(16);
        //设置居中
        paintWhite.setTextAlign(Paint.Align.CENTER);


        paintGreen = new Paint();
        //设置画笔颜色
        paintGreen.setColor(Color.GREEN);
        //设置抗锯齿
        paintGreen.setAntiAlias(true);
        //设置文字大小
        paintGreen.setTextSize(16);
        //设置居中
        paintGreen.setTextAlign(Paint.Align.CENTER);

        //  准备歌词
        lyrics = new ArrayList<>();
        Lyric lyric = new Lyric();
        for (int i = 0; i < 10000; i++) {
            //不同歌词
            lyric.setContent("aaaaaaaaaaaa_" + i);
            lyric.setSleepTime(2000);
            lyric.setTimePoint(2000 * i);
            //添加到集合
            lyrics.add(lyric);
            //重新创建新对象
            lyric = new Lyric();
        }
//
//        for (int i = 0; i < 10000; i++) {
//            Lyric lyric = new Lyric();
//            //不同歌词
//            lyric.setContent("aaaaaaaaaaaa_" + i);
//            lyric.setSleepTime(2000);
//            lyric.setTimePoint(2000*i);
//            //添加到集合
//            lyrics.add(lyric);
//            //重新创建新对象
//        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, widch / 2, height / 2, paintGreen);
            //得到中间句的坐标
            float tempY = height / 2;
            //绘制前面的歌词
            for (int i = index - 1; i >= 0; i--) {
                String precontent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(precontent, widch / 2, tempY, paintWhite);
            }
            tempY = height / 2;

            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, widch / 2, tempY, paintWhite);

            }


        } else {
            canvas.drawText("没找到歌词...", widch / 2, height / 2, paintGreen);

        }


    }

    public void setNextShowLyric(int currentPosition) {
        this.currentPosition = currentPosition;

        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 0; i < lyrics.size(); i++) {
            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    index = tempIndex;

                }
            }
        }
        invalidate();


    }
}
