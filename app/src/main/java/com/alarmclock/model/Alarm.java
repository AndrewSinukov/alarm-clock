package com.alarmclock.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Alarm {

    @DatabaseField(generatedId = true, columnName = "_id")
    private long id;
    @DatabaseField
    private long minute;
    @DatabaseField
    private long hour;
    @DatabaseField(columnName = "previous_time")
    private long previousTime;
    @DatabaseField(columnName = "repeat_song")
    private long repeatSong;
    @DatabaseField
    private boolean status;
    @DatabaseField
    private boolean repeat;
    @DatabaseField(columnName = "delete_alarm")
    private boolean deleteAlarm;
    @DatabaseField(columnName = "melody_path")
    private String melodyPath;
    @DatabaseField(columnName = "melody_name")
    private String melodyName;
    @DatabaseField(columnName = "vibrate")
    private boolean vibrate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getPrevious_time() {
        return previousTime;
    }

    public void setPrevious_time(long previous_time) {
        this.previousTime = previous_time;
    }

    public long getRepeat_song() {
        return repeatSong;
    }

    public void setRepeat_song(long repeat_song) {
        this.repeatSong = repeat_song;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean getDelete_alarm() {
        return deleteAlarm;
    }

    public void setDeleteAlarm(boolean delete_alarm) {
        this.deleteAlarm = delete_alarm;
    }

    public String getMelodyPath() {
        return melodyPath;
    }

    public void setMelodyPath(String melody) {
        this.melodyPath = melody;
    }

    public String getMelodyName() {
        return melodyName;
    }

    public void setMelodyName(String melody) {
        this.melodyName = melody;
    }

    public boolean getVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String checkRequiredFields() {
        String error = "";
        if (getMelodyName() == null) {
            error += " Field Melody Name Empty \n";
        }

        if (getMelodyPath() == null) {
            error += " Field Melody Path Empty  \n";
        }
        return error;
    }
}
