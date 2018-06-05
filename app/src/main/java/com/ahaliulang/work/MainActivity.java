package com.ahaliulang.work;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button beforeBtn;
    private Button afterBtn;
    private Button findBtn;
    private LinearLayout layoutLL;

    private Realm realm;
    private RealmResults<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        showLoading();
        realm = Realm.getDefaultInstance();
        records = realm.where(Record.class).findAll();
        if (records != null) {
            hideLoading();
        }


    }

    private void initView() {
        beforeBtn = findViewById(R.id.before_btn);
        afterBtn = findViewById(R.id.after_btn);
        findBtn = findViewById(R.id.find_btn);
        layoutLL = findViewById(R.id.layout_ll);
    }

    private void showLoading() {
        layoutLL.setVisibility(View.GONE);
    }

    private void hideLoading() {
        layoutLL.setVisibility(View.VISIBLE);
    }


    private void initListener() {
        beforeBtn.setOnClickListener(this);
        afterBtn.setOnClickListener(this);
        findBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.before_btn:
                break;
            case R.id.after_btn:
                if (Utils.canPunch()) {
                    afterBtn.setText("已打卡");
                } else {
                    Toast.makeText(MainActivity.this, "还不到下班时间哦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.find_btn:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
