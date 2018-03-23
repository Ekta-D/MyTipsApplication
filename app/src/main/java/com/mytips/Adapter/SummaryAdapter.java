package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    int index;

    public SummaryAdapter(int index, Context context, ArrayList<AddDay> addDayArrayList) {
        this.context = context;
        this.addDayArrayList = addDayArrayList;
        this.index = index;
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
        LinearLayout layoutEarningDetails;
        ImageView time;
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

            holder.time = (ImageView) convertView.findViewById(R.id.time);
            holder.layoutEarningDetails = (LinearLayout) convertView.findViewById(R.id.layout_earning_details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String date = "";
        try {
            date = getDate(addDayArrayList.get(position).getStart_long());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String month = "";
        try {
            month = getMonth(addDayArrayList.get(position).getStart_long());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.start_date.setText(date);
        holder.start_month.setText(month);

        holder.profileName.setText(addDayArrayList.get(position).getProfile());
        int index = addDayArrayList.get(position).getProfile_color();

        int[] color_array = context.getResources().getIntArray(R.array.profile_colors);

        int selected_color = color_array[index];
        holder.profileName.setTextColor(selected_color);
        holder.working_hours.setText(addDayArrayList.get(position).getCalculated_hours());

        int dayOff = addDayArrayList.get(position).getDay_off();
        if (dayOff == 1) {
            holder.working_hours.setText("Day off");
            holder.time.setBackgroundResource(R.drawable.ic_timer_icon);
            holder.layoutEarningDetails.setVisibility(View.GONE);
        } else {
            holder.working_hours.setText(addDayArrayList.get(position).getCalculated_hours());
            holder.time.setBackgroundResource(R.drawable.ic_clock);
            holder.layoutEarningDetails.setVisibility(View.VISIBLE);
        }

        String totaltips = "";
        double totaltips_double = 0;
        if (!addDayArrayList.get(position).getTotal_tips().equalsIgnoreCase("")) {
            totaltips_double = Double.parseDouble(addDayArrayList.get(position).getTotal_tips());
            totaltips = String.format("%.2f", totaltips_double);
        }

        if (totaltips.equalsIgnoreCase("")) {
            totaltips = "0";
        }
        String total_TDs = "0";
        if (addDayArrayList.get(position).getTounament_count() != null && !addDayArrayList.get(position).getTounament_count().equalsIgnoreCase("")) {
            total_TDs = String.valueOf(Integer.parseInt(addDayArrayList.get(position).getTounament_count()));
        }
        if (total_TDs.equalsIgnoreCase("")) {
            total_TDs = "0";
        }

        String tipO = "";
        double tips_outs = 0;
        if (!addDayArrayList.get(position).getTip_out().equalsIgnoreCase("") && addDayArrayList.get(position).getTip_out() != null) {
            tips_outs = Double.parseDouble(addDayArrayList.get(position).getTip_out());
            tipO = String.format("%.2f", tips_outs);
        } else if (tipO.equalsIgnoreCase("")) {
            tipO = "0";
        }
        String wH = "";

    /*    if (addDayArrayList.get(position).getWages_hourly() != null && !addDayArrayList.get(position).getWages_hourly().equalsIgnoreCase("")) {
            wH = String.valueOf(Double.parseDouble(addDayArrayList.get(position).getWages_hourly()));
        }*/
        double wage_hourly_double = 0;
      /*  if (addDayArrayList.get(position).getWages_hourly() != null && !addDayArrayList.get(position).getWages_hourly().equalsIgnoreCase("")) {
            wage_hourly_double = Double.parseDouble(addDayArrayList.get(position).getWages_hourly());
            wH = String.format("%.2f", wage_hourly_double);
        } else if (wH.equalsIgnoreCase("")) {
            wH = "0";
        }*/
        if (addDayArrayList.get(position).getProfile_wage_hourly() != null && !addDayArrayList.get(position).getProfile_wage_hourly().equalsIgnoreCase("")) {
            wage_hourly_double = Double.parseDouble(addDayArrayList.get(position).getProfile_wage_hourly());
            wH = String.format("%.2f", wage_hourly_double);
        } else if (wH.equalsIgnoreCase("")) {
            wH = "0";
        }
        String totalE = "";
        double total_earnings_double = 0;

        /*if (addDayArrayList.get(position).getTotal_earnings() != null && !addDayArrayList.get(position).getTotal_earnings().equalsIgnoreCase("")) {
            totalE = String.valueOf(Double.parseDouble(addDayArrayList.get(position).getTotal_earnings()));
        }
        */
        if (addDayArrayList.get(position).getTotal_earnings() != null && !addDayArrayList.get(position).getTotal_earnings().equalsIgnoreCase("")) {
            total_earnings_double = Double.parseDouble(addDayArrayList.get(position).getTotal_earnings());
            totalE = String.format("%.2f", total_earnings_double);
        } else if (totalE.equalsIgnoreCase("")) {
            totalE = "0";
        }


        holder.textView_tips.setText(totaltips);
        holder.textView_tipout.setText(tipO);
        holder.textView_hourlywage.setText(wH);
        holder.textView_tds.setText(total_TDs);
        holder.textView_total.setText(totalE);

        return convertView;
    }

    public String getDate(long date) throws ParseException {
        String date_format = "MMMM dd, yyyy";
        if (index == 2) {
            date_format = "MM/dd/yyyy";
        } else if (index == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }
        Date d = new Date(date);
        //  Date d = new SimpleDateFormat(date_format, Locale.ENGLISH).parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("dd").format(cal.getTime());
        return monthName;
    }

    public String getMonth(long date) throws ParseException {
        String date_format = "MMMM dd, yyyy";
        if (index == 2) {
            date_format = "MM/dd/yyyy";
        } else if (index == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }
        Date d = new Date(date);
        // Date d = new SimpleDateFormat(date_format, Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }
}
