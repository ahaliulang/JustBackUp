package com.ahaliulang.work;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.RealmResults;

public class SleepAdapter extends BaseAdapter {

    private Context mContext;
    private RealmResults<Sleep> sleeps;

    public SleepAdapter(Context mContext, RealmResults<Sleep> sleeps) {
        this.mContext = mContext;
        this.sleeps = sleeps;
    }

    @Override
    public int getCount() {
        return sleeps == null ? 0 : sleeps.size();
    }

    @Override
    public Object getItem(int position) {
        return sleeps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sleep, null);
            viewHolder.date = convertView.findViewById(R.id.per_day_tv);
            viewHolder.before = convertView.findViewById(R.id.before_tv);
            viewHolder.after = convertView.findViewById(R.id.after_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Sleep sleep = sleeps.get(position);
        viewHolder.date.setText("第" + String.valueOf(TimeUtil.countDays(sleep.getDate())+1) + "天");
        viewHolder.before.setText(sleep.getDate() + "|" + sleep.getWeek() + "|" + sleep.getWakeUpTime());
        viewHolder.after.setText(sleep.getDate() + "|" + sleep.getWeek() + "|" + sleep.getGoToBedTime());
        return convertView;
    }


    static class ViewHolder {
        TextView date;
        TextView before;
        TextView after;
    }
}
