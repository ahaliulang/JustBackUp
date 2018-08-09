package com.ahaliulang.work;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Egg extends RealmObject {
    @PrimaryKey
    private long id;
    private String left;
    private String loss;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }
}
