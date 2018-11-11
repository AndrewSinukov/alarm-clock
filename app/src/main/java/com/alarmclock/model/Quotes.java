package com.alarmclock.model;


import com.j256.ormlite.field.DatabaseField;

public class Quotes {

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;
    @DatabaseField
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}