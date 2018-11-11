package com.alarmclock.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alarmclock.R;
import com.alarmclock.db.DatabaseManager;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class EditTaskDialogFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private int idAlarm = 0;
    private Alarm mAlarm = null;
    private Days mDays = null;
    private CheckBox repeatAlarm;
    private TimePicker timePicker;
    private long alarmMinute, alarmHour;
    private Boolean alarmRepeat;
    private String alarmMusicName;
    private CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;
    private HashMap<String, Object> daysWeak = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);

        DatabaseManager.getInstance().init(getActivity().getApplicationContext());

        mAlarm = new Alarm();
        mDays = new Days();

        Mon = (CheckBox) view.findViewById(R.id.ckbMon);
        Tue = (CheckBox) view.findViewById(R.id.ckbTue);
        Wed = (CheckBox) view.findViewById(R.id.ckbWed);
        Thu = (CheckBox) view.findViewById(R.id.ckbThu);
        Fri = (CheckBox) view.findViewById(R.id.ckbFri);
        Sat = (CheckBox) view.findViewById(R.id.ckbSat);
        Sun = (CheckBox) view.findViewById(R.id.ckbSun);

        if (getArguments() != null) {
            idAlarm = getArguments().getInt("idAlarm", 0);

            DatabaseManager.getInstance().init(getActivity());
            List<Alarm> listArticle = DatabaseManager.getArticle(idAlarm);

            repeatAlarm = (CheckBox) view.findViewById(R.id.repeatAlarm);
            timePicker = (TimePicker) view.findViewById(R.id.timePicker);
            TextView addCurrentMusic = (TextView) view.findViewById(R.id.addCurrentMusic);
            addCurrentMusic.setOnClickListener(this);

            String currentMusicName = addCurrentMusic.getText().toString();

            Button addAlarm = (Button) view.findViewById(R.id.addAlarm);
            addAlarm.setOnClickListener(this);
            Button cancelAlarm = (Button) view.findViewById(R.id.cancelAlarm);
            cancelAlarm.setOnClickListener(this);

            Button addMusic = (Button) view.findViewById(R.id.addMusic);

            if (!currentMusicName.isEmpty()) {
                addMusic.setVisibility(View.GONE);
                addCurrentMusic.setVisibility(View.VISIBLE);
            } else {
                addMusic.setVisibility(View.VISIBLE);
                addMusic.setOnClickListener(this);
            }

            if (listArticle.size() != 0) {
                for (int g = 0; g < listArticle.size(); g++) {
                    alarmMinute = listArticle.get(g).getMinute();
                    alarmHour = listArticle.get(g).getHour();
                    alarmRepeat = listArticle.get(g).getRepeat();
                    alarmMusicName = listArticle.get(g).getMelodyName();
                }

                addCurrentMusic.setText(alarmMusicName);
                repeatAlarm.setChecked(alarmRepeat);
                timePicker.setCurrentHour((int) alarmHour);
                timePicker.setCurrentMinute((int) alarmMinute);
            }
        }
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addCurrentMusic:
                break;
            case R.id.addAlarm:
                Integer tHour = timePicker.getCurrentHour();
                Integer tMin = timePicker.getCurrentMinute();

                daysWeak.put("M", Mon.isChecked());
                daysWeak.put("T", Tue.isChecked());
                daysWeak.put("W", Wed.isChecked());
                daysWeak.put("Th", Thu.isChecked());
                daysWeak.put("F", Fri.isChecked());
                daysWeak.put("S", Sat.isChecked());
                daysWeak.put("Sn", Sun.isChecked());

                mAlarm.setMinute(tMin);
                mAlarm.setHour(tHour);
                mAlarm.setStatus(true);
                mAlarm.setRepeat(repeatAlarm.isChecked());
                mAlarm.setDeleteAlarm(false);

                try {
                    Alarm alarmUpdate = DatabaseManager.getInstance().getHelper().getAlarmDao().queryForId(idAlarm);
                    alarmUpdate.setMinute(tMin);
                    alarmUpdate.setHour(tHour);
                    alarmUpdate.setStatus(true);
                    alarmUpdate.setRepeat(repeatAlarm.isChecked());
                    alarmUpdate.setDeleteAlarm(false);
                    DatabaseManager.getInstance().getHelper().getAlarmDao().update(alarmUpdate);

                    for (Map.Entry<String, Object> entry : daysWeak.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        mDays.setDay(key);
                        mDays.setClockId(idAlarm);
                        mDays.setValue((Boolean) value);

                        DatabaseManager.getInstance().getHelper().getDaysDao().update(mDays);
                    }

                    Calendar timeToStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    timeToStart.set(Calendar.HOUR_OF_DAY, tHour);
                    timeToStart.set(Calendar.MINUTE, tMin);
                    timeToStart.set(Calendar.SECOND, 0);
                    long timeToStartM = timeToStart.getTimeInMillis();

                    Calendar curTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    curTime.get(Calendar.HOUR_OF_DAY);
                    curTime.get(Calendar.MINUTE);
                    curTime.get(Calendar.SECOND);
                    long curTimeM = curTime.getTimeInMillis();

                    SimpleDateFormat TERM_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    TERM_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

                    long difr = (timeToStartM - curTimeM - 7200000);

                    String curTimeSD = TERM_DATE_FORMAT.format(difr);

                    Toast.makeText(getActivity(), getResources().getString(R.string.alarm_start_time) + "\n  " + curTimeSD, Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                } catch (SQLException e) {
                    Log.d("MapTest", "e " + e);
                    e.printStackTrace();
                }
                break;
            case R.id.cancelAlarm:
                getActivity().onBackPressed();
                break;
        }
    }
}