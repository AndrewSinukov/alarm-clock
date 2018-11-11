package com.alarmclock.db.dao;

import com.alarmclock.model.Days;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class DaoDays extends BaseDao<Days> {

    public DaoDays(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Days.class);
    }
}