package com.pixeldart.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Archived;
import com.pixeldart.model.MyLogs;
import com.pixeldart.model.PublicLogs;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentLog extends Fragment implements View.OnClickListener {

    private static final String EXTRA_TEXT = "text";
    private TextView txt2, txtNoMyLog, txtNoPLog, txtNoArchived, txtMyLog, txtPLog, txtArchived;
    private FloatingActionButton btnAdd;
    private ViewPager viewPager;
    private LinearLayout llFirst, llSecond, llThird;
    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    private String property_id, cat_id;
    private int uid;
    private List<MyLogs> myLogsList = new ArrayList<>();
    private List<PublicLogs> publicLogsList = new ArrayList<>();
    private List<Archived> archivedList = new ArrayList<>();

    public static FragmentLog instance(String text) {
        FragmentLog fragment = new FragmentLog();
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
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText(getResources().getString(R.string.logs));

        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        initialization(view);
        return view;
    }

    private void initialization(View view) {

        txt2 = (TextView)view.findViewById(R.id.txt2);
        txtNoMyLog = (TextView)view.findViewById(R.id.txtNoMyLog);
        txtNoPLog = (TextView)view.findViewById(R.id.txtNoPLog);
        txtNoArchived = (TextView)view.findViewById(R.id.txtNoArchived);
        txtMyLog = (TextView)view.findViewById(R.id.txtMyLog);
        txtPLog = (TextView)view.findViewById(R.id.txtPLog);
        txtArchived = (TextView)view.findViewById(R.id.txtArchived);

        txt2.setTypeface(Glob.avenir(getActivity()));
        txtNoMyLog.setTypeface(Glob.avenir(getActivity()));
        txtNoPLog.setTypeface(Glob.avenir(getActivity()));
        txtNoArchived.setTypeface(Glob.avenir(getActivity()));
        txtMyLog.setTypeface(Glob.avenir(getActivity()));
        txtPLog.setTypeface(Glob.avenir(getActivity()));
        txtArchived.setTypeface(Glob.avenir(getActivity()));

        llFirst = (LinearLayout)view.findViewById(R.id.llFirst);
        llSecond = (LinearLayout)view.findViewById(R.id.llSecond);
        llThird = (LinearLayout)view.findViewById(R.id.llThird);

        llFirst.setOnClickListener(this);
        llSecond.setOnClickListener(this);
        llThird.setOnClickListener(this);

        btnAdd = (FloatingActionButton)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        viewPager = (ViewPager)view.findViewById(R.id.pager);
        setupViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        getLogs(viewPager);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter  {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.llFirst:
                viewPager.setCurrentItem(0);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
            case R.id.llSecond:
                viewPager.setCurrentItem(1);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
            case R.id.llThird:
                viewPager.setCurrentItem(2);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
            case R.id.btnAdd:
                FragmentCreateLog fragment2 = new FragmentCreateLog();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment2, "CREATE_LOG");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getLogs(final ViewPager pager) {
        mProgressBar.setVisibility(View.VISIBLE);
        myLogsList.clear();
        publicLogsList.clear();
        archivedList.clear();
        String tag_string_req = "req_login";
        String url = Glob.API_GET_LOG + property_id + ".json?user_id="+uid;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                longLog(response, "Low_response");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        JSONObject obj = jObj.getJSONObject("data");
                        JSONArray my_log = obj.getJSONArray("my_log");
                        for(int i = 0; i < my_log.length(); i++){
                            JSONObject r = my_log.getJSONObject(i);
                            MyLogs logs = new MyLogs();
                            logs.setId(r.getString("id"));
                            logs.setUserId(r.getString("user_id"));
                            logs.setBuildingId(r.getString("building_id"));
                            logs.setLogCategoryId(r.getString("log_category_id"));
                            logs.setTitle(r.getString("title"));
                            logs.setDescription(r.getString("description"));
                            logs.setStatus(r.getString("status"));
                            logs.setPrivates(r.getString("private"));
                            logs.setArchived_date(r.getString("archive_date"));
                            logs.setLogCategoryName(r.getString("log_category_name"));
                            JSONArray imag = r.getJSONArray("attachments");
                            if(imag != null){
                                String strings[] = new String[imag.length()];
                                for(int j=0;j<strings.length;j++) {
                                    strings[j] = imag.getString(j);
                                }
                                if(strings.length > 0){
                                    logs.setAttachments(strings[0]);
                                }
                            }
                            logs.setCreated(r.getString("created"));

                            myLogsList.add(logs);
                        }

                        JSONArray public_logs = obj.getJSONArray("public");
                        if(public_logs != null){
                            for(int i = 0; i < public_logs.length(); i++){
                                JSONObject r = public_logs.getJSONObject(i);
                                PublicLogs logs = new PublicLogs();
                                logs.setId(r.getString("id"));
                                logs.setUserId(r.getString("user_id"));
                                logs.setBuildingId(r.getString("building_id"));
                                logs.setLogCategoryId(r.getString("log_category_id"));
                                logs.setTitle(r.getString("title"));
                                logs.setDescription(r.getString("description"));
                                logs.setStatus(r.getString("status"));
                                logs.setPrivates(r.getString("private"));
                                logs.setArchived_date(r.getString("archive_date"));
                                logs.setLogCategoryName(r.getString("log_category_name"));
                                JSONArray imag = r.getJSONArray("attachments");
                                if(imag != null){
                                    String strings[] = new String[imag.length()];
                                    for(int j=0;j<strings.length;j++) {
                                        strings[j] = imag.getString(j);
                                    }
                                    if(strings.length > 0){
                                        logs.setAttachments(strings[0]);
                                    }

                                }
                                logs.setCreated(r.getString("created"));

                                publicLogsList.add(logs);
                            }

                        }

                        JSONArray archived_logs = obj.getJSONArray("archive");
                        if(archived_logs != null){
                            for(int i = 0; i < archived_logs.length(); i++){
                                JSONObject r = archived_logs.getJSONObject(i);
                                Archived logs = new Archived();
                                logs.setId(r.getString("id"));
                                logs.setUserId(r.getString("user_id"));
                                logs.setBuildingId(r.getString("building_id"));
                                logs.setLogCategoryId(r.getString("log_category_id"));
                                logs.setTitle(r.getString("title"));
                                logs.setDescription(r.getString("description"));
                                logs.setStatus(r.getString("status"));
                                logs.setPrivates(r.getString("private"));
                                logs.setArchived_date(r.getString("archive_date"));
                                logs.setLogCategoryName(r.getString("log_category_name"));
                                JSONArray imag = r.getJSONArray("attachments");
                                if(imag != null){
                                    String strings[] = new String[imag.length()];
                                    for(int j=0;j<strings.length;j++) {
                                        strings[j] = imag.getString(j);
                                    }
                                    if(strings.length > 0){
                                        logs.setAttachments(strings[0]);
                                    }
                                }

                                logs.setCreated(r.getString("created"));

                                archivedList.add(logs);
                            }
                        }

                        txtNoMyLog.setText(""+myLogsList.size());
                        txtNoPLog.setText(""+publicLogsList.size());
                        txtNoArchived.setText(""+archivedList.size());

                        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
                        adapter.addFragment(FragmentMyLogs.instance(myLogsList),
                                getResources().getString(R.string.mylogs));
                        adapter.addFragment(FragmentPublicLogs.instance(publicLogsList),
                                getResources().getString(R.string.plogs));
                        adapter.addFragment(FragmentArchived.instance(archivedList), getResources().getString(R.string.archived));
                        pager.setAdapter(adapter);
                        pager.getAdapter().notifyDataSetChanged();

                        mProgressBar.setVisibility(View.GONE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Law_error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
