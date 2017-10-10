package com.mytips;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mytips.Database.DatabaseOperations;
import com.mytips.Interface.TimeChangeListener;
import com.mytips.Model.Profiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddDayActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Profiles> profilesArrayList;
    Spinner spinnerProfile;
    int startDay, startMonth, startYear, endDay, endMonth, endYear, startHour,
            startMinute, endHour, endMinute, endSec;
    String time, date = "", selectedDate = null;
    ArrayList<String> profile_names;
    EditText editText_startShift, editText_endShift, edittext_clockIn, edittext_clockOut;
    boolean pastDateSelected;
    Calendar current_date;
    Date current_time;
    String start_date = "", end_date = "", start_time = "", end_time = "";

    String result = "";
    TextView texview_hours;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);

        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
        editText_startShift = (EditText) findViewById(R.id.editText_start_shift);
        editText_endShift = (EditText) findViewById(R.id.editText_end_shift);
        edittext_clockIn = (EditText) findViewById(R.id.editText_clock_in);
        edittext_clockOut = (EditText) findViewById(R.id.editText_clock_out);
        texview_hours = (TextView) findViewById(R.id.textView_calculated_hours);

        current_date = Calendar.getInstance();
        current_time = current_date.getTime();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        profilesArrayList = new ArrayList<>();
        profile_names = new ArrayList<>();
        profilesArrayList = new DatabaseOperations(AddDayActivity.this).fetchAllProfile(AddDayActivity.this);

        for (int i = 0; i < profilesArrayList.size(); i++) {
            Profiles p = profilesArrayList.get(i);
            String name = p.getProfile_name();
            profile_names.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDayActivity.this,
                R.layout.spinner_text_view, R.id.text_spinner, profile_names);
        spinnerProfile.setAdapter(adapter);

        editText_startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new DatePickerDialog(AddDayActivity.this, dateSetListener,
//                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                getDatePicker(R.id.editText_start_shift);
            }
        });
        editText_endShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new DatePickerDialog(AddDayActivity.this, dateSetListener1,
//                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                getDatePicker(R.id.editText_end_shift);
            }
        });

        edittext_clockIn.setOnClickListener(this);
        edittext_clockOut.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText_clock_in:
                getTimePicker(R.id.editText_clock_in, new TimeChangeListener() {
                    @Override
                    public void OnTimeChange(String start_time, String end_time, String start_date, String end_date) {

                        if (end_time.equalsIgnoreCase("") || start_date.equalsIgnoreCase("")
                                || end_date.equalsIgnoreCase("")) {
                            Toast.makeText(AddDayActivity.this, "Please fill all", Toast.LENGTH_SHORT).show();
                            return;
                        } else {

                        }
                    }
                });
                break;
            case R.id.editText_clock_out:

                getTimePicker(R.id.editText_clock_out, new TimeChangeListener() {
                    @Override
                    public void OnTimeChange(String start_time, String end_time, String start_date, String end_date) {

                        if (start_date.equalsIgnoreCase("") || start_time.equalsIgnoreCase("") || end_date.equalsIgnoreCase("")) {
                            Toast.makeText(AddDayActivity.this, "Please fill", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            String dateStart = start_date + " " + start_time + ":00";
                            String dateStop = end_date + " " + end_time + ":00";
                            SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy'T'HH:mm:ss.SSS'Z'");

                            Date d1 = null;
                            Date d2 = null;
                            try {
                                d1 = format.parse(dateStart);
                                d2 = format.parse(dateStop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            result = datesDifference(d1, d2);
                            texview_hours.setText(result);
                        }
                    }
                });
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
                time = timePick.getCurrentHour() + ":" + currentMinute;

                System.out.println("SETTED TIME>> " + time);

                if (viewId == R.id.editText_clock_in) {
                    startHour = timePick.getCurrentHour();
                    startMinute = timePick.getCurrentMinute();

                    edittext_clockIn.setText(time);
                } else if (viewId == R.id.editText_clock_out) {
                    endHour = timePick.getCurrentHour();
                    endMinute = timePick.getCurrentMinute();
                    edittext_clockOut.setText(time);
                }

                if (viewId == R.id.editText_clock_in) {
                    start_time = time;
                    onTimeChange.OnTimeChange(start_time, end_time, start_date, end_date);
                } else if (viewId == R.id.editText_clock_out) {
                    end_time = time;
                    onTimeChange.OnTimeChange(start_time, end_time, start_date, end_date);
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
                } else if (viewId == R.id.editText_end_shift) {
                    endDay = dayOfMonth;
                    endMonth = month;
                    endYear = year;
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
                        start_date = date;
                        editText_startShift.setText(selectedDate);
                    } else if (viewId == R.id.editText_end_shift) {
                        end_date = date;
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
        result = diffDays + "days," + diffHours + "hour" + diffMinutes + "min" + diffSeconds + "sec";
        return result;
    }
}


