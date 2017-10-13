package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.AddDay;
import com.mytips.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        TextView start_date, start_month, textView_tips, textView_tds, textView_tipout, textView_hourlywage, textView_total,
                profileName, working_hours;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_day, null);

            holder.start_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.start_month = (TextView) convertView.findViewById(R.id.tv_month);

            holder.textView_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            holder.textView_tds = (TextView) convertView.findViewById(R.id.tv_tds);
            holder.textView_tipout = (TextView) convertView.findViewById(R.id.tv_tip_out);
            holder.textView_hourlywage = (TextView) convertView.findViewById(R.id.tv_hourly);
            holder.textView_total = (TextView) convertView.findViewById(R.id.tv_total);


            holder.profileName = (TextView) convertView.findViewById(R.id.tv_profile_name);
            holder.working_hours = (TextView) convertView.findViewById(R.id.working_hours);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String date = "";
        try {
            date = getDate(addDayArrayList.get(position).getStart_shift());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String month = "";
        try {
            month = getMonth(addDayArrayList.get(position).getStart_shift());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.start_date.setText(date);
        holder.start_month.setText(month);


        holder.profileName.setText(addDayArrayList.get(position).getProfile());
        holder.working_hours.setText(addDayArrayList.get(position).getCalculated_hours());
        String totaltips = "";
        totaltips = addDayArrayList.get(position).getTotal_tips();
        if (totaltips.equalsIgnoreCase("")) {
            totaltips = "0";
        }
        String total_TDs = "";
        total_TDs = addDayArrayList.get(position).getTotal_tournament_downs();
        if (total_TDs.equalsIgnoreCase("")) {
            total_TDs = "0";
        }
        String tipO = addDayArrayList.get(position).getTip_out();
        if (tipO.equalsIgnoreCase("")) {
            tipO = "0";
        }
        String wH = addDayArrayList.get(position).getWages_hourly();
        if (wH.equalsIgnoreCase("")) {
            wH = "0";
        }
        String totalE = addDayArrayList.get(position).getTotal_earnings();
        if (totalE.equalsIgnoreCase("")) {
            totalE = "0";
        }
        holder.textView_tips.setText(totaltips);
        holder.textView_tipout.setText(tipO);
        holder.textView_hourlywage.setText(wH);
        holder.textView_tds.setText(total_TDs);
        holder.textView_total.setText(totalE);


        return convertView;
    }

    public String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("dd").format(cal.getTime());
        return monthName;
    }

    public String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }
}
