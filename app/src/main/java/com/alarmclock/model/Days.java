package com.alarmclock.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Days {

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;
    @DatabaseField
    private String day;
    @DatabaseField(columnName = "clock_id")
    private int clockId;
    @DatabaseField
    private boolean value;

    public Days(int id, String day, boolean value) {
        this.id = id;
        this.day = day;
        this.value = value;
    }

    public Days() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getClockId() {
        return clockId;
    }

    public void setClockId(int clockId) {
        this.clockId = clockId;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
