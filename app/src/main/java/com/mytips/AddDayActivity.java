package com.mytips;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Adapter.SpinnerAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Interface.TimeChangeListener;
import com.mytips.Interface.TipeeChecked;
import com.mytips.Model.AddDay;
import com.mytips.Model.CountList;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AddDayActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isStartDateChanged = false, isEndDateChanged = false;
    boolean keyDel = false;
    ArrayList<Profiles> profilesArrayList;
    Spinner spinnerProfile;
    int startDay, startMonth, startYear, endDay, endMonth, endYear, startHour,
            startMinute, endHour, endMinute;
    String time, date = "", selectedDate = null;
    ArrayList<String> profile_names;
    EditText editText_startShift, editText_endShift, edittext_clockIn, edittext_clockOut, edittext_count, edittext_perTD,
            edittext_total, edit_total_tips, editext_manual_tips;
    boolean pastDateSelected;
    Calendar current_date;
    Date current_time;
    String startDateDb = "", start_date = "", end_date = "", start_time = "", end_time = "";
    private String format = "";
    String result = "";
    TextView texview_hours;
    Calendar start_dateCalendar, end_dateCalendar;
    Calendar calStartDay, calEndDay;
    int count = 0;
    double count_perTd = 0;
    ImageButton close_icon;
    CheckBox checkBox_dayoff, holiday_pay;
    TextView save_details, textview_autoCalLabel, label_to, total_tipslabel, tipout_tipees_label, total_tipoutPer, total_tipout,
            tournament_downlabel, cout_label, perTd_label, totalcount_label, text_tip_out_percent, total_tipoutlabel,
            from_label, tipee_nodata, label_manual_tips, label_dollar_sign, label_per_sign;//multiply_label, equal_label,
    View tipSeparator, tournamentSeparator;
    ImageView image_to, day_off_timer;
    ListView fetchedTipees;
    double dynamic_tip_out_percentage = 0;
    RelativeLayout tipee_layout;
    double total_tipsInput = 0;
    String selected_profile = "";
    int selected_profile_color = 0;
    int holidayPay = 0, day_off = 0;
    List<String> selected_tipeesIDs;
    TextView total_earnings, live_tips, tournament_downs, tip_outs, hourly_wages;
    double wage_hourly = 0;
    double result_tip_outpercentage = 0;
    double total_tournamentdown = 0;
    SpinnerAdapter spinnerAdapter;
    int getting_tips = 0, getting_tournament = 0, is_supervisor = 0;
    Bundle b;
    AddDay addDay;
    String holiday_off_value = "";
    String calculated_wages_hourly = "";
    String start_week;
    long start_shift_long, end_shift_long;
    int selected_timeformatIndex = 0;
    SharedPreferences sharedPreferences;
    int default_date_format = 0;
    Toolbar toolbar;
    int total_counts_till_now = 0;
    Switch type_switch;
    Calendar start_calendar, end_calendar;
    String global_startday = "", global_payperiod = "";
    int switch_value = 0;
    boolean getTournamentTips = false;
    double manual_data = 0;
    String manually_added_tips = "";
    int selected_profile_id = 0;
    double manual_percentage_data = 0, manual_tips_data = 0;
    TextView new_count_textview;
    EditText editText_new_count;
    int new_count = 0;
    int stable_count = 0;
    TextView label_total_earnings;
    CheckBox checkBoxEndofPayPeriod;
    int isEndDay = 0;
    int tempEndDay = 0;
    HashMap<String, Boolean> temp_arraylist = new HashMap<>();
    List<String> checked;
    int totalsHours = 0, totalMinutes = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);

        checked = new ArrayList<>();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        CommonMethods.setTheme(getSupportActionBar(), AddDayActivity.this);

        start_calendar = Calendar.getInstance();
        end_calendar = Calendar.getInstance();

        total_earnings = (TextView) findViewById(R.id.total_earnings);
        editText_new_count = (EditText) findViewById(R.id.editText_new_count);
        new_count_textview = (TextView) findViewById(R.id.textViewnew_count);
        label_total_earnings = (TextView) findViewById(R.id.lbl_total);

        findViewByIds();

        selected_timeformatIndex = sharedPreferences.getInt("selected_time", 1);
        default_date_format = sharedPreferences.getInt("selected_date", 2);

        profilesArrayList = new ArrayList<>();
        profile_names = new ArrayList<>();
        profilesArrayList = new DatabaseOperations(AddDayActivity.this).fetchAllProfile(AddDayActivity.this);

        Profiles profiles0 = new Profiles();
        profiles0.setProfile_name("Select Profile");
        profiles0.setStartday("Sunday");
        profiles0.setHourly_pay("0");
        profiles0.setHoliday_pay("0");

        profilesArrayList.add(0, profiles0);

        spinnerAdapter = new SpinnerAdapter(AddDayActivity.this,
                profilesArrayList);
        spinnerProfile.setAdapter(spinnerAdapter);


        editText_startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(default_date_format, R.id.editText_start_shift);
            }
        });
        editText_endShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(default_date_format, R.id.editText_end_shift);
            }
        });

        edittext_clockIn.setOnClickListener(this);
        edittext_clockOut.setOnClickListener(this);

        edittext_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count1) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                int length = s.length();

                if (count_perTd == 0 && !str.equalsIgnoreCase("")) {
                    count = Integer.parseInt(str);
                    //stable_count = Integer.parseInt(str);
                } else if (!str.equalsIgnoreCase("") && count_perTd != 0) {

                    double total = calculate_total(String.valueOf(count_perTd), str);
                    count = 0;
                    total_tournamentdown = total;

                    String hr = String.valueOf(diffHours);
                    if (hr.contains("-")) {
                        hr = hr.replace("-", "");
                    }
                    String m = String.valueOf(diffMinutes);
                    if (m.contains("-")) {
                        m = m.replace("-", "");
                    }
                    String d = String.valueOf(diffDays);
                    if (d.contains("-")) {
                        d = d.replace("-", "");
                    }

                    calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                            Integer.parseInt(m),
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown
                            , holiday_off_value, b);
                    edittext_total.setText(String.valueOf(total_tournamentdown));

                } else if (str.equalsIgnoreCase("") && total_tournamentdown != 0) {
                    count_perTd = 0;

                    double total = calculate_total(String.valueOf(count_perTd), str);
                    count = 0;
                    total_tournamentdown = total;

                    String hr = String.valueOf(diffHours);
                    if (hr.contains("-")) {
                        hr = hr.replace("-", "");
                    }
                    String m = String.valueOf(diffMinutes);
                    if (m.contains("-")) {
                        m = m.replace("-", "");
                    }
                    String d = String.valueOf(diffDays);
                    if (d.contains("-")) {
                        d = d.replace("-", "");
                    }

                    calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                            Integer.parseInt(m),
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown
                            , holiday_off_value, b);
                    edittext_total.setText(String.valueOf(total_tournamentdown));
                } else if (str.equalsIgnoreCase("") && total_tournamentdown == 0) {
                    count = 0;
                    count_perTd = 0;
                }


            }
        });

        editText_new_count.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    keyDel = true;
                } else {
                    keyDel = false;
                }
                return false;
            }
        });

        editText_new_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count1) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                int manually_total_counting = 0;

                if (!keyDel) {
                    if (!str.equalsIgnoreCase("") && s.length() > 1) {
                        if (str.contains(".")) {
                            str = str.replace(".", "");
                        }
                        new_count = Integer.parseInt(str);

                        manually_total_counting = stable_count + new_count;

                        if (b != null) {
                            stable_count = manually_total_counting;
                        }

                        edittext_count.setText(String.valueOf(manually_total_counting));

                    } else if (!str.equalsIgnoreCase("") && s.length() == 1) {
                        int c = Integer.parseInt(str);

                        manually_total_counting = stable_count + c;
                        if (b != null) {
                            stable_count = manually_total_counting;
                        }
                        edittext_count.setText(String.valueOf(manually_total_counting));

                    }

                } else {
                    if (s.length() == 1) {
                        if (str.contains(".")) {
                            str = str.replace(".", "");
                        }
                        int c = Integer.parseInt(str);
                        manually_total_counting = stable_count - c;
                        //   stable_count = stable_count - c;
                        edittext_count.setText(String.valueOf(Math.abs(manually_total_counting)));
                        //new_count = 0;
                    } else if (s.length() == 0) {
                        if (b == null) {
                            new_count = 0;
                        }
                        manually_total_counting = stable_count - new_count;
                        if (b != null) {
                            stable_count = manually_total_counting;
                        }

                        edittext_count.setText(String.valueOf(Math.abs(manually_total_counting)));
                    }
                    keyDel = false;
                }


            }
        });
        edittext_perTD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!str.equalsIgnoreCase("")) {
                    count_perTd = Double.parseDouble(str);
                }
                double total = calculate_total(String.valueOf(count), str);
                total_tournamentdown = total;

                String hr = String.valueOf(diffHours);
                if (hr.contains("-")) {
                    hr = hr.replace("-", "");
                }
                String m = String.valueOf(diffMinutes);
                if (m.contains("-")) {
                    m = m.replace("-", "");
                }
                String d = String.valueOf(diffDays);
                if (d.contains("-")) {
                    d = d.replace("-", "");
                }

                if (str.equalsIgnoreCase("")) {
                    count_perTd = 0;

                }
                calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                        Integer.parseInt(m),
                        Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown
                        , holiday_off_value
                        , b);
                edittext_total.setText(String.valueOf(total));
            }
        });

        checkBox_dayoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    day_off = 1;
                    setDayOffView();
                } else {
                    day_off = 0;
                    updateDayOffView();
                }
            }
        });


        edit_total_tips.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                Log.i("count", String.valueOf(count));

                if (str.equalsIgnoreCase(".")) {
                    str = "0.";
                }
                if (!str.equalsIgnoreCase("")) {

                    total_tipsInput = Double.parseDouble(str);
                    if (percentage > 0) {
                        result_tip_outpercentage = (percentage * Double.parseDouble(str)) / 100;

                        total_tipout.setText(String.format("%.2f", result_tip_outpercentage));
                    }

                } else total_tipsInput = 0;
                String hr = String.valueOf(diffHours);
                if (hr.contains("-")) {
                    hr = hr.replace("-", "");
                }
                String m = String.valueOf(diffMinutes);
                if (m.contains("-")) {
                    m = m.replace("-", "");
                }
                String d = String.valueOf(diffDays);
                if (d.contains("-")) {
                    d = d.replace("-", "");
                }
                calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                        Integer.parseInt(m),
                        Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value
                        , b);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holiday_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holidayPay = 1;

                    String hr = String.valueOf(diffHours);
                    if (hr.contains("-")) {
                        hr = hr.replace("-", "");
                    }
                    String m = String.valueOf(diffMinutes);
                    if (m.contains("-")) {
                        m = m.replace("-", "");
                    }
                    String d = String.valueOf(diffDays);
                    if (d.contains("-")) {
                        d = d.replace("-", "");
                    }
                    calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                            Integer.parseInt(m),
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value
                            , b);

                } else {
                    holidayPay = 0;

                    String hr = String.valueOf(diffHours);
                    if (hr.contains("-")) {
                        hr = hr.replace("-", "");
                    }
                    String m = String.valueOf(diffMinutes);
                    if (m.contains("-")) {
                        m = m.replace("-", "");
                    }
                    String d = String.valueOf(diffDays);
                    if (d.contains("-")) {
                        d = d.replace("-", "");
                    }
                    calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                            Integer.parseInt(m),
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value
                            , b);
                }

            }
        });
        Intent i = getIntent();
        b = i.getExtras();
        if (b != null) {
            if (getSupportActionBar() != null)
                ((TextView) findViewById(R.id.add_day_textview)).setText("Update Day");
            addDay = (AddDay) b.getSerializable(Constants.AddDayProfile);
            fillAllFields(addDay);
        }


        spinnerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Profiles profile = profilesArrayList.get(position);

                if (profile.getGet_tournamenttip() == 1) {
                    getTournamentTips = true;
                }
                holiday_off_value = profile.getHoliday_pay();
                start_week = profile.getStartday();
                String wage = profile.getHourly_pay();
                if (!wage.equalsIgnoreCase("")) {
                    wage_hourly = Double.parseDouble(wage); // hourly wage of profile
                }
                profile.getIs_supervisor();
                getting_tips = profile.getGet_tips();

                getting_tournament = profile.getGet_tournamenttip();
                global_startday = profile.getStartday();
                global_payperiod = profile.getPay_period();
                is_supervisor = profile.getIs_supervisor();
                selected_profile = profile.getProfile_name();
                selected_profile_color = profile.getProfile_color();
                selected_profile_id = profile.getId();
                updateView(profile, b);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        type_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch_value = 1;

                editext_manual_tips.setText("");
                manual_percentage_data = 0;
                manual_data = 0;

                Double tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                total_tipout.setText(String.format("%.2f", tipOut));

                String hr = String.valueOf(diffHours);
                if (hr.contains("-")) {
                    hr = hr.replace("-", "");
                }
                String m = String.valueOf(diffMinutes);
                if (m.contains("-")) {
                    m = m.replace("-", "");
                }
                String d = String.valueOf(diffDays);
                if (d.contains("-")) {
                    d = d.replace("-", "");
                }
                calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                        Integer.parseInt(m),
                        Integer.parseInt(d), tipOut, total_tipsInput, total_tournamentdown, holiday_off_value, b);

            }
        });

        editext_manual_tips.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double tipOut = 0;
                String str = s.toString();

                if (type_switch.isChecked()) {
                    switch_value = 1;
                    if (!str.equalsIgnoreCase("")) {

                        Log.i("total_", String.valueOf(total_tipPercentage));
                        double p = Double.parseDouble(str);
                        manual_percentage_data = p;
                        manually_added_tips = String.valueOf(manual_percentage_data);
                        tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                        text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                        total_tipout.setText(String.format("%.2f", tipOut));
                    } else {
                        manual_percentage_data = 0;
                        manually_added_tips = String.valueOf(manual_percentage_data);
                        tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                        text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                        total_tipout.setText(String.format("%.2f", tipOut));
                    }
                } else {
                    if (!str.equalsIgnoreCase("")) {

                        manual_data = Double.parseDouble(str);
                        manually_added_tips = String.valueOf(manual_data);
                        double per = manual_data / total_tipsInput;
                        per = per * 100;

                        manual_percentage_data = per;

                        tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                        text_tip_out_percent.setText(String.format("%.2f", manual_percentage_data + tipeePercent) + "%");
                        total_tipout.setText(String.format("%.2f", tipOut));
                    } else {
                        manual_data = 0;
                        manually_added_tips = String.valueOf(manual_data);
                        tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                        text_tip_out_percent.setText(String.format("%.2f", manual_percentage_data + tipeePercent) + "%");
                        total_tipout.setText(String.format("%.2f", tipOut));
                    }
                }


                String hr = String.valueOf(diffHours);
                if (hr.contains("-")) {
                    hr = hr.replace("-", "");
                }
                String m = String.valueOf(diffMinutes);
                if (m.contains("-")) {
                    m = m.replace("-", "");
                }
                String d = String.valueOf(diffDays);
                if (d.contains("-")) {
                    d = d.replace("-", "");
                }
                calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                        Integer.parseInt(m),
                        Integer.parseInt(d), tipOut, total_tipsInput, total_tournamentdown, holiday_off_value, b);
            }
        });
    }

    public void findViewByIds() {
        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        type_switch = (Switch) findViewById(R.id.type_switch);
        editext_manual_tips = (EditText) findViewById(R.id.manual_tip);

        toolbar = (Toolbar) findViewById(R.id.day_toolbar);
        this.setSupportActionBar(toolbar);

        fetchedTipees = (ListView) findViewById(R.id.list_view_tipees);
        fetchedTipees.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        text_tip_out_percent = (TextView) findViewById(R.id.textView_tip_out_percent);
        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
        editText_startShift = (EditText) findViewById(R.id.editText_start_shift);
        editText_endShift = (EditText) findViewById(R.id.editText_end_shift);
        edittext_clockIn = (EditText) findViewById(R.id.editText_clock_in);
        edittext_clockOut = (EditText) findViewById(R.id.editText_clock_out);
        texview_hours = (TextView) findViewById(R.id.textView_calculated_hours);
        start_dateCalendar = Calendar.getInstance();
        end_dateCalendar = Calendar.getInstance();
        calStartDay = Calendar.getInstance();
        calEndDay = Calendar.getInstance();

        holiday_pay = (CheckBox) findViewById(R.id.checkBox_holiday_pay);
        edit_total_tips = (EditText) findViewById(R.id.editText_total_tips);
        if (edit_total_tips.isFocused())
            edit_total_tips.clearFocus();

        day_off_timer = (ImageView) findViewById(R.id.day_off_timer);

        total_tipslabel = (TextView) findViewById(R.id.textView9);
        tipout_tipees_label = (TextView) findViewById(R.id.textView11);
        total_tipoutPer = (TextView) findViewById(R.id.textView12);
        total_tipout = (TextView) findViewById(R.id.textView27);
        total_tipoutlabel = (TextView) findViewById(R.id.textView26);
        from_label = (TextView) findViewById(R.id.textView4);
        tournament_downlabel = (TextView) findViewById(R.id.textView14);
        cout_label = (TextView) findViewById(R.id.textView21);
        perTd_label = (TextView) findViewById(R.id.textView22);
        totalcount_label = (TextView) findViewById(R.id.textView23);
        tipee_nodata = (TextView) findViewById(R.id.no_data);

        tipee_nodata.setVisibility(View.GONE);
        current_date = Calendar.getInstance();
        current_time = current_date.getTime();

        edittext_count = (EditText) findViewById(R.id.editText_number_of_td);
        edittext_perTD = (EditText) findViewById(R.id.editText_amount_per_td);
        edittext_total = (EditText) findViewById(R.id.editText_total_td_amount);

        close_icon = (ImageButton) findViewById(R.id.close_icon);
        checkBox_dayoff = (CheckBox) findViewById(R.id.checkbox_dayoff);
        save_details = (TextView) findViewById(R.id.save_add_day);

        close_icon.setOnClickListener(this);
        save_details.setOnClickListener(this);
        textview_autoCalLabel = (TextView) findViewById(R.id.textView6);
        label_to = (TextView) findViewById(R.id.textView5);
        image_to = (ImageView) findViewById(R.id.imageView3);

        tipee_layout = (RelativeLayout) findViewById(R.id.linearLayout_tipees);

        tipSeparator = (View) findViewById(R.id.tips_separator);
        tournamentSeparator = (View) findViewById(R.id.tournament_separator);

        label_manual_tips = (TextView) findViewById(R.id.view_type);
        label_dollar_sign = (TextView) findViewById(R.id.dollar);
        label_per_sign = (TextView) findViewById(R.id.percent);

        checkBoxEndofPayPeriod = (CheckBox) findViewById(R.id.checkBox_end_of_pay_period);

        checkBoxEndofPayPeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEndDay = 1;
                    tempEndDay = isEndDay;
                    todayPayDay(tempEndDay, global_startday, global_payperiod);
                } else {
                    isEndDay = 0;
                    tempEndDay = isEndDay;
                    todayPayDay(tempEndDay, global_startday, global_payperiod);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText_clock_in:
                getTimePicker(selected_timeformatIndex, R.id.editText_clock_in, new TimeChangeListener() {
                    @Override
                    public void OnTimeChange(String start_time, String end_time, Calendar start_date1, Calendar end_date1) {

                        if (end_time.equalsIgnoreCase("") || start_date.equalsIgnoreCase("") || end_date.equalsIgnoreCase("")) {

                        } else {
                            Date date = start_date1.getTime();
                            Date date1 = end_date1.getTime();

                            result = datesDifference(date, date1);

                            texview_hours.setText(result);
                        }
                    }
                });
                break;
            case R.id.editText_clock_out:

                getTimePicker(selected_timeformatIndex, R.id.editText_clock_out, new TimeChangeListener() {
                    @Override
                    public void OnTimeChange(String start_time, String end_time, Calendar start_date1, Calendar end_date1) {
//
//                        if (start_time.equalsIgnoreCase("") || start_date.equalsIgnoreCase("")
//                                || end_date.equalsIgnoreCase("")) {
//
//                        } else {
                        Date date = start_date1.getTime();
                        Date date1 = end_date1.getTime();

//                        start_shift_long = date.getTime();
//                        end_shift_long = date1.getTime();

                        result = datesDifference(date, date1);

                        texview_hours.setText(result);
                        String hr = String.valueOf(diffHours);
                        if (hr.contains("-")) {
                            hr = hr.replace("-", "");
                        }
                        String m = String.valueOf(diffMinutes);
                        if (m.contains("-")) {
                            m = m.replace("-", "");
                        }
                        String d = String.valueOf(diffDays);
                        if (d.contains("-")) {
                            d = d.replace("-", "");
                        }
                        calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                                Integer.parseInt(m),
                                Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value, b);

//                        }

                    }
//                    }
                });
                break;

            case R.id.close_icon:
                this.finish();
                break;

            case R.id.save_add_day:

                save_add_day();
                break;
        }
    }

    public String getTimePicker(final int time_format_index, final int viewId, final TimeChangeListener onTimeChange) {
        final Dialog timePicker = new Dialog(AddDayActivity.this);

        timePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflaterTime = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewTimePicker = inflaterTime.inflate(R.layout.time_picker, null);
        timePicker.setContentView(viewTimePicker);
        final TimePicker timePick = (TimePicker) viewTimePicker
                .findViewById(R.id.timePicker);
        if (time_format_index == 1) {
            timePick.setIs24HourView(true);
        }

        // Selecting time from Time Picker
        Button selectTime = (Button) viewTimePicker
                .findViewById(R.id.timeSelect);

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentMinute = timePick.getCurrentMinute() <= 9 ? "0"
                        + timePick.getCurrentMinute() : timePick
                        .getCurrentMinute() + "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    time = timePick.getHour() + ":" + currentMinute;
                }
                time = timePick.getCurrentHour() + ":" + currentMinute;
                System.out.println("SETTED TIME>> " + time);

                if (viewId == R.id.editText_clock_in) {
                    startHour = timePick.getCurrentHour();
                    startMinute = timePick.getCurrentMinute();
                    // showTime(startHour, startMinute, edittext_clockIn);
                    //  edittext_clockIn.setText(time);

                    if (time_format_index == 0) {
                        int hr = startHour % 12;
                        edittext_clockIn.setText(String.format("%02d:%02d %s", hr == 0 ? 12 : hr,
                                startMinute, startHour < 12 ? "am" : "pm"));
                    } else {
                        edittext_clockIn.setText(String.format("%02d:%02d", startHour, startMinute));
                    }

                } else if (viewId == R.id.editText_clock_out) {
                    endHour = timePick.getCurrentHour();
                    endMinute = timePick.getCurrentMinute();

                    if (time_format_index == 0) {
                        int hr = endHour % 12;
                        edittext_clockOut.setText(String.format("%02d:%02d %s", hr == 0 ? 12 : hr,
                                endMinute, endHour < 12 ? "am" : "pm"));
                    } else {
                        edittext_clockOut.setText(String.format("%02d:%02d", endHour, endMinute));
                    }
                    //  showTime(endHour, endMinute, edittext_clockOut);

                }

                if (viewId == R.id.editText_clock_in) {
                    start_time = time;
                    start_dateCalendar.set(Calendar.HOUR_OF_DAY, startHour);
                    start_dateCalendar.set(Calendar.MINUTE, startMinute);
                    start_dateCalendar.set(Calendar.SECOND, 0);

                    onTimeChange.OnTimeChange(start_time, end_time, start_dateCalendar, end_dateCalendar);
                } else if (viewId == R.id.editText_clock_out) {
                    end_time = time;
                    end_dateCalendar.set(Calendar.HOUR_OF_DAY, endHour);
                    end_dateCalendar.set(Calendar.MINUTE, endMinute);
                    end_dateCalendar.set(Calendar.SECOND, 0);

                    onTimeChange.OnTimeChange(start_time, end_time, start_dateCalendar, end_dateCalendar);
                }
                timePicker.dismiss();
            }
        });
        timePicker.show();
        return time;
    }

    String date_format = "MMMM d, yyyy";

    public String getDatePicker(final int format_index, final int viewId) {

        // Infalating Dialog for Date Picker
        final Dialog datePicker = new Dialog(AddDayActivity.this);
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

                if (viewId == R.id.editText_start_shift) {
                    startDay = dayOfMonth;
                    startMonth = month;
                    startYear = year;

                    /*start_calendar.set(Calendar.MONTH, startMonth);
                    start_calendar.set(Calendar.DAY_OF_MONTH, startDay);*/
                    calStartDay.set(startYear, startMonth, startDay);
                   /* calStartDay.set(Calendar.HOUR_OF_DAY, 0);
                    calStartDay.set(Calendar.MINUTE, 0);
                    calStartDay.set(Calendar.SECOND, 0);
                    calStartDay.set(Calendar.MILLISECOND, 0);*/


                    start_dateCalendar.set(startYear, startMonth, startDay);

//                    selectedDate = String.valueOf(startMonth + 1) + "/"
//                            + String.valueOf(startDay) + "/"
//                            + String.valueOf(startYear);

                } else if (viewId == R.id.editText_end_shift) {
                    endDay = dayOfMonth;
                    endMonth = month;
                    endYear = year;

//                    selectedDate = String.valueOf(endMonth + 1) + "/"
//                            + String.valueOf(startDay) + "/"
//                            + String.valueOf(endYear);
                    calEndDay.set(endYear, endMonth, endDay);
                    /*calEndDay.set(Calendar.HOUR_OF_DAY, 0);
                    calEndDay.set(Calendar.MINUTE, 0);
                    calEndDay.set(Calendar.SECOND, 0);
                    calEndDay.set(Calendar.MILLISECOND, 0);*/

                    end_dateCalendar.set(endYear, endMonth, endDay);
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

//                if (parsedStartDate.before(currentDate)) {
//                    Toast.makeText(AddDayActivity.this,
//                            "Past date cannot be selected!", Toast.LENGTH_LONG)
//                            .show();
//                    pastDateSelected = true;
//                } else {
//                    pastDateSelected = false;
//                }

//                if (pastDateSelected) {
//                    if (viewId == R.id.editText_start_shift) {
//                        editText_startShift.setText("Set Start Date");
//                    } else if (viewId == R.id.editText_end_shift) {
//                        editText_endShift.setText("Set End Date");
//                    }
//                } else {
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
                }

//                }

            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (viewId == R.id.editText_start_shift) {

                    if (selected_profile.equalsIgnoreCase("Select Profile")) {
                        global_payperiod = "";
                    }
//                    todayPayDay(global_startday, global_payperiod);
                }
                if (viewId == R.id.editText_start_shift && startDay == 0) {
                    isStartDateChanged = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    startYear = Integer.parseInt(d.substring(0, 4));
                    startMonth = Integer.parseInt(d.substring(4, 6));
                    startDay = Integer.parseInt(d.substring(6));
                    System.out.println(sdf.format(new Date()) + startYear
                            + startMonth + startDay);
//                    String currentDate = String.valueOf(startMonth) + "/"
//                            + String.valueOf(startDay) + "/"
//                            + String.valueOf(startYear);
                    selectedDate = String.valueOf(calStartDay.get(Calendar.MONTH) + 1) + "/"
                            + String.valueOf(calStartDay.get(Calendar.DAY_OF_MONTH)) + "/"
                            + String.valueOf(calStartDay.get(Calendar.YEAR));
                    startDateDb = selectedDate;
                    Date dates = new Date(selectedDate);
                    start_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);


                    editText_startShift.setText(date);

                } else if (viewId == R.id.editText_end_shift && startDay == 0) {
                    isEndDateChanged = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    endYear = Integer.parseInt(d.substring(0, 4));
                    endMonth = Integer.parseInt(d.substring(4, 6));
                    endDay = Integer.parseInt(d.substring(6));

                    //  end_dateCalendar.set(endYear, endMonth, endDay);

                    System.out.println(sdf.format(new Date()) + endYear
                            + endMonth + endDay);

                    selectedDate = String.valueOf(calEndDay.get(Calendar.MONTH) + 1) + "/"
                            + String.valueOf(calEndDay.get(Calendar.DAY_OF_MONTH)) + "/"
                            + String.valueOf(calEndDay.get(Calendar.YEAR));
                    Date dates = new Date(selectedDate);
                    end_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);

                    editText_endShift.setText(date);

                } else if (viewId == R.id.editText_end_shift && startDay != 0) {
                    isEndDateChanged = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    endYear = Integer.parseInt(d.substring(0, 4));
                    endMonth = Integer.parseInt(d.substring(4, 6));
                    endDay = Integer.parseInt(d.substring(6));


                    //   end_dateCalendar.set(endYear, endMonth, endDay);

                    System.out.println(sdf.format(new Date()) + endYear
                            + endMonth + endDay);

                    selectedDate = String.valueOf(calEndDay.get(Calendar.MONTH) + 1) + "/"
                            + String.valueOf(calEndDay.get(Calendar.DAY_OF_MONTH)) + "/"
                            + String.valueOf(calEndDay.get(Calendar.YEAR));

                    date = new SimpleDateFormat(date_format).format(new Date(
                            selectedDate));
                    SimpleDateFormat sdfDate = new SimpleDateFormat(date_format);
                    String currentDateString = sdfDate.format(new Date(start_shift_long));
                    Date currentDate = null;
                    Date parsedStartDate = null;
                    try {
                        currentDate = sdfDate.parse(currentDateString);
                        parsedStartDate = sdfDate.parse(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (parsedStartDate.before(currentDate)) {
                        Toast.makeText(AddDayActivity.this,
                                "Past date cannot be selected!", Toast.LENGTH_LONG)
                                .show();
                        editText_endShift.setText("Select Date");
                        pastDateSelected = true;

                    } else {
                        pastDateSelected = false;
                        editText_endShift.setText(date);
                    }

                } else {

                    if (pastDateSelected) {
                        if (viewId == R.id.editText_start_shift) {
                            editText_startShift.setText("Set Start Date");
                        } else if (viewId == R.id.editText_end_shift) {
                            editText_endShift.setText("Set End Date");
                        }
                    } else {
                        // Toast.makeText(AddEventActivity.this,
                        // "Select start date once again!",
                        // Toast.LENGTH_SHORT).show();

                        if (viewId == R.id.editText_start_shift) {
                            isStartDateChanged = true;
                            try {
                                selectedDate = String.valueOf(calStartDay.get(Calendar.MONTH) + 1) + "/"
                                        + String.valueOf(calStartDay.get(Calendar.DAY_OF_MONTH)) + "/"
                                        + String.valueOf(calStartDay.get(Calendar.YEAR));
                                startDateDb = selectedDate;
                                Date selectedDate1 = new Date(selectedDate);
                                start_shift_long = selectedDate1.getTime();
                                date = new SimpleDateFormat(date_format)
                                        .format(selectedDate1);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                String d = sdf.format(selectedDate1);

                                startYear = Integer.parseInt(d.substring(0, 4));
                                startMonth = Integer.parseInt(d.substring(4, 6));
                                startDay = Integer.parseInt(d.substring(6));

                                //  calStartDay.set(startYear, startMonth, startDay);


//                                calStartDay.set(Calendar.HOUR_OF_DAY, 0);
//                                calStartDay.set(Calendar.MINUTE, 0);
//                                calStartDay.set(Calendar.SECOND, 0);
//                                calStartDay.set(Calendar.MILLISECOND, 0);

                                //  start_dateCalendar.set(startYear, startMonth, startDay);

                                System.out.println(sdf.format(new Date()) + startYear
                                        + startMonth + startDay);
                                selectedDate = String.valueOf(calStartDay.get(Calendar.MONTH) + 1) + "/"
                                        + String.valueOf(calStartDay.get(Calendar.DAY_OF_MONTH)) + "/"
                                        + String.valueOf(calStartDay.get(Calendar.YEAR));

                                Date dates = new Date(selectedDate);
                                start_shift_long = dates.getTime();

                                System.out.println(sdf.format(new Date()) + startYear
                                        + startMonth + startDay);

                                // startDateDb = currentDate;


                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            editText_startShift.setText(date);
                        }
                        if (viewId == R.id.editText_end_shift) {
                            isEndDateChanged = true;
                            try {
                                selectedDate = String.valueOf(calEndDay.get(Calendar.MONTH) + 1) + "/"
                                        + String.valueOf(calEndDay.get(Calendar.DAY_OF_MONTH)) + "/"
                                        + String.valueOf(calEndDay.get(Calendar.YEAR));

                                Date selectedDate1 = new Date(selectedDate);
                                end_shift_long = selectedDate1.getTime();
                                date = new SimpleDateFormat(date_format)
                                        .format(selectedDate1);
                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            editText_endShift.setText(date);
                        }

                    }
                }

                System.out.println("DATE>>>> " + date);

                datePicker.dismiss();

            }
        });

        // Selecting date from Date Dialog

        datePicker.show();
        return date;
    }

    long diffDays, diffMinutes, diffHours;

    public String datesDifference(Date startDate, Date endDate) {
        String result = "";
        long diff = startDate.getTime() - endDate.getTime();

        long diffSeconds = diff / 1000 % 60;
        diffMinutes = diff / (60 * 1000) % 60;
        diffHours = diff / (60 * 60 * 1000) % 24;
        diffDays = diff / (24 * 60 * 60 * 1000);
        String hours = String.valueOf(diffHours);
        diffHours = Math.round(diffHours);
        diffMinutes = Math.round(diffMinutes);
        diffDays = Math.round(diffDays);

        if (hours.contains("-")) {
            hours = hours.replace("-", " ");
        }
        String min = String.valueOf(diffMinutes);
        if (min.contains("-")) {
            min = min.replace("-", " ");

        }

        if (diffDays != 0) {
            String day = String.valueOf(diffDays);
            if (day.contains("-")) {
                day = day.replace("-", " ");
            }
            result = day + "days," + hours + "h" + min + "m";
        } else {
            result = hours + "h" + min + "m";
        }
        hours = hours.trim();
        min = min.trim();
        if (!hours.equalsIgnoreCase("") || !min.equalsIgnoreCase("")) {
            totalsHours = Integer.parseInt(hours);
            totalMinutes = Integer.parseInt(min);
        }


        return result;
    }

    public String showTime(int hour, int min, EditText editText) {

        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        editText.setText(new StringBuilder().append(hour).append(":").append(min).append(" ").append(format));
        return format.toLowerCase();
    }

    public double calculate_total(String counts, String per_TDs) {
        double total = 0;
        if (!counts.equalsIgnoreCase("") && !per_TDs.equalsIgnoreCase("")) {
            double count_1 = Double.parseDouble(counts);
            double per_tds = Double.parseDouble(per_TDs);
            total = count_1 * per_tds;
        }

        return total;
    }

    public void save_add_day() {
        StringBuilder joinedString = new StringBuilder();
        if (!selected_profile.equals("Select Profile") && !selected_profile.equals("") && !editText_startShift.getText().toString().trim().equals("") &&
                ((day_off == 0 && !texview_hours.getText().toString().trim().equals("")) || day_off == 1)) {

            if (b == null) {
                if (selected_tipeesIDs == null || selected_tipeesIDs.size() == 0) {

                } else {
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

                    //  joinedString = convertArrayToString(selected_tipeesIDs);
                }

                try {

                    new DatabaseOperations(AddDayActivity.this).insertAddDayInfo(selected_profile, selected_profile_id, startDateDb,
                            start_dateCalendar.getTimeInMillis(), editText_endShift.getText().toString().trim(), end_dateCalendar.getTimeInMillis(),
                            texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString.toString(), text_tip_out_percent.getText().toString().trim(),
                            total_tipout.getText().toString().trim(), editText_new_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                            day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, calStartDay.getTimeInMillis(), calEndDay.getTimeInMillis(),
                            switch_value, manually_added_tips, selected_profile_color, isEndDay, totalsHours, totalMinutes, String.valueOf(wage_hourly));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                // Don't require this check,
                // if (!addDay.getTip_out_tipees().equalsIgnoreCase("")) {
                if (addDay.getTip_out_tipees().equalsIgnoreCase(""))
                    selected_tipeesIDs = new ArrayList<>();
                else
                    selected_tipeesIDs = new ArrayList<>(Arrays.asList(convertStringToArray(addDay.getTip_out_tipees())).get(0));
                if (selected_tipeesIDs.size() == 0) {
                    if (temp_arraylist != null && temp_arraylist.size() > 0) {
                        for (Map.Entry<String, Boolean> newTippee : temp_arraylist.entrySet()) {
                            if (newTippee.getValue() == true) {
                                if (!selected_tipeesIDs.contains(newTippee.getKey()))
                                    selected_tipeesIDs.add(newTippee.getKey());

                            } else {
                                selected_tipeesIDs.remove(newTippee.getKey());
                                //unselectedTipees.add(newTippee.getKey());
                            }
                        }
                        if (selected_tipeesIDs.size() > 0) {
                            for (int i = 0; i < selected_tipeesIDs.size(); i++) {
                                joinedString.append(selected_tipeesIDs.get(i));
                                joinedString.append(",");
                            }

                        }
                    }
                } else if (selected_tipeesIDs != null && selected_tipeesIDs.size() > 0) {
                    if (temp_arraylist != null && temp_arraylist.size() > 0) {

                        if (tipeeInfos.size() > 0) {
                            //if selected comes again
                            for (int j = 0; j < selected_tipeesIDs.size(); j++) {
                                if (temp_arraylist.containsKey(selected_tipeesIDs.get(j))) {
                                    if (temp_arraylist.get(selected_tipeesIDs.get(j)) == true) {
                                        joinedString.append(temp_arraylist.get(selected_tipeesIDs.get(j)));
                                        joinedString.append(",");
                                    }
                                }
                            }
                            List<String> unselectedTipees = new ArrayList<>();
                            //if a new tippee is selected
                            for (Map.Entry<String, Boolean> newTippee : temp_arraylist.entrySet()) {
                                if (newTippee.getValue() == true) {
                                    if (!selected_tipeesIDs.contains(newTippee.getKey()))
                                        selected_tipeesIDs.add(newTippee.getKey());

                                } else {
                                    selected_tipeesIDs.remove(newTippee.getKey());
                                    //unselectedTipees.add(newTippee.getKey());
                                }
                            }

                            if (selected_tipeesIDs.size() > 0) {
                                for (int i = 0; i < selected_tipeesIDs.size(); i++) {
                                    joinedString.append(selected_tipeesIDs.get(i));
                                    joinedString.append(",");
                                }

                            }

                        }
                    }
                }
                // }


                // joinedString = convertArrayToString(selected_tipeesIDs);

                try {
                    if (isStartDateChanged && isEndDateChanged) {

                        new DatabaseOperations(AddDayActivity.this).updateAddDayInfo(addDayID, selected_profile, startDateDb,
                                start_dateCalendar.getTimeInMillis(), editText_endShift.getText().toString().trim(), end_dateCalendar.getTimeInMillis(),
                                texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString.toString(), text_tip_out_percent.getText().toString().trim(),
                                total_tipout.getText().toString().trim(), editText_new_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                                day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, calStartDay.getTime().getTime(), calEndDay.getTime().getTime(),
                                switch_value, manually_added_tips, selected_profile_color, isEndDay, totalsHours, totalMinutes
                                , String.valueOf(wage_hourly));

                        isStartDateChanged = false;
                        isEndDateChanged = false;
                    } else if (!isStartDateChanged && !isEndDateChanged) {
                        new DatabaseOperations(AddDayActivity.this).updateAddDayInfo(addDayID, selected_profile, startDateDb,
                                updated_start_values, editText_endShift.getText().toString().trim(), updated_end_values,
                                texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString.toString(), text_tip_out_percent.getText().toString().trim(),
                                total_tipout.getText().toString().trim(), editText_new_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                                day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, calStartDay.getTime().getTime(), calEndDay.getTime().getTime(),
                                switch_value, manually_added_tips, selected_profile_color, isEndDay, totalsHours, totalMinutes
                                , String.valueOf(wage_hourly));
                    } else if (!isStartDateChanged && isEndDateChanged) {
                        new DatabaseOperations(AddDayActivity.this).updateAddDayInfo(addDayID, selected_profile, startDateDb,
                                d.getTime(), editText_endShift.getText().toString().trim(), end_dateCalendar.getTimeInMillis(),
                                texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString.toString(), text_tip_out_percent.getText().toString().trim(),
                                total_tipout.getText().toString().trim(), editText_new_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                                day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, calStartDay.getTime().getTime(), calEndDay.getTime().getTime(),
                                switch_value, manually_added_tips, selected_profile_color, isEndDay, totalsHours, totalMinutes
                                , String.valueOf(wage_hourly));
                        isEndDateChanged = false;
                    } else {
                        new DatabaseOperations(AddDayActivity.this).updateAddDayInfo(addDayID, selected_profile, startDateDb,
                                start_dateCalendar.getTimeInMillis(), editText_endShift.getText().toString().trim(), d1.getTime(),
                                texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString.toString(), text_tip_out_percent.getText().toString().trim(),
                                total_tipout.getText().toString().trim(), editText_new_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                                day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, calStartDay.getTime().getTime(), calEndDay.getTime().getTime(),
                                switch_value, manually_added_tips, selected_profile_color, isEndDay, totalsHours, totalMinutes
                                , String.valueOf(wage_hourly));
                        isStartDateChanged = false;

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            startActivity(new Intent(AddDayActivity.this, LandingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            this.finish();
        } else {
            if (selected_profile.equals("") || selected_profile.equalsIgnoreCase("Select Profile")) {
                Snackbar.make(spinnerProfile, "Please select profile!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (editText_startShift.getText().toString().trim().equals("")) {
                Snackbar.make(spinnerProfile, "Please enter start shift!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (((day_off == 0 && texview_hours.getText().toString().trim().equals("")) || day_off == 1)) {
                Snackbar.make(spinnerProfile, "Need more details to save the day!", Snackbar.LENGTH_LONG).show();
                return;
            }

        }
    }

    List<String> selectedTipeesID;

    public void updateView(Profiles profile, Bundle b) {
        if (b != null) {
            total_earnings.setText("$" + addDay.getTotal_earnings());
            isEndDay = addDay.getIsEndPay();
        }

     /*   if (profile.getPay_period() != null && profile.getPay_period().equalsIgnoreCase("Daily ")) {
            tempEndDay = 1;
            isEndDay = tempEndDay;

        }*/

        if (b == null) {
            todayPayDay(tempEndDay, profile.getStartday(), profile.getPay_period());
            tempEndDay = 0;
        }

        if (is_supervisor == 1 && getting_tournament == 0) {
            editText_new_count.setVisibility(View.GONE);
            new_count_textview.setVisibility(View.GONE);
            checkBoxEndofPayPeriod.setVisibility(View.GONE);
            // checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
        } else if (is_supervisor == 0 && getting_tips == 0 && getting_tournament == 1) {
            checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
            editText_new_count.setVisibility(View.VISIBLE);
            new_count_textview.setVisibility(View.VISIBLE);
        } else if (is_supervisor == 1 && getting_tournament == 1) {
            checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
            editText_new_count.setVisibility(View.VISIBLE);
            new_count_textview.setVisibility(View.VISIBLE);
        } else {
            checkBoxEndofPayPeriod.setVisibility(View.GONE);
            editText_new_count.setVisibility(View.GONE);
            new_count_textview.setVisibility(View.GONE);
        }
        selectedTipeesID = new ArrayList<>();
        selectedTipeesID = convertStringToArray(String.valueOf(profile.getTipees_name()));
        if (profile.getGet_tournamenttip() == 0) {
            tournament_downlabel.setVisibility(View.GONE);
            //  cout_label.setVisibility(View.GONE);
            // perTd_label.setVisibility(View.GONE);
            totalcount_label.setVisibility(View.GONE);
            edittext_count.setVisibility(View.GONE);
            edittext_perTD.setVisibility(View.GONE);
            edittext_total.setVisibility(View.GONE);
            tournamentSeparator.setVisibility(View.GONE);
        } else if (profile.getGet_tournamenttip() == 1 && day_off == 0) {
            tournamentSeparator.setVisibility(View.VISIBLE);
            tournament_downlabel.setVisibility(View.VISIBLE);
        }

        if (profile.getGet_tips() == 0) {
            total_tipslabel.setVisibility(View.GONE);
            tipout_tipees_label.setVisibility(View.GONE);

            edit_total_tips.setVisibility(View.GONE);
            total_tipout.setVisibility(View.GONE);
            total_tipoutlabel.setVisibility(View.GONE);
            text_tip_out_percent.setVisibility(View.GONE);
            fetchedTipees.setVisibility(View.GONE);
            total_tipoutPer.setVisibility(View.GONE);
            tipee_layout.setVisibility(View.GONE);
            tipSeparator.setVisibility(View.GONE);

            label_manual_tips.setVisibility(View.GONE);
            label_dollar_sign.setVisibility(View.GONE);
            label_per_sign.setVisibility(View.GONE);
            type_switch.setVisibility(View.GONE);
            editext_manual_tips.setVisibility(View.GONE);

        } else if (profile.getGet_tips() == 1 && day_off == 0) {
            tipSeparator.setVisibility(View.VISIBLE);
            tipee_layout.setVisibility(View.VISIBLE);

            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);

            label_manual_tips.setVisibility(View.VISIBLE);
            label_dollar_sign.setVisibility(View.VISIBLE);
            label_per_sign.setVisibility(View.VISIBLE);
            type_switch.setVisibility(View.VISIBLE);
            editext_manual_tips.setVisibility(View.VISIBLE);

            if (selectedTipeesID.size() > 0) {
                getAllTipees(selectedTipeesID, fetched);
                //  text_tip_out_percent.setVisibility(View.VISIBLE);
//                total_tipoutPer.setVisibility(View.VISIBLE);
                //   total_tipoutlabel.setVisibility(View.VISIBLE);
                //total_tipout.setVisibility(View.VISIBLE);
                tipee_nodata.setVisibility(View.GONE);
                fetchedTipees.setVisibility(View.VISIBLE);
            } else {
//                total_tipoutPer.setVisibility(View.GONE);
                fetchedTipees.setVisibility(View.GONE);
                tipee_nodata.setVisibility(View.VISIBLE);
                //  total_tipout.setVisibility(View.GONE);
                //   total_tipoutlabel.setVisibility(View.GONE);
                tipee_nodata.setText("Tippee not found");
                // text_tip_out_percent.setVisibility(View.GONE);
            }
        }

        if (b != null) {
            int day_off = addDay.getDay_off();
            if (day_off == 1) {
                checkBox_dayoff.setChecked(true);
                setDayOffView();
            } else {
                updateDayOffView();
            }
        }


    }

    ArrayList<TipeeInfo> tipeeInfos;
    FetchedTipeeAdapter adapter;
    ArrayList<TipeeInfo> info;
    TipeeInfo tipeeInfo;
    double percentage = 0;
    double total_tipPercentage = 0;
    double tipeePercent = 0;

    public void getAllTipees(List<String> selectedProfileTipees, List<String> CheckedTipeesAddDays) {
        tipeeInfos = new ArrayList<>();
        info = new ArrayList<>();
        tipeeInfos = new DatabaseOperations(AddDayActivity.this).fetchTipeeList(AddDayActivity.this);
     /*   if (selectedTipeesID == null) {
            selectedTipeesID = new ArrayList<>();
        }*/
        if (selectedProfileTipees == null) {
            selectedProfileTipees = new ArrayList<>();
        }


    /*    if (tipeeInfos != null && tipeeInfos.size() > 0) {
            for (int i = 0; i < tipeeInfos.size(); i++) {
                for (int j = 0; j < selectedTipeesID.size(); j++) {
                    if (selectedTipeesID.get(j).equalsIgnoreCase(tipeeInfos.get(i).getId())) {
                        tipeeInfo = new TipeeInfo();
                        tipeeInfo = tipeeInfos.get(i);
                        info.add(tipeeInfo);
                    }
                }
            }
        }
*/

        if (tipeeInfos != null && tipeeInfos.size() > 0) {
            for (int i = 0; i < tipeeInfos.size(); i++) {
                for (int j = 0; j < selectedProfileTipees.size(); j++) {
                    if (selectedProfileTipees.get(j).equalsIgnoreCase(tipeeInfos.get(i).getId())) {
                        tipeeInfo = new TipeeInfo();
                        tipeeInfo = tipeeInfos.get(i);

                        info.add(tipeeInfo);
                    }
                }

            }
        }


        if (tipeeInfos != null) {
            selected_tipeesIDs = new ArrayList<>();
            //      checked = fetched;
            checked = CheckedTipeesAddDays;


            //    final List<String> finalSelectedTipeesID = selectedTipeesID;
            final List<String> finalSelectedTipeeID = selectedProfileTipees;
            adapter = new FetchedTipeeAdapter(AddDayActivity.this, selectedProfileTipees, info, true, new TipeeChecked() {
                @Override
                public void OnTipeeChange(boolean isChecked, TipeeInfo tipeeInfo, int position) {
                    if (isChecked) {
                        checkedTipeeCalculations(tipeeInfo);
                    } else {

                        if (selected_tipeesIDs != null && selected_tipeesIDs.size() > 0) {
                            for (int i = 0; i < selected_tipeesIDs.size(); i++) {
                                if (selected_tipeesIDs.get(i).equalsIgnoreCase(tipeeInfo.getId())) {
                                    selected_tipeesIDs.remove(tipeeInfo.getId());
                                }
                            }
                        }
                        String per = tipeeInfo.getPercentage();
                        double tipOut = 0;
                        if (!per.equalsIgnoreCase("")) {
                            tipeePercent = Math.abs(tipeePercent - Double.parseDouble(per));
                            tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                            text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                            total_tipout.setText(String.format("%.2f", tipOut));
                        }

                        String hr = String.valueOf(diffHours);
                        if (hr.contains("-")) {
                            hr = hr.replace("-", "");
                        }
                        String m = String.valueOf(diffMinutes);
                        if (m.contains("-")) {
                            m = m.replace("-", "");
                        }
                        String d = String.valueOf(diffDays);
                        if (d.contains("-")) {
                            d = d.replace("-", "");
                        }
                        calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                                Integer.parseInt(m),
                                Integer.parseInt(d), tipOut, total_tipsInput, total_tournamentdown, holiday_off_value, b);


                    }
                }
            }, checked);
            fetchedTipees.setAdapter(adapter);
            fetchedTipees.setDivider(null);
        }

  /*      if (b != null) {
            if (tipeeInfos != null) {

                if (fetched != null && fetched.size() > 0) {
                    checked = fetched;
                    //   checked = new ArrayList<>(Arrays.asList(fetched.get(0)));
                }

                adapter = new FetchedTipeeAdapter(AddDayActivity.this, fetched, info, true, new TipeeChecked() {
                    @Override
                    public void OnTipeeChange(boolean isChecked, TipeeInfo tipeeInfo, int position) {
                        temp_arraylist.put(tipeeInfo.getId(), isChecked);
                        //   selected_tipeesIDs = fetched;
                        if (fetched != null && fetched.size() > 0) {
                            selected_tipeesIDs = new ArrayList<>(Arrays.asList(fetched.get(0)));
                        }

                        if (isChecked) {
                            checkedTipeeCalculations(tipeeInfo);

                        } else {

                            if (selected_tipeesIDs != null && selected_tipeesIDs.size() > 0) {
                                for (int i = 0; i < selected_tipeesIDs.size(); i++) {
                                    if (selected_tipeesIDs.get(i).equalsIgnoreCase(tipeeInfo.getId())) {
                                        selected_tipeesIDs.remove(tipeeInfo.getId());
                                    }
                                }
                            }
                            String per = tipeeInfo.getPercentage();
                            double tipOut = 0;
                            if (!per.equalsIgnoreCase("")) {
                                tipeePercent = Math.abs(tipeePercent - Double.parseDouble(per));
                                tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                                text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                                total_tipout.setText(String.format("%.2f", tipOut));
                            }

                            String hr = String.valueOf(diffHours);
                            if (hr.contains("-")) {
                                hr = hr.replace("-", "");
                            }
                            String m = String.valueOf(diffMinutes);
                            if (m.contains("-")) {
                                m = m.replace("-", "");
                            }
                            String d = String.valueOf(diffDays);
                            if (d.contains("-")) {
                                d = d.replace("-", "");
                            }
                            calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                                    Integer.parseInt(m),
                                    Integer.parseInt(d), tipOut, total_tipsInput, total_tournamentdown, holiday_off_value, b);


                        }
                    }
                }, checked);
                fetchedTipees.setAdapter(adapter);
                fetchedTipees.setDivider(null);
            }
        }*/
    }

    public void setDayOffView() {

        from_label.setText("Select Day");


        day_off_timer.setVisibility(View.VISIBLE);

        tipee_nodata.setVisibility(View.GONE);
        tipSeparator.setVisibility(View.GONE);
        tournamentSeparator.setVisibility(View.GONE);

        editText_startShift.setHint("Select Day");

        editText_startShift.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        tipee_layout.setVisibility(View.GONE);
        fetchedTipees.setVisibility(View.GONE);
        texview_hours.setVisibility(View.GONE); // hide calculated hours

        edittext_clockIn.setVisibility(View.GONE);
        editText_endShift.setVisibility(View.GONE);
        edittext_clockOut.setVisibility(View.GONE);
        textview_autoCalLabel.setVisibility(View.GONE);
        label_to.setVisibility(View.GONE);
        image_to.setVisibility(View.GONE);
        holiday_pay.setVisibility(View.GONE);
        total_tipslabel.setVisibility(View.GONE);
        tipout_tipees_label.setVisibility(View.GONE);
        total_tipoutPer.setVisibility(View.GONE);
        edit_total_tips.setVisibility(View.GONE);
        total_tipout.setVisibility(View.GONE);

        if (getting_tournament == 1) {
            tournament_downlabel.setVisibility(View.VISIBLE);
            checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
        }


//        cout_label.setVisibility(View.VISIBLE);
        // perTd_label.setVisibility(View.VISIBLE);
//        totalcount_label.setVisibility(View.VISIBLE);

        checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
        perTd_label.setVisibility(View.GONE);
        totalcount_label.setVisibility(View.GONE);
//        edittext_perTD.setVisibility(View.GONE);
        edittext_total.setVisibility(View.GONE);

        //     edittext_count.setVisibility(View.VISIBLE);
        //   edittext_perTD.setVisibility(View.VISIBLE);
        // edittext_total.setVisibility(View.VISIBLE);

        text_tip_out_percent.setVisibility(View.GONE);
        total_tipoutlabel.setVisibility(View.GONE);
        fetchedTipees.setVisibility(View.GONE);
        label_manual_tips.setVisibility(View.GONE);
        label_dollar_sign.setVisibility(View.GONE);
        label_per_sign.setVisibility(View.GONE);
        type_switch.setVisibility(View.GONE);
        editext_manual_tips.setVisibility(View.GONE);
        editText_new_count.setVisibility(View.GONE);
        new_count_textview.setVisibility(View.GONE);
        total_earnings.setVisibility(View.GONE);
        label_total_earnings.setText("");
        total_earnings.setText("");

        if (isEndDay == 1 || getting_tournament == 1) {
            todayPayDay(isEndDay, global_startday, global_payperiod);
        }
        if (b != null) {
            if (day_off == 1) {
                earns = "0";
            }
        }

    }

    public List<String> convertStringToArray(String joinedString) {
        List<String> sellItems = new ArrayList<>();
        if (!joinedString.equalsIgnoreCase("")) {
            sellItems = Arrays.asList(joinedString.split(","));
        }

        return sellItems;

    }

    public void updateDayOffView() {

        from_label.setText("From");

        day_off_timer.setVisibility(View.GONE);
        editText_startShift.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        edittext_clockIn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (b == null) {
            tipee_nodata.setVisibility(View.VISIBLE);
        } else if (fetched != null && fetched.size() > 0) {
            tipee_nodata.setVisibility(View.GONE);
        }
        texview_hours.setVisibility(View.VISIBLE); // visible calculated hours
        if (is_supervisor == 0 && (getting_tips == 1 || getting_tournament == 1)) {
            checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
            tipSeparator.setVisibility(View.VISIBLE);
            tournamentSeparator.setVisibility(View.VISIBLE);
            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            if (b != null) {
                if (addDay.getGetting_tips() == 1) {
                    getTournamentTips = true;
                }

            }
            if (getTournamentTips) {
                if (is_todayPayDay) {

                    perTd_label.setVisibility(View.VISIBLE);
                    totalcount_label.setVisibility(View.VISIBLE);
                    edittext_perTD.setVisibility(View.VISIBLE);
                    edittext_total.setVisibility(View.VISIBLE);
                } else {
                    perTd_label.setVisibility(View.GONE);
                    totalcount_label.setVisibility(View.GONE);
                    edittext_perTD.setVisibility(View.GONE);
                    edittext_total.setVisibility(View.GONE);
                }
            }
            tournament_downlabel.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);
            fetchedTipees.setVisibility(View.VISIBLE);
            type_switch.setVisibility(View.VISIBLE);
            label_manual_tips.setVisibility(View.VISIBLE);
            label_dollar_sign.setVisibility(View.VISIBLE);
            label_per_sign.setVisibility(View.VISIBLE);
            editext_manual_tips.setVisibility(View.VISIBLE);

            editText_new_count.setVisibility(View.VISIBLE);
            new_count_textview.setVisibility(View.VISIBLE);
        }

        if (is_supervisor == 1 && (getting_tips == 1 || getting_tournament == 1)) {
            tipSeparator.setVisibility(View.VISIBLE);
            checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
            tournamentSeparator.setVisibility(View.VISIBLE);
            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            if (b != null) {
                if (addDay.getGetting_tips() == 1) {
                    getTournamentTips = true;
                }

            }
            if (getTournamentTips) {
                if (is_todayPayDay) {

                    perTd_label.setVisibility(View.VISIBLE);
                    totalcount_label.setVisibility(View.VISIBLE);
                    edittext_perTD.setVisibility(View.VISIBLE);
                    edittext_total.setVisibility(View.VISIBLE);
                } else {
                    perTd_label.setVisibility(View.GONE);
                    totalcount_label.setVisibility(View.GONE);
                    edittext_perTD.setVisibility(View.GONE);
                    edittext_total.setVisibility(View.GONE);
                }
            }
            tournament_downlabel.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);
            fetchedTipees.setVisibility(View.VISIBLE);
            type_switch.setVisibility(View.VISIBLE);
            label_manual_tips.setVisibility(View.VISIBLE);
            label_dollar_sign.setVisibility(View.VISIBLE);
            label_per_sign.setVisibility(View.VISIBLE);
            editext_manual_tips.setVisibility(View.VISIBLE);

            editText_new_count.setVisibility(View.VISIBLE);
            new_count_textview.setVisibility(View.VISIBLE);
        }



        /*if (getting_tournament == 0) {
            checkBoxEndofPayPeriod.setVisibility(View.GONE);
        } else {*/
        checkBoxEndofPayPeriod.setVisibility(View.VISIBLE);
        if (is_supervisor == 0 && getting_tournament == 0 && getting_tips == 1) {
            checkBoxEndofPayPeriod.setVisibility(View.GONE);
            editText_new_count.setVisibility(View.GONE);
            new_count_textview.setVisibility(View.GONE);
            tournament_downlabel.setVisibility(View.GONE);
            tournamentSeparator.setVisibility(View.GONE);
        }
        //}
        editText_startShift.setHint("Start Shift");
        edittext_clockIn.setVisibility(View.VISIBLE);
        editText_endShift.setVisibility(View.VISIBLE);
        edittext_clockOut.setVisibility(View.VISIBLE);
        textview_autoCalLabel.setVisibility(View.VISIBLE);
        label_to.setVisibility(View.VISIBLE);
        image_to.setVisibility(View.VISIBLE);
        holiday_pay.setVisibility(View.VISIBLE);

        if (selectedTipeesID.size() > 0) {
            fetchedTipees.setVisibility(View.VISIBLE);
            tipee_nodata.setVisibility(View.GONE);
        } else if (getting_tips == 1) {
            fetchedTipees.setVisibility(View.GONE);
            tipee_nodata.setVisibility(View.VISIBLE);
        }

        tipee_layout.setVisibility(View.VISIBLE);
        label_total_earnings.setText("Total Earnings: ");
        total_earnings.setVisibility(View.VISIBLE);
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

    String earns = "";

    public void calculateTotalEarnings(double wage_hourlyPerProfile, int total_hours, int mins, int days, double tip_out_total,
                                       double live_tip, double tounament_downs, String holiday_payoff_data, Bundle bundle) {
        double minutes_01 = 0;
        if (mins != 0) {
            minutes_01 = Double.parseDouble(String.valueOf(mins));
        }

        double tH = 0;
        double total_hr = Double.parseDouble(String.valueOf(total_hours));
        int total_hour = 0;


        live_tips = (TextView) findViewById(R.id.summery_tips);
        tournament_downs = (TextView) findViewById(R.id.summery_tds);
        tip_outs = (TextView) findViewById(R.id.summery_tip_out);
        hourly_wages = (TextView) findViewById(R.id.hour_wage);

        live_tips.setVisibility(View.GONE);
        tournament_downs.setVisibility(View.GONE);
        tip_outs.setVisibility(View.GONE);
        hourly_wages.setVisibility(View.GONE);

        if (mins > 60) {
            tH = total_hr + 1;
        } else if (mins > 0 && mins < 60) {
            tH = minutes_01 / 60;

        } else if (days <= 0 && total_hour <= 0 && mins > 0) {
            tH = minutes_01 / 60;
        }
        if (days > 0 && total_hours > 0) {
            tH = days * 24 * total_hours;
        } else if (days > 0 && total_hours < 0) {
            tH = days * 24;
        } else if (days <= 0) {
            tH = tH + total_hr;
        }

        double finalEarning = tH * wage_hourlyPerProfile; // wage hourly from profile

        if (holidayPay == 1) {
            if (holiday_payoff_data.equalsIgnoreCase("Time and a Half")) {
                double val = finalEarning / 2;
                if (holidayPay == 1) {
                    finalEarning = finalEarning + val;
                } else {
                    finalEarning = finalEarning - val;
                }


            } else if (holiday_payoff_data.equalsIgnoreCase("Double Pay")) {

                double val = finalEarning;

                if (holidayPay == 1) {
                    finalEarning = finalEarning + val;
                } else {
                    finalEarning = finalEarning - val;
                }
            }
        }
        calculated_wages_hourly = String.valueOf(finalEarning);

        double totalEarnings = finalEarning + live_tip + tounament_downs - tip_out_total;
        earns = String.valueOf(String.format("%.2f", totalEarnings));

        if (earns.contains("-")) {
            earns = earns.replace("-", "");
        }
        if (bundle != null) {
            addDay = (AddDay) b.getSerializable(Constants.AddDayProfile);
//            total_earnings.setText("$" + addDay.getTotal_earnings());
            total_earnings.setText("$" + earns);
        } else {
            total_earnings.setText("$" + earns);
        }

    }

    String addDayID;
    List<String> fetched = new ArrayList<>();
    Date d = null, d1 = null;
    Calendar startDateTime, endDateTime;
    long updated_start_values, updated_end_values;

    public void fillAllFields(AddDay addDay) {
        addDayID = addDay.getId();

        selected_profile_color = addDay.getProfile_color();

        selected_profile = addDay.getProfile();
        int pos = 0;
        if (profilesArrayList.size() > 0) {
            for (int i = 0; i < profilesArrayList.size(); i++) {
                if (profilesArrayList.get(i).getProfile_name().equalsIgnoreCase(selected_profile)) {
                    global_payperiod = profilesArrayList.get(i).getPay_period();
                    global_startday = profilesArrayList.get(i).getStartday();
                    pos = i;
                    break;
                }
            }
        }
        spinnerProfile.setSelection(pos);

        long start_format = 0;
        long end_format = 0;
        start_format = addDay.getCheck_in();
        end_format = addDay.getCheck_out();


        if (default_date_format == 2) {
            date_format = "MM/dd/yyyy";
        } else if (default_date_format == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }

        startDateTime = Calendar.getInstance();
        startDateTime.setTimeInMillis(start_format);

        endDateTime = Calendar.getInstance();
        endDateTime.setTimeInMillis(end_format);


        calStartDay.setTimeInMillis(start_format);
        calEndDay.setTimeInMillis(end_format);

        result = datesDifference(calStartDay.getTime(), calEndDay.getTime());

        if (addDay.getDay_off() == 0) {
            if (selected_timeformatIndex == 0) {
                // edittext_clockOut.setText(String.format("%02d:%02d", endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE)));
                edittext_clockIn.setText(String.format("%02d:%02d %s", startDateTime.get(Calendar.HOUR), startDateTime.get(Calendar.MINUTE), (startDateTime.get(Calendar.AM_PM) == 0) ? "AM" : "PM"));
            } else {
                edittext_clockIn.setText(String.format("%02d:%02d", startDateTime.get(Calendar.HOUR_OF_DAY), startDateTime.get(Calendar.MINUTE)));
            }

            if (selected_timeformatIndex == 0) {
                // edittext_clockOut.setText(String.format("%02d:%02d", endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE)));
                edittext_clockOut.setText(String.format("%02d:%02d %s", endDateTime.get(Calendar.HOUR), endDateTime.get(Calendar.MINUTE), (endDateTime.get(Calendar.AM_PM) == 0) ? "AM" : "PM"));
            } else {
                edittext_clockOut.setText(String.format("%02d:%02d", endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE)));
            }

        }


        edittext_total.setText(addDay.getTotal_tournament_downs());
        edittext_perTD.setText(addDay.getTournament_perday());


        d = new Date(start_format);
        d1 = new Date(end_format);
        updated_start_values = d.getTime();
        updated_end_values = d1.getTime();

        String start = new SimpleDateFormat(date_format).format(d);
        String end = new SimpleDateFormat(date_format).format(d1);


        editText_startShift.setText(start);
        startDateDb = addDay.getStart_shift();
        if (addDay.getDay_off() == 0) {

            editText_endShift.setText(end);
            texview_hours.setText(addDay.getCalculated_hours());

            if (!addDay.getTotal_tips().equalsIgnoreCase("")) {
                total_tipsInput = Double.parseDouble(addDay.getTotal_tips());
            }
            edit_total_tips.setText(addDay.getTotal_tips());
            if (!addDay.getManual_tips().equalsIgnoreCase("")) {
                result_tip_outpercentage = Double.parseDouble(addDay.getManual_tips());
            }

            text_tip_out_percent.setText(addDay.getTip_out_percentage());
            total_tipout.setText(addDay.getTip_out());
        /*    if (!addDay.getTounament_count().equalsIgnoreCase("")) {
                stable_count = Integer.parseInt(addDay.getTounament_count());
            }*/

            if (!addDay.getTounament_count().equalsIgnoreCase("")) {
                new_count = Integer.parseInt(addDay.getTounament_count());
            }
            if (!addDay.getProfile_wage_hourly().equalsIgnoreCase("")) {
                wage_hourly = Double.parseDouble(addDay.getProfile_wage_hourly());
            }
            editText_new_count.setText(addDay.getTounament_count());
            if (!addDay.getTounament_count().equalsIgnoreCase("")) {
                new_count = Integer.parseInt(addDay.getTounament_count());
            }

            //   edittext_count.setText(addDay.getTounament_count());
        /*    edittext_perTD.setText(addDay.getTournament_perday());
            edittext_total.setText(addDay.getTotal_tournament_downs());*/

            total_earnings.setText(addDay.getTotal_earnings());

            manually_added_tips = addDay.getManual_tips();
            switch_value = addDay.getDollar_checked();
            start_week = addDay.getStart_day_week();
            getting_tournament = addDay.getGettingg_tournamnts();
            getting_tips = addDay.getGetting_tips();
            earns = addDay.getTotal_earnings();
            calculated_wages_hourly = addDay.getWages_hourly();
            day_off = addDay.getDay_off();
            holidayPay = addDay.getIsHolidaypay();

            if (getting_tips == 0) {
                total_tipslabel.setVisibility(View.GONE);
                tipout_tipees_label.setVisibility(View.GONE);
                edit_total_tips.setVisibility(View.GONE);
                total_tipout.setVisibility(View.GONE);
                total_tipoutlabel.setVisibility(View.GONE);
                text_tip_out_percent.setVisibility(View.GONE);
                fetchedTipees.setVisibility(View.GONE);
                total_tipoutPer.setVisibility(View.GONE);
                tipee_layout.setVisibility(View.GONE);
            } else {
                tipee_layout.setVisibility(View.VISIBLE);
                if (!addDay.getTip_out_tipees().equalsIgnoreCase("")) {
                    fetched = new ArrayList<>(Arrays.asList(convertStringToArray(addDay.getTip_out_tipees())).get(0));
                }
                //  fetched = new ArrayList<>(Arrays.asList(convertStringToArray(String.valueOf(addDay.getTip_out_tipees())).get(0)));

                if (fetched != null && fetched.size() > 0) {
                    getAllTipees(selectedTipeesID, fetched);
                    text_tip_out_percent.setVisibility(View.VISIBLE);
                    total_tipoutPer.setVisibility(View.VISIBLE);
                    total_tipoutlabel.setVisibility(View.VISIBLE);
                    total_tipout.setVisibility(View.VISIBLE);
                    fetchedTipees.setVisibility(View.VISIBLE);
                    tipee_nodata.setVisibility(View.GONE);
                } else {
                    total_tipoutPer.setVisibility(View.GONE);
                    fetchedTipees.setVisibility(View.GONE);
                    tipee_nodata.setVisibility(View.VISIBLE);
                    total_tipout.setVisibility(View.GONE);
                    total_tipoutlabel.setVisibility(View.GONE);
                    tipee_nodata.setText("Tippee not found");
                    text_tip_out_percent.setVisibility(View.GONE);
                }

                total_tipslabel.setVisibility(View.VISIBLE);
                tipout_tipees_label.setVisibility(View.VISIBLE);
                total_tipoutPer.setVisibility(View.VISIBLE);
                edit_total_tips.setVisibility(View.VISIBLE);
                total_tipout.setVisibility(View.VISIBLE);
                text_tip_out_percent.setVisibility(View.VISIBLE);
                total_tipoutlabel.setVisibility(View.VISIBLE);


                if (addDay.getDollar_checked() == 1) {
                    type_switch.setChecked(true);
                }
                if (!addDay.getManual_tips().equalsIgnoreCase("")) {
                    editext_manual_tips.setText(addDay.getManual_tips());
                }

            }


            //   String start_day_frombundle = "", pay_period_frombundle = "";

            for (int i = 0; i < profilesArrayList.size(); i++) {
                if (profilesArrayList.get(i).getProfile_name().equalsIgnoreCase(addDay.getProfile())) {
                    updateView(profilesArrayList.get(i), b);

                    spinnerProfile.setSelection(i);
                }
            }
        }
        int getting_tournamenttips = addDay.getGettingg_tournamnts();

        if (getting_tournamenttips == 1) {
            getTournamentTips = true;

        }
        if (addDay.getIsEndPay() == 1) {
            checkBoxEndofPayPeriod.setChecked(true);
        }

        int isHoliday = addDay.getIsHolidaypay();
        if (isHoliday == 1) {
            holiday_pay.setChecked(true);
        }


        int day_off = addDay.getDay_off();
        if (day_off == 1) {
            checkBox_dayoff.setChecked(true);
            setDayOffView();
        } else {
            updateDayOffView();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonMethods.setTheme(getSupportActionBar(), AddDayActivity.this);

    }

    boolean is_todayPayDay = false;
    int day;
    // ArrayList<String> counts_list;
    ArrayList<CountList> counts_list;
    ArrayList<CountList> today_list;

    public void todayPayDay(int end_payperiod, String start_day, String pay_period) {

       /* if (pay_period != null && pay_period.equalsIgnoreCase("Daily ")) {
            end_payperiod = 1;
        }*/

        day = CommonMethods.getDay(start_day);
        String date_format = "";
        if (default_date_format == 2) {
            date_format = "MM/dd/yyyy";
        } else if (default_date_format == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }
        //new End of Pay period
        Calendar cal = Calendar.getInstance();
        int montth = calStartDay.get(Calendar.MONTH);
        int day1 = calStartDay.get(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DATE, calStartDay.get(Calendar.DATE));
        cal.set(Calendar.DAY_OF_MONTH, day1);
        cal.set(Calendar.MONTH, montth);

        cal.setTimeInMillis(calStartDay.getTimeInMillis());

        counts_list = new ArrayList<>();


        if (pay_period == null || pay_period.equalsIgnoreCase("")) {

        } else {
            if (pay_period.equalsIgnoreCase("Daily ")) {
                today_list = new ArrayList<>();
                //   Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();

                String current_date_str = new SimpleDateFormat("MM/dd/yyyy").format(date);

                Date current_date = cal.getTime();
                long current_date_long = current_date.getTime();

                today_list = new DatabaseOperations(AddDayActivity.this).dailyCounts(current_date_str, selected_profile);
                counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountTillToday(current_date_long, selected_profile);

                counts_list.addAll(today_list);
                counts_list = removeDuplicacy(counts_list);

//                is_todayPayDay = true;
                if (end_payperiod == 1) {
                    ifPayDay(true, counts_list);
                    end_payperiod = 0;
                } else {
                    ifPayDay(false, counts_list);
                }
            }
            if (pay_period.equalsIgnoreCase("Weekly")) {
                today_list = new ArrayList<>();

                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                ArrayList<String> string_dates = new ArrayList<>();
                ArrayList<Long> dates = new ArrayList<>();
                for (int i = 0; i < 7; i++) {

                    String str_date = dateFormat.format(cal.getTime().getTime());
                    Date date = cal.getTime();
                    long lon = date.getTime();

                    cal.add(Calendar.DATE, -1);


                    string_dates.add(str_date);


                    dates.add(lon);
                }
                long reset_next_week = 0;
                long reset_current = 0;
                if (dates.size() > 0) {

                    reset_current = dates.get(6);
                    reset_next_week = dates.get(0);
                }
                counts_list = new ArrayList<>();
                //  today_list = new DatabaseOperations(AddDayActivity.this).dailyCounts(string_dates.get(0), selected_profile);
                counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(reset_current, reset_next_week, selected_profile);

                //    counts_list.addAll(today_list);
                counts_list = removeDuplicacy(counts_list);

                if (end_payperiod == 1) {
                    ifPayDay(true, counts_list);
                    end_payperiod = 0;
                } else {
                    ifPayDay(false, counts_list);
                }


            }

            if (pay_period.equalsIgnoreCase("1st & 15th")) {
                counts_list = new ArrayList<>();
                today_list = new ArrayList<>();

                int month = start_calendar.get(Calendar.MONTH);

                int year = start_calendar.get(Calendar.YEAR);
                int days = CommonMethods.numDays(month, year);

                Calendar start_day_monthly = Calendar.getInstance();
                Calendar end_day_monthly = Calendar.getInstance();
                start_day_monthly.set(Calendar.MONTH, month);
                end_day_monthly.set(Calendar.MONTH, month);
                if (days == 28) {
                    int current_day = start_calendar.get(Calendar.DAY_OF_MONTH);
                    if (current_day <= 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 1);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 14);
                    } else if (current_day > 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 15);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 28);
                    }

                    Date d = start_day_monthly.getTime();
                    long start_l = d.getTime();

                    Date d2 = end_day_monthly.getTime();
                    long end_l = d2.getTime();

                    counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(start_l, end_l, selected_profile);


                    if (current_day == 14 || current_day == 28) {
                        is_todayPayDay = true;

                    } else {
                        is_todayPayDay = false;
                    }
                } else if (days == 29) {
                    int current_day = start_calendar.get(Calendar.DAY_OF_MONTH);

                    if (current_day <= 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 1);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 14);
                    } else if (current_day > 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 15);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 29);
                    }
                    Date d = start_day_monthly.getTime();
                    long start_l = d.getTime();

                    Date d2 = end_day_monthly.getTime();
                    long end_l = d2.getTime();

                    counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(start_l, end_l, selected_profile);


                    if (current_day == 14 || current_day == 29) {
                        is_todayPayDay = true;
                    } else {
                        is_todayPayDay = false;
                    }
                } else if (days == 30) {
                    int current_day = start_calendar.get(Calendar.DAY_OF_MONTH);

                    if (current_day <= 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 1);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 14);
                    } else if (current_day > 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 15);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 30);
                    }
                    Date d = start_day_monthly.getTime();
                    long start_l = d.getTime();

                    Date d2 = end_day_monthly.getTime();
                    long end_l = d2.getTime();

                    counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(start_l, end_l, selected_profile);


                    if (current_day == 14 || current_day == 30) {
                        is_todayPayDay = true;
                    } else {
                        is_todayPayDay = false;
                    }
                } else {
                    int current_day = start_calendar.get(Calendar.DAY_OF_MONTH);
                    if (current_day <= 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 1);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 14);
                    } else if (current_day > 14) {
                        start_day_monthly.set(Calendar.DAY_OF_MONTH, 15);
                        end_day_monthly.set(Calendar.DAY_OF_MONTH, 31);
                    }

                    Date d = start_day_monthly.getTime();
                    long start_l = d.getTime();

                    Date d2 = end_day_monthly.getTime();
                    long end_l = d2.getTime();

                    counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(start_l, end_l, selected_profile);
                    if (current_day == 14 || current_day == 31) {
                        is_todayPayDay = true;
                    } else {
                        is_todayPayDay = false;
                    }
                }
                if (end_payperiod == 1) {
                    ifPayDay(true, counts_list);
                    end_payperiod = 0;
                } else {
                    ifPayDay(false, counts_list);
                }
            }
            if (pay_period.equals("Every 2 Weeks ")) {

                Calendar cal1 = Calendar.getInstance();
                int montth1 = calStartDay.get(Calendar.MONTH);
                int day11 = calStartDay.get(Calendar.DAY_OF_MONTH);

                cal1.set(Calendar.DATE, calStartDay.get(Calendar.DATE));
                cal1.set(Calendar.DAY_OF_MONTH, day11);
                cal1.set(Calendar.MONTH, montth1);

                cal1.setTimeInMillis(calStartDay.getTimeInMillis());
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                ArrayList<String> string_dates = new ArrayList<>();
                ArrayList<Long> dates = new ArrayList<>();
                for (int i = 0; i < 14; i++) {

                    String str_date = dateFormat.format(cal1.getTime().getTime());

                    Date date = cal1.getTime();
                    long lon = date.getTime();

                    cal1.add(Calendar.DATE, -1);

                    string_dates.add(str_date);


                    dates.add(lon);
                }
                long reset_next_week = 0;
                long reset_current = 0;
                if (dates.size() > 0) {

                    reset_current = dates.get(13);
                    reset_next_week = dates.get(0);
                }
                counts_list = new ArrayList<>();
                today_list = new ArrayList<>();
                //   today_list = new DatabaseOperations(AddDayActivity.this).dailyCounts(string_dates.get(0), selected_profile);
                counts_list = new DatabaseOperations(AddDayActivity.this).tournamentCountPerDay(reset_current, reset_next_week, selected_profile);

                //  counts_list.addAll(today_list);

                counts_list = removeDuplicacy(counts_list);
                if (end_payperiod == 1) {
                    ifPayDay(true, counts_list);
                    end_payperiod = 0;
                } else {
                    ifPayDay(false, counts_list);
                }
            }
        }

    }

    public void ifPayDay(boolean isPayday, ArrayList<CountList> counts_list) {

        if (getTournamentTips) {
            if (isPayday) {
                is_todayPayDay = true;
                totalcount_label.setVisibility(View.VISIBLE);
                edittext_count.setVisibility(View.VISIBLE);
                perTd_label.setVisibility(View.VISIBLE);
                cout_label.setVisibility(View.VISIBLE);
                edittext_perTD.setVisibility(View.VISIBLE);
                edittext_total.setVisibility(View.VISIBLE);

                if (counts_list.size() > 0) {

                    for (int i = 0; i < counts_list.size(); i++) {
                        String str = counts_list.get(i).getCount();
                        if (!str.equalsIgnoreCase("")) {
                            int count = Integer.parseInt(str);
                            total_counts_till_now = total_counts_till_now + count;

                        }
                    }
                    stable_count = total_counts_till_now;
                    edittext_count.setText(String.valueOf(total_counts_till_now));
                    total_counts_till_now = 0;
                } else {
                    stable_count = 0;
//                    edittext_count.setText("0");
                    edittext_count.setText(editText_new_count.getText().toString().trim());
                }

            } else if (!isPayday) {
                perTd_label.setVisibility(View.GONE);
                totalcount_label.setVisibility(View.GONE);
                edittext_perTD.setVisibility(View.GONE);
                edittext_total.setVisibility(View.GONE);

                cout_label.setVisibility(View.GONE);
                edittext_count.setVisibility(View.GONE);
                if (counts_list.size() > 0) {

                    for (int i = 0; i < counts_list.size(); i++) {
                        String str = counts_list.get(i).getCount();
                        if (!str.equalsIgnoreCase("")) {
                            int count = Integer.parseInt(str);
                            total_counts_till_now = total_counts_till_now + count;

                        }
                    }
                    stable_count = total_counts_till_now;
                    edittext_count.setText(String.valueOf(total_counts_till_now));
                    total_counts_till_now = 0;
                } else {
                    stable_count = 0;
                    //  edittext_count.setText("0");
                    edittext_count.setText(editText_new_count.getText().toString().trim());
                }
            }

            editText_new_count.setVisibility(View.VISIBLE);
            new_count_textview.setVisibility(View.VISIBLE);

        }
    }

    public void checkedTipeeCalculations(TipeeInfo selectedTipeesID) {
        selected_tipeesIDs.add(selectedTipeesID.getId());

        checked.add(selectedTipeesID.getId());

        String per = selectedTipeesID.getPercentage();
        dynamic_tip_out_percentage = Double.parseDouble(per);


        if (!per.equalsIgnoreCase("")) {
            double tipOut = 0;
            if (!per.equalsIgnoreCase("")) {
                tipeePercent = tipeePercent + Double.parseDouble(per);
                tipOut = (manual_percentage_data + tipeePercent) / 100 * total_tipsInput;

                text_tip_out_percent.setText(String.format("%.2f", tipeePercent + manual_percentage_data) + "%");
                total_tipout.setText(String.format("%.2f", tipOut));
            }

            String hr = String.valueOf(diffHours);
            if (hr.contains("-")) {
                hr = hr.replace("-", "");
            }
            String m = String.valueOf(diffMinutes);
            if (m.contains("-")) {
                m = m.replace("-", "");
            }
            String d = String.valueOf(diffDays);
            if (d.contains("-")) {
                d = d.replace("-", "");
            }
            calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                    Integer.parseInt(m),
                    Integer.parseInt(d), tipOut, total_tipsInput, total_tournamentdown, holiday_off_value, b);
        }
    }

    public ArrayList<CountList> removeDuplicacy(ArrayList<CountList> countLists) {
        ArrayList<CountList> count_list = new ArrayList();
        /*HashSet<CountList> hashSet = new HashSet<CountList>();
        hashSet.addAll(countLists);
        count_list.clear();
        count_list.addAll(hashSet);*/

        count_list = countLists;
        Collections.sort(count_list, new CustomComparator());

        return count_list;
    }


    public class CustomComparator implements Comparator<CountList> {
        @Override
        public int compare(CountList o1, CountList o2) {

            return o1.getId() == o2.getId() ? 1 : (o1.getId() != o2.getId() ? -1 : 0);
        }
    }
}


