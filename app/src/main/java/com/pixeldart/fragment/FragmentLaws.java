package com.pixeldart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.Glob;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentLaws extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout ll1, ll2;
    private TextView txtSummary;

    public static FragmentLaws instance(String text) {
        FragmentLaws fragment = new FragmentLaws();
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
        View view = inflater.inflate(R.layout.fragment_laws, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {

        ll1 = (LinearLayout)view.findViewById(R.id.ll1);
        ll2 = (LinearLayout)view.findViewById(R.id.ll2);
        txtSummary = (TextView) view.findViewById(R.id.txtSummary);
        txtSummary.setTypeface(Glob.avenir(getActivity()));

        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("By-Laws");

        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();
        OnPageChange();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
        //  Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(FragmentCurrentLaws.newInstance("", ""),
                getResources().getString(R.string.current));
        adapter.addFragment(FragmentFutureVote.newInstance("", ""), getResources().getString(R.string.future));
        viewPager.setAdapter(adapter);
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

}
