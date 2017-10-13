package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.AddDay;
import com.mytips.Model.Profiles;
import com.mytips.R;

import java.util.ArrayList;

/**
 * Created by Beesolver on 13-10-2017.
 */

public class SpinnerProfile extends BaseAdapter {

    ArrayList<Profiles> addDayArrayList;
    Context context;
    LayoutInflater layoutInflater;

    public SpinnerProfile(Context context, ArrayList<Profiles> addDayArrayList) {
        this.addDayArrayList = addDayArrayList;
        this.context = context;
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
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.spinner_text_view, null);

            holder.textView = (TextView) convertView.findViewById(R.id.text_spinner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(addDayArrayList.get(position).getProfile_name());

        return convertView;
    }
}
