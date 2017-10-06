package com.mytips.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mytips.Model.TipeeInfo;
import com.mytips.Preferences.Constants;
import com.mytips.Preferences.Preferences;
import com.mytips.R;
import com.mytips.SettingsActivity;

import java.util.ArrayList;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class AddTipeeAdapter extends BaseAdapter {

    ArrayList<TipeeInfo> arrayList;
    Context context;
    LayoutInflater inflater;

    public AddTipeeAdapter(Context context, ArrayList<TipeeInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
        ImageButton edit_btn, delete_btn;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tipee_list_row, null);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.tipee_text);
            viewHolder.edit_btn = (ImageButton) convertView.findViewById(R.id.edit_button);
            viewHolder.delete_btn = (ImageButton) convertView.findViewById(R.id.delete_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(arrayList.get(position).getName() + " " + arrayList.get(position).getPercentage() + "%");
        viewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_tipee_dialog);

                final TipeeInfo tI = arrayList.get(position);

                Button button_ok, button_cancel;
                final EditText editText_tipeename, editText_tipee_out;

                editText_tipeename = (EditText) dialog.findViewById(R.id.tipee_name);
                editText_tipee_out = (EditText) dialog.findViewById(R.id.tipee_out);


                editText_tipeename.setText(tI.getName());
                editText_tipee_out.setText(tI.getPercentage());
                button_ok = (Button) dialog.findViewById(R.id.ok_button);
                button_cancel = (Button) dialog.findViewById(R.id.cancel_button);


                button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updated_name = editText_tipeename.getText().toString().trim();
                        String updated_per = editText_tipee_out.getText().toString().trim();
                        tI.setName(updated_name);
                        tI.setPercentage(updated_per);
                        notifyDataSetChanged();
                        Preferences.getInstance(context).save_list(Constants.TipeeListKey, arrayList);
                        dialog.dismiss();
                    }
                });
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });

                dialog.show();
            }
        });
        viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder al = new AlertDialog.Builder(context);
                al.setTitle("Are you sure to delete!");
                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                al.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                AlertDialog alert = al.create();
                alert.show();

            }
        });
        return convertView;
    }
}
