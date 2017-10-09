package com.mytips;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.mytips.Adapter.ActiveProfileAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Database.DatabaseUtils;
import com.mytips.Model.Profiles;
import com.mytips.Preferences.Constants;

import java.util.ArrayList;

public class ActiveProfiles extends AppCompatActivity {

    Button btn_add_profile;
    ListView activeList, deactive;
    ActiveProfileAdapter adapter, deactiveAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_profiles);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        btn_add_profile = (Button) findViewById(R.id.add_new_profile);
        btn_add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActiveProfiles.this, AddProfileActivity.class));
            }
        });
        activeList = (ListView) findViewById(R.id.active_profile_list);
        deactive = (ListView) findViewById(R.id.deactive_profile_list);
        activeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final Profiles profile = activeProfileList.get(position);
                //Profiles profiles=(Profiles)parent.get`
                AlertDialog.Builder alert = new AlertDialog.Builder(ActiveProfiles.this);
                alert.setTitle("Make your selection");
                final String names[] = {"Edit", "Delete", "Deactivate"};
                alert.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(ActiveProfiles.this, AddProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.ProfileData, profile);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (which == 1) {
                            deleteProfile(profile);
                            activeProfileList.remove(position);
                            adapter.notifyDataSetChanged();
                        } else {
                            setDeactiveProfileList(profile.getProfile_id());
                            profileDeactiveList.add(profile);
                            activeProfileList.remove(position);
                            adapter.notifyDataSetChanged();
                            if (profileDeactiveList.size() > 0) {
                                deactiveAdater = new ActiveProfileAdapter(ActiveProfiles.this, profileDeactiveList);
                                deactive.setAdapter(deactiveAdater);
                                adapter.notifyDataSetChanged();
                            }


                        }
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });


        fetchProfiles();

    }

    public void deleteProfile(Profiles profiles) {
        try {

            new DatabaseOperations(ActiveProfiles.this).delete_profile(profiles.getProfile_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDeactiveProfileList(String profileID) {
        new DatabaseOperations(ActiveProfiles.this).setDeactivate(profileID);
    }

    ArrayList<Profiles> profilesArrayList;
    ArrayList<Profiles> profileDeactiveList = new ArrayList<>();
    ArrayList<Profiles> activeProfileList = new ArrayList<>();

    public void fetchProfiles() {
        Cursor cursor = null;
        String[] projections = new String[10];
        projections[0] = DatabaseUtils.ProfileID;
        projections[1] = DatabaseUtils.ProfileName;
        projections[2] = DatabaseUtils.IsSupervisor;
        projections[3] = DatabaseUtils.IsActive;
        projections[4] = DatabaseUtils.GetTournamentTip;
        projections[5] = DatabaseUtils.GetTips;
        projections[6] = DatabaseUtils.PayPeriod;
        projections[7] = DatabaseUtils.StartDayWeek;
        projections[8] = DatabaseUtils.HourlyPay;
        projections[9] = DatabaseUtils.HolidayPay;

//        String selection[] = new String[]{"1"};
        try {
            cursor = new DatabaseOperations(ActiveProfiles.this).dataFetch(DatabaseUtils.PROFILE_TABLE, projections,
                    null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;
        try {
            cursor_count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        profilesArrayList = new ArrayList<>();
        if (cursor_count != 0) {

            if (cursor.moveToFirst()) {
                do {
                    Profiles profiles = new Profiles();
                    String profileName = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileName));
                    String payPeriod = cursor.getString(cursor.getColumnIndex(DatabaseUtils.PayPeriod));
                    String hourlypay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HourlyPay));
                    String profileId = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileID));
                    int isActive = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsActive));
                    int is_supervisor = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsSupervisor));
                    int get_tournament = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTournamentTip));
                    int get_tips = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTips));
                    String startday = cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek));
                    String holodayPay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HolidayPay));

                    profiles.setIs_supervisor(is_supervisor);
                    profiles.setStartday(startday);
                    profiles.setGet_tips(get_tips);
                    profiles.setGet_tournamenttip(get_tournament);
                    profiles.setHoliday_pay(holodayPay);
                    profiles.setProfile_id(profileId);
                    profiles.setProfile_name(profileName);
                    profiles.setPay_period(payPeriod);
                    profiles.setHourly_pay(hourlypay);
                    profiles.setIs_active(isActive);
                    profilesArrayList.add(profiles);
                }
                while (cursor.moveToNext());
            }

        }

        if (profilesArrayList.size() > 0) {
            for (int i = 0; i < profilesArrayList.size(); i++) {
                Profiles profiles = profilesArrayList.get(i);
                int active = profiles.getIs_active();
                if (active == 1) {
                    activeProfileList.add(profiles);
                } else {
                    profileDeactiveList.add(profiles);
                }
            }
            if (activeProfileList.size() > 0)

            {
                adapter = new ActiveProfileAdapter(ActiveProfiles.this, activeProfileList);
                activeList.setAdapter(adapter);
            }
            if (profileDeactiveList.size() > 0) {
                deactiveAdater = new ActiveProfileAdapter(ActiveProfiles.this, profileDeactiveList);
                deactive.setAdapter(deactiveAdater);
            }

        }
    }

}
