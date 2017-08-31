package com.pixeldart.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.adapter.AdapterEvent;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MonthYearPickerDialog;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Events;
import com.pixeldart.service.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EditText edtTitle, edtDate, edtSTime, edtETime, edtDetail;
    private Button btnSubmit;

    String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    String[] months = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"};

    private CompactCalendarView compactCalendarView;
    private FloatingActionButton btnAdd;
    private RelativeLayout llRoot1, llRoot2;

    private RecyclerView recycleEvent;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterEvent adapter;

    private List<Events> mList = new ArrayList<>();

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
        uid = pref.getInt("uid", 0);

        initialization(view);

        getEvent(getActivity());

        return view;
    }

    private void initialization(View view) {
        mProgressBar = (ProgressBar)view.findViewById(R.id.mProgressbar);
        recycleEvent = (RecyclerView) view.findViewById(R.id.recycleEvent);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleEvent.setLayoutManager(mLayoutManager);

        adapter = new AdapterEvent(getActivity(), mList);
        recycleEvent.setAdapter(adapter);

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
        edtDetail = (EditText) view.findViewById(R.id.edtDetail);

        edtDate.setTypeface(Glob.avenir(getActivity()));
        edtSTime.setTypeface(Glob.avenir(getActivity()));
        edtETime.setTypeface(Glob.avenir(getActivity()));
        edtDetail.setTypeface(Glob.avenir(getActivity()));

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
            case R.id.edtSTime:
                showStartTimePicker();
                break;

            case R.id.edtETime:
                showEndTimePicker();
                break;
            case R.id.btnSubmit:
                if(validation()){
                    String title= edtTitle.getText().toString();
                    String desc = edtDetail.getText().toString();
                    String date = edtDate.getText().toString();
                    String sTime = edtSTime.getText().toString();
                    String eTime = edtETime.getText().toString();
                    postEvent(getActivity(), title, desc, date, sTime, eTime);
                }
                break;
        }
    }

    public void showEndTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtETime.setText(getTime(selectedHour, selectedMinute));
            }
        }, hour, minute, false);
        mTimePicker.show();
    }

    public void showStartTimePicker() {
        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtSTime.setText(getTime(selectedHour, selectedMinute));
            }
        }, hour, minute, false);//Yes 12 hour time
        mTimePicker.show();
    }

    public String getTime(int selectedHour, int selectedMinute) {
        String time = selectedHour + ":" + selectedMinute;
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = fmt.parse(time);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
        String formattedTime = fmtOut.format(date);
        return formattedTime;
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getEvent(final Context context) {
        String tag_string_req = "req_login";
        mProgressBar.setVisibility(View.VISIBLE);
        String url = Glob.API_GET_EVENT + property_id + ".json";
        Log.d("URL", url);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                longLog(response.toString(), "TAG");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject data = jObj.getJSONObject("data");
                       /* Events events = new Events();
                        events.setTitle(obj_event.getString("title"));
                        events.setsTime(obj_event.getString("start_time"));
                        events.seteTime(obj_event.getString("end_time"));
                        mList.add(events);
                        edtTitle.setText("");
                        edtDetail.setText("");
                        edtDate.setText("");
                        edtSTime.setText("");
                        edtETime.setText("");
                        llRoot2.setVisibility(View.GONE);
                        llRoot1.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();

                        String tarikh = obj_event.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
                        Date date = sdf.parse(tarikh);
                        long millis = date.getTime();
                        Event event = new Event(Color.BLUE, millis);
                        compactCalendarView.addEvent(event, true);*/
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void postEvent(final Context context, final String title,
                           final String desc, final String event_date, final String sTime, final String eTime) {
        String tag_string_req = "req_login";
        mProgressBar.setVisibility(View.VISIBLE);
        String url = Glob.API_POST_EVENT + property_id + ".json";
        Log.d("URL", url);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "EVENT Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject data = jObj.getJSONObject("data");
                        JSONObject obj_event = data.getJSONObject("Event");
                        Events events = new Events();
                        events.setTitle(obj_event.getString("title"));
                        events.setsTime(obj_event.getString("start_time"));
                        events.seteTime(obj_event.getString("end_time"));
                        mList.add(events);
                        edtTitle.setText("");
                        edtDetail.setText("");
                        edtDate.setText("");
                        edtSTime.setText("");
                        edtETime.setText("");
                        llRoot2.setVisibility(View.GONE);
                        llRoot1.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();

                        String tarikh = obj_event.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
                        Date date = sdf.parse(tarikh);
                        long millis = date.getTime();
                        Event event = new Event(Color.BLUE, millis);
                        compactCalendarView.addEvent(event, true);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("data[Event][user_id]", String.valueOf(uid));
                params.put("data[Event][title]", title);
                params.put("data[Event][description]", desc);
                params.put("data[Event][date]", event_date);
                params.put("data[Event][start_time]", sTime);
                params.put("data[Event][end_time]", eTime);

                Log.d("PARAM", params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public boolean validation() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            edtTitle.setError("Title is empty");
            return false;
        }
        else if (edtDetail.getText().toString().trim().isEmpty()) {
            edtDetail.setError("detail is empty");
            return false;
        }
        else if (edtDate.getText().toString().trim().isEmpty()) {
            edtDate.setError("Date is empty");
            return false;
        }
        else if (edtSTime.getText().toString().trim().isEmpty()) {
            edtSTime.setError("Add start time");
            return false;
        } else if (edtETime.getText().toString().trim().isEmpty()) {
            edtETime.setError("Add end time");
            return false;
        }
        else {
            return true;
        }
    }
}
