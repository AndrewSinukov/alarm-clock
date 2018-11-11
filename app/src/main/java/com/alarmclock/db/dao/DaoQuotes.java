package com.alarmclock.db.dao;

import com.alarmclock.model.Quotes;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class DaoQuotes extends BaseDao<Quotes> {

    public DaoQuotes(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Quotes.class);
    }
}