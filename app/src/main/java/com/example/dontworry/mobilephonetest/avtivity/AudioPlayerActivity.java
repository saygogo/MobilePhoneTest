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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dontworry.mobilephonetest.IMySercvier;
import com.example.dontworry.mobilephonetest.R;
import com.example.dontworry.mobilephonetest.servier.MySercvier;
import com.example.dontworry.mobilephonetest.utils.Utils;

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
    //这个就是IMusicPlayService.Stub的实例-很多方法-seekTo
    private IMySercvier service;
    private int position;
    private MyReceiver receiver;
    private Utils utils;

    private final static int PROGRESS = 0;

    private boolean notification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);

                        //设置更新时间
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    //每秒中更新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };
    //连接好服务后的回调
    private ServiceConnection conon = new ServiceConnection() {

        /**
         * 当绑定服务成功后的回调
         * @param name
         * @param iBinder 就是IMusicPlayService.Stub的实例
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //这个就是stub，stub包含很多方法，这些方法调用服务的方法
            service = IMySercvier.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (notification) {
                        //从新从Service获取数据
                        setViewData();
                    } else {
                        service.openAudio(position);//打开播放第0个音频
                        //service.getDuration();//能直接调用了-不能
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-24 14:38:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
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

        btnPlaymode.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyric.setOnClickListener(this);
        // 设置监听拖动视频
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

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-24 14:38:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
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
            //保存到服务里面
            service.setPlaymode(playmode);

            setButtonImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            //从服务得到播放模式
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
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MySercvier.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);

        utils = new Utils();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程
            setViewData();

        }
    }


    private void setViewData() {
        try {
            setButtonImage();
            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getAudioName());
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);
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

        //广播取消注册
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }

    /**
     * 启动服务
     */
    private void startAndBindService() {
        Intent intent = new Intent(this, MySercvier.class);
//        intent.setAction("com.atguigu.mobileplayer0224.service.MUSICPLAYSERVICE");
        //绑定-得到服务的操作对象-IMusicPlayService service
        bindService(intent, conon, Context.BIND_AUTO_CREATE);
        //防止多次实例化Service
        startService(intent);
    }
}
