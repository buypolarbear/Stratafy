package com.stratafy.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.Notifications;
import com.stratafy.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentInbox extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView txt1;
    private ProgressBar mProgressBar;

    private List<Notifications> mList = new ArrayList<>();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    private String property_id;
    private int uid;

    public static FragmentInbox instance(String text) {
        FragmentInbox fragment = new FragmentInbox();
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
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        initialization(view);
        return view;
    }

    private void initialization(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);
        txt1 = (TextView)view.findViewById(R.id.txt1);
        txt1.setTypeface(Glob.avenir(getActivity()));

        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        OnPageChange();

    }

    public void OnPageChange(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
                MainActivity.txtToolbarTitle.setText(upperCaseFirstLetter(tabLayout.getTabAt(position).getText().toString()));

                if(position == 2){
                    txt1.setVisibility(View.VISIBLE);
                }else {
                    txt1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Glob.avenir(getActivity()));
                }
            }
        }
    }

    public String upperCaseFirstLetter(String string) {
        return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
    }


    private void setupViewPager(ViewPager viewPager) {
        getNotification(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
            return POSITION_NONE;
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

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getNotification(final ViewPager pager) {
        mProgressBar.setVisibility(View.VISIBLE);
        mList.clear();
        String tag_string_req = "req_login";
        String url = Glob.API_GET_NOTIFICATION +uid + "/" + property_id + ".json";
        Log.d("URl", url);
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
                        JSONArray data = jObj.getJSONArray("data");

                        if(data != null){
                            for (int i = 0; i < data.length(); i++){
                                JSONObject r = data.getJSONObject(i);
                                Notifications notifications = new Notifications();
                                notifications.setMessage(r.getString("message"));
                                mList.add(notifications);
                            }
                        }

                        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
                        adapter.addFragment(FragmentNotification.instance(mList),
                                getResources().getString(R.string.notification));
                        adapter.addFragment(FragmentInboxList.instance(""),
                                getResources().getString(R.string.inbox));
                        adapter.addFragment(FragmentChat.instance(""), getResources().getString(R.string.strata));
                        pager.setAdapter(adapter);
                        pager.getAdapter().notifyDataSetChanged();
                        changeTabsFont();
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
        strReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}




