package com.mytips;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mytips.Adapter.FetchedTipeeAdapter;
import com.mytips.Adapter.SpinnerAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Interface.TimeChangeListener;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDayActivity extends AppCompatActivity implements View.OnClickListener {

    String tip_outpercentage, tip_out_amount;
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
    int count = 0, count_perTd = 0;
    ImageButton close_icon;
    CheckBox checkBox_dayoff, holiday_pay;
    TextView save_details, textview_autoCalLabel, label_to, total_tipslabel, tipout_tipees_label, total_tipoutPer, total_tipout,
            tournament_downlabel, cout_label, perTd_label, totalcount_label, text_tip_out_percent, total_tipoutlabel,
            from_label, multiply_label, equal_label, tipee_nodata;
    ImageView image_to;
    ListView fetchedTipees;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);


        findViewByIds();

        profilesArrayList = new ArrayList<>();
        profile_names = new ArrayList<>();
        profilesArrayList = new DatabaseOperations(AddDayActivity.this).fetchAllProfile(AddDayActivity.this);

        SpinnerAdapter adapter = new SpinnerAdapter(AddDayActivity.this,
                profilesArrayList);
        spinnerProfile.setAdapter(adapter);

        spinnerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Profiles profile = profilesArrayList.get(position);
                updateView(profile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editText_startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(R.id.editText_start_shift);
            }
        });
        editText_endShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(R.id.editText_end_shift);
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
                    count_perTd = Integer.parseInt(str);
                }

                int total = calculate_total(String.valueOf(count), str);
                edittext_total.setText(String.valueOf(total));

            }
        });

        checkBox_dayoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                int result = 0;
                String str = s.toString();
                if (!str.equalsIgnoreCase("")) {

                    result = (percentage * Integer.parseInt(str)) / 100;

                    total_tipout.setText(String.valueOf(result));
                }
            }
        });
    }

    public void findViewByIds() {
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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText_clock_in:
                getTimePicker(R.id.editText_clock_in, new TimeChangeListener() {
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

                getTimePicker(R.id.editText_clock_out, new TimeChangeListener() {
                    @Override
                    public void OnTimeChange(String start_time, String end_time, Calendar start_date1, Calendar end_date1) {

                        if (start_time.equalsIgnoreCase("") || start_date.equalsIgnoreCase("")
                                || end_date.equalsIgnoreCase("")) {

                        } else {
                            Date date = start_date1.getTime();
                            Date date1 = end_date1.getTime();


                            result = datesDifference(date, date1);

                            texview_hours.setText(result);
                        }

                    }
//                    }
                });
                break;

            case R.id.close_icon:
                this.finish();
                break;

            case R.id.save_add_day:
                break;
        }
    }

    public String getTimePicker(final int viewId, final TimeChangeListener onTimeChange) {
        final Dialog timePicker = new Dialog(AddDayActivity.this);

        timePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflaterTime = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewTimePicker = inflaterTime.inflate(R.layout.time_picker, null);
        timePicker.setContentView(viewTimePicker);
        final TimePicker timePick = (TimePicker) viewTimePicker
                .findViewById(R.id.timePicker);
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
                    showTime(startHour, startMinute, edittext_clockIn);
                    //  edittext_clockIn.setText(time);
                } else if (viewId == R.id.editText_clock_out) {
                    endHour = timePick.getCurrentHour();
                    endMinute = timePick.getCurrentMinute();
                    showTime(endHour, endMinute, edittext_clockOut);
//                    edittext_clockOut.setText(time);
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

    public String getDatePicker(final int viewId) {

        // Infalating Dialog for Date Picker
        final Dialog datePicker = new Dialog(AddDayActivity.this);
        datePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDatePicker = inflater.inflate(R.layout.date_picker, null);
        datePicker.setContentView(viewDatePicker);

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
                date = new SimpleDateFormat("MMMM d, yyyy").format(new Date(
                        selectedDate));


                SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM d, yyyy");
                String currentDateString = sdfDate.format(new Date());
                Date currentDate = null;
                Date parsedStartDate = null;

                start_date = selectedDate;
                String date1 = new SimpleDateFormat("MM/dd/yyyy").format(new Date(selectedDate));
                end_date = date1;
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
                    pastDateSelected = true;
                } else {
                    pastDateSelected = false;
                }

                if (pastDateSelected) {
                    if (viewId == R.id.editText_start_shift) {
                        editText_startShift.setText("Set Start Date");
                    } else if (viewId == R.id.editText_end_shift) {
                        editText_endShift.setText("Set End Date");
                    }
                } else {
                    date = new SimpleDateFormat("MMMM d, yyyy")
                            .format(new Date(date));
                    if (viewId == R.id.editText_start_shift) {
                        start_date = selectedDate;
                        editText_startShift.setText(selectedDate);
                    } else if (viewId == R.id.editText_end_shift) {
                        date1 = new SimpleDateFormat("MM/dd/yyyy").format(new Date(date));
                        end_date = date1;
//                        end_date = date;
                        editText_endShift.setText(date);
                    }

                }

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
                    date = new SimpleDateFormat("MMMM d, yyyy")
                            .format(new Date(currentDate));
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
                    date = new SimpleDateFormat("MMMM d, yyyy")
                            .format(new Date(currentDate));

                    editText_endShift.setText(date);

                } else {

                    if (pastDateSelected) {
                        if (viewId == R.id.editText_start_shift) {
                            editText_startShift.setText("Set Start Date");
                        } else if (viewId == R.id.editText_end_shift) {
                            editText_endShift.setText("Set End Date");
                        }
                    } else {
                        try {
                            date = new SimpleDateFormat("MMMM d, yyyy")
                                    .format(new Date(selectedDate));
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                            // Toast.makeText(AddEventActivity.this,
                            // "Select start date once again!",
                            // Toast.LENGTH_SHORT).show();
                        }
                        if (viewId == R.id.editText_start_shift)
                            editText_startShift.setText(date);
                        else if (viewId == R.id.editText_end_shift)
                            editText_endShift.setText(date);
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

    public String datesDifference(Date startDate, Date endDate) {
        String result = "";
        long diff = startDate.getTime() - endDate.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
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

    public int calculate_total(String counts, String per_TDs) {
        int total = 0;
        if (!counts.equalsIgnoreCase("") && !per_TDs.equalsIgnoreCase("")) {
            int count_1 = Integer.parseInt(counts);
            int per_tds = Integer.parseInt(per_TDs);
            total = count_1 * per_tds;
        }

        return total;
    }

    public void save_add_day() {
        String total_tips = edit_total_tips.getText().toString().trim();


    }

    List<String> selectedTipeesID;

    public void updateView(Profiles profile) {
        int percentage = 0;
        selectedTipeesID = new ArrayList<>();
        if (profile.getGet_tournamenttip() == 1) {
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

        if (profile.getGet_tips() == 1) {
            total_tipslabel.setVisibility(View.GONE);
            tipout_tipees_label.setVisibility(View.GONE);
            total_tipoutPer.setVisibility(View.GONE);
            edit_total_tips.setVisibility(View.GONE);
            total_tipout.setVisibility(View.GONE);
            total_tipoutlabel.setVisibility(View.GONE);
            text_tip_out_percent.setVisibility(View.GONE);
        } else {
            total_tipslabel.setVisibility(View.VISIBLE);
            tipout_tipees_label.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            edit_total_tips.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);
        }
        selectedTipeesID = convertStringToArray(String.valueOf(profile.getTipees_name()));
        if (selectedTipeesID.size() > 0) {
            percentage = getAllTipees(selectedTipeesID);
            text_tip_out_percent.setVisibility(View.VISIBLE);
            total_tipoutPer.setVisibility(View.VISIBLE);
            total_tipoutlabel.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.VISIBLE);
            text_tip_out_percent.setText(String.valueOf(percentage) + "%");
        } else {
            total_tipoutPer.setVisibility(View.GONE);
            fetchedTipees.setVisibility(View.GONE);
            tipee_nodata.setVisibility(View.VISIBLE);
            total_tipout.setVisibility(View.GONE);
            total_tipoutlabel.setVisibility(View.GONE);
            tipee_nodata.setText("Tipees not found");
            text_tip_out_percent.setVisibility(View.GONE);

        }
    }

    ArrayList<TipeeInfo> tipeeInfos;
    FetchedTipeeAdapter adapter;
    ArrayList<TipeeInfo> info;
    TipeeInfo tipeeInfo;
    int percentage = 0;

    public int getAllTipees(List<String> selectedTipeesID) {

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
        percentage = getTipOutPercentage(info);

        if (tipeeInfos != null) {
            adapter = new FetchedTipeeAdapter(AddDayActivity.this, selectedTipeesID, info, true);
            fetchedTipees.setAdapter(adapter);
            fetchedTipees.setDivider(null);
        }
        return percentage;
    }

    public void setDayOffView() {

        from_label.setText("Select Day");
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
    }


    public int getTipOutPercentage(ArrayList<TipeeInfo> infos) {
        int total_percentage = 0;

        for (int i = 0; i < infos.size(); i++) {
            String tip_per = infos.get(i).getPercentage();
            if (!tip_per.equalsIgnoreCase("")) {
                int tip = Integer.parseInt(tip_per);
                total_percentage = total_percentage + tip;
            }
        }
        return total_percentage;
    }
}


