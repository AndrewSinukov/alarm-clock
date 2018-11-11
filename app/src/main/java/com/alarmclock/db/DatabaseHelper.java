package com.alarmclock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alarmclock.Constants;
import com.alarmclock.db.dao.DaoAlarm;
import com.alarmclock.db.dao.DaoDays;
import com.alarmclock.db.dao.DaoQuotes;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;
import com.alarmclock.model.Quotes;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private DaoAlarm daoAlarm = null;
    private DaoDays daoDays = null;
    private DaoQuotes daoQuotes = null;

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE.DATABASE_NAME, null, Constants.DATABASE.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Alarm.class);
            TableUtils.createTable(connectionSource, Days.class);
            TableUtils.createTable(connectionSource, Quotes.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Alarm.class, true);
            TableUtils.dropTable(connectionSource, Days.class, true);
            TableUtils.dropTable(connectionSource, Quotes.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public DaoQuotes getQuotesDao() throws SQLException {
        if (daoQuotes == null) {
            daoQuotes = new DaoQuotes(getConnectionSource());
        }
        return daoQuotes;
    }

    public DaoDays getDaysDao() throws SQLException {
        if (daoDays == null) {
            daoDays = new DaoDays(getConnectionSource());
        }
        return daoDays;
    }

    public DaoAlarm getAlarmDao() throws SQLException {
        if (daoAlarm == null) {
            daoAlarm = new DaoAlarm(getConnectionSource());
        }
        return daoAlarm;
    }

    @Override
    public void close() {
        super.close();
        daoAlarm = null;
        daoDays = null;
        daoQuotes = null;
    }
}