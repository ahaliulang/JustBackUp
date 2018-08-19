package com.ahaliulang.work;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {


    private long index;
    private Realm mRealm;
    private RealmResults<Sleep> sleeps;
    private RealmResults<Sleep> allSleeps;

    private TextView dateTv;
    private TextView eggLeftTv;
    private TextView eggLossTv;
    private TextView eggReset;
    private EditText eggResetNum;
    private TextView yogurtLeftTv;
    private TextView yogurtLossTv;
    private TextView yogurtReset;
    private EditText yogurtResetNum;
    private ListView sleepList;
    private TextView changeReset;

    private TextView goToBed;
    private TextView wakeUp;

    private SleepAdapter sleepAdapter;

    private Egg mEggs;
    private Yogurt mYogurt;
    private int resetType; // 0为重置剩余的，1为重置损耗的


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        allSleeps = mRealm.where(Sleep.class).findAll();
        sleepAdapter = new SleepAdapter(this, allSleeps);
        index = allSleeps.size();
        sleeps = mRealm.where(Sleep.class).equalTo("date", TimeUtil.getDate()).findAll();
        mEggs = mRealm.where(Egg.class).findFirst();
        if (mEggs == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Egg egg = new Egg();
                    egg.setLoss("0");
                    egg.setLeft("0");
                    egg.setId(1001);
                    realm.insertOrUpdate(egg);
                }
            });
        }
        mYogurt = mRealm.where(Yogurt.class).findFirst();
        if (mYogurt == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Yogurt yogurt = new Yogurt();
                    yogurt.setId(1002);
                    yogurt.setLeft("0");
                    yogurt.setLoss("0");
                    realm.insertOrUpdate(yogurt);
                }
            });
        }
        setContentView(R.layout.activity_second);
        initView();
        initListener();

        if (sleeps != null && sleeps.size() > 0) {
            if (!TextUtils.isEmpty(sleeps.get(0).getGoToBedTime())) {
                goToBed.setText("入睡时间_" + sleeps.get(0).getGoToBedTime());
                goToBed.setClickable(false);
                goToBed.setBackgroundResource(R.drawable.unclicked);
            }

            if (!TextUtils.isEmpty(sleeps.get(0).getWakeUpTime())) {
                wakeUp.setText("起床时间_" + sleeps.get(0).getWakeUpTime());
                wakeUp.setClickable(false);
                wakeUp.setBackgroundResource(R.drawable.unclicked);
            }
        }

    }

    private void initView() {
        dateTv = findViewById(R.id.date_tv);
        dateTv.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        eggLeftTv = findViewById(R.id.egg_left_tv);
        eggLossTv = findViewById(R.id.egg_loss_tv);
        if (mEggs == null) {
            eggLeftTv.setText(String.format(getResources().getString(R.string.egg_left), "0"));
            eggLossTv.setText(String.format(getResources().getString(R.string.egg_loss), "0"));
        } else {
            eggLeftTv.setText(String.format(getResources().getString(R.string.egg_left), mEggs.getLeft()));
            eggLossTv.setText(String.format(getResources().getString(R.string.egg_loss), mEggs.getLoss()));
        }
        eggReset = findViewById(R.id.egg_reset);
        eggResetNum = findViewById(R.id.egg_reset_num);
        yogurtLeftTv = findViewById(R.id.yogurt_left);
        yogurtLossTv = findViewById(R.id.yogurt_loss_tv);
        if (mYogurt == null) {
            yogurtLeftTv.setText(String.format(getResources().getString(R.string.yogurt_left), "0"));
            yogurtLossTv.setText(String.format(getResources().getString(R.string.yogurt_loss), "0"));
        } else {
            yogurtLeftTv.setText(String.format(getResources().getString(R.string.yogurt_left), mYogurt.getLeft()));
            yogurtLossTv.setText(String.format(getResources().getString(R.string.yogurt_loss), mYogurt.getLoss()));
        }
        yogurtReset = findViewById(R.id.reset_yogurt);
        yogurtResetNum = findViewById(R.id.yogurt_reset_num);
        sleepList = findViewById(R.id.sleep_list);
        goToBed = findViewById(R.id.go_to_bed);
        wakeUp = findViewById(R.id.wake_up);
        sleepList.setAdapter(sleepAdapter);
        changeReset = findViewById(R.id.change_reset);
    }

    private void initListener() {
        eggLossTv.setOnClickListener(this);
        eggLeftTv.setOnClickListener(this);
        eggReset.setOnClickListener(this);
        yogurtLossTv.setOnClickListener(this);
        yogurtLeftTv.setOnClickListener(this);
        yogurtReset.setOnClickListener(this);
        goToBed.setOnClickListener(this);
        wakeUp.setOnClickListener(this);
        changeReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.egg_loss_tv:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int leftEgg = Integer.valueOf(mEggs.getLeft());
                        int lossEgg = Integer.valueOf(mEggs.getLoss());
                        if (leftEgg <= 0) {
                            Toast.makeText(SecondActivity.this, "没有鸡蛋了，要去购买了哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mEggs.setLeft(String.valueOf(--leftEgg));
                        mEggs.setLoss(String.valueOf(++lossEgg));
                        realm.insertOrUpdate(mEggs);
                        Egg first = mRealm.where(Egg.class).findFirst();
                        eggLeftTv.setText(String.format(getString(R.string.egg_left), first.getLeft()));
                        eggLossTv.setText(String.format(getString(R.string.egg_loss), first.getLoss()));
                    }
                });
                break;
            case R.id.egg_reset:
                try {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int resetNum = Integer.valueOf(eggResetNum.getText().toString());
                            if (resetNum < 0) {
                                Toast.makeText(SecondActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (resetType == 0) {
                                mEggs.setLeft(String.valueOf(resetNum));
                            } else {
                                mEggs.setLoss(String.valueOf(resetNum));
                            }
                            realm.insertOrUpdate(mEggs);
                            Egg first = mRealm.where(Egg.class).findFirst();
                            eggLeftTv.setText(String.format(getString(R.string.egg_left), first.getLeft()));
                            eggLossTv.setText(String.format(getString(R.string.egg_loss), first.getLoss()));
                            eggResetNum.setText("");
                        }
                    });

                } catch (NumberFormatException e) {
                    Toast.makeText(SecondActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.egg_left_tv:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int leftEgg = Integer.valueOf(mEggs.getLeft());
                        if (leftEgg <= 0) {
                            Toast.makeText(SecondActivity.this, "没有鸡蛋了，要去购买了哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mEggs.setLeft(String.valueOf(--leftEgg));
                        realm.insertOrUpdate(mEggs);
                        Egg first = mRealm.where(Egg.class).findFirst();
                        eggLeftTv.setText(String.format(getString(R.string.egg_left), first.getLeft()));
                        eggLossTv.setText(String.format(getString(R.string.egg_loss), first.getLoss()));
                    }
                });
                break;
            case R.id.yogurt_loss_tv:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int leftEgg = Integer.valueOf(mYogurt.getLeft());
                        int lossEgg = Integer.valueOf(mYogurt.getLoss());
                        if (leftEgg <= 0) {
                            Toast.makeText(SecondActivity.this, "没有酸奶了，要去购买了哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mYogurt.setLeft(String.valueOf(--leftEgg));
                        mYogurt.setLoss(String.valueOf(++lossEgg));
                        realm.insertOrUpdate(mYogurt);
                        Yogurt first = mRealm.where(Yogurt.class).findFirst();
                        yogurtLeftTv.setText(String.format(getString(R.string.yogurt_left), first.getLeft()));
                        yogurtLossTv.setText(String.format(getString(R.string.yogurt_loss), first.getLoss()));
                    }
                });
                break;
            case R.id.reset_yogurt:
                try {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int resetNum = Integer.valueOf(yogurtResetNum.getText().toString());
                            if (resetNum < 0) {
                                Toast.makeText(SecondActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (resetType == 0) {
                                mYogurt.setLeft(String.valueOf(resetNum));
                            } else {
                                mYogurt.setLoss(String.valueOf(resetNum));
                            }
                            realm.insertOrUpdate(mYogurt);
                            Yogurt first = mRealm.where(Yogurt.class).findFirst();
                            yogurtLeftTv.setText(String.format(getString(R.string.yogurt_left), first.getLeft()));
                            yogurtLossTv.setText(String.format(getString(R.string.yogurt_loss), first.getLoss()));
                            yogurtResetNum.setText("");
                        }
                    });

                } catch (NumberFormatException e) {
                    Toast.makeText(SecondActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.yogurt_left:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int leftEgg = Integer.valueOf(mYogurt.getLeft());
                        if (leftEgg <= 0) {
                            Toast.makeText(SecondActivity.this, "没有酸奶了，要去购买了哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mYogurt.setLeft(String.valueOf(--leftEgg));
                        realm.insertOrUpdate(mYogurt);
                        Yogurt first = mRealm.where(Yogurt.class).findFirst();
                        yogurtLeftTv.setText(String.format(getString(R.string.yogurt_left), first.getLeft()));
                        yogurtLossTv.setText(String.format(getString(R.string.yogurt_loss), first.getLoss()));
                    }
                });

                break;
            case R.id.go_to_bed:
                final String goToBedTime = TimeUtil.getTime();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String wakeUpTime = "";
                        if (wakeUp.getText().toString().contains("起床时间")) {
                            wakeUpTime = sleeps.get(0).getWakeUpTime();
                        }
                        if (TextUtils.isEmpty(wakeUpTime)) {
                            index++;
                        }
                        Sleep sleep = new Sleep();
                        sleep.setId(index);
                        sleep.setDate(TimeUtil.getDate());
                        sleep.setWeek(TimeUtil.getWeek());
                        sleep.setGoToBedTime(goToBedTime);
                        sleep.setWakeUpTime(wakeUpTime);
                        realm.insertOrUpdate(sleep);
                        allSleeps = realm.where(Sleep.class).findAll();
                        index = allSleeps.size();
                        sleepAdapter.notifyDataSetChanged();
                        goToBed.setClickable(false);
                        goToBed.setBackgroundResource(R.drawable.unclicked);
                        goToBed.setText("入睡时间_" + goToBedTime);
                        Toast.makeText(SecondActivity.this, "狗命重要啊，要早睡啊", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.wake_up:

                final String wakeUpTime = TimeUtil.getTime();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Sleep sleep = new Sleep();
                        sleep.setId(++index);
                        sleep.setDate(TimeUtil.getDate());
                        sleep.setWakeUpTime(wakeUpTime);
                        sleep.setWeek(TimeUtil.getWeek());
                        realm.insert(sleep);
                        allSleeps = realm.where(Sleep.class).findAll();
                        index = allSleeps.size();
                        sleepAdapter.notifyDataSetChanged();
                        wakeUp.setClickable(false);
                        wakeUp.setText("起床时间_" + wakeUpTime);
                        wakeUp.setBackgroundResource(R.drawable.unclicked);
                        Toast.makeText(SecondActivity.this, "今天又是元气满满的一天呢", Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.change_reset:
                if (resetType == 0) {
                    resetType = 1;
                    Toast.makeText(this, "切换重置类型为损耗", Toast.LENGTH_SHORT).show();
                } else {
                    resetType = 0;
                    Toast.makeText(this, "切换重置类型为剩余", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
