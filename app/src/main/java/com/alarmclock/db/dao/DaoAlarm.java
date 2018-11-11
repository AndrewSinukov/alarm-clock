package com.alarmclock.db.dao;

import com.alarmclock.model.Alarm;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class DaoAlarm extends BaseDao<Alarm> {

    public DaoAlarm(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Alarm.class);
    }
}