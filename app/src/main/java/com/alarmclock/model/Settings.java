package com.alarmclock.model;


public class Settings {

    private String language;
    private long volume;
    private String defaultMelodyName;
    private String defaultMelodyPath;
    private long timePlaySong;
    private long repeatTimeInterval;

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getDefaultMelodyName() {
        return defaultMelodyName;
    }

    public void setDefaultMelodyName(String defaultMelodyName) {
        this.defaultMelodyName = defaultMelodyName;
    }

    public String getDefaultMelodyPath() {
        return defaultMelodyPath;
    }

    public void setDefaultMelodyPath(String defaultMelodyPath) {
        this.defaultMelodyPath = defaultMelodyPath;
    }

    public String getLanguage() {
        return language;

    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public long getTimePlaySong() {
        return timePlaySong;
    }

    public void setTimePlaySong(long timePlaySong) {
        this.timePlaySong = timePlaySong;
    }

    public long getRepeatTimeInterval() {
        return repeatTimeInterval;
    }

    public void setRepeatTimeInterval(long repeatTimeInterval) {
        this.repeatTimeInterval = repeatTimeInterval;
    }
}
