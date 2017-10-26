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
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Adapter.SpinnerAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Interface.TimeChangeListener;
import com.mytips.Interface.TipeeChecked;
import com.mytips.Model.AddDay;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.Preferences.Preferences;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddDayActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<Profiles> profilesArrayList;
    Spinner spinnerProfile;
    int startDay, startMonth, startYear, endDay, endMonth, endYear, startHour,
            startMinute, endHour, endMinute;
    String time, date = "", selectedDate = null;
    ArrayList<String> profile_names;
    EditText editText_startShift, editText_endShift, edittext_clockIn, edittext_clockOut, edittext_count, edittext_perTD, edittext_total, edit_total_tips;
    boolean pastDateSelected;
    Calendar current_date;
    Date current_time;
    String start_date = "", end_date = "", start_time = "", end_time = "";
    private String format = "";
    String result = "";
    TextView texview_hours;
    Calendar start_dateCalendar, end_dateCalendar;
    int count = 0;
    double count_perTd = 0;
    ImageButton close_icon;
    CheckBox checkBox_dayoff, holiday_pay;
    TextView save_details, textview_autoCalLabel, label_to, total_tipslabel, tipout_tipees_label, total_tipoutPer, total_tipout,
            tournament_downlabel, cout_label, perTd_label, totalcount_label, text_tip_out_percent, total_tipoutlabel,
            from_label, multiply_label, equal_label, tipee_nodata;
    View tipSeparator, tournamentSeparator;
    ImageView image_to, day_off_timer;
    ListView fetchedTipees;
    double dynamic_tip_out_percentage = 0;
    RelativeLayout tipee_layout;
    double total_tipsInput = 0;
    String selected_profile = "";
    int holidayPay = 0, day_off = 0;
    List<String> selected_tipeesIDs;
    TextView total_earnings, live_tips, tournament_downs, tip_outs, hourly_wages;
    double wage_hourly = 0;
    double result_tip_outpercentage = 0;
    double total_tournamentdown = 0;
    SpinnerAdapter spinnerAdapter;
    int getting_tips = 0, getting_tournament = 0;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        CommonMethods.setTheme(getSupportActionBar(), AddDayActivity.this);

        findViewByIds();

        selected_timeformatIndex = sharedPreferences.getInt("selected_time", 1);
        default_date_format = sharedPreferences.getInt("selected_date", 2);

        profilesArrayList = new ArrayList<>();
        profile_names = new ArrayList<>();
        profilesArrayList = new DatabaseOperations(AddDayActivity.this).fetchAllProfile(AddDayActivity.this);

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
                String str = s.toString();
                if (!str.equalsIgnoreCase("")) {
                    count = Integer.parseInt(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!str.equalsIgnoreCase("")) {
                    count = Integer.parseInt(str);
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

                calculateTotalEarnings(wage_hourly, Integer.parseInt(hr),
                        Integer.parseInt(m),
                        Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown
                        , holiday_off_value);
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

            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = s.toString();
                if (!str.equalsIgnoreCase("")) {
                    total_tipsInput = Double.parseDouble(str);
                    if (percentage > 0) {
                        result_tip_outpercentage = (percentage * Double.parseDouble(str)) / 100;

                        // total_tipout.setText(String.valueOf(result));
                        total_tipout.setText(String.format("%.2f", result_tip_outpercentage));
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
                        Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value);
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
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value);

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
                            Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value);
                }

            }
        });
        Intent i = getIntent();
        b = i.getExtras();
        if (b != null) {
            addDay = (AddDay) b.getSerializable(Constants.AddDayProfile);
            fillAllFields(addDay);
        }


        spinnerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Profiles profile = profilesArrayList.get(position);
                holiday_off_value = profile.getHoliday_pay();
                start_week = profile.getStartday();
                String wage = profile.getHourly_pay();
                if (!wage.equalsIgnoreCase("")) {
//                    wage_hourly = Integer.parseInt(wage);
                    wage_hourly = Double.parseDouble(wage);
                }
                getting_tips = profile.getGet_tips();
                getting_tournament = profile.getGet_tournamenttip();

                selected_profile = profile.getProfile_name();
                updateView(profile, b);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void findViewByIds() {

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.day_toolbar);
        this.setSupportActionBar(toolbar);

        fetchedTipees = (ListView) findViewById(R.id.list_view_tipees);
        equal_label = (TextView) findViewById(R.id.textView25);
        multiply_label = (TextView) findViewById(R.id.textView24);
        text_tip_out_percent = (TextView) findViewById(R.id.textView_tip_out_percent);
        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
        editText_startShift = (EditText) findViewById(R.id.editText_start_shift);
        editText_endShift = (EditText) findViewById(R.id.editText_end_shift);
        edittext_clockIn = (EditText) findViewById(R.id.editText_clock_in);
        edittext_clockOut = (EditText) findViewById(R.id.editText_clock_out);
        texview_hours = (TextView) findViewById(R.id.textView_calculated_hours);
        start_dateCalendar = Calendar.getInstance();
        end_dateCalendar = Calendar.getInstance();
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

//                            start_shift_long = date.getTime();
//                            end_shift_long = date1.getTime();

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
                                Integer.parseInt(d), result_tip_outpercentage, total_tipsInput, total_tournamentdown, holiday_off_value);

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

    public String getTimePicker(int time_format_index, final int viewId, final TimeChangeListener onTimeChange) {
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

                    int hr = startHour % 12;
                    edittext_clockIn.setText(String.format("%02d:%02d %s", hr == 0 ? 12 : hr,
                            startMinute, startHour < 12 ? "am" : "pm"));

                } else if (viewId == R.id.editText_clock_out) {
                    endHour = timePick.getCurrentHour();
                    endMinute = timePick.getCurrentMinute();
                    int hr = endHour % 12;
                    edittext_clockOut.setText(String.format("%02d:%02d %s", hr == 0 ? 12 : hr,
                            endMinute, endHour < 12 ? "am" : "pm"));

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
        Button selectDate = (Button) viewDatePicker
                .findViewById(R.id.dateSelect);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                if (viewId == R.id.editText_start_shift) {
                    startDay = dayOfMonth;
                    startMonth = month;
                    startYear = year;


                    start_dateCalendar.set(year, month, dayOfMonth);
                } else if (viewId == R.id.editText_end_shift) {
                    endDay = dayOfMonth;
                    endMonth = month;
                    endYear = year;

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

                if (viewId == R.id.editText_start_shift && startDay == 0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    startYear = Integer.parseInt(d.substring(0, 4));
                    startMonth = Integer.parseInt(d.substring(4, 6));
                    startDay = Integer.parseInt(d.substring(6));
                    System.out.println(sdf.format(new Date()) + startYear
                            + startMonth + startDay);
                    String currentDate = String.valueOf(startMonth) + "/"
                            + String.valueOf(startDay) + "/"
                            + String.valueOf(startYear);
                    Date dates = new Date(currentDate);
                    start_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);
                    editText_startShift.setText(date);

                } else if (viewId == R.id.editText_end_shift && endDay == 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    endYear = Integer.parseInt(d.substring(0, 4));
                    endMonth = Integer.parseInt(d.substring(4, 6));
                    endDay = Integer.parseInt(d.substring(6));
                    System.out.println(sdf.format(new Date()) + endYear
                            + endMonth + endDay);
                    String currentDate = String.valueOf(endMonth) + "/"
                            + String.valueOf(endDay) + "/"
                            + String.valueOf(endYear);
                    Date dates = new Date(currentDate);
                    end_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);

                    editText_endShift.setText(date);

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
                            try {
                                Date selectedDate1 = new Date(selectedDate);
                                start_shift_long = selectedDate1.getTime();
                                date = new SimpleDateFormat(date_format)
                                        .format(selectedDate1);
                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            editText_startShift.setText(date);
                        }
                        if (viewId == R.id.editText_end_shift) {
                            try {
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
        if (hours.contains("-")) {
            hours = hours.replace("-", " ");
        }
        String min = String.valueOf(diffMinutes);
        if (min.contains("-")) {
            min = min.replace("-", " ");

        }
        String sec = String.valueOf(diffSeconds);
        if (sec.contains("-")) {
            sec = sec.replace("-", " ");
        }
        if (diffDays != 0) {
            String day = String.valueOf(diffDays);
            if (day.contains("-")) {
                day = day.replace("-", " ");
            }
            result = day + "days," + hours + "h" + min + "m" + sec + "s";
        } else {
            result = hours + "h" + min + "m" + sec + "s";
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
            int count_1 = Integer.parseInt(counts);
            double per_tds = Double.parseDouble(per_TDs);
            total = count_1 * per_tds;
        }

        return total;
    }

    public void save_add_day() {
        String joinedString = "";
        if (!selected_profile.equals("") && !editText_startShift.getText().toString().trim().equals("") && ((day_off == 0 && !texview_hours.getText().toString().trim().equals("")) || day_off == 1)) {

            if (b == null) {
                if (selected_tipeesIDs == null || selected_tipeesIDs.size() == 0) {

                } else {
                    joinedString = convertArrayToString(selected_tipeesIDs);
                }

                try {
                    new DatabaseOperations(AddDayActivity.this).insertAddDayInfo(selected_profile, editText_startShift.getText().toString().trim(),
                            edittext_clockIn.getText().toString().trim(), editText_endShift.getText().toString().trim(), edittext_clockOut.getText().toString().trim(),
                            texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString, text_tip_out_percent.getText().toString().trim(),
                            total_tipout.getText().toString().trim(), edittext_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                            day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, start_shift_long, end_shift_long);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if (selected_tipeesIDs == null || selected_tipeesIDs.size() == 0) {

                } else {
                    joinedString = convertArrayToString(selected_tipeesIDs);
                }
                try {
                    new DatabaseOperations(AddDayActivity.this).updateAddDayInfo(addDayID, selected_profile, editText_startShift.getText().toString().trim(),
                            edittext_clockIn.getText().toString().trim(), editText_endShift.getText().toString().trim(), edittext_clockOut.getText().toString().trim(),
                            texview_hours.getText().toString().trim(), holidayPay, String.valueOf(total_tipsInput), joinedString, text_tip_out_percent.getText().toString().trim(),
                            total_tipout.getText().toString().trim(), edittext_count.getText().toString().trim(), edittext_perTD.getText().toString().trim(), edittext_total.getText().toString().trim(),
                            day_off, calculated_wages_hourly, earns, getting_tips, getting_tournament, start_week, start_shift_long, end_shift_long);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            startActivity(new Intent(AddDayActivity.this, LandingActivity.class));
            this.finish();
        } else {
            Snackbar.make(spinnerProfile, "Need more details to save the day!", Snackbar.LENGTH_LONG).show();
        }
    }

    List<String> selectedTipeesID;

    public void updateView(Profiles profile, Bundle b) {
        selectedTipeesID = new ArrayList<>();
        if (profile.getGet_tournamenttip() == 0) {
            tournament_downlabel.setVisibility(View.GONE);
            cout_label.setVisibility(View.GONE);
            perTd_label.setVisibility(View.GONE);
            totalcount_label.setVisibility(View.GONE);
            edittext_count.setVisibility(View.GONE);
            edittext_perTD.setVisibility(View.GONE);
            multiply_label.setVisibility(View.GONE);
            edittext_total.setVisibility(View.GONE);
            equal_label.setVisibility(View.GONE);
            tournamentSeparator.setVisibility(View.GONE);
        } else {
            tournament_downlabel.setVisibility(View.VISIBLE);
            cout_label.setVisibility(View.VISIBLE);
            perTd_label.setVisibility(View.VISIBLE);
            totalcount_label.setVisibility(View.VISIBLE);
            edittext_count.setVisibility(View.VISIBLE);
            edittext_perTD.setVisibility(View.VISIBLE);
            edittext_total.setVisibility(View.VISIBLE);
            equal_label.setVisibility(View.VISIBLE);
            tournamentSeparator.setVisibility(View.VISIBLE);
            multiply_label.setVisibility(View.VISIBLE);
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

        } else {
            tipSeparator.setVisibility(View.VISIBLE);
            tipee_layout.setVisibility(View.VISIBLE);
            selectedTipeesID = convertStringToArray(String.valueOf(profile.getTipees_name()));


            if (selectedTipeesID.size() > 0) {
                getAllTipees(selectedTipeesID);
                text_tip_out_percent.setVisibility(View.VISIBLE);
                total_tipoutPer.setVisibility(View.VISIBLE);
                total_tipoutlabel.setVisibility(View.VISIBLE);
                total_tipout.setVisibility(View.VISIBLE);
                tipee_nodata.setVisibility(View.GONE);
                fetchedTipees.setVisibility(View.VISIBLE);
            } else {
                total_tipoutPer.setVisibility(View.GONE);
                fetchedTipees.setVisibility(View.GONE);
                tipee_nodata.setVisibility(View.VISIBLE);
                total_tipout.setVisibility(View.GONE);
                total_tipoutlabel.setVisibility(View.GONE);
                tipee_nodata.setText("Tipees not found");
                text_tip_out_percent.setVisibility(View.GONE);
            }

            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);

        }

    }

    ArrayList<TipeeInfo> tipeeInfos;
    FetchedTipeeAdapter adapter;
    ArrayList<TipeeInfo> info;
    TipeeInfo tipeeInfo;
    double percentage = 0;
    double total_tipPercentage = 0;

    public void getAllTipees(List<String> selectedTipeesID) {

        if (selectedTipeesID == null)
            selectedTipeesID = new ArrayList<>();
        tipeeInfos = new ArrayList<>();
        info = new ArrayList<>();
        tipeeInfos = new DatabaseOperations(AddDayActivity.this).fetchTipeeList(AddDayActivity.this);


        for (int i = 0; i < tipeeInfos.size(); i++) {
            for (int j = 0; j < selectedTipeesID.size(); j++) {
                if (selectedTipeesID.get(j).equalsIgnoreCase(tipeeInfos.get(i).getId())) {
                    tipeeInfo = new TipeeInfo();
                    tipeeInfo = tipeeInfos.get(i);
                    info.add(tipeeInfo);
                }
            }
        }

        if (tipeeInfos != null) {
            selected_tipeesIDs = new ArrayList<>();
            final List<String> finalSelectedTipeesID = selectedTipeesID;
            adapter = new FetchedTipeeAdapter(AddDayActivity.this, selectedTipeesID, info, true, new TipeeChecked() {
                @Override
                public void OnTipeeChange(boolean isChecked, TipeeInfo tipeeInfo) {
                    if (isChecked) {
                        selected_tipeesIDs.add(tipeeInfo.getId());
                        String per = tipeeInfo.getPercentage();
                        if (!per.equalsIgnoreCase("")) {
                            dynamic_tip_out_percentage = Double.parseDouble(per);
                            total_tipPercentage = total_tipPercentage + dynamic_tip_out_percentage;
                            percentage = total_tipPercentage;
                            text_tip_out_percent.setText(String.format("%.2f", total_tipPercentage) + "%");
                            percentage = total_tipPercentage;
                            double result1 = (percentage * total_tipsInput) / 100;

                            total_tipout.setText(String.format("%.2f", result1));

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
                                    Integer.parseInt(d), result1, total_tipsInput, total_tournamentdown
                                    , holiday_off_value);
                        }
                    } else {
                        String per = tipeeInfo.getPercentage();

//                        if (finalSelectedTipeesID.contains(tipeeInfo.getId())) {
//                            int index = finalSelectedTipeesID.indexOf(tipeeInfo.getId());
                        //  selected_tipeesIDs.remove(index);
//                        }

                        if (!per.equalsIgnoreCase("")) {
                            dynamic_tip_out_percentage = Double.parseDouble(per);
                            total_tipPercentage = total_tipPercentage - dynamic_tip_out_percentage;
                            percentage = total_tipPercentage;
                            if (total_tipPercentage < 0) {
                                text_tip_out_percent.setText(String.valueOf(0) + "%");
                            } else {
                                text_tip_out_percent.setText(String.format("%.2f", total_tipPercentage) + "%");
                            }
                            double result1 = (percentage * total_tipsInput) / 100;

                            total_tipout.setText(String.format("%.2f", result1));
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
                                    Integer.parseInt(d), result1, total_tipsInput, total_tournamentdown
                                    , holiday_off_value);
                        }
                    }
                }
            });
            fetchedTipees.setAdapter(adapter);
            fetchedTipees.setDivider(null);
        }
    }

    public void setDayOffView() {

        from_label.setText("Select Day");

        day_off_timer.setVisibility(View.VISIBLE);

        tipee_nodata.setVisibility(View.GONE);
        tipSeparator.setVisibility(View.GONE);
        tournamentSeparator.setVisibility(View.GONE);

        editText_startShift.setHint("Select Day");
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

        tournament_downlabel.setVisibility(View.GONE);
        cout_label.setVisibility(View.GONE);
        perTd_label.setVisibility(View.GONE);
        totalcount_label.setVisibility(View.GONE);

        edittext_count.setVisibility(View.GONE);
        edittext_perTD.setVisibility(View.GONE);
        edittext_total.setVisibility(View.GONE);
        text_tip_out_percent.setVisibility(View.GONE);
        total_tipoutlabel.setVisibility(View.GONE);
        multiply_label.setVisibility(View.GONE);
        equal_label.setVisibility(View.GONE);
        fetchedTipees.setVisibility(View.GONE);
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

        if (tipee_nodata.getText().toString().contains("Tipees not found"))
            tipee_nodata.setVisibility(View.VISIBLE);

        tipSeparator.setVisibility(View.VISIBLE);
        tournamentSeparator.setVisibility(View.VISIBLE);
        editText_startShift.setHint("Start Shift");
        edittext_clockIn.setVisibility(View.VISIBLE);
        editText_endShift.setVisibility(View.VISIBLE);
        edittext_clockOut.setVisibility(View.VISIBLE);
        textview_autoCalLabel.setVisibility(View.VISIBLE);
        label_to.setVisibility(View.VISIBLE);
        image_to.setVisibility(View.VISIBLE);
        holiday_pay.setVisibility(View.VISIBLE);
        total_tipslabel.setVisibility(View.VISIBLE);
        tipout_tipees_label.setVisibility(View.VISIBLE);
        total_tipoutPer.setVisibility(View.VISIBLE);
        edit_total_tips.setVisibility(View.VISIBLE);
        total_tipout.setVisibility(View.VISIBLE);
        multiply_label.setVisibility(View.VISIBLE);
        tournament_downlabel.setVisibility(View.VISIBLE);
        cout_label.setVisibility(View.VISIBLE);
        perTd_label.setVisibility(View.VISIBLE);
        totalcount_label.setVisibility(View.VISIBLE);
        equal_label.setVisibility(View.VISIBLE);
        edittext_count.setVisibility(View.VISIBLE);
        edittext_perTD.setVisibility(View.VISIBLE);
        edittext_total.setVisibility(View.VISIBLE);
        text_tip_out_percent.setVisibility(View.VISIBLE);
        total_tipoutlabel.setVisibility(View.VISIBLE);
        fetchedTipees.setVisibility(View.VISIBLE);
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

    public void calculateTotalEarnings(double earnings, int total_hours, int mins, int days, double tip_out_total,
                                       double live_tip, double tounament_downs, String holiday_payoff_data) {

        double tH = Float.valueOf(total_hours);
        int total_hour = 0;
        int earning = 0;
        total_earnings = (TextView) findViewById(R.id.total_earnings);

        live_tips = (TextView) findViewById(R.id.summery_tips);
        tournament_downs = (TextView) findViewById(R.id.summery_tds);
        tip_outs = (TextView) findViewById(R.id.summery_tip_out);
        hourly_wages = (TextView) findViewById(R.id.hour_wage);

        live_tips.setVisibility(View.GONE);
        tournament_downs.setVisibility(View.GONE);
        tip_outs.setVisibility(View.GONE);
        hourly_wages.setVisibility(View.GONE);

        if (mins > 60) {
            tH = total_hours + 1;
        } else if (mins > 0 && mins < 60) {
            tH = mins / 60;

        } else if (days <= 0 && total_hour <= 0 && mins > 0) {
            tH = mins / 60;
        }
        if (days > 0 && total_hours > 0) {
            tH = days * 24 * total_hours;
        } else if (days > 0 && total_hours < 0) {
            tH = days * 24;
        } else if (days <= 0) {
            tH = total_hours;
        }

//        earning = tH * earnings;
        double finalEarning = tH * earnings;
        calculated_wages_hourly = String.valueOf(finalEarning);
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


        double totalEarnings = finalEarning + live_tip + tounament_downs - tip_out_total;
        earns = String.valueOf(totalEarnings);

        if (earns.contains("-")) {
            earns = earns.replace("-", "");
        }
        total_earnings.setText("$" + earns);
    }

    String addDayID;
    List<String> fetched = new ArrayList<>();

    public void fillAllFields(AddDay addDay) {
        addDayID = addDay.getId();

        long start_forma = 0;
        long end_format = 0;
        start_forma = addDay.getStart_long();
        end_format = addDay.getEnd_long();


        if (default_date_format == 2) {
            date_format = "MM/dd/yyyy";
        } else if (default_date_format == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }
        Date d = null, d1 = null;
        d = new Date(start_forma);
        d1 = new Date(end_format);

        String start = new SimpleDateFormat(date_format).format(d);
        String end = new SimpleDateFormat(date_format).format(d1);


        editText_startShift.setText(start);
        editText_endShift.setText(end);
        edittext_clockIn.setText(addDay.getCheck_in());
        edittext_clockOut.setText(addDay.getCheck_out());
        texview_hours.setText(addDay.getCalculated_hours());

        int isHoliday = addDay.getIsHolidaypay();
        if (isHoliday == 1) {
            holiday_pay.setChecked(true);


        }

        edit_total_tips.setText(addDay.getTotal_tips());
        text_tip_out_percent.setText(addDay.getTip_out_percentage());
        total_tipout.setText(addDay.getTip_out());
        edittext_count.setText(addDay.getTounament_count());
        edittext_perTD.setText(addDay.getTournament_perday());
        edittext_total.setText(addDay.getTotal_tournament_downs());

        total_earnings.setText(addDay.getTotal_earnings());

        int day_off = addDay.getDay_off();
        if (day_off == 1) {
            setDayOffView();
        } else {
            updateDayOffView();
        }

        int getting_tips = addDay.getGetting_tips();


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
            fetched = convertStringToArray(String.valueOf(addDay.getTip_out_tipees()));

            if (fetched.size() > 0) {
                getAllTipees(fetched);
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
                tipee_nodata.setText("Tipees not found");
                text_tip_out_percent.setVisibility(View.GONE);
            }

            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);
        }


        int getting_tournamenttips = addDay.getGettingg_tournamnts();

        if (getting_tournamenttips == 0) {
            tournament_downlabel.setVisibility(View.GONE);
            cout_label.setVisibility(View.GONE);
            perTd_label.setVisibility(View.GONE);
            totalcount_label.setVisibility(View.GONE);
            edittext_count.setVisibility(View.GONE);
            edittext_perTD.setVisibility(View.GONE);
            multiply_label.setVisibility(View.GONE);
            edittext_total.setVisibility(View.GONE);
            equal_label.setVisibility(View.GONE);
        } else {
            tournament_downlabel.setVisibility(View.VISIBLE);
            cout_label.setVisibility(View.VISIBLE);
            perTd_label.setVisibility(View.VISIBLE);
            totalcount_label.setVisibility(View.VISIBLE);
            edittext_count.setVisibility(View.VISIBLE);
            edittext_perTD.setVisibility(View.VISIBLE);
            edittext_total.setVisibility(View.VISIBLE);
            equal_label.setVisibility(View.VISIBLE);
            multiply_label.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < profilesArrayList.size(); i++) {
            if (profilesArrayList.get(i).getProfile_name().equalsIgnoreCase(addDay.getProfile())) {
                updateView(profilesArrayList.get(i), b);
                spinnerProfile.setSelection(i);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonMethods.setTheme(getSupportActionBar(), AddDayActivity.this);

    }
}


