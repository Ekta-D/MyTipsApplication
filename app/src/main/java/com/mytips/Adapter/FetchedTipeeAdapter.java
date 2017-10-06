package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mytips.Model.TipeeInfo;
import com.mytips.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class FetchedTipeeAdapter extends BaseAdapter {

    ArrayList<TipeeInfo> tipeeInfoArrayList;
    Context context;
    LayoutInflater inflater;

    public FetchedTipeeAdapter(Context context, ArrayList<TipeeInfo> tipeeInfoArrayList) {
        this.context = context;
        this.tipeeInfoArrayList = tipeeInfoArrayList;
    }

    @Override
    public int getCount() {
        return tipeeInfoArrayList.size();
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
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_tipees_list, null);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.fetched_tipee_text);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_fetch_tipee);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(tipeeInfoArrayList.get(position).getName() + " " + tipeeInfoArrayList.get(position).getPercentage() + "%");
        return convertView;
    }
}
