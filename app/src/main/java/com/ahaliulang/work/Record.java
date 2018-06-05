package com.ahaliulang.work;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Record extends RealmObject{


    private String date;

    private String week;

    private String time;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
