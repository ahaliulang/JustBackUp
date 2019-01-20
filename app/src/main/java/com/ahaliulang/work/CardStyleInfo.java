package com.ahaliulang.work;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class CardStyleInfo {
    public List<Bitmap> bitmapInfos = new ArrayList<>(); //第一个一定要是个人头像
    public String qrUrl; //二维码地址
    public String day; //昵称
    public String introduction;//简介
    public String author;//作者
}
