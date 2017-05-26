package com.example.dontworry.mobilephonetest.avtivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dontworry.mobilephonetest.IMySercvier;
import com.example.dontworry.mobilephonetest.R;
import com.example.dontworry.mobilephonetest.bean.Lyric;
import com.example.dontworry.mobilephonetest.bean.MediaItem;
import com.example.dontworry.mobilephonetest.servier.MySercvier;
import com.example.dontworry.mobilephonetest.utils.LyricsUtils;
import com.example.dontworry.mobilephonetest.utils.Utils;
import com.example.dontworry.mobilephonetest.view.LyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import static com.example.dontworry.mobilephonetest.R.id.iv_icon;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private RelativeLayout rlTop;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvAudioname;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnLyric;
    private IMySercvier service;
    private int position;
    private MyReceiver receiver;
    private Utils utils;
    private final static int PROGRESS = 0;
    private static final int SHOW_LYRIC = 1;
    private boolean notification;
    private LyricView lyric_show_view;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        lyric_show_view.setNextShowLyric(currentPosition);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };
    private ServiceConnection conon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMySercvier.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (notification) {
                        setViewData(null);
                    } else {
                        service.openAudio(position);
                        tvArtist.setText(service.getArtistName());
                        tvAudioname.setText(service.getAudioName());

                        long endTime = SystemClock.uptimeMillis();
                        long passTime = endTime - MySercvier.startTime;
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        //初始化控件
        ivIcon = (ImageView) findViewById(iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        rlTop = (RelativeLayout) findViewById(R.id.rl_top);
        ivIcon = (ImageView) findViewById(iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvAudioname = (TextView) findViewById(R.id.tv_audioname);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnPlaymode = (Button) findViewById(R.id.btn_playmode);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnLyric = (Button) findViewById(R.id.btn_lyric);
        lyric_show_view = (LyricView) findViewById(R.id.lyric_show_view);

        btnPlaymode.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyric.setOnClickListener(this);
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
    @Override
    public void onClick(View v) {
        if (v == btnPlaymode) {
            // Handle clicks for btnPlaymode
            setPlayMode();
        } else if (v == btnPre) {
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            // Handle clicks for btnStartPause
            try {
                if (service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮状态-播放
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                } else {
                    //播放
                    service.start();
                    //按钮专题-暂停
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnNext) {
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnNext
        } else if (v == btnLyric) {
            // Handle clicks for btnLyric
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MySercvier.REPEAT_NORMAL) {
                playmode = MySercvier.REPEAT_SINGLE;
            } else if (playmode == MySercvier.REPEAT_SINGLE) {
                playmode = MySercvier.REPEAT_ALL;
            } else if (playmode == MySercvier.REPEAT_ALL) {
                playmode = MySercvier.REPEAT_NORMAL;
            }
            service.setPlaymode(playmode);

            setButtonImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MySercvier.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MySercvier.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MySercvier.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        startAndBindService();
    }

    private void initData() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MySercvier.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);

        utils = new Utils();
        EventBus.getDefault().register(this);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData(null);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem mediaItem) {
        try {
            long endTime = SystemClock.uptimeMillis();
            long passTime = endTime - MySercvier.startTime;

            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getAudioName());


            setButtonImage();
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);

            String audioPath = service.getAudioPath();//mnt/sdcard/audio/beijingbeijing.mp3

            String lyricPath = audioPath.substring(0, audioPath.lastIndexOf("."));//mnt/sdcard/audio/beijingbeijing
            File file = new File(lyricPath + ".lrc");
            if (!file.exists()) {
                file = new File(lyricPath + ".txt");
            }
            LyricsUtils lyricsUtils = new LyricsUtils();
            lyricsUtils.readFile(file);

            //2.传入解析歌词的工具类
            ArrayList<Lyric> lyrics = lyricsUtils.getLyrics();
            lyric_show_view.setLyrics(lyrics);

            //3.如果有歌词，就歌词同步

            if (lyricsUtils.isLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //发消息更新进度
        handler.sendEmptyMessage(PROGRESS);


    }

    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    @Override
    protected void onDestroy() {

        if (conon != null) {
            unbindService(conon);
            conon = null;
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        EventBus.getDefault().unregister(this);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MySercvier.class);
        bindService(intent, conon, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
}
