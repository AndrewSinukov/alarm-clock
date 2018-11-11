package com.alarmclock.db;

import android.content.Context;
import android.util.Log;

import com.alarmclock.db.dao.DaoAlarm;
import com.alarmclock.db.dao.DaoDays;
import com.alarmclock.model.Alarm;
import com.alarmclock.model.Days;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private DatabaseHelper helper;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public static void deleteAlarm(int id) throws SQLException {
        DaoAlarm daoAlarm = DatabaseManager.getInstance().getHelper().getAlarmDao();
        DeleteBuilder<Alarm, Integer> deleteAlarmBuilder = daoAlarm.deleteBuilder();
        deleteAlarmBuilder.where().eq("_id",id);
        deleteAlarmBuilder.delete();

        DaoDays daoDays = DatabaseManager.getInstance().getHelper().getDaysDao();
        DeleteBuilder<Days, Integer> deleteDaysBuilder = daoDays.deleteBuilder();
        deleteDaysBuilder.where().eq("clock_id",id);
        deleteDaysBuilder.delete();
    }

    public void init(Context context) {
        if (helper == null) helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static List getArticle(int articleId) {
        List<Alarm> listArticle = new ArrayList<>();
        try {
            DaoAlarm daoArticle = DatabaseManager.getInstance().getHelper().getAlarmDao();
            QueryBuilder<Alarm, Integer> queryBuilderGoal = daoArticle.queryBuilder();
            queryBuilderGoal.where().eq("_id", articleId).query();
            listArticle = queryBuilderGoal.query();
        } catch (SQLException e) {
            Log.i("DatabaseManager", "error " + e);
            e.printStackTrace();
        }
        return listArticle;
    }

    public DatabaseHelper getHelper() {
        return helper;
    }
}