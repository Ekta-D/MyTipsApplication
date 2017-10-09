package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.Profiles;
import com.mytips.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Beesolver on 09-10-2017.
 */

public class ActiveProfileAdapter extends BaseAdapter {

    Context context;
    ArrayList<Profiles> profiles;
    LayoutInflater inflater;


    public ActiveProfileAdapter(Context context, ArrayList<Profiles> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public int getCount() {
        return profiles.size();
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
        TextView profileName, hourlyPay, payPeriod;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.active_profile_list_row, null);

            viewHolder.profileName = (TextView) convertView.findViewById(R.id.profile_name);
            viewHolder.hourlyPay = (TextView) convertView.findViewById(R.id.hourly_pay);
            viewHolder.payPeriod = (TextView) convertView.findViewById(R.id.pay_period);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.profileName.setText(profiles.get(position).getProfile_name());
        viewHolder.hourlyPay.setText("$" + profiles.get(position).getHourly_pay() + "/hr");
        viewHolder.payPeriod.setText("Pay Period: " + profiles.get(position).getPay_period());
        return convertView;
    }
}
