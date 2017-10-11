package com.mytips.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mytips.Model.Profiles;
import com.mytips.R;

import java.util.ArrayList;

/**
 * Created by Beesolver on 11-10-2017.
 */

public class SpinnerAdapter extends BaseAdapter {
    ArrayList<Profiles> profilesArrayList;
    Context context;
    LayoutInflater inflater;

    public SpinnerAdapter(Context context, ArrayList<Profiles> profilesArrayList) {
        this.context = context;
        this.profilesArrayList = profilesArrayList;

    }

    @Override
    public int getCount() {
        return profilesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder
    {
        TextView textView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView==null)
        {
            holder=new ViewHolder();

            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.spinner_text_view,null);

            holder.textView=(TextView)convertView.findViewById(R.id.text_spinner);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.textView.setText(profilesArrayList.get(position).getProfile_name());
        return convertView;
    }
}
