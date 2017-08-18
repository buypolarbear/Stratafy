package com.pixeldart.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixeldart.R;
import com.pixeldart.fragment.FragmentAround;
import com.pixeldart.fragment.FragmentBook;
import com.pixeldart.fragment.FragmentBuilding;
import com.pixeldart.fragment.FragmentConcierge;
import com.pixeldart.fragment.FragmentDocument;
import com.pixeldart.fragment.FragmentEvent;
import com.pixeldart.fragment.FragmentHome;
import com.pixeldart.fragment.FragmentInbox;
import com.pixeldart.fragment.FragmentLaws;
import com.pixeldart.fragment.FragmentLog;
import com.pixeldart.fragment.FragmentProfile;
import com.pixeldart.helper.ConnectionDetector;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.SessionManager;
import com.pixeldart.menu.DrawerAdapter;
import com.pixeldart.menu.DrawerItem;
import com.pixeldart.menu.SimpleItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener {

    private static final int POS_HOME= 0;
    private static final int POS_INBOX = 1;
    private static final int POS_LOG = 2;
    private static final int POS_BOOK = 3;
    private static final int POS_BUILDING = 4;
    private static final int POS_LAWS = 5;
    private static final int POS_DOCUMENT = 6;
    private static final int POS_EVENT = 7;
    private static final int POS_BOARD = 8;
    private static final int POS_AROUND = 9;
    private static final int POS_PROFILE = 10;
    private static final int POS_CONCIERGE = 11;

    SlidingRootNav mSlideMenu;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private Toolbar mToolbar;
    private TextView txtSetting, txtLogout;
    private ImageView imgClose, imgRefresh;

    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Toast.makeText(this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        mSlideMenu = new SlidingRootNavBuilder(this)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_INBOX),
                createItemFor(POS_LOG),
                createItemFor(POS_BOOK),
                createItemFor(POS_BUILDING),
                createItemFor(POS_LAWS),
                createItemFor(POS_DOCUMENT),
                createItemFor(POS_EVENT),
                createItemFor(POS_BOARD),
                createItemFor(POS_AROUND),
                createItemFor(POS_PROFILE),
                createItemFor(POS_CONCIERGE)));
        adapter.setListener(this);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);

        initialization(this);
        listener(this);

    }

    private void listener(MainActivity context) {
        txtSetting.setOnClickListener(context);
        txtLogout.setOnClickListener(context);
        imgClose.setOnClickListener(context);
        imgRefresh.setOnClickListener(context);
    }

    private void initialization(Context context) {
        txtSetting = (TextView)findViewById(R.id.txtSetting);
        txtLogout = (TextView)findViewById(R.id.txtLogout);

        txtSetting.setTypeface(Glob.avenir(context));
        txtLogout.setTypeface(Glob.avenir(context));

        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgRefresh = (ImageView)findViewById(R.id.imgRefresh);
    }

    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(MainActivity.this, screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onItemSelected(int position) {
        Fragment frag = null;
        switch (position) {
            case POS_HOME:
                frag = FragmentHome.instance(screenTitles[position]);
                break;
            case POS_INBOX:
                frag = FragmentInbox.instance(screenTitles[position]);
                break;
            case POS_LOG:
                frag = FragmentLog.instance(screenTitles[position]);;
                break;
            case POS_BOOK:
                frag = FragmentBook.instance(screenTitles[position]);
                break;
            case POS_BUILDING:
                frag = FragmentBuilding.instance(screenTitles[position]);
                break;
            case POS_LAWS:
                frag = FragmentLaws.instance(screenTitles[position]);
                break;
            case POS_DOCUMENT:
                frag = FragmentDocument.instance(screenTitles[position]);
                break;
            case POS_EVENT:
                frag = FragmentEvent.instance(screenTitles[position]);
                break;
            case POS_BOARD:
                frag = FragmentAround.instance(screenTitles[position]);
                break;
            case POS_AROUND:
                frag = FragmentAround.instance(screenTitles[position]);
                break;
            case POS_PROFILE:
                frag = FragmentProfile.instance(screenTitles[position]);
                break;
            case POS_CONCIERGE:
                frag = FragmentConcierge.instance(screenTitles[position]);
                break;
        }
        showFragment(frag);
        mSlideMenu.closeMenu();
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgClose:
                mSlideMenu.closeMenu();
                break;
            case R.id.imgRefresh:
                break;
            case R.id.txtSetting:
                break;
            case R.id.txtLogout:
                session.setLogin(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        mSlideMenu.openMenu();
        return true;
    }

}
