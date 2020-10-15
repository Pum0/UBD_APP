package com.ubd_app.Ride.Guide;

import android.media.MediaPlayer;

public class SoundThread extends Thread{

    private MediaPlayer mediaPlayer1 = null;
    private MediaPlayer mediaPlayer2 = null;

    public SoundThread(MediaPlayer mediaPlayer){
        this.mediaPlayer1 = mediaPlayer;
    }
    public SoundThread(MediaPlayer mediaPlayer1, MediaPlayer mediaPlayer2) {
        this.mediaPlayer1 = mediaPlayer1;
        this.mediaPlayer2 = mediaPlayer2;
    }

    @Override
    public void run() {
        super.run();
        if (mediaPlayer2 == null) {
            mediaPlayer1.start();
        }else {
            mediaPlayer1.start();
            boolean ch = true;
            while (ch){
                if (!mediaPlayer1.isPlaying()){
                    ch = false;
                }
            }
            mediaPlayer2.start();
        }
    }
}
