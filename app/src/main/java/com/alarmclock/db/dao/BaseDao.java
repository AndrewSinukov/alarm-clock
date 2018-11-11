package com.alarmclock.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public abstract class BaseDao<T> extends BaseDaoImpl<T, Integer> {

    protected BaseDao(ConnectionSource source, Class<T> dataClass) throws SQLException {
        super(source, dataClass);
    }
}