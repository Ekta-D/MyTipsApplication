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

import com.mytips.Interface.TipeeSelected;
import com.mytips.Model.TipeeInfo;
import com.mytips.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class FetchedTipeeAdapter extends ArrayAdapter<TipeeInfo> implements CompoundButton.OnCheckedChangeListener{

    ArrayList<TipeeInfo> tipeeInfoArrayList;
    Context context;
    LayoutInflater inflater;
    public SparseBooleanArray checkedItems;

    //TipeeSelected tipeeSelectedCallback;
    List<String> selected_tipeesIds;

    public  FetchedTipeeAdapter(Context context, List<String> selected_tipeesIds, ArrayList<TipeeInfo> tipeeInfoArrayList) {
        super(context, 0);

        this.context = context;
        this.tipeeInfoArrayList = tipeeInfoArrayList;
        //this.tipeeSelectedCallback = tipeeSelectedCallback;
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


        TipeeInfo tipeeInfo = tipeeInfoArrayList.get(position);
        viewHolder.textView.setText(tipeeInfo.getName() + " " + tipeeInfo.getPercentage() + "%");

        viewHolder.checkBox.setTag(position);
        if(selected_tipeesIds.contains(tipeeInfo.getId()))
            viewHolder.checkBox.setChecked(checkedItems.get(position, true));
        else
            viewHolder.checkBox.setChecked(checkedItems.get(position, false));
        viewHolder.checkBox.setOnCheckedChangeListener(this);

        /*if (selected_tipeesIds == null) {

        } else if (selected_tipeesIds.size() > 0) {


            for (int i = 0; i < selected_tipeesIds.size(); i++) {

                if (tipeeInfoArrayList.get(position).getId().equals(selected_tipeesIds.get(i))) {
                    viewHolder.checkBox.setChecked(true);
                    notifyDataSetChanged();
                }
            }
        }
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipeeInfo tipeeInfo = tipeeInfoArrayList.get(position);
                if (((CompoundButton) v).isChecked()) {
                    viewHolder.checkBox.setEnabled(true);

                    tipeeSelectedCallback.TipeeCheckedList(position, true, tipeeInfo,selected_tipeesIds);
                } else {
                    tipeeSelectedCallback.TipeeCheckedList(position, false, tipeeInfo,selected_tipeesIds);
                }
            }
        });*/
        return convertView;
    }
}
