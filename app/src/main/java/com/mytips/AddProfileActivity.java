package com.mytips;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class AddProfileActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    ImageButton imageButton;
    String profile_name = "";
    double hourly_pay = 0;
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
    FetchedTipeeAdapter adapter;
    TextView no_data;
    int[] profile_colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISFIRST_TIME, true);
        editor.commit();
        profile_colors = getResources().getIntArray(R.array.profile_colors);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        CommonMethods.setTheme(getSupportActionBar(), AddProfileActivity.this);

        initView();

        Intent i = getIntent();
        b = i.getExtras();
        if (b != null) {
            getSupportActionBar().setTitle("Update Profile");

            profiles = (Profiles) b.getSerializable(Constants.ProfileData);

            selected_tipeesID = convertStringToArray(String.valueOf(profiles.getTipees_name()));
            fillAllFields(profiles, selected_tipeesID);
        }
        getAllTipees();

    }

    public void initView() {

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


        payAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, pay_period_array);
        spinner_payperiod.setAdapter(payAdapter);

        spinner_payperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                payPeriod = parent.getItemAtPosition(position).toString();
                updateView(payPeriod);
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

                } else {
                    String profile_id = "";
                    if (b == null) {

                        StringBuilder joinedString = new StringBuilder();

//                        if (selected_tipeesID != null && selected_tipeesID.size() > 0) {
                        if (adapter!=null)
                        {
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

                            int color = getRandomColor(profile_colors);

                            dbOperations.insertProfileInfoIntoDatabase(profile_id, profile_name, isSupervisor, isGettingTournament,
                                    isGetTips, payPeriod, startDay, hourly_pay, holidayPay,
                                    joinedString.toString(), image_name, color);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {


                        int id = profiles.getId();

                        StringBuilder joinedString = new StringBuilder();
//                        if (selected_tipeesID != null && selected_tipeesID.size() > 0)
                        if (selected_tipeesID != null && selected_tipeesID.size() > 0) {

                            for (int j = 0; j < selected_tipeesID.size(); j++) {
                                joinedString.append(tipeeInfos.get(j).getId());
                                joinedString.append(",");
                            }

                        }
                        if (adapter!=null)
                        {
                            if (adapter.checkedItems == null) {

                            } else if (adapter.checkedItems != null && adapter.checkedItems.size() > 0) {

                                for (int i = 0; i < adapter.checkedItems.size(); i++) {
//                            if (adapter.checkedItems.get(i, false) == true) {
                                    if (adapter.checkedItems.get(i) == true) {
                                        joinedString.append(tipeeInfos.get(i).getId());
                                        joinedString.append(",");
                                    }
                                }
                                Log.i("update_joinedString", joinedString.toString());
                            }

                        }


                        try {
                            dbOperations.updateProfileValues(id, profile_id, profile_name, isSupervisor, isGettingTournament,
                                    isGetTips, payPeriod, startDay, hourly_pay, holidayPay,
                                    joinedString.toString(), profileColors);
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
            adapter = new FetchedTipeeAdapter(AddProfileActivity.this, selected_tipeesID, tipeeInfos, false, null);
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

    public void fillAllFields(Profiles profiles, List<String> selected) {
        editText_profilename.setText(profiles.getProfile_name());
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
        profileColors = profiles.getProfile_color();
        tipeeInfos = new ArrayList<>();

        tipeeInfos = new DatabaseOperations(AddProfileActivity.this).fetchTipeeList(AddProfileActivity.this);


        if (tipeeInfos != null && tipeeInfos.size() > 0) {
            if (selected.size() > 0) {
                adapter = new FetchedTipeeAdapter(AddProfileActivity.this, selected, tipeeInfos, false, null);
                listView_fetched_tipees.setAdapter(adapter);
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

    public void updateView(String payPeriod) {

        if (payPeriod.equalsIgnoreCase("Every 2 Weeks")) {

        }

    }

/*    public boolean checkPermissions() {
        int permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int camera_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int finger_print = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera_permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (finger_print != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.USE_FINGERPRINT);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(SplashActivity.this, "permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(SplashActivity.this, "permissions not granted!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/


    public static int getRandomColor(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return rnd;
    }
}
