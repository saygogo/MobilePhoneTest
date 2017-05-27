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
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    private int index = 0;
    private float textHeight = 20;
    private int currentPosition;
    private long timePoint;
    private long sleepTime;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {
        paintGreen = new Paint();
        //设置画笔颜色
        paintGreen.setColor(Color.GREEN);
        //设置抗锯齿
        paintGreen.setAntiAlias(true);
        //设置文字大小
        paintGreen.setTextSize(16);
        //设置居中
        paintGreen.setTextAlign(Paint.Align.CENTER);

        paintWhite = new Paint();
        //设置画笔颜色
        paintWhite.setColor(Color.WHITE);
        //设置抗锯齿
        paintWhite.setAntiAlias(true);
        //设置文字大小
        paintWhite.setTextSize(16);
        //设置居中
        paintWhite.setTextAlign(Paint.Align.CENTER);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {

            if (index != lyrics.size() - 1) {
                float push = 0;
                if (sleepTime == 0) {
                    push = 0;
                } else {
                    push = ((currentPosition - timePoint) / sleepTime) * textHeight;
                }
                canvas.translate(0, -push);
            }

            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paintGreen);

            float tempY = height / 2;

            for (int i = index - 1; i >= 0; i--) {

                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }

                canvas.drawText(preContent, width / 2, tempY, paintWhite);

            }

            tempY = height / 2;

            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, paintWhite);
            }
        } else {
            canvas.drawText("没有找到歌词...", width / 2, height / 2, paintGreen);
        }
    }

    public void setNextShowLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {

            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    index = tempIndex;
                    timePoint = lyrics.get(index).getTimePoint();

                    sleepTime = lyrics.get(index).getSleepTime();
                }
            } else {
                index = i;
            }

        }
        invalidate();
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }
}
