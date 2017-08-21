package com.pixeldart.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentInbox extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView txt1;

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

        initialization(view);
        return view;
    }

    private void initialization(View view) {
        txt1 = (TextView)view.findViewById(R.id.txt1);
        txt1.setTypeface(Glob.avenir(getActivity()));

        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();
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

    public String upperCaseFirstLetter(String string) {
        return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
    }


    private void setupViewPager(ViewPager viewPager) {
                ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
                adapter.addFragment(FragmentNotification.instance(""),
                        getResources().getString(R.string.notification));
                adapter.addFragment(FragmentInboxList.instance(""),
                        getResources().getString(R.string.inbox));
                adapter.addFragment(FragmentChat.instance(""), getResources().getString(R.string.strata));
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




