package com.ahaliulang.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button beforeBtn;
    private Button afterBtn;
    private TextView dateTv;
    private ListView monthList;
    private TextView mCountTv;

    private long index;
    private Realm realm;
    private RealmResults<Record> records;
    private RealmResults<Record> allRecords;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        allRecords = realm.where(Record.class).findAll();
        myAdapter = new MyAdapter(this, allRecords);
        index = allRecords.size();
        records = realm.where(Record.class).equalTo("date", TimeUtil.getDate()).findAll();
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        if (records != null && records.size() > 0) {
            if (!TextUtils.isEmpty(records.get(0).getBeforeTime())) {
                beforeBtn.setText("已打卡_" + records.get(0).getBeforeTime());
                beforeBtn.setClickable(false);
                beforeBtn.setBackgroundResource(R.drawable.unclicked);
            }

            if (!TextUtils.isEmpty(records.get(0).getAfterTime())) {
                afterBtn.setText("已打卡_" + records.get(0).getAfterTime());
                afterBtn.setClickable(false);
                afterBtn.setBackgroundResource(R.drawable.unclicked);
            }
        }
    }

    private void initView() {
        beforeBtn = findViewById(R.id.before_btn);
        afterBtn = findViewById(R.id.after_btn);
        dateTv = findViewById(R.id.date_tv);
        dateTv.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        monthList = findViewById(R.id.month_list);
        mCountTv = findViewById(R.id.count_day);
        mCountTv.setText("第" + String.valueOf(TimeUtil.countDays() + 1) + "天");
        monthList.setAdapter(myAdapter);
    }


    private void initListener() {
        beforeBtn.setOnClickListener(this);
        afterBtn.setOnClickListener(this);
        mCountTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.before_btn:
                if (TimeUtil.canBeforePunch()) {
                    final String beforeTime = TimeUtil.getTime();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Record record = new Record();
                            record.setId(++index);
                            record.setDate(TimeUtil.getDate());
                            record.setBeforeTime(beforeTime);
                            record.setWeek(TimeUtil.getWeek());
                            realm.insert(record);
                            allRecords = realm.where(Record.class).findAll();
                            index = allRecords.size();
                            myAdapter.notifyDataSetChanged();
                            beforeBtn.setClickable(false);
                            beforeBtn.setBackgroundResource(R.drawable.unclicked);
                            beforeBtn.setText("已打卡_" + beforeTime);
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "太迟了，笨蛋", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.after_btn:
                if (TimeUtil.canAfterPunch()) {
                    String saveBeforeTime = "";
                    if (beforeBtn.getText().toString().contains("已打卡")) {
                        saveBeforeTime = records.get(0).getBeforeTime();
                    }
                    if (TextUtils.isEmpty(saveBeforeTime)) {
                        index++;
                    }
                    final String afterTime = TimeUtil.getTime();
                    final String finalSaveBeforeTime = saveBeforeTime;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Record record = new Record();
                            record.setId(index);
                            record.setDate(TimeUtil.getDate());
                            record.setBeforeTime(finalSaveBeforeTime);
                            record.setAfterTime(afterTime);
                            record.setWeek(TimeUtil.getWeek());
                            realm.insertOrUpdate(record);
                            allRecords = realm.where(Record.class).findAll();
                            index = allRecords.size();
                            myAdapter.notifyDataSetChanged();
                            afterBtn.setClickable(false);
                            afterBtn.setText("已打卡_" + afterTime);
                            afterBtn.setBackgroundResource(R.drawable.unclicked);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "还不到下班时间哦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.count_day:
                startActivity(new Intent(this,SecondActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
