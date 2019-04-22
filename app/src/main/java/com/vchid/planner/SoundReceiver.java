package com.vchid.planner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class SoundReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String s = intent.getStringExtra("Uri");
            if (s == null) {
                if(mediaPlayer!=null)mediaPlayer.stop();
            } else {
                nMediaPlayer();
                mediaPlayer.setDataSource(context, Uri.parse(s));
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }catch(Exception e){ e.printStackTrace(); }
    }
    private void nMediaPlayer(){
        if(mediaPlayer!=null)mediaPlayer.stop();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
    }
    public static void stop(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    static MediaPlayer mediaPlayer;
    static Uri uri;
}
