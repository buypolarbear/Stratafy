package com.pixeldart.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MonthYearPickerDialog;
import com.pixeldart.service.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.pixeldart.helper.MyApplication.TAG;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentEvent extends Fragment implements View.OnClickListener {

    Calendar myCalendar = Calendar.getInstance();

    private static final String EXTRA_TEXT = "text";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar mProgressBar;

    private String property_id;
    int uid;

    private TextView txtDates, txtDetails;
    private EditText edtTitle, edtDate, edtSTime, edtETime;
    private Button btnSubmit;

    String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    String[] months = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"};

    private CompactCalendarView compactCalendarView;
    private FloatingActionButton btnAdd;
    private RelativeLayout llRoot1, llRoot2;
    private boolean isVisible = false;

    public static FragmentEvent instance(String text) {
        FragmentEvent fragment = new FragmentEvent();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Events & Calender");

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);

        initialization(view);

        return view;
    }

    private void initialization(View view) {
        txtDates = (TextView) view.findViewById(R.id.txtDates);
        txtDates.setTypeface(Glob.avenir(getActivity()));

        edtTitle = (EditText) view.findViewById(R.id.edtTitle);
        edtTitle.setTypeface(Glob.avenir(getActivity()));

        txtDetails = (TextView) view.findViewById(R.id.txtDetails);
        txtDetails.setTypeface(Glob.avenir(getActivity()));

        llRoot2 = (RelativeLayout) view.findViewById(R.id.root2);
        llRoot1 = (RelativeLayout) view.findViewById(R.id.root1);

        edtDate = (EditText) view.findViewById(R.id.edtDate);
        edtSTime = (EditText) view.findViewById(R.id.edtSTime);
        edtETime = (EditText) view.findViewById(R.id.edtETime);

        edtDate.setTypeface(Glob.avenir(getActivity()));
        edtSTime.setTypeface(Glob.avenir(getActivity()));
        edtETime.setTypeface(Glob.avenir(getActivity()));

        edtDate.setOnClickListener(this);
        edtSTime.setOnClickListener(this);
        edtETime.setOnClickListener(this);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(Glob.avenir(getActivity()));
        btnSubmit.setOnClickListener(this);

        btnAdd = (FloatingActionButton) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked", "YES");
                llRoot1.setVisibility(View.GONE);
                llRoot2.setVisibility(View.VISIBLE);
            }
        });

        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setDayColumnNames(days);
        txtDates.setText(String.valueOf(months[myCalendar.get(Calendar.MONTH)]) + " " + myCalendar.get(Calendar.YEAR));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String[] date = String.valueOf(firstDayOfNewMonth).split(" ");
                txtDates.setText(date[1] + " " + date[5]);
            }
        });

        txtDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setListener(date);
                pd.show(getFragmentManager(), "MonthYearPickerDialog");

            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            try {
                compactCalendarView.setCurrentDate(new SimpleDateFormat("yyyy-MM-dd")
                        .parse(year + "-" + monthOfYear + "-" + dayOfMonth));
                txtDates.setText(months[monthOfYear - 1] + " " + year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            edtDate.setText(dayOfMonth + " " + months[monthOfYear] + " " + year);
        }
    };


    private DatePickerDialog datePicker() {
        DatePickerDialog dpd2 = new DatePickerDialog(getActivity(), date2, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        return dpd2;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edtDate:
                datePicker().show();
                break;
            case  R.id.edtSTime:
                showStartTimePicker();
                break;

            case  R.id.edtETime:
                showEndTimePicker();
                break;
            case R.id.btnSubmit:
                isVisible = true;
                llRoot2.setVisibility(View.GONE);
                llRoot1.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void showEndTimePicker(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtETime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.show();
    }

    public void showStartTimePicker(){
        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtSTime.setText( selectedHour + ":" + selectedMinute + " " + ((mcurrentTime.get(Calendar.AM_PM) == Calendar.AM) ? "am" : "pm"));
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.show();
    }
}
