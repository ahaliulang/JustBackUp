package com.ahaliulang.work;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sleep extends RealmObject {
    @PrimaryKey
    private long id;

    private String date;

    private String week;

    private String goToBedTime;

    private String wakeUpTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getGoToBedTime() {
        return goToBedTime;
    }

    public void setGoToBedTime(String goToBedTime) {
        this.goToBedTime = goToBedTime;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(String wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }
}