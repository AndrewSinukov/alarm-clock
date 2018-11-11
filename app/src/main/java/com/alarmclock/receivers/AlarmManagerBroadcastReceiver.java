package com.alarmclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.alarmclock.service.AlarmService;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private int alarmId = 0;
    private String alarmSongPath = "";
    private Boolean repeatSong, dayValue;

    public AlarmManagerBroadcastReceiver() {
    }

    @Override

    public void onReceive(Context context, Intent intent) {
        alarmSongPath = intent.getStringExtra("songPath");
        dayValue = intent.getBooleanExtra("dayValue", false);
        repeatSong = intent.getBooleanExtra("repeat", false);
        alarmId = intent.getIntExtra("idAlarm", 0);

        new SubtaskLoaderTask(context).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, alarmId);
    }

    private class SubtaskLoaderTask extends AsyncTask<Integer, Void, Integer> {
        private Context mContext;

        public SubtaskLoaderTask(Context context) {
            mContext = context;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int data = 0;
            try {
                data = params[0];
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(Integer data) {
            super.onPostExecute(data);

            Intent i = new Intent(mContext, AlarmService.class);
            i.putExtra("id", alarmId);
            i.putExtra("name", alarmSongPath);
            i.putExtra("repeat", repeatSong);
            i.putExtra("day_value", dayValue);
            mContext.startService(i);
        }
    }
}