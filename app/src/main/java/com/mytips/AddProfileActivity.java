package com.mytips;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.parser.Line;
import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Interface.TipeeChecked;
import com.mytips.Model.AddDay;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class AddProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    ImageButton imageButton;
    String profile_name = "";
    double hourly_pay = 0;
    EditText editText_profilename, editText_hourly_pay, editText_startDay;
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
    FetchedTipeeAdapter adapter;
    TextView no_data, tv_biweeklystart;
    int[] profile_colors;
    ArrayList<Integer> clicked_positions;
    HashMap<String, Boolean> temp_arraylist = new HashMap<>();
    int default_date_format = 0;

    LinearLayout color01, color02, color03, color04, color05, color06;
    ImageView selected01, selected02, selected03, selected04, selected05, selected06;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISFIRST_TIME, true);
        editor.commit();
        profile_colors = getResources().getIntArray(R.array.profile_colors);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        CommonMethods.setTheme(getSupportActionBar(), AddProfileActivity.this);

        initView();
        default_date_format = sharedPreferences.getInt("selected_date", 2);
        toggleBiweeklyView(false);

        Intent i = getIntent();
        b = i.getExtras();
        if (b != null) {
            getSupportActionBar().setTitle("Update Profile");

            profiles = (Profiles) b.getSerializable(Constants.ProfileData);

            selected_tipeesID = new ArrayList<>(Arrays.asList(convertStringToArray(String.valueOf(profiles.getTipees_name()))).get(0));

            if (selected_tipeesID.size() > 0) {
                for (int j = 0; j < selected_tipeesID.size(); j++) {
                    String id = selected_tipeesID.get(j);
                    temp_arraylist.put(id, true);
                }
            }
            fillAllFields(profiles, selected_tipeesID);
        }
        getAllTipees();

        if (b != null) {
            clicked_positions = new ArrayList<>();
        }


    }

    private void toggleBiweeklyView(boolean show) {
        if (show) {
            editText_startDay.setVisibility(View.VISIBLE);
            tv_biweeklystart.setVisibility(View.VISIBLE);
        } else {
            editText_startDay.setVisibility(View.GONE);
            tv_biweeklystart.setVisibility(View.GONE);
        }

    }

    public void initView() {

        color01 = (LinearLayout) findViewById(R.id.color_1);
        color02 = (LinearLayout) findViewById(R.id.color_2);
        color03 = (LinearLayout) findViewById(R.id.color_3);
        color04 = (LinearLayout) findViewById(R.id.color_4);
        color05 = (LinearLayout) findViewById(R.id.color_5);
        color06 = (LinearLayout) findViewById(R.id.color_6);

        selected01 = (ImageView) findViewById(R.id.selected_1);
        selected02 = (ImageView) findViewById(R.id.selected_2);
        selected03 = (ImageView) findViewById(R.id.selected_3);
        selected04 = (ImageView) findViewById(R.id.selected_4);
        selected05 = (ImageView) findViewById(R.id.selected_5);
        selected06 = (ImageView) findViewById(R.id.selected_6);

        color01.setOnClickListener(this);
        color02.setOnClickListener(this);
        color03.setOnClickListener(this);
        color04.setOnClickListener(this);
        color05.setOnClickListener(this);
        color06.setOnClickListener(this);

        dbOperations = new DatabaseOperations(AddProfileActivity.this);
        listView_fetched_tipees = (ListView) findViewById(R.id.fetched_tipees);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        no_data = (TextView) findViewById(R.id.no_data_profile);
        no_data.setVisibility(View.GONE);
        editText_profilename = (EditText) findViewById(R.id.editText);
        checkBox_supervisor = (CheckBox) findViewById(R.id.checkBox_is_supervisor);
        checkBox_getTournament = (CheckBox) findViewById(R.id.checkBox_gets_tournament_tips);
        checkBox_getTips = (CheckBox) findViewById(R.id.checkBox_gets_tips);
        spinner_payperiod = (Spinner) findViewById(R.id.spinner_pay_period);
        spinner_startday = (Spinner) findViewById(R.id.spinner_start_day_of_week);
        spinner_holiday_pay = (Spinner) findViewById(R.id.spinner_holiday_pay);
        editText_hourly_pay = (EditText) findViewById(R.id.editText_houly_pay);

        editText_startDay = (EditText) findViewById(R.id.editText_biweeklystart);
        tv_biweeklystart = (TextView) findViewById(R.id.textView_biweeklystart);
        editText_startDay.setOnClickListener(this);


        payAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, pay_period_array);
        spinner_payperiod.setAdapter(payAdapter);

        spinner_payperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                payPeriod = parent.getItemAtPosition(position).toString();
                spinner_payperiod.setSelection(position);
                if (payPeriod.trim().equals("Every 2 Weeks"))
                    toggleBiweeklyView(true);
                else
                    toggleBiweeklyView(false);
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
              /*  boolean grantedPermission = checkPermissions();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!grantedPermission) {
                        checkPermissions();
                    }
                    //only api 23 above
                } else {*/
                AlertDialog.Builder alert = new AlertDialog.Builder(AddProfileActivity.this);
                alert.setTitle("Make your selecetion");
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
//                }

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
        Intent intent = /*(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                ? new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
                :*/ new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra("image", Uri.parse(String.valueOf(imagePath)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

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

                String name_profile = editText_profilename.getText().toString().trim();

                profile_name = name_profile;
                String hourPay = editText_hourly_pay.getText().toString().trim();
                if (!hourPay.equalsIgnoreCase("")) {
                    hourly_pay = Double.parseDouble(hourPay);
                }

                if (profile_name.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Please enter profile name!", Toast.LENGTH_SHORT).show();
                } else if (payPeriod.trim().equals("Every 2 Weeks") && editText_startDay.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Choose a start date for biweekly pay period!", Toast.LENGTH_SHORT).show();
                } else {
                    String profile_id = "";
                    if (b == null) {

                        StringBuilder joinedString = new StringBuilder();

//                        if (selected_tipeesID != null && selected_tipeesID.size() > 0) {
                        if (adapter != null) {
                            if (adapter.checkedItems == null) {

                            } else if (adapter.checkedItems != null && adapter.checkedItems.size() > 0) {
                                for (int i = 0; i < adapter.checkedItems.size(); i++) {
                                    if (adapter.checkedItems.get(i) == true) {
                                        joinedString.append(tipeeInfos.get(i).getId());
                                        joinedString.append(",");
                                    }
//                            }
                                    Log.i("insert_joinedString", joinedString.toString());
                                }
                            }
                        }


                        try {

//                            int color = getRandomColor(profile_colors);

                            dbOperations.insertProfileInfoIntoDatabase(profile_id, profile_name, isSupervisor, isGettingTournament,
                                    isGetTips, payPeriod, startDay, hourly_pay, holidayPay,
                                    joinedString.toString(), image_name, chosen_color, String.valueOf(start_dateCalendar != null ? start_dateCalendar.getTimeInMillis() : ""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        StringBuilder joinedString = new StringBuilder();
                        int id = profiles.getId();

                        String joinedStringToString = "";
                        if (temp_arraylist != null && temp_arraylist.size() > 0) {

                            if (tipeeInfos.size() > 0) {
                                //if selected comes again
                                for (int j = 0; j < selected_tipeesID.size(); j++) {
                                    if (temp_arraylist.containsKey(selected_tipeesID.get(j))) {
                                        if (temp_arraylist.get(selected_tipeesID.get(j)) == true) {
                                            joinedString.append(temp_arraylist.get(selected_tipeesID.get(j)));
                                            joinedString.append(",");
                                        }
                                    }
                                }
                                List<String> unselectedTipees = new ArrayList<>();
                                //if a new tippee is selected
                                for (Map.Entry<String, Boolean> newTippee : temp_arraylist.entrySet()) {
                                    if (newTippee.getValue() == true) {
                                        if (!selected_tipeesID.contains(newTippee.getKey()))
                                            selected_tipeesID.add(newTippee.getKey());

                                    } else {
                                        selected_tipeesID.remove(newTippee.getKey());
                                        //unselectedTipees.add(newTippee.getKey());
                                    }
                                }

                                if (selected_tipeesID.size() > 0) {
                                    for (int i = 0; i < selected_tipeesID.size(); i++) {
                                        joinedString.append(selected_tipeesID.get(i));
                                        joinedString.append(",");
                                    }

                                }

                            }
                        }


                        ArrayList<AddDay> addDays = new DatabaseOperations(AddProfileActivity.this).fetchAddDayForProfile(id);

                        if (addDays.size() > 0) {
                            new DatabaseOperations(AddProfileActivity.this).updateProfileInfoInDays(addDays,
                                    profile_name, id, isGettingTournament, isGetTips);
                        }
                        try {
                            dbOperations.updateProfileValues(id, profile_id, profile_name, isSupervisor, isGettingTournament,
                                    isGetTips, payPeriod, startDay, hourly_pay, holidayPay,
                                    joinedString.toString(), image_name, profileColors, String.valueOf(start_dateCalendar != null ? start_dateCalendar.getTimeInMillis() : ""));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    Toast.makeText(this, "Thanks! your profile has been saved", Toast.LENGTH_SHORT).show();


                    Intent intentProfile = new Intent(AddProfileActivity.this, ActiveProfiles.class);
                    intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentProfile);
                    emptyFields();
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
    String image_name = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            //    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            image_name = saveImage(bitmap);
            imageButton.setImageBitmap(bitmap);
        }
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                try {

                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(data.getData(), filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);

                    c.close();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    image_name = saveImage(bitmap);
                    //    createDirectoryAndSaveFile(bitmap,"MyTips");
                    imageButton.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ArrayList<TipeeInfo> tipeeInfos;
    List<String> selected_tipeesID;


    public void getAllTipees() {
        if (selected_tipeesID == null)
            selected_tipeesID = new ArrayList<>();
        tipeeInfos = new ArrayList<>();
        tipeeInfos = new DatabaseOperations(AddProfileActivity.this).fetchTipeeList(AddProfileActivity.this);


        if (tipeeInfos != null && tipeeInfos.size() > 0) {
            adapter = new FetchedTipeeAdapter(AddProfileActivity.this, selected_tipeesID, tipeeInfos, false, new TipeeChecked() {
                @Override
                public void OnTipeeChange(boolean isChecked, TipeeInfo tipeeInfo, int position) {
                    temp_arraylist.put(tipeeInfo.getId(), isChecked);
                }
            }, null);
            listView_fetched_tipees.setAdapter(adapter);
        } else {
            no_data.setVisibility(View.VISIBLE);
        }
        listView_fetched_tipees.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    int profileColors;

    public void fillAllFields(Profiles profiles, final List<String> selected) {
        editText_profilename.setText(profiles.getProfile_name());
        selectedDate = profiles.getBiWeeklyStartDate();
        /*Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(selectedDate));*/
        if (selectedDate != null && !selectedDate.equals(""))
            date = new SimpleDateFormat(date_format).format(new Date(Long.valueOf(selectedDate)));
        editText_startDay.setText("" + date == null ? "" : date);
        if (profiles.getIs_supervisor() == 1) {
            checkBox_supervisor.setChecked(true);
            isSupervisor = true;
        }
        if (profiles.getGet_tournamenttip() == 1) {
            checkBox_getTournament.setChecked(true);
            isGettingTournament = true;
        }
        if (profiles.getGet_tips() == 1) {
            checkBox_getTips.setChecked(true);
            isGetTips = true;
        }
        int payperiod = payAdapter.getPosition(profiles.getPay_period());
        spinner_payperiod.setSelection(payperiod);
        if (profiles.getPay_period().trim().equals("Every 2 Weeks"))
            toggleBiweeklyView(true);
        else
            toggleBiweeklyView(false);

        int startday = dayAdapter.getPosition(profiles.getStartday());
        spinner_startday.setSelection(startday);
        int holidayPay = holidayAdapter.getPosition(profiles.getHoliday_pay());
        spinner_holiday_pay.setSelection(holidayPay);

        editText_hourly_pay.setText(profiles.getHourly_pay());

        if (!profiles.getProfile_pic().equalsIgnoreCase("")) {
            File image = new File(Environment.getExternalStorageDirectory()
                    .toString() + "/MyTipsAppImages/" + profiles.getProfile_pic());
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            if (bitmap != null) {
                imageButton.setImageBitmap(bitmap);
            }

        }

        image_name = profiles.getProfile_pic();
        profileColors = profiles.getProfile_color();
        if (profileColors == 0) {
            selected01.setVisibility(View.VISIBLE);
            selected02.setVisibility(View.GONE);
            selected03.setVisibility(View.GONE);
            selected04.setVisibility(View.GONE);
            selected05.setVisibility(View.GONE);
            selected06.setVisibility(View.GONE);

        } else if (profileColors == 1) {
            selected02.setVisibility(View.VISIBLE);
            selected01.setVisibility(View.GONE);
            selected03.setVisibility(View.GONE);
            selected04.setVisibility(View.GONE);
            selected05.setVisibility(View.GONE);
            selected06.setVisibility(View.GONE);

        } else if (profileColors == 2) {
            selected03.setVisibility(View.VISIBLE);

            selected02.setVisibility(View.GONE);
            selected01.setVisibility(View.GONE);
            selected04.setVisibility(View.GONE);
            selected05.setVisibility(View.GONE);
            selected06.setVisibility(View.GONE);

        } else if (profileColors == 3) {
            selected04.setVisibility(View.VISIBLE);
            selected02.setVisibility(View.GONE);
            selected01.setVisibility(View.GONE);
            selected03.setVisibility(View.GONE);
            selected05.setVisibility(View.GONE);
            selected06.setVisibility(View.GONE);

        } else if (profileColors == 4) {
            selected05.setVisibility(View.VISIBLE);
            selected02.setVisibility(View.GONE);
            selected01.setVisibility(View.GONE);
            selected03.setVisibility(View.GONE);
            selected04.setVisibility(View.GONE);
            selected06.setVisibility(View.GONE);

        } else if (profileColors == 5) {
            selected06.setVisibility(View.VISIBLE);
            selected02.setVisibility(View.GONE);
            selected01.setVisibility(View.GONE);
            selected03.setVisibility(View.GONE);
            selected04.setVisibility(View.GONE);
            selected05.setVisibility(View.GONE);

        }
        tipeeInfos = new ArrayList<>();

        tipeeInfos = new DatabaseOperations(AddProfileActivity.this).fetchTipeeList(AddProfileActivity.this);


        if (tipeeInfos != null && tipeeInfos.size() > 0) {
            if (selected.size() > 0) {

                adapter = new FetchedTipeeAdapter(AddProfileActivity.this, selected, tipeeInfos, false, new TipeeChecked() {
                    @Override
                    public void OnTipeeChange(boolean isChecked, TipeeInfo tipeeInfo, int position) {
                        System.out.println(tipeeInfo);
                        temp_arraylist.put(tipeeInfo.getId(), isChecked);

                    }
                }, null);

                listView_fetched_tipees.setAdapter(adapter);

//                adapter.set
            }
        }
    }

    public String convertArrayToString(List<String> selected_tipeesID) {
        String result;
        StringBuilder sb = new StringBuilder();
        for (String tipeeInfo : selected_tipeesID) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(tipeeInfo);
        }
        result = sb.toString();
        return result;
    }

    public List<String> convertStringToArray(String joinedString) {
        List<String> sellItems = new ArrayList<>();
        if (!joinedString.equalsIgnoreCase("")) {
            sellItems = Arrays.asList(joinedString.split(","));
        }

        return sellItems;

    }


    public String saveImage(Bitmap bitmap) {
        String file_name;
        String root = Environment.getExternalStorageDirectory()
                .toString();

        File myDir = new File(root + "/MyTipsAppImages/");
        if (!myDir.exists()) {

            myDir.mkdirs();

        }
        Random generator = new Random();
        int n = 10000;

        n = generator.nextInt(n);

        String iname = "Image-" + n + ".jpg";
        file_name = iname;
        File file = new File(myDir, iname);

        if (file.exists())

            file.delete();

        try {

            FileOutputStream out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            out.flush();

            out.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
        return file_name;
    }


    public static int getRandomColor(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return rnd;
    }

    int chosen_color = 0;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.color_1:
                chosen_color = 0;
                selected01.setVisibility(View.VISIBLE);
                selected02.setVisibility(View.GONE);
                selected03.setVisibility(View.GONE);
                selected04.setVisibility(View.GONE);
                selected05.setVisibility(View.GONE);
                selected06.setVisibility(View.GONE);
                break;
            case R.id.color_2:
                chosen_color = 1;
                selected02.setVisibility(View.VISIBLE);
                selected01.setVisibility(View.GONE);
                selected03.setVisibility(View.GONE);
                selected04.setVisibility(View.GONE);
                selected05.setVisibility(View.GONE);
                selected06.setVisibility(View.GONE);
                break;
            case R.id.color_3:
                chosen_color = 2;
                selected03.setVisibility(View.VISIBLE);

                selected02.setVisibility(View.GONE);
                selected01.setVisibility(View.GONE);
                selected04.setVisibility(View.GONE);
                selected05.setVisibility(View.GONE);
                selected06.setVisibility(View.GONE);
                break;
            case R.id.color_4:
                chosen_color = 3;
                selected04.setVisibility(View.VISIBLE);
                selected02.setVisibility(View.GONE);
                selected01.setVisibility(View.GONE);
                selected03.setVisibility(View.GONE);
                selected05.setVisibility(View.GONE);
                selected06.setVisibility(View.GONE);
                break;
            case R.id.color_5:
                chosen_color = 4;
                selected05.setVisibility(View.VISIBLE);
                selected02.setVisibility(View.GONE);
                selected01.setVisibility(View.GONE);
                selected03.setVisibility(View.GONE);
                selected04.setVisibility(View.GONE);
                selected06.setVisibility(View.GONE);
                break;
            case R.id.color_6:
                chosen_color = 5;
                selected06.setVisibility(View.VISIBLE);
                selected02.setVisibility(View.GONE);
                selected01.setVisibility(View.GONE);
                selected03.setVisibility(View.GONE);
                selected04.setVisibility(View.GONE);
                selected05.setVisibility(View.GONE);
                break;
            case R.id.editText_biweeklystart:
                getDatePicker(default_date_format, R.id.editText_start_shift);
                break;
        }

    }

    String date_format = "MMMM d, yyyy";
    String selectedDate, date;
    Calendar start_dateCalendar;

    private String getDatePicker(int format_index, final int viewId) {
        // Infalating Dialog for Date Picker
        final Dialog datePicker = new Dialog(AddProfileActivity.this);
        datePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDatePicker = inflater.inflate(R.layout.date_picker, null);
        datePicker.setContentView(viewDatePicker);

        if (format_index == 2) {
            date_format = "MM/dd/yyyy";
        } else if (format_index == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }

        // Getting date from Calendar View
        final CalendarView calendar = (CalendarView) viewDatePicker
                .findViewById(R.id.datePicker);
        final Button selectDate = (Button) viewDatePicker
                .findViewById(R.id.dateSelect);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                Log.i("info_date", String.valueOf(dayOfMonth));
                selectedDate = String.valueOf(month + 1) + "/"
                        + String.valueOf(dayOfMonth) + "/"
                        + String.valueOf(year);
                System.out.println("Selected date: " + selectedDate);
                start_dateCalendar = Calendar.getInstance();
                start_dateCalendar.set(year, month, dayOfMonth);
                date = new SimpleDateFormat(date_format).format(new Date(
                        selectedDate));
                   /* if (viewId == R.id.editText_biweeklystart) {
                        startDay = dayOfMonth;
                        startMonth = month;
                        startYear = year;

                    *//*start_calendar.set(Calendar.MONTH, startMonth);
                    start_calendar.set(Calendar.DAY_OF_MONTH, startDay);*//*
                        calStartDay.set(startYear, startMonth, startDay);
                   *//* calStartDay.set(Calendar.HOUR_OF_DAY, 0);
                    calStartDay.set(Calendar.MINUTE, 0);
                    calStartDay.set(Calendar.SECOND, 0);
                    calStartDay.set(Calendar.MILLISECOND, 0);*//*


                        start_dateCalendar.set(startYear, startMonth, startDay);

//                    selectedDate = String.valueOf(startMonth + 1) + "/"
//                            + String.valueOf(startDay) + "/"
//                            + String.valueOf(startYear);

                    }

                    selectedDate = String.valueOf(month + 1) + "/"
                            + String.valueOf(dayOfMonth) + "/"
                            + String.valueOf(year);
                    System.out.println("Selected date: " + selectedDate);


                    date = new SimpleDateFormat(date_format).format(new Date(
                            selectedDate));


                    SimpleDateFormat sdfDate = new SimpleDateFormat(date_format);
                    String currentDateString = sdfDate.format(new Date());
                    Date currentDate = null;
                    Date parsedStartDate = null;

                    start_date = selectedDate;
                    startDateDb = selectedDate;
//                startDateDb = currentDateString;
                    String date1 = new SimpleDateFormat(date_format).format(new Date(selectedDate));
                    end_date = date1;
                    try {
                        currentDate = sdfDate.parse(currentDateString);
                        parsedStartDate = sdfDate.parse(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    date = new SimpleDateFormat(date_format)
                            .format(new Date(date));
                    if (viewId == R.id.editText_start_shift) {
                        start_date = selectedDate;
                        editText_startShift.setText(selectedDate);

                    } else if (viewId == R.id.editText_end_shift) {
                        date1 = new SimpleDateFormat(date_format).format(new Date(date));
                        end_date = date1;
//                        end_date = date;
                        editText_endShift.setText(date);
                    }*/

//                }

            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                editText_startDay.setText(date);
                datePicker.dismiss();

            }
        });

        // Selecting date from Date Dialog

        datePicker.show();
        return "";
    }
}
