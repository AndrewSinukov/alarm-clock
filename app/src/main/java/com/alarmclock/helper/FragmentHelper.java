package com.alarmclock.helper;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.db.DatabaseManager;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;
import com.alarmclock.receivers.AlarmManagerBroadcastReceiver;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class FragmentHelper {

    private static FragmentHelper instance;
    private Context context;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    public FragmentHelper() {
    }

    public static FragmentHelper getInstance() {
        if (instance == null) {
            instance = new FragmentHelper();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public Intent createIntent(String key, int getLastInsertIdAlarm, boolean valueDay, String getLastInsertSongPathAlarm, boolean repeat) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.setAction("action_" + key);
        intent.putExtra("idAlarm", getLastInsertIdAlarm);
        intent.putExtra("dayValue", valueDay);
        intent.putExtra("songPath", getLastInsertSongPathAlarm);
        intent.putExtra("repeat", repeat);
        return intent;
    }

    private PendingIntent createPendingIntent(Context context, int alarmId, Intent intent) {
        pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public void changeAlarmManagerClockStatus(String type, int getIdAlarm, String getSongPathAlarm, Alarm alarm, List<Days> days) {
        switch (type) {
            case "add":
                if (alarm.getRepeat()) {
                    for (int i = 0; i < days.size(); i++) {
                        int indexNumberDay = days.get(i).getId();
                        String keyDay = days.get(i).getDay();
                        boolean valueDay = days.get(i).getValue();
                        if (valueDay) {
                            long selectedTime = getSelectedTime(indexNumberDay, alarm.getHour(), alarm.getMinute());
                            long startAlarmInWeek = selectedTime + AlarmManager.INTERVAL_DAY * 7;
                            long difOne = System.currentTimeMillis() - selectedTime;
                            Intent intentAddAla = createIntent(keyDay + getIdAlarm, getIdAlarm, true, getSongPathAlarm, true);

                            Toast.makeText(context.getApplicationContext(),
                                    context.getResources().getString(R.string.toast_start_time) + "\n " +
                                    Constants.FORMATS.TERM_TIME_FORMAT.format(selectedTime),
                                    Toast.LENGTH_LONG).show();
                            if (difOne > 0) {
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startAlarmInWeek,
                                        AlarmManager.INTERVAL_DAY * 7,
                                        createPendingIntent(context.getApplicationContext(), getIdAlarm, intentAddAla));
                            } else {
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectedTime,
                                        AlarmManager.INTERVAL_DAY * 7,
                                        createPendingIntent(context.getApplicationContext(), getIdAlarm, intentAddAla));
                            }
                        }
                    }
                } else {
                    long selectedTime = getSelectedTime(0, alarm.getHour(), alarm.getMinute());
                    long differenceBetweenTime = System.currentTimeMillis() - selectedTime;
                    long throughOneDay = AlarmManager.INTERVAL_DAY + selectedTime;
                    Intent intentAddAlar = createIntent(String.valueOf(getIdAlarm), getIdAlarm, false, getSongPathAlarm, false);

                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.toast_start_time) + "\n " + Constants.FORMATS.TERM_TIME_FORMAT.format(selectedTime), Toast.LENGTH_LONG).show();

                    if (differenceBetweenTime < 0) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTime, createPendingIntent(context.getApplicationContext(), getIdAlarm, intentAddAlar));
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, throughOneDay, createPendingIntent(context.getApplicationContext(), getIdAlarm, intentAddAlar));
                    }
                }
                break;
            case "delete":
                Intent intent;
                if (alarm.getRepeat()) {
                    for (int i = 0; i < days.size(); i++) {
                        String keyDay = days.get(i).getDay();
                        boolean valueDay = days.get(i).getValue();
                        if (valueDay) {
                            intent = createIntent(keyDay + getIdAlarm, getIdAlarm, true, getSongPathAlarm, true);
                            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), getIdAlarm, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                    break;
                } else {
                    intent = createIntent(String.valueOf(getIdAlarm), getIdAlarm, false, getSongPathAlarm, false);
                    pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), getIdAlarm, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
        }
    }

    private long getSelectedTime(int indexNumberDay, long hour, long minute) {
        Calendar calendarSelectedDate = Calendar.getInstance();
        if (indexNumberDay != 0) {
            calendarSelectedDate.set(Calendar.DAY_OF_WEEK, indexNumberDay);
        }
        calendarSelectedDate.set(Calendar.HOUR_OF_DAY, (int) hour);
        calendarSelectedDate.set(Calendar.MINUTE, (int) minute);
        calendarSelectedDate.set(Calendar.SECOND, 0);
        calendarSelectedDate.getTimeInMillis();

        return calendarSelectedDate.getTimeInMillis();
    }

    public String contentUriFilename(Uri uri, String columnName) {
        Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, new String[]{columnName}, null, null, null);
        cursor.moveToFirst();
        try {
            return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        } catch (Exception e) {
            return "";
        }
    }

    public void changeStatusAlarm(long id, boolean status) {
        Alarm alarmUpdate;
        int alarmId = (int) id;
        if (alarmId != 0) {
            try {
                alarmUpdate = DatabaseManager.getInstance().getHelper().getAlarmDao().queryForId(alarmId);
                alarmUpdate.setStatus(status);
                DatabaseManager.getInstance().getHelper().getAlarmDao().update(alarmUpdate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate random background colour.
     */
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }
}