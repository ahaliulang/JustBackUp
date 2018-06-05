package com.ahaliulang.work;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.RealmResults;

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private RealmResults<Record> records;

    public MyAdapter(Context mContext, RealmResults<Record> records) {
        this.mContext = mContext;
        this.records = records;
    }

    @Override
    public int getCount() {
        return records == null ? 0 : records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.date = convertView.findViewById(R.id.per_day_tv);
            viewHolder.before = convertView.findViewById(R.id.before_tv);
            viewHolder.after = convertView.findViewById(R.id.after_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Record record = records.get(position);
        viewHolder.date.setText(record.getDate() + "(" + record.getWeek() + ")");
        viewHolder.before.setText(record.getBeforeTime());
        viewHolder.after.setText(record.getAfterTime());
        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView before;
        TextView after;
    }
}
