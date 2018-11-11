package com.alarmclock.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;

import com.alarmclock.activity.QuoteActivity;

import java.io.IOException;

public class AlarmService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer player;
    private int alarmId;
    private String alarmSongPath;
    private Vibrator vib;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private int delay = 100;
    private int times = 1000;
    private boolean on = false;
    private Thread t;

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmId = intent.getIntExtra("id", 0);
        alarmSongPath = intent.getStringExtra("name");

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolumeMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setMode(AudioManager.STREAM_MUSIC);
        audioManager.setSpeakerphoneOn(true);

        if (audioManager.isWiredHeadsetOn()) {
            audioManager.setWiredHeadsetOn(false);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setRouting(AudioManager.MODE_CURRENT, AudioManager.ROUTE_SPEAKER, AudioManager.ROUTE_ALL);
            audioManager.setMode(AudioManager.MODE_CURRENT);
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeMusic, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        Uri myUri = Uri.parse(alarmSongPath);
        player = MediaPlayer.create(getApplication().getApplicationContext(), myUri);

        setVibrate();

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setLooping(true);

        player.setVolume(100, 100);
        player.start();

        t = new Thread() {
            public void run() {
                try {
                    if (mCamera == null) {
                        mCamera = Camera.open();
                        try {
                            mCamera.setPreviewDisplay(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCamera.startPreview();
                    }
                    for (int i = 0; i < times * 2; i++) {
                        toggleFlashLight();
                        sleep(delay);
                    }

                    if (mCamera != null) {
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        sendNotif();
        return START_STICKY;
    }

    /**
     * set vibrate
     */
    private void setVibrate() {
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int dot = 200;
        int dash = 500;
        int short_gap = 200;
        int medium_gap = 500;
        int long_gap = 1000;

        long[] pattern = {
                0,
                dot, short_gap, dot, short_gap, dot, medium_gap,
                dash, short_gap, dash, short_gap, dash, medium_gap,
                dot, short_gap, dot, short_gap, dot, long_gap
        };
        vib.vibrate(pattern, 1);
    }


    /**
     * Turn the devices FlashLight on
     */
    public void turnOn() {
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);

            on = true;
        }
    }

    /**
     * Turn the devices FlashLight off
     */
    public void turnOff() {
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            if (mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(mParameters);
            }
        }
        on = false;
    }

    /**
     * Toggle the flashlight on/off status
     */
    public void toggleFlashLight() {
        if (!on) {
            turnOn();
        } else {
            turnOff();
        }
    }

    public void onDestroy() {
        player.stop();
        vib.cancel();

        Thread th = t;
        t = null;
        th.interrupt();

        player.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    void sendNotif() {
        Intent intent = new Intent(this, QuoteActivity.class);
        intent.putExtra("id", alarmId);
        intent.putExtra("name", alarmSongPath);
        String quoteName = "";
        intent.putExtra("quoteName", quoteName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public class LocalBinder extends Binder {
        AlarmService getService() {
            return AlarmService.this;
        }
    }
}
