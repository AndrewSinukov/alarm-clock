package com.alarmclock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alarmclock.R;
import com.alarmclock.helper.FragmentHelper;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;

import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AlarmViewHolder alarmViewHolder;
    private List<Alarm> alarm;
    private List<Days> days;
    private Alarm mAlarm;
    private Context context;
    private ClickListener clickListener;
    private FragmentHelper fragmentHelper;

    public AlarmListAdapter(Context context, List<Alarm> recyclerAlarm, List<Days> recyclerDays) {
        this.alarm = recyclerAlarm;
        this.days = recyclerDays;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void delete(int position) {
        alarm.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmViewHolder(view);
    }

    public Alarm getItem(int position) {
        return alarm.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        mAlarm = alarm.get(i);
        alarmViewHolder = (AlarmViewHolder) viewHolder;
        alarmViewHolder.timeAlarm.setText(mAlarm.getHour() + " : " + mAlarm.getMinute());
        alarmViewHolder.currentMusicAlarm.setText(mAlarm.getMelodyName());
        alarmViewHolder.repeatAlarm.setChecked(mAlarm.getRepeat());
        alarmViewHolder.statusBtn.setChecked(mAlarm.getStatus());
        alarmViewHolder.vibrateAlarm.setChecked(mAlarm.getVibrate());

        if (!mAlarm.getRepeat()) {
            alarmViewHolder.intervalData.setVisibility(View.GONE);
        } else {
            alarmViewHolder.intervalData.setVisibility(View.VISIBLE);
        }

        drawDays(i);
    }

    private void drawDays(int i) {
        for (int j = 0; j < days.size(); j++) {
            if (alarm.get(i).getRepeat() && alarm.get(i).getId() == days.get(j).getClockId()) {
                switch (days.get(j).getDay()) {
                    case "Sn":
                        alarmViewHolder.Sun.setChecked(days.get(j).getValue());
                        break;
                    case "M":
                        alarmViewHolder.Mon.setChecked(days.get(j).getValue());
                        break;
                    case "T":
                        alarmViewHolder.Tue.setChecked(days.get(j).getValue());
                        break;
                    case "W":
                        alarmViewHolder.Wed.setChecked(days.get(j).getValue());
                        break;
                    case "Th":
                        alarmViewHolder.Thu.setChecked(days.get(j).getValue());
                        break;
                    case "F":
                        alarmViewHolder.Fri.setChecked(days.get(j).getValue());
                        break;
                    case "S":
                        alarmViewHolder.Sat.setChecked(days.get(j).getValue());
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return alarm.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemCliked(View view, int position);
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private RecyclerView rv;
        private TextView timeAlarm, currentMusicAlarm;
        private ToggleButton statusBtn;
        private RelativeLayout intervalData;
        private CheckBox repeatAlarm, vibrateAlarm, Mon, Tue, Wed, Thu, Fri, Sat, Sun;

        AlarmViewHolder(View itemView) {
            super(itemView);
            FragmentHelper.getInstance().init(context);
            fragmentHelper = FragmentHelper.getInstance();

            rv = (RecyclerView) itemView.findViewById(R.id.listAlarms);
            timeAlarm = (TextView) itemView.findViewById(R.id.timeAlarm);
            intervalData = (RelativeLayout) itemView.findViewById(R.id.intervalData);
            statusBtn = (ToggleButton) itemView.findViewById(R.id.statusBtn);
            statusBtn.setOnClickListener(this);
            repeatAlarm = (CheckBox) itemView.findViewById(R.id.repeatAlarm);
            vibrateAlarm = (CheckBox) itemView.findViewById(R.id.vibrateAlarm);
            currentMusicAlarm = (TextView) itemView.findViewById(R.id.currentMusicAlarm);

            Mon = (CheckBox) itemView.findViewById(R.id.M);
            Tue = (CheckBox) itemView.findViewById(R.id.T);
            Wed = (CheckBox) itemView.findViewById(R.id.W);
            Thu = (CheckBox) itemView.findViewById(R.id.Th);
            Fri = (CheckBox) itemView.findViewById(R.id.F);
            Sat = (CheckBox) itemView.findViewById(R.id.S);
            Sun = (CheckBox) itemView.findViewById(R.id.Sn);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null) {
                clickListener.itemCliked(view, getPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                Alarm checkAlarm = alarm.get(getPosition());
                delete(getPosition());
                fragmentHelper.changeStatusAlarm(checkAlarm.getId(), statusBtn.isChecked());
                fragmentHelper.changeAlarmManagerClockStatus("delete", (int) checkAlarm.getId(), checkAlarm.getMelodyPath(), mAlarm, days);
            }
        }
    }
}