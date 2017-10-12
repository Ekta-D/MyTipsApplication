package com.mytips.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mytips.Interface.TipeeChecked;
import com.mytips.Interface.TipeeSelected;
import com.mytips.Model.TipeeInfo;
import com.mytips.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class FetchedTipeeAdapter extends ArrayAdapter<TipeeInfo> implements CompoundButton.OnCheckedChangeListener {

    ArrayList<TipeeInfo> tipeeInfoArrayList;
    Context context;
    LayoutInflater inflater;
    public SparseBooleanArray checkedItems;
    List<String> selected_tipeesIds;
    boolean fromAddDay;
    TipeeChecked tipeeChecked;

    public FetchedTipeeAdapter(Context context, List<String> selected_tipeesIds, ArrayList<TipeeInfo> tipeeInfoArrayList,
                               boolean fromAddDay, TipeeChecked tipeeChecked) {
        super(context, 0);

        this.tipeeChecked = tipeeChecked;
        this.fromAddDay = fromAddDay;
        this.context = context;
        this.tipeeInfoArrayList = tipeeInfoArrayList;
        this.checkedItems = new SparseBooleanArray(tipeeInfoArrayList.size());
        this.selected_tipeesIds = selected_tipeesIds;
    }

    @Override
    public int getCount() {
        return tipeeInfoArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isChecked(int position) {
        return checkedItems.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        checkedItems.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        checkedItems.put((Integer) compoundButton.getTag(), b);
    }

    static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

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


        final TipeeInfo tipeeInfo = tipeeInfoArrayList.get(position);
        viewHolder.textView.setText(tipeeInfo.getName() + " " + tipeeInfo.getPercentage() + "%");

        viewHolder.checkBox.setTag(position);

        if (fromAddDay) {
            viewHolder.checkBox.setChecked(checkedItems.get(position, false));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        tipeeChecked.OnTipeeChange(true, tipeeInfo);
                    } else {
                        tipeeChecked.OnTipeeChange(false, tipeeInfo);
                    }


                }
            });


        } else {
            if (selected_tipeesIds.contains(tipeeInfo.getId())) {
                viewHolder.checkBox.setChecked(checkedItems.get(position, true));
            } else {
                viewHolder.checkBox.setChecked(checkedItems.get(position, false));
                viewHolder.checkBox.setOnCheckedChangeListener(this);
            }

        }


        return convertView;
    }
}
