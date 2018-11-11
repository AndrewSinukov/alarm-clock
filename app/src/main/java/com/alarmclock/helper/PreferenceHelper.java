package com.alarmclock.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class PreferenceHelper {

    private static PreferenceHelper instance;
    private static SharedPreferences sPreferences;
    private Context context;

    private PreferenceHelper() {
    }

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        sPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return sPreferences.getLong(key, 0);
    }

    public boolean getBoolean(String key) {
        return sPreferences.getBoolean(key, false);
    }

    public void setLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}