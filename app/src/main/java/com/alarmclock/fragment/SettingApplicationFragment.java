package com.alarmclock.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.helper.FragmentHelper;
import com.alarmclock.model.Settings;

public class SettingApplicationFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar timePlaySong, repeatTimeIntervalAlarm, volumeAlarmApp;
    private Spinner languageApp;
    private FragmentHelper fragmentHelper;
    private Settings mSettings;
    private SharedPreferences sSharedPreferences;
    private TextView tVDefaultMelodyApp, volumeAlarmAppValue, timePlaySongAppValue, repeatTimeIntervalAppValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_application, container, false);

        sSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);

        String melodyName = sSharedPreferences.getString(Constants.SHARED_PREF.APP_MELODY_NAME, "");
        String language = sSharedPreferences.getString(Constants.SHARED_PREF.APP_LANGUAGE, "");
        int repeat_time_interval = (int) sSharedPreferences.getLong(Constants.SHARED_PREF.APP_REPEAT_TIME_INTERVAL, 0);
        int volume = (int) sSharedPreferences.getLong(Constants.SHARED_PREF.APP_VOLUME, 0);
        int repeat_time_play_melody = (int) sSharedPreferences.getLong(Constants.SHARED_PREF.APP_REPEAT_TIME_PLAY_MELODY, 0);

        mSettings = new Settings();

        FragmentHelper.getInstance().init(getActivity().getApplicationContext());
        fragmentHelper = FragmentHelper.getInstance();

        timePlaySong = (SeekBar) view.findViewById(R.id.timePlaySongApp);
        repeatTimeIntervalAlarm = (SeekBar) view.findViewById(R.id.repeatTimeIntervalApp);
        volumeAlarmApp = (SeekBar) view.findViewById(R.id.volumeAlarmApp);

        tVDefaultMelodyApp = (TextView) view.findViewById(R.id.tVDefaultMelodyApp);
        volumeAlarmAppValue = (TextView) view.findViewById(R.id.volumeAlarmAppValue);
        timePlaySongAppValue = (TextView) view.findViewById(R.id.timePlaySongAppValue);
        repeatTimeIntervalAppValue = (TextView) view.findViewById(R.id.repeatTimeIntervalAppValue);

        if (volume != 0) {
            volumeAlarmApp.setProgress(volume);
            volumeAlarmAppValue.setText(String.valueOf(volume));
        }

        if (repeat_time_interval != 0) {
            repeatTimeIntervalAlarm.setProgress(repeat_time_interval);
            repeatTimeIntervalAppValue.setText(String.valueOf(repeat_time_interval));
        }

        if (repeat_time_play_melody != 0) {
            timePlaySong.setProgress(repeat_time_play_melody);
            timePlaySongAppValue.setText(String.valueOf(repeat_time_play_melody));
        }

        timePlaySong.setOnSeekBarChangeListener(this);
        repeatTimeIntervalAlarm.setOnSeekBarChangeListener(this);
        volumeAlarmApp.setOnSeekBarChangeListener(this);

        languageApp = (Spinner) view.findViewById(R.id.languageApp);
        String[] items = new String[]{"en", "ru", "bg"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, items);
        languageApp.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(language);

        languageApp.setSelection(spinnerPosition);

        Button defaultMelodyApp = (Button) view.findViewById(R.id.defaultMelodyApp);

        if (!melodyName.isEmpty()) {
            defaultMelodyApp.setVisibility(View.GONE);
            tVDefaultMelodyApp.setVisibility(View.VISIBLE);
            tVDefaultMelodyApp.setText(melodyName);
        }

        Button saveSetting = (Button) view.findViewById(R.id.saveSetting);
        Button saveCancel = (Button) view.findViewById(R.id.saveCancel);
        defaultMelodyApp.setOnClickListener(this);
        tVDefaultMelodyApp.setOnClickListener(this);
        saveSetting.setOnClickListener(this);
        saveCancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RESULT.RESULT_LOAD_MUSIC && data != null) {
            String fileName = fragmentHelper.contentUriFilename(data.getData(), MediaStore.MediaColumns.DISPLAY_NAME);
            tVDefaultMelodyApp.setText(fileName);
            mSettings.setDefaultMelodyName(fileName);
            mSettings.setDefaultMelodyPath(String.valueOf(data.getData()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.defaultMelodyApp:
            case R.id.tVDefaultMelodyApp:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.RESULT.RESULT_LOAD_MUSIC);
                break;
            case R.id.saveSetting:
                mSettings.setLanguage(languageApp.getSelectedItem().toString());

                mSettings.setRepeatTimeInterval(repeatTimeIntervalAlarm.getProgress());
                mSettings.setTimePlaySong(timePlaySong.getProgress());
                mSettings.setVolume(volumeAlarmApp.getProgress());

                SharedPreferences.Editor editor = sSharedPreferences.edit();
                editor.putString(Constants.SHARED_PREF.APP_LANGUAGE, mSettings.getLanguage());
                editor.putLong(Constants.SHARED_PREF.APP_REPEAT_TIME_PLAY_MELODY, mSettings.getTimePlaySong());
                editor.putLong(Constants.SHARED_PREF.APP_REPEAT_TIME_INTERVAL, mSettings.getRepeatTimeInterval());
                editor.putLong(Constants.SHARED_PREF.APP_VOLUME, mSettings.getVolume());
                editor.putString(Constants.SHARED_PREF.APP_MELODY_NAME, mSettings.getDefaultMelodyName());
                editor.putString(Constants.SHARED_PREF.APP_MELODY_PATH, mSettings.getDefaultMelodyPath());
                editor.apply();

                String melodyName = sSharedPreferences.getString(Constants.SHARED_PREF.APP_MELODY_NAME, "");

                getActivity().onBackPressed();
                break;
            case R.id.saveCancel:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            case R.id.timePlaySongApp:
                timePlaySongAppValue.setText(String.valueOf(progress));
                break;
            case R.id.repeatTimeIntervalApp:
                repeatTimeIntervalAppValue.setText(String.valueOf(progress));
                break;
            case R.id.volumeAlarmApp:
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
}