package com.alarmclock;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public final static String QUOITE_RANDOW = "quoite";

    public static class DATABASE {
        public static final String DATABASE_NAME = "alarm.db";
        public static final int DATABASE_VERSION = 1;
    }

    public static class FORMATS {
        public static final SimpleDateFormat TERM_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        public static final SimpleDateFormat TERM_TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
        public static final SimpleDateFormat DAY_MONTH = new SimpleDateFormat("dd/MM", Locale.getDefault());
        public static final SimpleDateFormat MONTH = new SimpleDateFormat("MMMM", Locale.getDefault());
        public static final String PROGRESS = "%.0f %s";
    }

    public static class SHARED_PREF {
        public static final String APP_MELODY_PATH = "app_melody_path";
        public static final String APP_MELODY_NAME = "app_melody_name";
        public static final String APP_LANGUAGE = "app_language";
        public static final String APP_REPEAT_TIME_INTERVAL = "app_repeat_time_interval";
        public static final String APP_VOLUME = "app_volume";
        public static final String APP_REPEAT_TIME_PLAY_MELODY = "app_repeat_time_play_melody";
        public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";
    }

    public static class MENU {
        public static final int MENU_DELETE = 1;
        public static final int MENU_EDIT = 2;
    }

    public static class RESULT {
        public static final int RESULT_LOAD_MUSIC = 1;
    }
}