package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.AddDay;
import com.mytips.R;

import java.util.ArrayList;

/**
 * Created by Beesolver on 12-10-2017.
 */

public class SummaryAdapter extends BaseAdapter {

    Context context;
    ArrayList<AddDay> addDayArrayList;
    LayoutInflater inflater;

    public SummaryAdapter(Context context, ArrayList<AddDay> addDayArrayList) {
        this.context = context;
        this.addDayArrayList = addDayArrayList;

    }

    @Override
    public int getCount() {
        return addDayArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView profileName, working_hours;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_day, null);

            holder.profileName = (TextView) convertView.findViewById(R.id.tv_profile_name);
            holder.working_hours = (TextView) convertView.findViewById(R.id.working_hours);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.profileName.setText(addDayArrayList.get(position).getProfile());
        holder.working_hours.setText(addDayArrayList.get(position).getCalculated_hours());

        return convertView;
    }
}
