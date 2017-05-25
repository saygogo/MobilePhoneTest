package com.example.dontworry.mobilephonetest.servier;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.dontworry.mobilephonetest.IMySercvier;
import com.example.dontworry.mobilephonetest.bean.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

public class MySercvier extends Service {
    private int position;
    private MediaItem mediaItem;

    public MySercvier() {

    }

    public IMySercvier.Stub stub = new IMySercvier.Stub() {

        MySercvier sercvier = MySercvier.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            sercvier.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            sercvier.start();
        }

        @Override
        public void pause() throws RemoteException {
            sercvier.pause();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return sercvier.getArtistName();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return sercvier.getAudioName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return sercvier.getAudioPath();
        }

        @Override
        public int getDuration() throws RemoteException {
            return sercvier.getDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return sercvier.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            sercvier.seekTo(position);
        }

        @Override
        public void next() throws RemoteException {
            sercvier.next();
        }

        @Override
        public void pre() throws RemoteException {
            sercvier.pre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return 0;
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {

        }
    };


    private ArrayList<MediaItem> mediaItems;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        getData();
    }

    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频在sdcard上的名称
                        MediaStore.Audio.Media.DURATION,//视频时长
                        MediaStore.Audio.Media.SIZE,//视频文件的大小
                        MediaStore.Audio.Media.DATA,//视频播放地址
                        MediaStore.Audio.Media.ARTIST//艺术家
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data + ",artist==" + artist);

                        if (duration > 10 * 1000) {
                            mediaItems.add(new MediaItem(name, duration, size, data, artist));
                        }
                    }

                    cursor.close();
                }

            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    public void openAudio(int position) throws RemoteException {
        this.position = position;
        if (mediaItems != null && mediaItems.size() > 0) {
            if (position < mediaItems.size()) {
                mediaItem = mediaItems.get(position);

                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(mediaItem.getData());
                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } else {
            Toast.makeText(MySercvier.this, "音频没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            try {
                start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            try {
                next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            try {
                next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void start() throws RemoteException {
        mediaPlayer.start();
    }

    public void pause() throws RemoteException {
        mediaPlayer.pause();
    }

    public String getArtistName() throws RemoteException {
        return "";
    }

    public String getAudioName() throws RemoteException {
        return "";
    }

    public String getAudioPath() throws RemoteException {
        return "";
    }

    public int getDuration() throws RemoteException {
        return 0;
    }

    public int getCurrentPosition() throws RemoteException {
        return 0;
    }

    public void seekTo(int position) throws RemoteException {
    }

    public void next() throws RemoteException {
    }

    public void pre() throws RemoteException {
    }

    public boolean isPlaying() throws RemoteException {
        return false;
    }

    public int getPlaymode() throws RemoteException {
        return 0;
    }

    public void setPlaymode(int playmode) throws RemoteException {
    }
}
