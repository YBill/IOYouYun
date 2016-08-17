package com.ioyouyun.chat;

import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by 卫彪 on 2016/6/15.
 */
public class VoicePlay {

    private static MediaPlayer mediaPlayer;

    public static void playVoice(String filePath, final OnPlayListener listener) {
        if (!(new File(filePath).exists())) {
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayVoice(listener);
                    mediaPlayer = null;
                }
            });
            mediaPlayer.start();
            if (listener != null) {
                listener.audioPlay();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void stopPlayVoice(OnPlayListener listener) {
        if (listener != null) {
            listener.audioStop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public interface OnPlayListener {
        void audioPlay();

        void audioStop();
    }

}
