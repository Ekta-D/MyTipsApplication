package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.DataBlocksSets;
import com.mytips.R;

import java.util.ArrayList;

/**
 * Created by Beesolver on 06-03-2018.
 */

public class WeeklySummaryAdapter extends BaseAdapter {

    Context context;
    ArrayList<DataBlocksSets> dataBlocksSetsArrayList;
    LayoutInflater inflater;

    public WeeklySummaryAdapter(Context context, ArrayList<DataBlocksSets> dataBlocksSetsArrayList) {
        this.context = context;
        this.dataBlocksSetsArrayList = dataBlocksSetsArrayList;

    }

    @Override
    public int getCount() {
        return dataBlocksSetsArrayList.size();
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
        TextView _profile, _liveTips, _TDownsCount, _TipOuts, _TipOutPercentage, _HrWages, _PerTournamentDown, _TotalTDowns,
                _TotalIncome, _dates, _summary, _totalHoursWorked;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.weekly_row, null);

            viewHolder._profile = (TextView) convertView.findViewById(R.id.mprofile_value);
            viewHolder._liveTips = (TextView) convertView.findViewById(R.id.live_tip_value);
            viewHolder._TDownsCount = (TextView) convertView.findViewById(R.id.TDowns_count);
            viewHolder._TipOuts = (TextView) convertView.findViewById(R.id.mTip_out_value);
            viewHolder._TipOutPercentage = (TextView) convertView.findViewById(R.id.mTip_out_percentage);
            viewHolder._PerTournamentDown = (TextView) convertView.findViewById(R.id.TDowns_amount);
            viewHolder._HrWages = (TextView) convertView.findViewById(R.id.HrlyWage_value);
//            viewHolder._TotalTDowns = (TextView) convertView.findViewById(R.id.mTotalTDs_value);
            viewHolder._TotalTDowns = (TextView) convertView.findViewById(R.id.TDowns_totals);
            viewHolder._TotalIncome = (TextView) convertView.findViewById(R.id.totalsIncome_value);
            viewHolder._dates = (TextView) convertView.findViewById(R.id.dates_textview);
            viewHolder._summary = (TextView) convertView.findViewById(R.id.summary_type_val);
            viewHolder._totalHoursWorked = (TextView) convertView.findViewById(R.id.total_worked_hr_val);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder._profile.setText(dataBlocksSetsArrayList.get(position).getmProfile());
        viewHolder._liveTips.setText("$" + dataBlocksSetsArrayList.get(position).getmLiveTips());
//        viewHolder._TDowns.setText(dataBlocksSetsArrayList.get(position).getmTipOut());
//        viewHolder._TipOuts.setText(dataBlocksSetsArrayList.get(position).getmTipOut());
        viewHolder._TipOutPercentage.setText("(" + dataBlocksSetsArrayList.get(position).getmTipoutPercentage() + ")");
        viewHolder._PerTournamentDown.setText(dataBlocksSetsArrayList.get(position).getmTDPerDay());
        viewHolder._HrWages.setText("$" + dataBlocksSetsArrayList.get(position).getmHrlyWage());
        viewHolder._TDownsCount.setText(dataBlocksSetsArrayList.get(position).getmTotalTDDowns());
        viewHolder._TotalIncome.setText("Total Income : " + " " + "$" + dataBlocksSetsArrayList.get(position).getmTotalIncome());
        viewHolder._TipOuts.setText("$" + dataBlocksSetsArrayList.get(position).getmTipOut());
        viewHolder._dates.setText(dataBlocksSetsArrayList.get(position).getmDates());
        viewHolder._totalHoursWorked.setText(dataBlocksSetsArrayList.get(position).getmTotalWorkedHr());
        viewHolder._summary.setText(dataBlocksSetsArrayList.get(position).getSummary_type());
        viewHolder._TotalTDowns.setText(dataBlocksSetsArrayList.get(position).getmAdditionOfDowns());
        return convertView;
    }
}
