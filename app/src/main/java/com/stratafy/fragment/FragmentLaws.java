package com.stratafy.fragment;

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
import android.widget.LinearLayout;
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
import com.stratafy.model.Laws;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentLaws extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout ll1, ll2;
    private TextView txtSummary;
    private ProgressBar mProgressBar;
    private List<Laws> mLawsList = new ArrayList<>();
    private String summery;

    public static FragmentLaws instance() {
        FragmentLaws fragment = new FragmentLaws();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laws, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);
        ll1 = (LinearLayout)view.findViewById(R.id.ll1);
        ll2 = (LinearLayout)view.findViewById(R.id.ll2);
        txtSummary = (TextView) view.findViewById(R.id.txtSummary);

        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("By-Laws");
        MainActivity.imgStreaming.setVisibility(View.GONE);

        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        OnPageChange();
        getLaws();
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

    public void OnPageChange(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ll1.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.GONE);
                }else {
                    ll1.setVisibility(View.GONE);
                    ll2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(FragmentCurrentLaws.newInstance(mLawsList, summery),
                getResources().getString(R.string.current));
        adapter.addFragment(FragmentFutureVote.newInstance("", ""), getResources().getString(R.string.future));
        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();
        changeTabsFont();
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

    private void getLaws() {
        String url = Glob.API_GET_LAWS + MainActivity.property_id + ".json?user_id="+MainActivity.uid;
        Log.d("URL", url);
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
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject data = jObj.getJSONObject("data");
                        JSONArray laws = data.getJSONObject("by_law").getJSONArray("laws");
                        summery = data.getJSONObject("by_law").getString("summery");
                        for(int i= 0; i < laws.length(); i++){
                            JSONObject obj = laws.getJSONObject(i);
                            Laws laws1 = new Laws();
                            laws1.setLaw_id(obj.getString("id"));
                            laws1.setLaw_name(obj.getString("title"));
                            laws1.setLaw_text(obj.getString("description"));
                            laws1.setStatus(obj.getString("status"));
                            laws1.setAgree(obj.getString("aggreed"));
                            laws1.setDisagree(obj.getString("disagreed"));
                            mLawsList.add(laws1);
                        }
                        setupViewPager();

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
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }
}
