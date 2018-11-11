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

public class AlarmListOffFragment extends MainFragment {
    // To do finish this class
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        List<Alarm> recyclerAlarm = getAlarms(false);
        List<Days> recyclerDays = getDays();

        if (recyclerAlarm != null) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listAlarms);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(llm);

            AlarmListAdapter alarmRecyclerAdapter = new AlarmListAdapter(getActivity().getApplicationContext(), recyclerAlarm, recyclerDays);
            alarmRecyclerAdapter.setClickListener(this);
            registerForContextMenu(recyclerView);

            recyclerView.setAdapter(alarmRecyclerAdapter);
        }

        return view;
    }
}
