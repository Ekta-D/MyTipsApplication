package com.mytips;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.mytips.Adapter.ActiveProfileAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Model.Profiles;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

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

        CommonMethods.setTheme(getSupportActionBar(), ActiveProfiles.this);

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
                            setDeactiveProfileList(String.valueOf(profile.getId()));
                            profileDeactiveList.add(profile);
                            activeProfileList.remove(position);
                            adapter.notifyDataSetChanged();
                            if (profileDeactiveList.size() > 0) {
                                deactiveAdater = new ActiveProfileAdapter(ActiveProfiles.this, profileDeactiveList);
                                deactive.setAdapter(deactiveAdater);
                                deactiveAdater.notifyDataSetChanged();
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
        updateDeactiveList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteProfile(Profiles profiles) {
        try {
            new DatabaseOperations(ActiveProfiles.this).delete_profile(String.valueOf(profiles.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDeactiveList() {
        ArrayList<Profiles> deactive_list = new ArrayList<>();
        deactive_list = new DatabaseOperations(ActiveProfiles.this).fetchDeactiveProfiles();
        if (deactive_list.size() == 0 || deactive_list == null) {
        } else if (deactive_list.size() > 0) {

            deactiveAdater = new ActiveProfileAdapter(ActiveProfiles.this, deactive_list);
            deactive.setAdapter(deactiveAdater);
        }
    }

    public void setDeactiveProfileList(String profileID) {
        new DatabaseOperations(ActiveProfiles.this).setDeactivate(profileID);
    }

    ArrayList<Profiles> profilesArrayList;
    ArrayList<Profiles> profileDeactiveList = new ArrayList<>();
    ArrayList<Profiles> activeProfileList = new ArrayList<>();

    public void fetchProfiles() {
        profilesArrayList = new ArrayList<>();
        profilesArrayList = new DatabaseOperations(ActiveProfiles.this).fetchAllProfile(ActiveProfiles.this);
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
