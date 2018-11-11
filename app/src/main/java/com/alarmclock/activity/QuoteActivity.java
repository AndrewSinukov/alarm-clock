package com.alarmclock.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.db.DatabaseManager;
import com.alarmclock.helper.FragmentHelper;
import com.alarmclock.model.Alarm;
import com.alarmclock.receivers.AlarmManagerBroadcastReceiver;
import com.alarmclock.service.AlarmService;

import java.sql.SQLException;
import java.util.GregorianCalendar;

public class QuoteActivity extends Activity implements View.OnClickListener {

    private int alarmId;
    private String alarmSong;
    private RelativeLayout relativeLayout;
    private ChangeColor changeColor;
    private FragmentHelper fragmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        FragmentHelper.getInstance().init(QuoteActivity.this.getApplicationContext());
        fragmentHelper = FragmentHelper.getInstance();

        setContentView(R.layout.quote);
        relativeLayout = (RelativeLayout) findViewById(R.id.wrapContentAlarm);
        relativeLayout.setBackgroundColor(fragmentHelper.randomColor());

        changeColor = (ChangeColor) new ChangeColor().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        Bundle bundle = getIntent().getExtras();
        alarmId = bundle.getInt("id");
        alarmSong = bundle.getString("name");
//        Boolean repeatSong = bundle.getBoolean("repeat");

        DatabaseManager.getInstance().init(getApplicationContext());
        String fileName = getIntent().getStringExtra(Constants.QUOITE_RANDOW);
        TextView quoteRandow = (TextView) findViewById(R.id.quoteRandow);
        quoteRandow.setText(fileName);

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);
        Button repeatButton = (Button) findViewById(R.id.repeatButton);
        repeatButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        changeColor.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stopButton:
                try {
                    Alarm alarmUpdate = DatabaseManager.getInstance().getHelper().getAlarmDao().queryForId(alarmId);
                    stopService(new Intent(QuoteActivity.this, AlarmService.class));
                    alarmUpdate.setStatus(false);
                    DatabaseManager.getInstance().getHelper().getAlarmDao().update(alarmUpdate);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.repeatButton:
                try {
                    // To do update
                    Alarm alarmUpdate = DatabaseManager.getInstance().getHelper().getAlarmDao().queryForId(alarmId);
                    long time = new GregorianCalendar().getTimeInMillis() + (60 * 1000) * 10;
                    stopService(new Intent(QuoteActivity.this, AlarmService.class));

                    Intent intentAlarm = new Intent(this, AlarmManagerBroadcastReceiver.class);
                    intentAlarm.putExtra("alarm_id", alarmId);
                    intentAlarm.putExtra("name", alarmSong);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class ChangeColor extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            relativeLayout.setBackgroundColor(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int times = 1000;
            for (int i = 0; i < times * 2; i++) {
                publishProgress(fragmentHelper.randomColor());
                int delay = 2000;
                SystemClock.sleep(delay);
                if (isCancelled()) break;
            }
            return null;
        }
    }
}