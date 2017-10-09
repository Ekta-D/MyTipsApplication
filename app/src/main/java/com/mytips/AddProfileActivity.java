package com.mytips;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Database.DatabaseUtils;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.Preferences.Constants;
import com.mytips.Preferences.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static com.mytips.R.id.fetched_tipees;
import static com.mytips.R.id.spinner_pay_period;
import static com.mytips.R.id.tipeess;


public class AddProfileActivity extends AppCompatActivity {


    ImageButton imageButton;
    String profile_name;
    int hourly_pay = 0;
    EditText editText_profilename, editText_hourly_pay;
    CheckBox checkBox_supervisor, checkBox_getTournament, checkBox_getTips;
    Spinner spinner_payperiod, spinner_startday, spinner_holiday_pay;
    boolean isSupervisor = false, isGettingTournament = false, isGetTips = false;
    String payPeriod, startDay, holidayPay;
    String[] pay_period_array = new String[]{"Daily ", "Weekly", "Every 2 Weeks ", "1st & 15th"};
    String[] start_days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    String[] holiday_pays = new String[]{"[none]", "Time and a Half", "Double Pay"};
    DatabaseOperations dbOperations;
    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;
    public static final String ISFIRST_TIME = "Isfirst_time";
    SharedPreferences sharedPreferences;
    ListView listView_fetched_tipees;
    ArrayAdapter<String> payAdapter, dayAdapter, holidayAdapter;
    Bundle b;
    Profiles profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISFIRST_TIME, true);
        editor.commit();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        initView();
        getAllTipees();

        Intent i = getIntent();
        b = i.getExtras();
        if (b != null) {
            profiles = (Profiles) b.getSerializable(Constants.ProfileData);
            fillAllFields(profiles);
        }


    }

    public void initView() {

        dbOperations = new DatabaseOperations(AddProfileActivity.this);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        editText_profilename = (EditText) findViewById(R.id.editText);
        checkBox_supervisor = (CheckBox) findViewById(R.id.checkBox_is_supervisor);
        checkBox_getTournament = (CheckBox) findViewById(R.id.checkBox_gets_tournament_tips);
        checkBox_getTips = (CheckBox) findViewById(R.id.checkBox_gets_tips);
        spinner_payperiod = (Spinner) findViewById(R.id.spinner_pay_period);
        spinner_startday = (Spinner) findViewById(R.id.spinner_start_day_of_week);
        spinner_holiday_pay = (Spinner) findViewById(R.id.spinner_holiday_pay);
        editText_hourly_pay = (EditText) findViewById(R.id.editText_houly_pay);
        //  addTipee = (Button) findViewById(R.id.add_tipee);


        payAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, pay_period_array);
        spinner_payperiod.setAdapter(payAdapter);

        spinner_payperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                payPeriod = parent.getItemAtPosition(position).toString();
                spinner_payperiod.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, start_days);
        spinner_startday.setAdapter(dayAdapter);

        spinner_startday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startDay = parent.getItemAtPosition(position).toString();
                spinner_startday.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holidayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, holiday_pays);
        spinner_holiday_pay.setAdapter(holidayAdapter);
        spinner_holiday_pay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holidayPay = parent.getItemAtPosition(position).toString();
                spinner_holiday_pay.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBox_supervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSupervisor = checkBox_supervisor.isChecked();
            }
        });
        checkBox_getTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGettingTournament = checkBox_getTournament.isChecked();
            }
        });
        checkBox_getTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGetTips = checkBox_getTips.isChecked();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddProfileActivity.this);
                alert.setTitle("Make your selection");
                final String names[] = {"Camera", "Gallery"};
                alert.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            OpenCamera();
                            dialog.dismiss();
                        } else {
                            openGallery();
                            dialog.dismiss();
                        }
                    }
                });

                AlertDialog al = alert.create();
                al.show();
            }
        });


    }

    public void OpenCamera() {
        File pictureFileDir = new File(Environment
                .getExternalStorageDirectory().getPath()
                + File.separator
                + "MyTipsee");
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdir();
        }

        File imagePath = new File(pictureFileDir, "profile" + Calendar.getInstance().getTime() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("image", Uri.parse(String.valueOf(imagePath)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, REQUEST_GALLERY);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save_profile:

                String id = UUID.randomUUID().toString();
                profile_name = editText_profilename.getText().toString().trim();
                String hourPay = editText_hourly_pay.getText().toString().trim();
                if (!hourPay.equalsIgnoreCase("")) {
                    hourly_pay = Integer.parseInt(hourPay);
                }

                if (profile_name.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Please enter profile name!", Toast.LENGTH_SHORT).show();

                } else {
                    String joinedString = TextUtils.join(",", tipees_array);
                    if (b != null) {
                        dbOperations.updateProfileValues(profiles.getProfile_id(), profile_name, isSupervisor, isGettingTournament, isGetTips,
                                payPeriod, startDay, hourly_pay, holidayPay, joinedString);
                    } else {
                        try {
                            dbOperations.insertProfileInfoIntoDatabase(id, profile_name, isSupervisor, isGettingTournament,
                                    isGetTips, payPeriod, startDay, hourly_pay, holidayPay,
                                    joinedString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    Toast.makeText(this, "Thanks! your profile has been saved", Toast.LENGTH_SHORT).show();

                    emptyFields();
                    startActivity(new Intent(AddProfileActivity.this, ActiveProfiles.class));
                    this.finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void emptyFields() {
        editText_profilename.setText("");
        editText_hourly_pay.setText("");
        checkBox_supervisor.setChecked(false);
        checkBox_getTournament.setChecked(false);
        checkBox_getTips.setChecked(false);
        spinner_payperiod.setSelection(0);
        spinner_startday.setSelection(0);
        spinner_holiday_pay.setSelection(0);
        tipees_array.clear();
        imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round));
    }

    ArrayList<String> tipees_array = new ArrayList<>();

    private TextView createNewTextView(String name, String tip_out) {
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setTextColor(getResources().getColor(R.color.colorBlack));
        tipees_array.add(name + " " + tip_out + "%");
        textView.setText(name + " " + tip_out + "%" + ", ");
        return textView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageButton.setImageBitmap(bitmap);
        }
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    imageButton.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getAllTipees() {
        ArrayList<TipeeInfo> tipeeInfos = Preferences.getInstance(AddProfileActivity.this).getTipeeList(Constants.TipeeListKey);
        listView_fetched_tipees = (ListView) findViewById(R.id.fetched_tipees);
        if (tipeeInfos != null) {
            FetchedTipeeAdapter adapter = new FetchedTipeeAdapter(AddProfileActivity.this, tipeeInfos);
            listView_fetched_tipees.setAdapter(adapter);
        }
    }

    public void fillAllFields(Profiles profiles) {
        editText_profilename.setText(profiles.getProfile_name());
        if (profiles.getIs_supervisor() == 1) {
            checkBox_supervisor.setChecked(true);
        }
        if (profiles.getGet_tournamenttip() == 1) {
            checkBox_getTournament.setChecked(true);
        }
        if (profiles.getGet_tips() == 1) {
            checkBox_getTips.setChecked(true);
        }
        int payperiod = payAdapter.getPosition(profiles.getPay_period());
        spinner_payperiod.setSelection(payperiod);

        int startday = dayAdapter.getPosition(profiles.getStartday());
        spinner_startday.setSelection(startday);
        int holidayPay = holidayAdapter.getPosition(profiles.getHoliday_pay());
        spinner_holiday_pay.setSelection(holidayPay);

        editText_hourly_pay.setText(profiles.getHourly_pay());

    }


}
