package com.alarmclock.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.adapter.AlarmListAdapter;
import com.alarmclock.adapter.TabAdapter;
import com.alarmclock.db.DatabaseHelper;
import com.alarmclock.db.DatabaseManager;
import com.alarmclock.db.dao.DaoAlarm;
import com.alarmclock.db.dao.DaoDays;
import com.alarmclock.helper.FragmentHelper;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements AlarmListAdapter.ClickListener {

    private List<Alarm> recyclerAlarm = new ArrayList<>();
    private List<Days> recyclerDays = new ArrayList<>();
    private int currentPosition;
    private AlarmListAdapter alarmRecyclerAdapter;
    private LinearLayoutManager llm;
    private FragmentHelper fragmentHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.alarm_on)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.alarm_off)));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        TabAdapter tabAdapter = new TabAdapter(getFragmentManager(), 2);

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        FragmentHelper.getInstance().init(getActivity().getApplicationContext());
        fragmentHelper = FragmentHelper.getInstance();

        LinearLayout noAlarms = (LinearLayout) view.findViewById(R.id.noAlarms);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        DatabaseManager.getInstance().init(getActivity().getApplicationContext());
        setUI(view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listAlarms);

        recyclerAlarm = getAlarms(true);
        recyclerDays = getDays();

        if (recyclerAlarm.size() != 0) {
            noAlarms.setVisibility(View.GONE);
            llm = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(llm);

            alarmRecyclerAdapter = new AlarmListAdapter(getActivity().getApplicationContext(), recyclerAlarm, recyclerDays);
            alarmRecyclerAdapter.setClickListener(this);
            registerForContextMenu(recyclerView);
            recyclerView.setAdapter(alarmRecyclerAdapter);
        } else {
            noAlarms.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setUI(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingTaskDialogFragment addingFrag = new AddingTaskDialogFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addingFrag, "TAG_FRAGMENT_ADDING")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    protected List<Alarm> getAlarms(boolean statusAlarm) {
        DaoAlarm daoAlarm;
        DaoDays daoDays;
        try {
            daoAlarm = DatabaseManager.getInstance().getHelper().getAlarmDao();
            QueryBuilder<Alarm, Integer> queryBuilderAlarm = daoAlarm.queryBuilder();
            queryBuilderAlarm.where().eq("status", statusAlarm).query();
            recyclerAlarm = queryBuilderAlarm.query();
            for (int j = 0; j < recyclerAlarm.size(); j++) {
                daoDays = DatabaseManager.getInstance().getHelper().getDaysDao();
                QueryBuilder<Days, Integer> queryBuilderDays = daoDays.queryBuilder();
                queryBuilderDays.where().eq("clock_id", recyclerAlarm.get(j).getId()).query();
            }
        } catch (SQLException e) {
            Log.d("MapTest", "e " + e);
            e.printStackTrace();
        }
        return recyclerAlarm;
    }

    protected List<Days> getDays() {
        DaoDays daoDays;
        try {
            daoDays = DatabaseManager.getInstance().getHelper().getDaysDao();
            QueryBuilder<Days, Integer> queryBuilderDays = daoDays.queryBuilder();
            recyclerDays = queryBuilderDays.query();

        } catch (SQLException e) {
            Log.d("MapTest", "e " + e);
            e.printStackTrace();
        }
        return recyclerDays;
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

    @Override
    public void itemCliked(View view, int position) {
        currentPosition = position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (alarmRecyclerAdapter != null) {
            int currentId = (int) alarmRecyclerAdapter.getItem(currentPosition).getId();
            Alarm alarmModel = alarmRecyclerAdapter.getItem(currentPosition);
            switch (item.getItemId()) {
                case Constants.MENU.MENU_DELETE:
                    if (recyclerAlarm.size() != 0) {
                        recyclerAlarm.remove(currentPosition);
                    }
                    llm.removeViewAt(currentPosition);
                    alarmRecyclerAdapter.notifyItemRemoved(currentPosition);
                    alarmRecyclerAdapter.notifyItemRangeChanged(currentPosition, recyclerAlarm.size());

                    alarmRecyclerAdapter.notifyDataSetChanged();
                    fragmentHelper.changeAlarmManagerClockStatus("delete", currentId, "", alarmModel, recyclerDays);
                    try {
                        DatabaseManager.deleteAlarm(currentId);
                        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                        databaseHelper.close();
                        Toast.makeText(getActivity(), getResources().getString(R.string.alarm_clock_deleted), Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MENU.MENU_EDIT:
                    EditTaskDialogFragment editFrag = new EditTaskDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("idAlarm", currentId);
                    editFrag.setArguments(bundle);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, editFrag, "TAG_FRAGMENT_EDIT")
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

// To do tabs on MainFragment
//    @Override
//    public void onTabSelected(TabLayout.Tab tab) {
//        viewPager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabUnselected(TabLayout.Tab tab) {
//
//    }
//
//    @Override
//    public void onTabReselected(TabLayout.Tab tab) {
//
//    }
}