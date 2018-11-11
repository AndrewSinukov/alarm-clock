package com.alarmclock.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alarmclock.R;
import com.alarmclock.adapter.AlarmListAdapter;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;

import java.util.ArrayList;
import java.util.List;

public class AlarmListOnFragment extends MainFragment implements AlarmListAdapter.ClickListener {
    // To do finish this class
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        List<Alarm> recyclerAlarm = getAlarms(true);
        List<Days> recyclerDays = getDays();
        return view;
    }

}