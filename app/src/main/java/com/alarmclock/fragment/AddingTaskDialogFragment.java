package com.alarmclock.fragment;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.db.DatabaseHelper;
import com.alarmclock.db.DatabaseManager;
import com.alarmclock.db.dao.DaoAlarm;
import com.alarmclock.helper.FragmentHelper;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddingTaskDialogFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected List<Alarm> alarmList = null;
    private CheckBox repeatAlarm, vibrateAlarm;
    private Button addMusic;
    private RelativeLayout intervalData;
    private LinearLayout wrapperCheckBox;
    private TimePicker timePicker;
    private Alarm mAlarm = null;
    private Days mDays = null;
    private CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;
    private TextView addCurrentMusic, volumeAlarmAppValue;
    private FragmentHelper fragmentHelper;
    private View view;
    private List<Days> days = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        mAlarm = new Alarm();
        mDays = new Days();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        String appMelodyPath = sharedPreferences.getString(Constants.SHARED_PREF.APP_MELODY_PATH, "");
        String appMelodyName = sharedPreferences.getString(Constants.SHARED_PREF.APP_MELODY_NAME, "");

        FragmentHelper.getInstance().init(getActivity().getApplicationContext());
        fragmentHelper = FragmentHelper.getInstance();

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Mon = (CheckBox) view.findViewById(R.id.ckbMon);
        Tue = (CheckBox) view.findViewById(R.id.ckbTue);
        Wed = (CheckBox) view.findViewById(R.id.ckbWed);
        Thu = (CheckBox) view.findViewById(R.id.ckbThu);
        Fri = (CheckBox) view.findViewById(R.id.ckbFri);
        Sat = (CheckBox) view.findViewById(R.id.ckbSat);
        Sun = (CheckBox) view.findViewById(R.id.ckbSun);
        Mon.setOnCheckedChangeListener(this);
        Tue.setOnCheckedChangeListener(this);
        Wed.setOnCheckedChangeListener(this);
        Thu.setOnCheckedChangeListener(this);
        Fri.setOnCheckedChangeListener(this);
        Sat.setOnCheckedChangeListener(this);
        Sun.setOnCheckedChangeListener(this);

        volumeAlarmAppValue = (TextView) view.findViewById(R.id.volumeAlarmAppValue);
        repeatAlarm = (CheckBox) view.findViewById(R.id.repeatAlarm);
        SeekBar volumeAlarm = (SeekBar) view.findViewById(R.id.volumeAlarm);
        volumeAlarm.setOnSeekBarChangeListener(this);
        vibrateAlarm = (CheckBox) view.findViewById(R.id.vibrateAlarm);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        addCurrentMusic = (TextView) view.findViewById(R.id.addCurrentMusic);

        repeatAlarm.setOnCheckedChangeListener(this);

        addMusic = (Button) view.findViewById(R.id.addMusic);

        if (!appMelodyName.isEmpty()) {
            addCurrentMusic.setText(appMelodyName);
            addCurrentMusic.setVisibility(View.VISIBLE);
            addMusic.setVisibility(View.GONE);
            mAlarm.setMelodyName(appMelodyName);
        }

        if (!appMelodyPath.isEmpty()) {
            mAlarm.setMelodyPath(appMelodyPath);
        }

        Button addAlarm = (Button) view.findViewById(R.id.addAlarm);
        Button cancelAlarm = (Button) view.findViewById(R.id.cancelAlarm);
        addMusic.setOnClickListener(this);
        addAlarm.setOnClickListener(this);
        cancelAlarm.setOnClickListener(this);
        addCurrentMusic.setOnClickListener(this);

        intervalData = (RelativeLayout) view.findViewById(R.id.intervalData);
        wrapperCheckBox = (LinearLayout) view.findViewById(R.id.wrapperCheckBox);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.volumeAlarm:
                volumeAlarmAppValue.setText(String.valueOf(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ckbMon:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbTue:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbWed:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbThu:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbFri:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbSat:
                hideShowBlockCheckBox();
                break;
            case R.id.ckbSun:
                hideShowBlockCheckBox();
                break;

            case R.id.repeatAlarm:
                if (isChecked) {
                    blockCheckBox(true);
                    intervalData.setVisibility(View.VISIBLE);
                } else {
                    intervalData.setVisibility(View.GONE);
                    blockCheckBox(false);
                }
                break;
        }
    }

    private void blockCheckBox(boolean type) {
        Mon.setChecked(type);
        Tue.setChecked(type);
        Wed.setChecked(type);
        Thu.setChecked(type);
        Fri.setChecked(type);
        Sat.setChecked(type);
        Sun.setChecked(type);
    }

    private void hideShowBlockCheckBox() {
        int countCb = 0;
        for (int i = 0; i < wrapperCheckBox.getChildCount(); i++) {
            View v = wrapperCheckBox.getChildAt(i);
            CheckBox checkBox = (CheckBox) view.findViewById(v.getId());
            checkBox.isChecked();
            if (!checkBox.isChecked()) {
                countCb++;
            }
            if (countCb == wrapperCheckBox.getChildCount()) {
                intervalData.setVisibility(View.GONE);
                repeatAlarm.setChecked(false);
            } else {
                intervalData.setVisibility(View.VISIBLE);
                repeatAlarm.setChecked(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RESULT.RESULT_LOAD_MUSIC && data != null) {
            mAlarm.setMelodyPath(String.valueOf(data.getData()));
            String fileName = fragmentHelper.contentUriFilename(data.getData(), MediaStore.MediaColumns.DISPLAY_NAME);
            mAlarm.setMelodyName(fileName);
            addMusic.setVisibility(View.GONE);
            addCurrentMusic.setText(fileName);
            addCurrentMusic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addMusic:
            case R.id.addCurrentMusic:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.RESULT.RESULT_LOAD_MUSIC);
                break;
            case R.id.addAlarm:
                int timeHourAlarm = timePicker.getCurrentHour();
                int timeMinuteAlarm = timePicker.getCurrentMinute();
                if (mAlarm.getMelodyPath() != null && !mAlarm.getMelodyPath().isEmpty()) {
                    mAlarm.setMelodyPath(mAlarm.getMelodyPath());
                }

                if (mAlarm.getMelodyName() != null && !mAlarm.getMelodyName().isEmpty()) {
                    mAlarm.setMelodyName(mAlarm.getMelodyName());
                }
                days.add(new Days(2, "M", Mon.isChecked()));
                days.add(new Days(3, "T", Tue.isChecked()));
                days.add(new Days(4, "W", Wed.isChecked()));
                days.add(new Days(5, "Th", Thu.isChecked()));
                days.add(new Days(6, "F", Fri.isChecked()));
                days.add(new Days(7, "S", Sat.isChecked()));
                days.add(new Days(1, "Sn", Sun.isChecked()));

                mAlarm.setMinute(timeMinuteAlarm);
                mAlarm.setHour(timeHourAlarm);
                mAlarm.setStatus(true);
                mAlarm.setRepeat(repeatAlarm.isChecked());
                mAlarm.setDeleteAlarm(false);
                mAlarm.setVibrate(vibrateAlarm.isChecked());
                String checkRequiredFields = mAlarm.checkRequiredFields();
                if (!checkRequiredFields.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), checkRequiredFields, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    DaoAlarm daoAlarm = DatabaseManager.getInstance().getHelper().getAlarmDao();
                    daoAlarm.create(mAlarm);
                    QueryBuilder<Alarm, Integer> queryBuilderGoal = daoAlarm.queryBuilder();
                    alarmList = queryBuilderGoal.query();
                    int getLastInsertItemAlarm = alarmList.size() - 1;
                    int getLastInsertIdAlarm = (int) alarmList.get(getLastInsertItemAlarm).getId();
                    String getLastInsertSongPathAlarm = alarmList.get(getLastInsertItemAlarm).getMelodyPath();

                    fragmentHelper.changeAlarmManagerClockStatus("add", getLastInsertIdAlarm, getLastInsertSongPathAlarm, mAlarm, days);
                    if (mAlarm.getRepeat()) {
                        for (int i = 0; i < days.size(); i++) {
                            mDays.setDay(days.get(i).getDay());
                            mDays.setClockId(getLastInsertIdAlarm);
                            mDays.setValue(days.get(i).getValue());
                            DatabaseManager.getInstance().getHelper().getDaysDao().create(mDays);
                        }
                    }
                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    databaseHelper.close();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.listAlarms:
                menu.add(0, Constants.MENU.MENU_DELETE, 0, getResources().getString(R.string.menu_delete));
                menu.add(0, Constants.MENU.MENU_EDIT, 0, getResources().getString(R.string.menu_edit));
                break;
        }
    }
}