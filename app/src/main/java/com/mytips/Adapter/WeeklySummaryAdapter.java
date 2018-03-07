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
        TextView _profile, _liveTips, _TDowns, _TipOuts, _TipOutPercentage, _HrWages, _PerTournamentDown, _TotalTDowns, _TotalIncome, _dates;
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
            viewHolder._TDowns = (TextView) convertView.findViewById(R.id.TDowns_value);
            viewHolder._TipOuts = (TextView) convertView.findViewById(R.id.mTip_out_value);
            viewHolder._TipOutPercentage = (TextView) convertView.findViewById(R.id.mTip_out_percentage);
            viewHolder._PerTournamentDown = (TextView) convertView.findViewById(R.id.TipPerTD_value);
            viewHolder._HrWages = (TextView) convertView.findViewById(R.id.HrlyWage_value);
            viewHolder._TotalTDowns = (TextView) convertView.findViewById(R.id.mTotalTDs_value);
            viewHolder._TotalIncome = (TextView) convertView.findViewById(R.id.totalsIncome_value);
            viewHolder._dates = (TextView) convertView.findViewById(R.id.dates_textview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder._profile.setText(dataBlocksSetsArrayList.get(position).getmProfile());
        viewHolder._liveTips.setText(dataBlocksSetsArrayList.get(position).getmLiveTips());
        viewHolder._TDowns.setText(dataBlocksSetsArrayList.get(position).getmTournamentDowns());
        viewHolder._TipOuts.setText(dataBlocksSetsArrayList.get(position).getmTipOut());
        viewHolder._TipOutPercentage.setText(dataBlocksSetsArrayList.get(position).getmTipoutPercentage());
        viewHolder._PerTournamentDown.setText(dataBlocksSetsArrayList.get(position).getmTipPerDown());
        viewHolder._HrWages.setText(dataBlocksSetsArrayList.get(position).getmHrlyWage());
        viewHolder._TotalTDowns.setText(dataBlocksSetsArrayList.get(position).getmTotalTDDowns());
        viewHolder._TotalIncome.setText(dataBlocksSetsArrayList.get(position).getmTotalIncome());

        viewHolder._dates.setBackground(context.getResources().getDrawable(R.drawable.dates_rounded_corners));
//        viewHolder._dates.setText(dataBlocksSetsArrayList.get(position).getmDates());

        return convertView;
    }
}
